import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Main
{

  private final String blackSquareImagePath = "/images/blacksq.jpg";
  private final String whiteSquareImagePath = "/images/whitesq.jpeg";
  private final String redPieceImagePath = "/images/redpiece.jpg";
  private final String whitePieceImagePath = "/images/whitepiece.jpg";
  private final String redKingImagePath = "/images/redking.jpg";
  private final String whiteKingImagePath = "/images/whiteking.jpg";
  private final String possibleMoveImagePath = "/images/possibleMoveSquare.jpg";

  private JFrame frame;
  private JButton[][] gridOfSquares;
  private Move currentlySelectedPiece;

  private InternalLogic internalLogic;

  /**
   * Launch the application.
   */
  public static void main(String[] args)
  {
    EventQueue.invokeLater(new Runnable()
    {
      public void run()
      {
        try
        {
          Main window = new Main();
          window.frame.setVisible(true);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    });

  }

  public Main()
  {
    initializeFrame();
    initializeGrid();
    readInternalLogic();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initializeFrame()
  {
    frame = new JFrame();
    frame.setBounds(100, 100, 639, 727);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.setTitle("Classic Checkers");
    frame.getContentPane().setLayout(null);

    internalLogic = new InternalLogic();

    JButton newGameButton = new JButton("New");
    newGameButton.setFocusPainted(false);
    newGameButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent arg0)
      {
        internalLogic.setGridDefaults();
        currentlySelectedPiece = null;
        readInternalLogic();
      }
    });
    newGameButton.setBounds(274, 663, 101, 23);
    frame.getContentPane().add(newGameButton);

  }

  private void initializeGrid()
  {

    gridOfSquares = new JButton[8][8];

    for (int row = 0; row < 8; row++)
    {
      for (int column = 0; column < 8; column++)
      {
        gridOfSquares[row][column] = new JButton("");
        String imagePath =
          (row + column) % 2 == 0 ? whiteSquareImagePath : blackSquareImagePath;
        gridOfSquares[row][column]
          .setIcon(new ImageIcon(Main.class.getResource(imagePath)));
        gridOfSquares[row][column].setBounds(79 * column, 80 * row, 80, 80);
        gridOfSquares[row][column].setContentAreaFilled(false);
        final int r = row;
        final int c = column;
        gridOfSquares[row][column].addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent arg0)
          {
            readInternalLogic();
            int pieceNum = internalLogic.getGrid()[r][c];
            if (pieceNum == 1 || pieceNum == 3)
            {
              currentlySelectedPiece = new Move(r, c, 0);
              // highlight all possible moves
              int opposingPiece = 2;
              ArrayList<Move> possibleMoves =
                pieceNum == 1
                  ? internalLogic.getValidMovesForForwardPiece(r, c,
                    opposingPiece)
                  : internalLogic.getValidMovesForKing(r, c, opposingPiece);
              currentlySelectedPiece.setPossibleMoves(possibleMoves);
              for (Move m : possibleMoves)
              {
                gridOfSquares[m.getRow()][m.getColumn()].setIcon(
                  new ImageIcon(Main.class.getResource(possibleMoveImagePath)));
              }
            }

            if (pieceNum == 0 && currentlySelectedPiece != null)
            {
              for (Move move : currentlySelectedPiece
                .getAssignedPossibleMoves())
              {
                if (move.getRow() == r && move.getColumn() == c)
                {
                  int selectedPieceValue =
                    internalLogic.getGrid()[currentlySelectedPiece
                      .getRow()][currentlySelectedPiece.getColumn()];
                  internalLogic.getGrid()[currentlySelectedPiece
                    .getRow()][currentlySelectedPiece.getColumn()] = 0;

                  // if skip over opp piece, we must make the in-btwn piece 0
                  if (Math.abs(currentlySelectedPiece.getRow() - r) > 1)
                  {
                    // if move up right
                    if (c > currentlySelectedPiece.getColumn()
                      && r < currentlySelectedPiece.getRow())
                    {
                      internalLogic.getGrid()[r + 1][c - 1] = 0;
                    }
                    // if move up left
                    else if (c < currentlySelectedPiece.getColumn()
                      && r < currentlySelectedPiece.getRow())
                    {
                      internalLogic.getGrid()[r + 1][c + 1] = 0;
                    }
                    // if move down left
                    else if (c < currentlySelectedPiece.getColumn()
                      && r > currentlySelectedPiece.getRow())
                    {
                      internalLogic.getGrid()[r - 1][c + 1] = 0;
                    }
                    // if move down right
                    else if (c > currentlySelectedPiece.getColumn()
                      && r > currentlySelectedPiece.getRow())
                    {
                      internalLogic.getGrid()[r - 1][c - 1] = 0;
                    }

                  }
                  if (r == 0)
                    internalLogic.getGrid()[r][c] = 3;
                  else
                    internalLogic.getGrid()[r][c] = selectedPieceValue;
                  // readInternalLogic();

                  // opponent move logic
                  Move[] oppMove = internalLogic.makeNaiveOpponentMove();

                  if (oppMove != null)
                  {
                    int oppStartRow = oppMove[0].getRow();
                    int oppStartCol = oppMove[0].getColumn();
                    int oppEndRow = oppMove[1].getRow();
                    int oppEndCol = oppMove[1].getColumn();
                    int pieceValue =
                      internalLogic.getGrid()[oppStartRow][oppStartCol];
                    internalLogic.getGrid()[oppStartRow][oppStartCol] = 0;

                    // if skip
                    if (Math.abs(oppStartRow - oppEndRow) > 1)
                    {
                      if (oppEndCol > oppStartCol && oppEndRow < oppStartRow)
                      {
                        internalLogic.getGrid()[oppEndRow + 1][oppEndCol - 1] =
                          0;
                      }
                      else if (oppEndCol < oppStartCol
                        && oppEndRow < oppStartRow)
                      {
                        internalLogic.getGrid()[oppEndRow + 1][oppEndCol + 1] =
                          0;
                      }
                      else if (oppEndCol < oppStartCol
                        && oppEndRow > oppStartRow)
                      {
                        internalLogic.getGrid()[oppEndRow - 1][oppEndCol + 1] =
                          0;
                      }
                      else if (oppEndCol > oppStartCol
                        && oppEndRow > oppStartRow)
                      {
                        internalLogic.getGrid()[oppEndRow - 1][oppEndCol - 1] =
                          0;
                      }
                    }
                    if (oppEndRow == 7)
                      internalLogic.getGrid()[oppEndRow][oppEndCol] = 4;
                    else
                      internalLogic.getGrid()[oppEndRow][oppEndCol] =
                        pieceValue;
                  }

                  readInternalLogic();
                }
              }

            }

          }
        });
        frame.getContentPane().add(gridOfSquares[row][column]);

      }

    }

  }

  private void readInternalLogic()
  {
    int[][] grid = internalLogic.getGrid();

    for (int row = 0; row < 8; row++)
      for (int col = 0; col < 8; col++)
        if ((row + col) % 2 == 1)
        {
          String imagePath = "";
          int pieceNum = grid[row][col];
          if (pieceNum == 4)
            imagePath = redKingImagePath;
          else if (pieceNum == 3)
            imagePath = whiteKingImagePath;
          else if (pieceNum == 2)
            imagePath = redPieceImagePath;
          else if (pieceNum == 1)
            imagePath = whitePieceImagePath;
          else
            imagePath = blackSquareImagePath;
          gridOfSquares[row][col]
            .setIcon((new ImageIcon(Main.class.getResource(imagePath))));
        }

    String winner = internalLogic.getWinner();
    if (!winner.equals("none"))
      JOptionPane.showMessageDialog(null, winner);
  }

}
