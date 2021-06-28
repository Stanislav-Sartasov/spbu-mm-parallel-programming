CC = gcc
CFLAGS = -Wall -Wextra -Werror -I$(IDIR) -pthread

IDIR = ./include
PRODCONS_DIR = ./producer_consumer
TPOOL_DIR = ./thread_pool

PRODCONS.c = $(PRODCONS_DIR)/main.c
TPOOL.c = $(TPOOL_DIR)/main.c $(TPOOL_DIR)/tpool.c

PRODCONS_PROG = $(PRODCONS_DIR)/main
TPOOL_PROG = $(TPOOL_DIR)/main

PRODCONS_TEST = prodcons_test.py
TPOOL_TEST = tpool_test.py

PHONY := all
all : prodcons tpool

PHONY += prodcons
prodcons : $(PRODCONS_PROG)

PHONY += tpool
tpool : $(TPOOL_PROG)

PHONY += prodcons_test
prodcons_test : prodcons prodcons_test.py
	./prodcons_test.py
	
PHONY += tpool_test
tpool_test : tpool tpool_test.py
	./tpool_test.py

$(PRODCONS_PROG) : $(PRODCONS.c)
	$(CC) $(CFLAGS) -o $@ $^

$(TPOOL_PROG) : $(TPOOL.c)
	$(CC) $(CFLAGS) -o $@ $^
