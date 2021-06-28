#ifndef TPOOL_H
#define TPOOL_H

#include <pthread.h>

#include "list.h"

typedef void (*work_func_t)(void *arg);

struct work_queue {
	list_head_t wq_list;
	work_func_t func;
	void       *arg;
};
typedef struct work_queue work_queue_t;

struct tpool {
	list_head_t    *wq_list_head;
	pthread_mutex_t tpool_mutex;
	pthread_cond_t  wake_cond;
	pthread_cond_t  sleep_cond;
	int             thread_num;
	int             busy_num;
	int             stop;
};
typedef struct tpool tpool_t;

void wq_init(list_head_t **wq_list_head);
void wq_add_work(list_head_t *wq_list_head, work_func_t func, void *arg);
void wq_delete(list_head_t *wq_list_head);

void tpool_init(tpool_t **tpool, int thread_num);
int  tpool_add_wq(tpool_t *tpool, list_head_t *wq_list_head);
void tpool_wait(tpool_t *tpool);
void tpool_delete(tpool_t *tpool);

#endif /* TPOOL_H */
