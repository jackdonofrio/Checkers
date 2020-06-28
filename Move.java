import java.util.ArrayList;

public class Move
{
  private int row;
  private int column;
  private int moveWeight;
  private ArrayList<Move> possibleMoves;

  public Move(int row, int column, int moveWeight)
  {
    this.row = row;
    this.column = column;
    this.moveWeight = moveWeight;
  }

  public int getRow()
  {
    return row;
  }

  public int getColumn()
  {
    return column;
  }

  public int getMoveWeight()
  {
    return moveWeight;
  }

  public void setPossibleMoves(ArrayList<Move> possible)
  {
    possibleMoves = new ArrayList<>();
    for (int i = 0; i < possible.size(); i++)
      possibleMoves.add(possible.get(i));
  }

  public ArrayList<Move> getAssignedPossibleMoves()
  {
    return possibleMoves;
  }

}