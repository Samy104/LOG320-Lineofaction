package game;


import ai.DeepThought;

public class TreeGenerator extends Thread {
	private Move bestMove;
	private DeepThought player;
	private long timeAllocated;
	
	public TreeGenerator(DeepThought player, long timeAllocated) 
	{
		this.player = player;
		this.timeAllocated = timeAllocated;
	}
	public void run() 
	{
		this.bestMove = this.player.findBestMove(this.timeAllocated);
    }
	
	public Move getBestMove()
	{
		return this.bestMove;
	}
}
