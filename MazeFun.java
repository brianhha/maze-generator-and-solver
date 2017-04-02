import java.util.Random;

/**
 * This program generates a maze with a starting and ending point (labeled 'D' for desk and 'C' for coffee) and then solves it.
 * Both methods are implemented using recursive depth-first search.
 * 
 * Credit to Wikipedia's page on Maze generation algorithm (https://en.wikipedia.org/wiki/Maze_generation_algorithm)
 *
 * @author Brian Ha
 * @version 1.0
 */
public class MazeFun {

	/** Keeps track of the maze state for all method invocations. */
	private static char[][] maze;

	/** Used to generate random integers via Fisher–Yates shuffle used for maze generation. */
	private static Random random;
	
	/**
	 * Main method takes in 2 inputs from the command line for number of rows and columns respectively.
	 * Note that because the maze generation algorithm moves by 2 steps at a time, we change even inputs to odd by adding 1.
	 * For simplicity's sake: inputs <= 1 and those that could cause stack overflow in the recursive methods cause an error message to be displayed.
	 *
	 * @param args  user input row and column values in String array form
	 */
	public static void main (String[] args) {
		try {
			int inpRows = Integer.parseInt(args[0]);
			int inpCols = Integer.parseInt(args[1]);

			
			if (inpRows <= 1 || inpRows > 201) { // 201 is an arbitrarily large value I selected
				throw new Exception();
			}
			if (inpRows % 2 == 0) { // change even values to odd
				inpRows++;
			}

			if (inpCols <= 1 || inpCols > 201) {
				throw new Exception();
			}
			if (inpCols % 2 == 0) {
				inpCols++;
			}
	 
			random = new Random();

			maze = createMaze(inpRows, inpCols);
			System.out.println(convertMazeToString());
			maze[1][1] = ' '; // temporarily remove the desk indicator to allow (maze[row][col] != ' ') to be false in the solveMaze method
			solveMaze(1, 1); // all mazes I create will have starting position of [1][1]
			maze[1][1] = 'D'; 
			System.out.println("\n");
			System.out.println(convertMazeToString());
		} catch (Exception ex) {
			System.out.println("Please input two integers between 2 and 201.");
		}
	}

	/**
	 * Creates a maze of size rows by cols, using recursive depth-first search.
	 * Walls are represented by the character '|'.
	 * Free spaces are represented by a space character ' '.
	 *
	 * @param rows 	number of rows for the maze
	 * @param cols 	number of columns for the maze
	 * @return char[][]  the generated maze
	 */
	public static char[][] createMaze(int rows, int cols) {
		maze = new char[rows][cols];
	
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				maze[r][c] = '|';
			}
		}
	
		int randRow = 1;
		int randCol = 1;
	
		maze[randRow][randCol] = ' '; // create free space at starting point
		
		maze = createPathsDFS(randRow, randCol);
		maze[1][1] = 'D'; // manually set starting and ending points to be at opposite corners
		maze[rows-2][cols-2] = 'C';
		return maze;
	}

	/**
	 * Creates paths and dead ends from the starting to ending point represented by ' ' characters, via recursive DFS.
	 * We achieve this by attempting to move 2 spaces in all 4 directions, and if this is possible, setting those spaces to 
	 * free spaces and recursively calling this function from the position 2 spaces over.
	 *
	 * @param row 	starting point row index
	 * @param col  	starting point column index
	 * @return char[][]  the maze with the generated paths
	 */
	public static char[][] createPathsDFS(int row, int col) {
		int rows = maze.length;
		int cols = maze[0].length;
	
		int[] dirs = {1, 2, 3, 4}; // possible directions we can traverse: represent right, down, left, and up respectively
	
		for (int j = 1; j < dirs.length; j++) { // use Fisher–Yates shuffle to ensure randomly generated mazes
			int i = random.nextInt(j + 1);
			int temp = dirs[i];
			dirs[i] = dirs[j];
			dirs[j] = temp;
		}
	
		for (int dir : dirs) { // traverse in all possible directions
			if (dir == 1) { // right
				if (col + 2 >= cols) { // check if out of bounds
					continue;
				}
				if (maze[row][col+2] != ' ') { // check if overlapping on existing path
					maze[row][col+1] = ' ';
					maze[row][col+2] = ' ';
					createPathsDFS(row, col + 2); // recursive call on indices we have moved to
				}
			} else if (dir == 2) { // down
				if (row + 2 >= rows) {
					continue;
				}
				if (maze[row+2][col] != ' ') {
					maze[row+1][col] = ' ';
					maze[row+2][col] = ' ';
					createPathsDFS(row + 2, col);
				}
			} else if (dir == 3) { // left
				if (col - 2 < 0) {
					continue;
				}
				if (maze[row][col-2] != ' ') {
					maze[row][col-1] = ' ';
					maze[row][col-2] = ' ';
					createPathsDFS(row, col - 2);
				}
			} else { // dir == 4 // up
				if (row - 2 < 0) {
					continue;
				}
				if (maze[row-2][col] != ' ') {
					maze[row-1][col] = ' ';
					maze[row-2][col] = ' ';
					createPathsDFS(row - 2, col);
				}
			}
		}

		return maze;
	}

	/**
	 * Solves the maze by traversing in all 4 directions until the ending point is found, via recursive DFS.
	 * If traversing in a direction is invalid or the ending point is found, a boolean is passed up the recursive call stack
	 * and used to determine the validity of the current traversed path.
	 *
	 * As we traverse on a path, we mark it with '*' to indicate that it is the solution path (this also deals with cyclic cases).
	 * If the path is found to be invalid, the true conditionals are not satisfied and thus we remark the path back to ' '.
	 *
	 * @param row 	row index of the current position of the traversal
	 * @param col 	column index of the current position of the traversal
	 * @return boolean  whether the current path traversed leads to the coffee machine
	 */
	public static boolean solveMaze(int row, int col) {
		if (maze[row][col] == 'C') {
			return true;
		}
		if (maze[row][col] != ' ') {
			return false;
		}
		maze[row][col] = '*';
		if (solveMaze(row, col + 1) == true) {
			return true;
		}
		if (solveMaze(row + 1, col) == true) {
			return true;
		}
		if (solveMaze(row, col - 1) == true) {
			return true;
		}
		if (solveMaze(row - 1 , col) == true) {
			return true;
		}
		maze[row][col] = ' ';
		
		return false;
	}
	
	/**
	 * Creates a String representation of the maze.
	 *
	 * @return String  the maze, also with correct path if already solved via solveMaze
	 */
	public static String convertMazeToString() {
		StringBuilder sb =  new StringBuilder();
		for (char[] arr : maze) {
			for (char c : arr) {
				sb.append(c);
			}
			sb.append('\n');
		}
		return sb.toString();
	}
}
