import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Classic Checkers Game
 * 
 * @author Jack Donofrio
 * @date 6-28-2020
 */
public class Checkers {

	// image path constants
	private final String BLACK_SQUARE_IMAGE_PATH = "/images/blacksq.jpg";
	private final String WHITE_SQUARE_IMAGE_PATH = "/images/whitesq.jpeg";
	private final String RED_PIECE_IMAGE_PATH = "/images/redpiece.jpg";
	private final String WHITE_PIECE_IMAGE_PATH = "/images/whitepiece.jpg";
	private final String RED_KING_IMAGE_PATH = "/images/redking.jpg";
	private final String WHITE_KING_IMAGE_PATH = "/images/whiteking.jpg";
	private final String POSSIBLE_MOVE_IMAGE_PATH = "/images/possibleMoveSquare.jpg";

	// piece value constants
	private final int EMPTY_SQUARE_VALUE = 0;
	private final int WHITE_PIECE_VALUE = 1;
	private final int RED_PIECE_VALUE = 2;
	private final int WHITE_KING_VALUE = 3;
	private final int RED_KING_VALUE = 4;

	private JFrame frame;
	private JButton[][] gridOfSquares;
	private Move currentlySelectedPiece;

	private InternalLogic internalLogic;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Checkers window = new Checkers();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Checkers() {
		initializeFrame();
		initializeGrid();
		readInternalLogic();
	}

	/**
	 * Initialize the contents of the frame, the internal logic, and the reset
	 * button
	 */
	private void initializeFrame() {
		frame = new JFrame();
		frame.setBounds(100, 100, 639, 727);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setTitle("Classic Checkers");
		frame.getContentPane().setLayout(null);

		internalLogic = new InternalLogic();

		JButton newGameButton = new JButton("New");
		newGameButton.setFocusPainted(false);
		newGameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				internalLogic.setGridDefaults();
				currentlySelectedPiece = null;
				readInternalLogic();
			}
		});
		newGameButton.setBounds(274, 663, 101, 23);
		frame.getContentPane().add(newGameButton);
	}

	/**
	 * Create the grid of buttons used to represent the blank squares and pieces
	 */
	private void initializeGrid() {

		gridOfSquares = new JButton[8][8];

		for (int row = 0; row < 8; row++)
			for (int column = 0; column < 8; column++) {
				gridOfSquares[row][column] = new JButton("");
				String imagePath = (row + column) % 2 == 0 ? WHITE_SQUARE_IMAGE_PATH : BLACK_SQUARE_IMAGE_PATH;
				gridOfSquares[row][column].setIcon(new ImageIcon(Checkers.class.getResource(imagePath)));
				gridOfSquares[row][column].setBounds(79 * column, 80 * row, 80, 80);
				gridOfSquares[row][column].setContentAreaFilled(false);

				// values used inside action listener must be declared as final
				final int playerEndingRow = row;
				final int playerEndingCol = column;

				/**
				 * Action listener for when a square on the grid is clicked
				 */
				gridOfSquares[row][column].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						readInternalLogic();
						int pieceValue = internalLogic.getGrid()[playerEndingRow][playerEndingCol];
						if (pieceValue == WHITE_PIECE_VALUE || pieceValue == WHITE_KING_VALUE) {
							currentlySelectedPiece = new Move(playerEndingRow, playerEndingCol, 0);
							// highlight all possible moves
							int opposingPiece = RED_PIECE_VALUE;
							ArrayList<Move> possibleMoves = pieceValue == WHITE_PIECE_VALUE
									? internalLogic.getValidMovesForForwardPiece(playerEndingRow, playerEndingCol, opposingPiece)
									: internalLogic.getValidMovesForKing(playerEndingRow, playerEndingCol, opposingPiece);
							currentlySelectedPiece.setPossibleMoves(possibleMoves);
							for (Move m : possibleMoves) {
								gridOfSquares[m.getRow()][m.getColumn()]
										.setIcon(new ImageIcon(Checkers.class.getResource(POSSIBLE_MOVE_IMAGE_PATH)));
							}
						}

						if (pieceValue == EMPTY_SQUARE_VALUE && currentlySelectedPiece != null
								&& internalLogic.getGrid()[currentlySelectedPiece.getRow()][currentlySelectedPiece
										.getColumn()] != 0) {
							for (Move move : currentlySelectedPiece.getAssignedPossibleMoves()) {
								if (move.getRow() == playerEndingRow && move.getColumn() == playerEndingCol) {

									int playerEndRow = playerEndingRow;
									int playerEndCol = playerEndingCol;

									int playerStartingRow = currentlySelectedPiece.getRow();
									int playerStartingCol = currentlySelectedPiece.getColumn();

									int selectedPieceValue = internalLogic.getGrid()[currentlySelectedPiece
											.getRow()][playerStartingCol];
									internalLogic.getGrid()[playerStartingRow][currentlySelectedPiece
											.getColumn()] = EMPTY_SQUARE_VALUE;

									// NEGATE ALL PIECES BETWEEN START -- > END

									// IF UP LEFT ^>
									if (playerEndRow < playerStartingRow && playerEndCol < playerStartingCol) {
										int i = playerStartingRow;
										int j = playerStartingCol;
										while (i > playerEndRow && j > playerEndCol) {
											internalLogic.getGrid()[i][j] = EMPTY_SQUARE_VALUE;
											i--;
											j--;
										}
										internalLogic.getGrid()[i][j] = selectedPieceValue;
									}

									// IF UP RIGHT
									if (playerEndRow < playerStartingRow && playerEndCol > playerStartingCol) {
										int i = playerStartingRow;
										int j = playerStartingCol;
										while (i > playerEndRow && j < playerEndCol) {
											internalLogic.getGrid()[i][j] = EMPTY_SQUARE_VALUE;
											i--;
											j++;
										}
										internalLogic.getGrid()[i][j] = selectedPieceValue;
									}

									// IF DOWN LEFT
									if (playerEndRow > playerStartingRow && playerEndCol < playerStartingCol) {
										int i = playerStartingRow;
										int j = playerStartingCol;
										while (i < playerEndRow && j > playerEndCol) {
											internalLogic.getGrid()[i][j] = EMPTY_SQUARE_VALUE;
											i++;
											j--;
										}
										internalLogic.getGrid()[i][j] = selectedPieceValue;
									}

									// IF DOWN RIGHT
									if (playerEndRow > playerStartingRow && playerEndCol > playerStartingCol) {
										int i = playerStartingRow;
										int j = playerStartingCol;
										while (i < playerEndRow && j < playerEndCol) {
											internalLogic.getGrid()[i][j] = EMPTY_SQUARE_VALUE;
											i++;
											j++;
										}
										internalLogic.getGrid()[i][j] = selectedPieceValue;
									}

									// if reach top, make white piece king
									if (playerEndRow == 0)
										internalLogic.getGrid()[playerEndRow][playerEndCol] = WHITE_KING_VALUE;
									// else, it retains its value
									else
										internalLogic.getGrid()[playerEndRow][playerEndCol] = selectedPieceValue;

									// opponent move logic
									Move[] oppMove = internalLogic.makeNaiveOpponentMove();

									if (oppMove != null) {
										int opponentStartingRow = oppMove[0].getRow();
										int opponentStartingColumn = oppMove[0].getColumn();
										int opponentEndingRow = oppMove[1].getRow();
										int opponentEndingColumn = oppMove[1].getColumn();
										pieceValue = internalLogic.getGrid()[opponentStartingRow][opponentStartingColumn];

										// IF UP LEFT
										if (opponentEndingRow < opponentStartingRow
												&& opponentEndingColumn < opponentStartingColumn) {
											int i = opponentStartingRow;
											int j = opponentStartingColumn;
											while (i > opponentEndingRow && j > opponentEndingColumn) {
												internalLogic.getGrid()[i][j] = EMPTY_SQUARE_VALUE;
												i--;
												j--;
											}
											internalLogic.getGrid()[i][j] = pieceValue;
										}

										// IF UP RIGHT
										if (opponentEndingRow < opponentStartingRow
												&& opponentEndingColumn > opponentStartingColumn) {
											int i = opponentStartingRow;
											int j = opponentStartingColumn;
											while (i > opponentEndingRow && j < opponentEndingRow) {
												internalLogic.getGrid()[i][j] = EMPTY_SQUARE_VALUE;
												i--;
												j++;
											}
											internalLogic.getGrid()[i][j] = pieceValue;
										}

										// IF DOWN LEFT
										if (opponentEndingRow > opponentStartingRow
												&& opponentEndingColumn < opponentStartingColumn) {
											int i = opponentStartingRow;
											int j = opponentStartingColumn;
											while (i < opponentEndingRow && j > opponentEndingColumn) {
												internalLogic.getGrid()[i][j] = EMPTY_SQUARE_VALUE;
												i++;
												j--;
											}
											internalLogic.getGrid()[i][j] = pieceValue;
										}

										// IF DOWN RIGHT
										if (opponentEndingRow > opponentStartingRow
												&& opponentEndingColumn > opponentStartingColumn) {
											int i = opponentStartingRow;
											int j = opponentStartingColumn;
											while (i < opponentEndingRow && j < opponentEndingColumn) {
												internalLogic.getGrid()[i][j] = EMPTY_SQUARE_VALUE;
												i++;
												j++;
											}
											internalLogic.getGrid()[i][j] = pieceValue;

										}

										// if reach bottom, make red piece king
										if (opponentEndingRow == 7)
											internalLogic.getGrid()[opponentEndingRow][opponentEndingColumn] = RED_KING_VALUE;
										else
											internalLogic.getGrid()[opponentEndingRow][opponentEndingColumn] = pieceValue;
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

	/**
	 * Reads board data from internal grid, displays it to the GUI board
	 */

	private void readInternalLogic() {
		int[][] grid = internalLogic.getGrid();

		for (int row = 0; row < 8; row++)
			for (int col = 0; col < 8; col++)
				if ((row + col) % 2 == 1) {
					String imagePath = "";
					int pieceValue = grid[row][col];
					switch (pieceValue) {
					case 4:
						imagePath = RED_KING_IMAGE_PATH;
						break;
					case 3:
						imagePath = WHITE_KING_IMAGE_PATH;
						break;
					case 2:
						imagePath = RED_PIECE_IMAGE_PATH;
						break;
					case 1:
						imagePath = WHITE_PIECE_IMAGE_PATH;
						break;
					default:
						imagePath = BLACK_SQUARE_IMAGE_PATH;
					}
					gridOfSquares[row][col].setIcon((new ImageIcon(Checkers.class.getResource(imagePath))));
				}

		String winner = internalLogic.getWinner();
		if (!winner.equals("none"))
			JOptionPane.showMessageDialog(null, winner);
	}

}
