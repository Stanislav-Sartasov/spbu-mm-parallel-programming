#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <pthread.h>
#include <signal.h>
#include <time.h>
#include <errno.h>
#include <error.h>

#include "list.h"

struct node {
	list_head_t node_list;
	int 	    num;
};
typedef struct node node_t;

LIST_HEAD(node_list_head);

pthread_mutex_t node_mutex;

volatile sig_atomic_t interrupt_flag = 0;

void interrupt()
{
      	interrupt_flag = 1;
}

static void* producer();

static void* consumer();

int main(int argc, char **argv)
{
	int i, err = 0;
	int prod_num, cons_num;
	pthread_t *thread_id;
	
	signal(SIGINT, interrupt);
	srand(time(NULL));

	if (argc < 3) {
		printf("Usage: %s PROD_NUM CONS_NUM\n", argv[0]);
		exit(0);
	}
	
	prod_num = strtol(argv[1], NULL, 10);
	cons_num = strtol(argv[2], NULL, 10);
	printf("Running producer_consumer for %i producers and %i consumers\n\n",
	       prod_num, cons_num);

	pthread_mutex_init(&node_mutex, NULL);

	thread_id = (pthread_t *) calloc((prod_num + cons_num), sizeof(pthread_t));
	i = 0;
	while (i < prod_num) {
		err = pthread_create((thread_id + i), NULL, &producer, NULL);
		
		if (err != 0) {
			free(thread_id);
			pthread_mutex_destroy(&node_mutex);
			error(EXIT_FAILURE, err, "Failed to create a thread");
		}

		i++;
	}
	while (i < prod_num + cons_num) {
		err = pthread_create((thread_id + i), NULL, &consumer, NULL);

		if (err != 0) {
			free(thread_id);
			pthread_mutex_destroy(&node_mutex);
			error(EXIT_FAILURE, err, "Failed to create a thread");
		}

		i++;
	}

	i = 0;
	while (i < (prod_num + cons_num)) {
		pthread_join(thread_id[i], NULL);
		i++;
	}

	printf("\nRemaining nodes:\n");

	list_head_t *cur_node_list;
	list_head_t *next_node_list;
	list_for_each_safe (cur_node_list, next_node_list, &node_list_head) {
		node_t *cur_node = list_entry(cur_node_list,
					      node_t,
					      node_list);

		printf("NODE num: %i\n", cur_node->num);

		list_del(cur_node_list);
		free(cur_node);
	}

	pthread_mutex_destroy(&node_mutex);

	return 0;
}

static void *producer()
{
	int num;

	while (!interrupt_flag) {
		num = rand() % 1000;

		node_t *node = (node_t *) malloc(sizeof(node_t));
		node->num = num;

		pthread_mutex_lock(&node_mutex);
		list_add_tail(&(node->node_list), &node_list_head);
		pthread_mutex_unlock(&node_mutex);

		printf("PRODUCE [%li] num: %i\n", (long)pthread_self(), num);

		usleep(500000);
	}

	printf("PRODUCER [%li] closed\n", (long)pthread_self());
	return NULL;
}

static void *consumer()
{
	int num;

	while(!interrupt_flag) {
		node_t *node;

		pthread_mutex_lock(&node_mutex);
		if (list_empty(&node_list_head)) {
			pthread_mutex_unlock(&node_mutex);
			sleep(1);
			continue;
		}

		node = list_last_entry(&node_list_head, node_t, node_list);
		num = node->num;

		list_del(&(node->node_list));
		free(node);
		pthread_mutex_unlock(&node_mutex);

		printf("CONSUME [%li] num: %i\n", (long)pthread_self(), num);
		usleep(500000);
	}

	printf("CONSUMER [%li] closed\n", (long)pthread_self());
	return NULL;
}
