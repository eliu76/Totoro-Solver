/* Evan Liu
 * Period 3
 * Totoro Solver
 * This class uses a board inner class and a priority queue to determine 
 * the number of steps it takes to reach the finished/complete board from a matrix of 
 * integers and prints out those steps. 
 */
import java.util.*;

public class Liu_TotoroSolver {

	private PriorityQueue<Board> list = new PriorityQueue<Board>();
	private int[] vertdisp = {-1, 1, 0, 0};
	private int[] horzdisp = {0, 0, -1, 1};

	public Liu_TotoroSolver(int[][] board) {

		list.add(new Board(board, 0, null));
	}

	//determines the optimal move set to move Totoro using a PQ and manhattan values
	public void search() {

		//checks if board is solveable or not
		if(!list.peek().isSolveable()) {

			System.out.println("Board is not solveable");
			return;
		}

		while(!list.isEmpty()) {

			Board current = list.remove();
			if(current.gameOver()) {

				printSolution(current);
				return;
			}

			for(int i = 0; i < vertdisp.length; i++) {

				//checks if move is in bounds
				if(current.totLoc[0] + vertdisp[i] >= 0 && current.totLoc[1] + horzdisp[i] >= 0 && current.totLoc[0] + vertdisp[i] < current.curBoard.length && current.totLoc[1] + horzdisp[i] < current.curBoard.length) {

					Board toAdd = current.swap(current.totLoc[0] + vertdisp[i], current.totLoc[1] + horzdisp[i]);

					//cannot be result of previous boards
					if(!toAdd.equals(current.prevBoard)) {

						list.add(toAdd);
					}
				}
			}
		}
	}

	//prints out the steps took to reach final board and the actual number of steps
	private void printSolution(Board print) {

		Stack<Board> temp = new Stack<Board>();
		int moves = 0;

		while(print.prevBoard != null) {

			temp.push(print);
			print = print.prevBoard;
		}

		while(!temp.isEmpty()) {

			moves++;
			temp.pop().print();
			System.out.println();
		}
		System.out.println("Moves made: " + moves);
	}

	public static void main(String[] args) {
		int[][] a = {{0, 1, 3},{4, 2, 5},{7, 8, 6}};
		Liu_TotoroSolver test = new Liu_TotoroSolver(a);
		test.search();
	}

	public class Board implements Comparable<Board>{

		private int[][] curBoard;
		private int movesMade;
		private Board prevBoard;
		private int[] totLoc;
		private int manhattan;

		//initializes curBoard, totLoc and calculates manhattan
		Board (int[][] board, int moves, Board prev) {

			//deep copy of board
			int[][] copy = new int[board.length][board.length];

			for(int row = 0; row < board.length; row++) {

				for(int col = 0; col < board.length; col++) {

					copy[row][col] = board[row][col];
				}
			}
			curBoard = copy;

			totLoc = new int[2];

			totLoc = findBoard(curBoard, 0);

			//shallow copy of prev
			prevBoard = prev;

			int[][] goal = goalBoard();

			//checks to see how far each value is from where they should be in the goal matrix
			for(int row = 0; row < curBoard.length; row++){

				for(int col = 0; col < curBoard.length; col++){

					if(curBoard[row][col] != 0) {

						int[] result = findBoard(goal, curBoard[row][col]);
						manhattan += Math.abs(row - result[0]);
						manhattan += Math.abs(col - result[1]);
					}
				}
			}
		}

		//swaps totoro's location with the value at row and col
		public Board swap (int row, int col) {

			Board toReturn = new Board(curBoard, movesMade, this);

			toReturn.curBoard[totLoc[0]][totLoc[1]] = toReturn.curBoard[row][col];
			toReturn.curBoard[row][col] = 0;
			toReturn.totLoc[0] = row;
			toReturn.totLoc[1] = col;

			return new Board(toReturn.curBoard, movesMade + 1, this);
		}

		//prints out curBoard
		public void print() {

			for (int i = 0; i < curBoard.length; i++) {

				for (int j = 0; j < curBoard[i].length; j++) {

					System.out.print(curBoard[i][j] + " ");
				}
				System.out.println();
			}
		}

		//creates what the final game board should look like and returns it
		private int[][] goalBoard() {

			int[][] goal = new int[curBoard.length][curBoard.length];

			int temp = 1;

			for(int row = 0; row < curBoard.length; row++) {

				for(int col = 0; col < curBoard.length; col++) {

					goal[row][col] = temp;
					temp++;
				}
			}

			goal[goal.length - 1][goal.length - 1] = 0;
			return goal;
		}

		//finds the row and col location of value and returns it as an array
		private int[] findBoard(int[][] goalBoard, int value) {

			int[] result = new int[2];

			for(int row = 0; row < goalBoard.length; row++) {

				for(int col = 0; col < goalBoard.length; col++) {

					if(goalBoard[row][col] == value) {

						result[0] = row;
						result[1] = col;
						return result;
					}
				}
			}
			return result;
		}

		//returns true if the board is in the correct order
		public boolean gameOver() {

			if(manhattan == 0) {

				return true;
			}
			return false;
		}

		public int compareTo(Board other) {

			return (this.manhattan+ this.movesMade) - (other.manhattan+other.movesMade);
		}

		public boolean equals(Object other){

			if(!(other instanceof Board))
				return false;

			Board otherBoard = (Board)other;

			for(int r = 0; r < curBoard.length; r++){
				for(int c = 0; c < curBoard[0].length; c++)
					if(curBoard[r][c] != otherBoard.curBoard[r][c])
						return false;
			}
			return true;
		}

		//determines if the board is solveable using number of elements out of order
		public boolean isSolveable() {

			int OutofOrder = 0;
			int c = 0;

			for(int rowPos = 0; rowPos < curBoard.length; rowPos++) {
				
				for(int colPos = 0; colPos < curBoard.length; colPos++) {
					
					for(int row = rowPos; row < curBoard.length; row++) {
						
						if(row == rowPos) 
							c = colPos;
						else 
							c = 0;
						
						for(int col = c; col < curBoard.length; col++) {
							//does not include totoro
							if(curBoard[rowPos][colPos] != 0 && curBoard[row][col] != 0) {

								if(curBoard[row][col] < curBoard[rowPos][colPos]) {

									OutofOrder++;
								}
							}
						}
					}
				}
			}
			//length of board is odd
			if(curBoard.length % 2 > 0) {

				if(OutofOrder % 2 == 0) {
					return true;
				}
				return false;
			}

			//length of board is even
			else {

				//totoro in even row
				if(totLoc[0] % 2 == 0) {

					if(OutofOrder % 2 > 0) {
						return true;
					}
					return false;
				}

				//totoro in odd row
				else {

					if(OutofOrder % 2 == 0) {
						return true;
					}
					return false;
				}
			}
		}
	}
}