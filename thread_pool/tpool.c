#include <stdio.h>
#include <stdlib.h>
#include <stddef.h>
#include <unistd.h>

#include "tpool.h"
#include "list.h"

void wq_init(list_head_t **wq_list_head)
{
	*wq_list_head = (list_head_t *) malloc(sizeof(list_head_t));
	INIT_LIST_HEAD(*wq_list_head);
}

void wq_add_work(list_head_t *wq_list_head, work_func_t func, void *arg)
{
	if (func == NULL) {
		return;
	}

	work_queue_t *wq = (work_queue_t *) malloc(sizeof(work_queue_t));

	list_add_tail(&(wq->wq_list), wq_list_head);
	wq->func = func;
	wq->arg = arg;
}

static work_queue_t *wq_get_work(list_head_t *wq_list_head)
{
	work_queue_t *wq = NULL;

	if (!list_empty(wq_list_head)) {
		wq = list_first_entry(wq_list_head, work_queue_t, wq_list);
		list_del_init(&(wq->wq_list));
	}

	return wq;
}

static void wq_remove_work(work_queue_t *wq)
{
	list_del(&(wq->wq_list));
	free(wq);
}

void wq_delete(list_head_t *wq_list_head)
{
	if (!list_empty(wq_list_head)) {
		list_head_t *cur_wq_list;
		list_head_t *next_wq_list;
		list_for_each_safe (cur_wq_list, next_wq_list, wq_list_head) {
			work_queue_t *cur_wq = list_entry(cur_wq_list,
							  work_queue_t,
							  wq_list);
			list_del(cur_wq_list);
			free(cur_wq);
		}
	}

	free(wq_list_head);
}

static void *tpool_thread(void *arg)
{
	tpool_t *tpool = (tpool_t *) arg;
	work_queue_t *wq;

	printf("THREAD [%li] started\n", (long)pthread_self());
	
	while (1) {
		pthread_mutex_lock(&(tpool->tpool_mutex));

		/* Waiting for work to be added to queue*/
		while (!tpool->stop
		       && (tpool->wq_list_head == NULL
		           || list_empty(tpool->wq_list_head)))
		{
			pthread_cond_wait(&(tpool->wake_cond), &(tpool->tpool_mutex));
		}

		/* True when tpool_delete called */
		if (tpool->stop)
			break;

		wq = wq_get_work(tpool->wq_list_head);
		tpool->busy_num++;
		pthread_mutex_unlock(&(tpool->tpool_mutex));

		if (wq != NULL) {
			wq->func(wq->arg);
			wq_remove_work(wq);
			usleep(500000);
		}

		pthread_mutex_lock(&(tpool->tpool_mutex));
		tpool->busy_num--;
		/* Notify tpool_wait if we're the last worker and no more work */
		if (!tpool->stop && tpool->busy_num == 0 && list_empty(tpool->wq_list_head))
			pthread_cond_signal(&(tpool->sleep_cond));
		pthread_mutex_unlock(&(tpool->tpool_mutex));
	}

	printf("THREAD [%li] stopped\n", (long)pthread_self());

	/* Stop thread and notify tpool_wait */
	tpool->thread_num--;
	pthread_cond_signal(&(tpool->sleep_cond));
	pthread_mutex_unlock(&(tpool->tpool_mutex));
	
	return NULL;
}

void tpool_init(tpool_t **tpool, int thread_num)
{
	pthread_t thread_id;
	int i;

	*tpool = (tpool_t *) malloc(sizeof(tpool_t));
	(*tpool)->wq_list_head = NULL;
	(*tpool)->thread_num = thread_num;
	(*tpool)->busy_num = 0;
	(*tpool)->stop = 0;

	pthread_mutex_init(&((*tpool)->tpool_mutex), NULL);
	pthread_cond_init(&((*tpool)->wake_cond), NULL);
	pthread_cond_init(&((*tpool)->sleep_cond), NULL);

	for (i = 0; i < thread_num; ++i) {
		pthread_create(&thread_id, NULL, tpool_thread, (*tpool));
		pthread_detach(thread_id);
	}

	return;
}

int tpool_add_wq(tpool_t *tpool, list_head_t *wq_list_head)
{
	if (tpool == NULL)
		return 1;

	if (list_empty(wq_list_head)) {
		return 1;
	}

	pthread_mutex_lock(&(tpool->tpool_mutex));
	tpool->wq_list_head = wq_list_head;
	pthread_cond_broadcast(&(tpool->wake_cond));
	pthread_mutex_unlock(&(tpool->tpool_mutex));

	return 0;
}

void tpool_wait(tpool_t *tpool)
{
	if (tpool == NULL)
		return;

	pthread_mutex_lock(&(tpool->tpool_mutex));
	while (1) {
		if ((!tpool->stop && !list_empty(tpool->wq_list_head))
		    || (!tpool->stop && tpool->busy_num   != 0)
		    || ( tpool->stop && tpool->thread_num != 0))
		{
			pthread_cond_wait(&(tpool->sleep_cond), &(tpool->tpool_mutex));
		} else {
			break;
		}
	}
	pthread_mutex_unlock(&(tpool->tpool_mutex));
}

void tpool_delete(tpool_t *tpool)
{
	if (tpool == NULL)
		return;

	pthread_mutex_lock(&(tpool->tpool_mutex));
	tpool->stop = 1;
	pthread_cond_broadcast(&(tpool->wake_cond));
	pthread_mutex_unlock(&(tpool->tpool_mutex));

	tpool_wait(tpool);

	pthread_mutex_destroy(&(tpool->tpool_mutex));
	pthread_cond_destroy(&(tpool->wake_cond));
	pthread_cond_destroy(&(tpool->sleep_cond));

	free(tpool);
}
