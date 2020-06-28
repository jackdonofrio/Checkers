import java.util.ArrayList;

public class InternalLogic
{
  private int[][] grid;

  public InternalLogic()
  {
    setGridDefaults();
  }

  public int[][] getGrid()
  {
    return grid;
  }

  public void setGridDefaults()
  {
    // 4 = red king
    // 3 = white king
    // 2 = red
    // 1 = white
    // 0 = empty
    int[][] boardDefaults =
      {{0, 2, 0, 2, 0, 2, 0, 2, 0, 2}, {2, 0, 2, 0, 2, 0, 2, 0},
          {0, 2, 0, 2, 0, 2, 0, 2}, {0, 0, 0, 0, 0, 0, 0, 0},
          {0, 0, 0, 0, 0, 0, 0, 0}, {1, 0, 1, 0, 1, 0, 1, 0},
          {0, 1, 0, 1, 0, 1, 0, 1}, {1, 0, 1, 0, 1, 0, 1, 0}};
    grid = new int[8][8];
    for (int row = 0; row < grid.length; row++)
      for (int column = 0; column < grid[row].length; column++)
        grid[row][column] = boardDefaults[row][column];
  }

  public ArrayList<Move> getValidMovesForForwardPiece(int row, int column,
    int opposingPiece)
  {
    ArrayList<Move> moves = new ArrayList<>();

    if (row > 0 && column > 0 && grid[row - 1][column - 1] == 0)
    {
      moves.add(new Move(row - 1, column - 1, 0));
    }
    if (row > 0 && column < 7 && grid[row - 1][column + 1] == 0)
    {
      moves.add(new Move(row - 1, column + 1, 0));
    }

    if (row > 1 && column > 1
      && (grid[row - 1][column - 1] == opposingPiece
        || grid[row - 1][column - 1] == opposingPiece + 2)
      && grid[row - 2][column - 2] == 0)
    {
      moves.add(new Move(row - 2, column - 2, 1));
    }

    if (row > 1 && column < 6
      && (grid[row - 1][column + 1] == opposingPiece
        || grid[row - 1][column + 1] == opposingPiece + 2)
      && grid[row - 2][column + 2] == 0)
    {
      moves.add(new Move(row - 2, column + 2, 1));
    }

    return moves;
  }

  public ArrayList<Move> getValidMovesForReversePiece(int row, int column,
    int opposingPiece)
  {
    ArrayList<Move> moves = new ArrayList<>();

    if (row < 7 && column > 0 && grid[row + 1][column - 1] == 0)
    {
      moves.add(new Move(row + 1, column - 1, 0));
    }
    if (row < 7 && column < 7 && grid[row + 1][column + 1] == 0)
    {
      moves.add(new Move(row + 1, column + 1, 0));
    }
    if (row < 6 && column > 1
      && (grid[row + 1][column - 1] == opposingPiece
        || grid[row + 1][column - 1] == opposingPiece + 2)
      && grid[row + 2][column - 2] == 0)
    {
      moves.add(new Move(row + 2, column - 2, 1));
    }
    if (row < 6 && column < 6
      && (grid[row + 1][column + 1] == opposingPiece
        || grid[row + 1][column + 1] == opposingPiece + 2)
      && grid[row + 2][column + 2] == 0)
    {
      moves.add(new Move(row + 2, column + 2, 1));
    }

    return moves;
  }

  public ArrayList<Move> getValidMovesForKing(int row, int column,
    int opposingPiece)
  {
    ArrayList<Move> moves = new ArrayList<>();
    ArrayList<Move> reverseMoves =
      getValidMovesForReversePiece(row, column, opposingPiece);
    ArrayList<Move> forwardMoves =
      getValidMovesForForwardPiece(row, column, opposingPiece);
    for (Move move : reverseMoves)
      moves.add(move);
    for (Move move : forwardMoves)
      moves.add(move);
    return moves;

  }

  /**
   * Returns current position at index 0 and TO position at index 1
   */
  public Move[] makeNaiveOpponentMove()
  {
    ArrayList<Move[]> all = new ArrayList<>();
    for (int row = grid.length - 1; row >= 0; row--)
      for (int column = 0; column < grid[row].length; column++)
        if (grid[row][column] == 2 || grid[row][column] == 4)
        {
          ArrayList<Move> moves =
            grid[row][column] == 2
              ? getValidMovesForReversePiece(row, column, 1)
              : getValidMovesForKing(row, column, 1);
          if (moves.size() > 0)
            for (Move move : moves)
            {
              all.add(new Move[] {new Move(row, column, 0), move});
              // prioritize taking opponent pieces (these moves are weighted 1)
              if (move.getMoveWeight() == 1)
                return new Move[] {new Move(row, column, 0), move};
            }
        }
    if (all.size() > 0)
      return all.get((int) (Math.random() * all.size()));
    return null;

  }

  public String getWinner()
  {
    boolean hasNoWhite = true;
    boolean hasNoRed = true;

    for (int r = 0; r < 8; r++)
      for (int c = 0; c < 8; c++)
      {
        if (grid[r][c] == 2 || grid[r][c] == 4)
          hasNoRed = false;
        else if (grid[r][c] == 1 || grid[r][c] == 3)
          hasNoWhite = false;
      }

    if (hasNoWhite)
      return "Red wins.";
    else if (hasNoRed)
      return "White wins";
    return "none";
  }

}
