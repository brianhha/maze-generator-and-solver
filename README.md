# maze-generator-and-solver

There is only one Java class to be run, and it takes 2 command line arguments that should each be an integer between 2 and 201 (I chose 201 arbitrarily to prevent recursive stack overflow for large inputs).

Even inputs are incremented by 1 because the maze generation algorithm works for odd values of rows and columns.

The program generates a random maze where the first argument is the number of rows and the second is the number of columns, then it solves the maze. Both of these are printed to stdout.

