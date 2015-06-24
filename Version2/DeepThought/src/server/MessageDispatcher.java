package server;

import game.Board;
import game.Move;
import ai.DeepThought;

import java.io.IOException;
import java.util.Random;

public class MessageDispatcher {
	// Server settings
	private static final String SERVER_HOST = "localhost";
	private static final int SERVER_PORT = 8888;
	
	// Server message types
	private static final char INIT_GAME_BLACK_MSG = '2';
	private static final char INIT_GAME_WHITE_MSG = '1';
	private static final char NEXT_MOVE_MSG = '3';
	private static final char INVALID_MOVE_MSG = '4';
	
	private static final int SEARCH_BEST_MOVE_TIME = 4700;
	
	private Client client;
	private Board board;
	private DeepThought player;
	private int playerColor;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		warmUp();
		
		Client client = Client.connectClient(SERVER_HOST, SERVER_PORT);
				
		// Connection to the server has been established
		if(client != null)
		{
			MessageDispatcher dispatcher = new MessageDispatcher(client);
			
			// Iterates while the game is not finished
			while(dispatcher.nextMessage());
		}
	}
	
	// Executes a quick search to let JIT optimized frequent functions
	public static void warmUp() throws InterruptedException
	{
		Board board = new Board(new int[][] {
				{0,2,2,2,2,2,2,0},
				{4,0,0,0,0,0,0,4},
				{4,0,0,0,0,0,0,4},
				{4,0,0,0,0,0,0,4},
				{4,0,0,0,0,0,0,4},
				{4,0,0,0,0,0,0,4},
				{4,0,0,0,0,0,0,4},
				{0,2,2,2,2,2,2,0},
		});
		
		DeepThought ai = new DeepThought(board, 2);
		
		Thread searchThread = new Thread(ai);
		searchThread.start();
		Thread.sleep(3000);
		ai.stopSearchBestMove();
	}
	
	// Constructor
	public MessageDispatcher(Client client)
	{
		this.client = client;
		this.board = null;
		this.player = null;
	}
	
	// Reads the next message sent by the server
	private boolean nextMessage() throws IOException, InterruptedException
	{
		boolean validStep = true;
		char[] serverInputs = this.client.readInputs();
		
		if(serverInputs.length > 0)
		{
			char messageID = serverInputs[0];
			
			char[] messageContent = new char[serverInputs.length - 1];
			System.arraycopy(serverInputs, 1, messageContent, 0, messageContent.length);
			
			switch(messageID)
			{
				case INIT_GAME_BLACK_MSG :
					validStep = this.readBoard(new String(messageContent).trim());
					this.setPlayer(2);
					break;
				case INIT_GAME_WHITE_MSG :
					validStep = this.readBoard(new String(messageContent).trim());
					this.setPlayer(4);
					this.playNextMove();
					break;
				case NEXT_MOVE_MSG :
					Move otherPlayerMove = Move.readMove(new String(messageContent).trim());
					validStep = this.setMove(otherPlayerMove);
					
					this.playNextMove();
					
					if(this.board.isWinning(Board.BLACK_PLAYER) || this.board.isWinning(Board.WHITE_PLAYER))
					{
						validStep = false;
					}
					
					break;
				case INVALID_MOVE_MSG :
					this.invalidMove();
					
					if(this.board.isWinning(Board.BLACK_PLAYER) || this.board.isWinning(Board.WHITE_PLAYER))
					{
						validStep = false;
					}
					
					break;
				default :
					System.err.println("Error");
					validStep = false;
					break;
			}
		}
		
		return validStep;
	}
	
	private void invalidMove() throws IOException
	{
		System.out.println("Invalid Move has been called");
		
		Move[] moves = this.board.getPossibleMoves(this.playerColor);
		
		Random random = new Random(System.nanoTime());
		this.client.sendOutputs(moves[random.nextInt(moves.length)].toString().getBytes());
	}
	
	private void playNextMove() throws IOException, InterruptedException
	{		
		// Starts the search
		Thread searchThread = new Thread(this.player);
		searchThread.start();
		
		// Lets the search run a maximum amount of time
		Thread.sleep(SEARCH_BEST_MOVE_TIME);
		this.player.stopSearchBestMove();
		
		// Plays the best move found so far
		Move nextMove = this.player.getBestMoveFound();
		this.client.sendOutputs(nextMove.toString().getBytes());
		this.board.makeMove(nextMove);
	}
	
	private boolean setMove(Move move)
	{
		System.out.println("Set Move has been called");
		
		this.board.makeMove(move);
		
		this.board.printIntoConsole();
		
		return true;
	}
	
	private boolean readBoard(String squaresAsString)
	{
		System.out.println("Read Board has been called");
		
		this.board = Board.createBoard(squaresAsString);
		
		this.board.printIntoConsole();
		
		return (board != null);
	}
	
	private void setPlayer(int playerColor)
	{
		System.out.println("Set Player has been called");
		
		int opponentColor = (playerColor == 2) ? 4 : 2;
		
		this.player = new DeepThought(this.board, playerColor);
		this.playerColor = playerColor;
	}
}
