#include <stdio.h>
#include <stdlib.h>
#include <stddef.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <error.h>

#include "tpool.h"
#include "list.h"

#define LINE_MAX_NUM 128
#define LINE_MAX_LEN 256

void singer(void *arg);

int main(int argc, char **argv)
{
	list_head_t *wq_list_head;
	tpool_t *tpool;
	int thread_num;
	FILE *fp;
	char song[LINE_MAX_NUM + 1][LINE_MAX_LEN + 1];
	char c;
	int i, k, err;

	if (argc < 2) {
		printf("Usage: %s THREAD_NUM\n", argv[0]);
		exit(0);
	}
	
	thread_num = strtol(argv[1], NULL, 10);
	printf("Running thread_pool for %i threads\n\n", thread_num);

	fp = fopen("song.txt", "r");
	if (fp == NULL) {
		error(EXIT_FAILURE, errno, "Failed to open: song.txt\n");
	}

	i = 0;
	k = 0;
	memset(song, 0, (LINE_MAX_NUM + 1) * (LINE_MAX_LEN + 1));
	while (EOF != (c = fgetc(fp)) && i < LINE_MAX_NUM) {
		if (c == '\n') {
			k = 0;
			++i;
		} else if (k < LINE_MAX_LEN) {
			song[i][k] = c;
			++k;
		}
	}
	if (k != 0 && i < LINE_MAX_NUM) ++i;
	song[i][0] = EOF;

	fclose(fp);

	wq_list_head = NULL;
	wq_init(&wq_list_head);
	if (wq_list_head == NULL) {
		error(EXIT_FAILURE, errno, "wq_init returned NULL\n");
	}

	for (i = 0; i < LINE_MAX_NUM && song[i][0] != EOF; ++i) {
		wq_add_work(wq_list_head, singer, song[i]);
	}
	
	tpool = NULL;
	tpool_init(&tpool, thread_num);
	if (tpool == NULL) {
		error(EXIT_FAILURE, errno, "tpool_init returned NULL\n");
	}

	err = tpool_add_wq(tpool, wq_list_head);

	if (!err) {
		tpool_wait(tpool);
	} else {
		error(0, errno, "Failed to add worq queue to thread pool\n");
	}

	tpool_delete(tpool);
	wq_delete(wq_list_head);

	return 0;
}

void singer(void *arg){
	char *line = (char *) arg;

	printf("SONG [%li] %s\n", (long)pthread_self(), line);

	return;
}
