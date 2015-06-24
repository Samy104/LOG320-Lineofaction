package server;

import game.Board;
import game.Move;
import game.TreeGenerator;
import ai.DeepThought;

import java.io.IOException;

public class MessageDispatcher {
	// Server settings
	private static final String SERVER_HOST = "localhost";
	private static final int SERVER_PORT = 8888;
	
	// Server message types
	private static final char INIT_GAME_BLACK_MSG = '2';
	private static final char INIT_GAME_WHITE_MSG = '1';
	private static final char NEXT_MOVE_MSG = '3';
	private static final char INVALID_MOVE_MSG = '4';
	private static long LAST_REFRESH = 0L;
	
	private Client client;
	private Board board;
	private DeepThought player;
	
	public static void main(String[] args) throws IOException {
		Client client = Client.connectClient(SERVER_HOST, SERVER_PORT);
				
		// Connection to the server has been established
		if(client != null)
		{
			MessageDispatcher dispatcher = new MessageDispatcher(client);
			
			// Iterates while the game is not finished
			while(dispatcher.nextMessage());
		}
	}
	
	public MessageDispatcher(Client client)
	{
		this.client = client;
		this.board = null;
		this.player = null;
	}
	
	private boolean nextMessage() throws IOException
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
					break;
				case INVALID_MOVE_MSG :
					this.invalidMove();
					break;
				default :
					System.err.println("Error");
					break;
			}
		}
		
		return validStep;
	}
	
	private void invalidMove()
	{
		System.out.println("Invalid Move has been called");
	}
	
	private void playNextMove() throws IOException
	{
		long sn = System.nanoTime();
		long waitTime = 4000000000L;
		TreeGenerator treeThread = new TreeGenerator(player, (sn + waitTime));
		treeThread.run();
		Move nextMove = treeThread.getBestMove();
		treeThread.interrupt();
		this.client.sendOutputs(nextMove.toString().getBytes());
		this.board.executeMove(nextMove);
		
		long en = System.nanoTime();
		long last = (getLastRefresh() == 0L)? System.nanoTime() : getLastRefresh();
		setLastRefresh(System.nanoTime());
		
		System.out.println((double)(en - last) / 1000000000.0);
		this.board.printIntoConsole();
		
		/*long sn = System.nanoTime();
		Move nextMove = this.player.findBestMove();
		long en = System.nanoTime();
		
		this.client.sendOutputs(nextMove.toString().getBytes());
		this.board.executeMove(nextMove);
		
		System.out.println((double)(en - sn) / 1000000000.0);
		this.board.printIntoConsole();*/
	}
	
	private boolean setMove(Move move)
	{
		System.out.println("Set Move has been called");
		
		this.board.executeMove(move);
		
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
	}

	public static long getLastRefresh() {
		return LAST_REFRESH;
	}

	public static void setLastRefresh(long lr) {
		LAST_REFRESH = lr;
	}
}
