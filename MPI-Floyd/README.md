# MPI Floyd Algorithm Implementation

This project implements the Floyd-Warshall algorithm using MPI for parallel computing.

## Requirements

- MPI (Message Passing Interface) implementation (e.g., MPICH, OpenMPI)
- Python 3.x for generating graphs

### Building the Project

To compile the Floyd-Warshall implementation, use the provided Makefile:

```bash
make
```

### Running the Project

- **Graph Generation:** First, generate a graph input file using the included Python script:

```bash
python3 graph_generator.py -v 5056
```

You can also use `-e` and `-s` options to specify number of edges and seed, respectively

- **Execution:** Use `mpiexec` with `-n #num_of_processors` option to run:
```bash
mpiexec -n 4 ./floyd
```
