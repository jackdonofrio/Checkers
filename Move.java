import java.util.ArrayList;

public class Move {
	private int row;
	private int column;
	private int moveWeight;
	private ArrayList<Move> possibleMoves;

	/**
	 * Move object constructor
	 * 
	 * @param row
	 * @param column
	 * @param moveWeight
	 */
	public Move(int row, int column, int moveWeight) {
		this.row = row;
		this.column = column;
		this.moveWeight = moveWeight;
	}

	/**
	 * @return the Move object's position on the y-axis of the board
	 */
	public int getRow() {
		return row;
	}

	/**
	 * @return the Move object's position on the x-axis of the board
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * @return the weight of a move - can be 0 or 1
	 */
	public int getMoveWeight() {
		return moveWeight;
	}

	/**
	 * assigns all possible moves of a given piece on the board to a Move object
	 * 
	 * @param possible
	 */
	public void setPossibleMoves(ArrayList<Move> possible) {
		possibleMoves = new ArrayList<>();
		for (int i = 0; i < possible.size(); i++)
			possibleMoves.add(possible.get(i));
	}

	/**
	 * @return all possible moves assigned to a Move object
	 */
	public ArrayList<Move> getAssignedPossibleMoves() {
		return possibleMoves;
	}

}