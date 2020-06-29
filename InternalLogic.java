import java.util.ArrayList;
import java.util.Collections;

/**
 * Class to handle the internal underlying logic behind the checkers board
 * 
 * @author Jack Donofrio
 * @date 6-28-2020
 */
public class InternalLogic {

	// piece value constants
	private final int EMPTY_SQUARE_VALUE = 0;
	private final int WHITE_PIECE_VALUE = 1;
	private final int RED_PIECE_VALUE = 2;
	private final int WHITE_KING_VALUE = 3;
	private final int RED_KING_VALUE = 4;

	private int[][] grid;

	public InternalLogic() {
		setGridDefaults();
	}

	/**
	 * 
	 * @return the 2d array representing the interal logic of the board
	 */
	public int[][] getGrid() {
		return grid;
	}

	/**
	 * 4 = red king 3 = white king 2 = red 1 = white 0 = empty
	 * 
	 * sets the grid values to their defaults
	 */
	public void setGridDefaults() {
		int[][] boardDefaults = { { 0, 2, 0, 2, 0, 2, 0, 2 }, { 2, 0, 2, 0, 2, 0, 2, 0 }, { 0, 2, 0, 2, 0, 2, 0, 2 },
				{ 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 1, 0, 1, 0, 1, 0, 1, 0 }, { 0, 1, 0, 1, 0, 1, 0, 1 },
				{ 1, 0, 1, 0, 1, 0, 1, 0 } };
		grid = new int[8][8];
		for (int row = 0; row < grid.length; row++)
			for (int column = 0; column < grid[row].length; column++)
				grid[row][column] = boardDefaults[row][column];
	}

	/**
	 * 
	 * @param row
	 * @param column
	 * @param opposingPieceValue
	 * @return all valid moves for pieces moving up the board (forward)
	 */
	public ArrayList<Move> getValidMovesForForwardPiece(int row, int column, int opposingPieceValue) {
		ArrayList<Move> moves = new ArrayList<>();

		moves = addEmptySpaceMove(row - 1, column - 1, moves, 0);
		moves = addEmptySpaceMove(row - 1, column + 1, moves, 0);

		int startRow = row;
		int startCol = column;
		while (row > 1 && column > 1
				&& (grid[row - 1][column - 1] == opposingPieceValue || grid[row - 1][column - 1] == opposingPieceValue + 2)
				&& grid[row - 2][column - 2] == EMPTY_SQUARE_VALUE) {
			moves.add(new Move(row - 2, column - 2, 1));
			row -= 2;
			column -= 2;
		}
		row = startRow;
		column = startCol;
		while (row > 1 && column < 6
				&& (grid[row - 1][column + 1] == opposingPieceValue || grid[row - 1][column + 1] == opposingPieceValue + 2)
				&& grid[row - 2][column + 2] == EMPTY_SQUARE_VALUE) {
			moves.add(new Move(row - 2, column + 2, 1));
			row -= 2;
			column += 2;
		}

		return moves;
	}

	/**
	 * 
	 * @param row
	 * @param column
	 * @param opposingPieceValue
	 * @return all valid moves for pieces moving down the board (reverse)
	 */
	public ArrayList<Move> getValidMovesForReversePiece(int row, int column, int opposingPieceValue) {
		ArrayList<Move> moves = new ArrayList<>();
		moves = addEmptySpaceMove(row + 1, column - 1, moves, 0);
		moves = addEmptySpaceMove(row + 1, column + 1, moves, 0);

		int startRow = row;
		int startCol = column;

		try {
			int weight = 1;
			while ((grid[row + 1][column - 1] == opposingPieceValue || grid[row + 1][column - 1] == opposingPieceValue + 2)
					&& grid[row + 2][column - 2] == EMPTY_SQUARE_VALUE) {
				moves.add(new Move(row + 2, column - 2, weight));
				weight++;
				row += 2;
				column -= 2;
			}
			row = startRow;
			column = startCol;
		} catch (IndexOutOfBoundsException e) {
		}
		try {
			int weight = 1;
			while ((grid[row + 1][column + 1] == opposingPieceValue || grid[row + 1][column + 1] == opposingPieceValue + 2)
					&& grid[row + 2][column + 2] == EMPTY_SQUARE_VALUE) {
				moves.add(new Move(row + 2, column + 2, weight));
				weight++;
				row += 2;
				column += 2;
			}
		} catch (IndexOutOfBoundsException e) {
		}

		return moves;
	}

	private ArrayList<Move> addEmptySpaceMove(int row, int column, ArrayList<Move> m, int weight) {
		ArrayList<Move> moves = m;
		try {
			if (grid[row][column] == EMPTY_SQUARE_VALUE) {
				moves.add(new Move(row, column, weight));
				return moves;
			}
		} catch (IndexOutOfBoundsException e) {
		}
		return moves;
	}

	/**
	 * 
	 * @param row
	 * @param column
	 * @param opposingPieceValue
	 * @return all valid moves for a given king piece at (row,column) on the grid
	 */
	public ArrayList<Move> getValidMovesForKing(int row, int column, int opposingPieceValue) {
		ArrayList<Move> moves = new ArrayList<>();
		ArrayList<Move> reverseMoves = getValidMovesForReversePiece(row, column, opposingPieceValue);
		ArrayList<Move> forwardMoves = getValidMovesForForwardPiece(row, column, opposingPieceValue);
		for (Move move : reverseMoves)
			moves.add(move);
		for (Move move : forwardMoves)
			moves.add(move);
		return moves;

	}

	/**
	 * makes naive opponent move - scans the possible moves of each red piece, from
	 * bottom up (to prioritize movement of pieces further down the board) if it
	 * finds one that can take an opponent's piece or become king, it does it
	 * otherwise a random move is selected from all possible moves. I may add a
	 * minimax or alpha-beta pruning function to create different difficulty levels
	 * 
	 * @return 2-long array with FROM position at index 0 and TO position at index 1
	 */
	public Move[] makeNaiveOpponentMove() {
		ArrayList<Move[]> all = new ArrayList<>();
		Move[] max = null;
		int maxWeight = 0;
		for (int row = grid.length - 1; row >= 0; row--)
			for (int column = 0; column < grid[row].length; column++)
				if (grid[row][column] == RED_PIECE_VALUE || grid[row][column] == RED_KING_VALUE) {
					ArrayList<Move> moves = grid[row][column] == RED_PIECE_VALUE ? getValidMovesForReversePiece(row, column, 1)
							: getValidMovesForKing(row, column, 1);

					if (moves.size() > 0) {
						// take most heavily weighted
						for (Move move : moves) {
							all.add(new Move[] { new Move(row, column, 0), move });
							if (move.getMoveWeight() > maxWeight) {
								maxWeight = move.getMoveWeight();
								max = new Move[] { new Move(row, column, 0), move };
							}
						}
						all.add(new Move[] { new Move(row, column, 0), moves.get((int) (Math.random() * moves.size())) });
					}
				}
		if (max != null) {
			return max;
		} else if (all.size() > 0) {
			return all.get((int) (Math.random() * all.size()));
		}

		return null;
	}

	/**
	 * @return the winner of the game, "none" if there is no winner yet
	 */
	public String getWinner() {
		boolean hasNoWhite = true;
		boolean hasNoRed = true;

		for (int r = 0; r < 8; r++)
			for (int c = 0; c < 8; c++) {
				if (grid[r][c] == RED_PIECE_VALUE || grid[r][c] == RED_KING_VALUE)
					hasNoRed = false;
				else if (grid[r][c] == WHITE_PIECE_VALUE || grid[r][c] == WHITE_KING_VALUE)
					hasNoWhite = false;
			}

		if (hasNoWhite)
			return "Red wins.";
		else if (hasNoRed)
			return "White wins";
		return "none";
	}

}
