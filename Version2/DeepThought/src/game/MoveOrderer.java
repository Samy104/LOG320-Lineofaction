package game;

public class MoveOrderer
{
	private static final int MAX_DEPTH_LEVEL = 10;
	private static final int NUMBER_KILLER_MOVES_PER_DEPTH = 2;
	
	private Move[][] killerMoves;

	public MoveOrderer()
	{
		this.killerMoves = new Move[MAX_DEPTH_LEVEL][NUMBER_KILLER_MOVES_PER_DEPTH];
	}
	
	public void signalCutoff(Move move, int depth)
	{
		if(!move.isEquals(this.killerMoves[depth][0]) && !move.isEquals(this.killerMoves[depth][1]))
		{
			this.killerMoves[depth][0] = this.killerMoves[depth][1];
			this.killerMoves[depth][1] = move;
		}
	}
	
	public void orderMoves(Move[] moves, int depth)
	{
		int killerMovesFound = 0;
		
		for(int index = 0; index < moves.length; index++)
		{
			if(moves[index].isEquals(this.killerMoves[depth][0]) || moves[index].isEquals(this.killerMoves[depth][1]))
			{
				Move moveTemp = moves[killerMovesFound];
				moves[killerMovesFound] = moves[index];
				moves[index] = moveTemp;
			}
		}
	}
	
	public void resetKillerMoves()
	{
		this.killerMoves = new Move[MAX_DEPTH_LEVEL][NUMBER_KILLER_MOVES_PER_DEPTH];
	}
}
