package game;

import java.io.IOException;
import java.io.StringReader;

public class Move {
	private static final int MOVE_STRING_LENGTH = 7;
	
	// Position of the square to move
	private int fromRowIndex;
	private int fromColumnIndex;
	
	// Position where to move the square
	private int toRowIndex;
	private int toColumnIndex;
	
	// Value of the square that is overwrited by the move
	private int overwrittenFromSquare;
	private int overwrittenToSquare;
	
	private boolean reversed;
	
	// Reads the move that is represented by the string (format must be : Z9 - Z9)
	public static Move readMove(String moveAsString)
	{
		Move move = null;
		
		try
		{
			if(moveAsString != null)
			{				
				if(moveAsString.length() == MOVE_STRING_LENGTH)
				{
					StringReader moveReader = new StringReader(moveAsString);
					
					int readFromColumnIndex = moveReader.read() - (int) 'A';
					int readFromRowIndex = (Character.getNumericValue(moveReader.read()) - 1);
					
					moveReader.skip(3);
					
					int readToColumnIndex = moveReader.read() - (int) 'A';
					int readToRowIndex = (Character.getNumericValue(moveReader.read()) - 1);
					
					if(readFromRowIndex >= 0 && readFromRowIndex <= 7
						&& readFromColumnIndex >= 0 && readFromColumnIndex <= 7
						&& readToRowIndex >= 0 && readToRowIndex <= 7
						&& readToColumnIndex >= 0 && readToColumnIndex <= 7)
					{						
						move = new Move(readFromRowIndex, readFromColumnIndex, readToRowIndex, readToColumnIndex);
					}
					else
					{
						System.err.println("Invalid move index range!");
					}
				}
				else
				{
					System.err.println("Invalid move string format!");
				}
			}
		}
		catch(IOException e)
		{
			System.err.println("Error while reading the move string!");
			System.err.println(e.getMessage());
		}
		
		return move;
	}
	
	// Constructor
	public Move(int fromRowIndex, int fromColumnIndex, int toRowIndex, int toColumnIndex)
	{
		this.fromRowIndex = fromRowIndex;
		this.fromColumnIndex = fromColumnIndex;
		this.toRowIndex = toRowIndex;
		this.toColumnIndex = toColumnIndex;
	}
	
	// Constructor
	public Move(int fromRowIndex, int fromColumnIndex, int toRowIndex, int toColumnIndex, int overwrittenFromSquare, int overwrittenToSquare)
	{
		this.fromRowIndex = fromRowIndex;
		this.fromColumnIndex = fromColumnIndex;
		this.toRowIndex = toRowIndex;
		this.toColumnIndex = toColumnIndex;
		
		this.overwrittenFromSquare = overwrittenFromSquare;
		this.overwrittenToSquare = overwrittenToSquare;
	}
	
	// Gets the row index of the pawn to move
	public int getFromRowIndex()
	{
		return this.fromRowIndex;
	}
	
	// Gets the column index of the pawn to move
	public int getFromColumnIndex()
	{
		return this.fromColumnIndex;
	}
	
	// Gets the row index where to move the pawn
	public int getToRowIndex()
	{
		return this.toRowIndex;
	}
	
	// Gets the column index where to move the pawn
	public int getToColumnIndex()
	{
		return this.toColumnIndex;
	}
	
	// Gets the value of the square that was overwrited by the move
	public int getOverwrittenFromSquare()
	{
		return this.overwrittenFromSquare;
	}
	
	// Gets the value of the square that was overwrited by the move
	public int getOverwrittenToSquare()
	{
		return this.overwrittenToSquare;
	}
	
	// Sets the value of the square thas is overwrited by the move
	public void setOverwrittenFromSquare(int value)
	{
		this.overwrittenFromSquare = value;
	}
	
	// Sets the value of the square thas is overwrited by the move
	public void setOverwrittenToSquare(int value)
	{
		this.overwrittenToSquare = value;
	}
	
	public void setReversed(boolean reversed)
	{
		this.reversed = reversed;
	}
	
	public boolean isReversed()
	{
		return this.reversed;
	}
	
	public boolean isEquals(Move otherMove)
	{
		if(otherMove == null)
		{
			return false;
		}
		
		return (this.fromColumnIndex == otherMove.fromColumnIndex &&
				this.fromRowIndex == otherMove.fromRowIndex &&
				this.toColumnIndex == otherMove.toColumnIndex &&
				this.toRowIndex == otherMove.toRowIndex);
	}
	
	// Gets the string representation of the move
	public String toString()
	{		
		char[] moveAsChars = new char[] {
				convertColumnToChar(this.fromColumnIndex),
				convertRowToChar(this.fromRowIndex),
				convertColumnToChar(this.toColumnIndex),
				convertRowToChar(this.toRowIndex)
			};
		
		return new String(moveAsChars);
		
	}
	
	// Converts a row from its character value to its index value
	public static int convertRowCharToInt(char rowIndex)
	{
		return Character.getNumericValue(rowIndex) - 1;
	}
	
	// Converts a column from its character value to its index value
	public static int convertColumnCharToInt(char columnIndex)
	{		
		int columnIndexAsInt = -1;
		
		switch(columnIndex)
		{
			case 'A' :
				columnIndexAsInt = 0;
				break;
			case 'B' :
				columnIndexAsInt = 1;
				break;
			case 'C' :
				columnIndexAsInt = 2;
				break;
			case 'D' :
				columnIndexAsInt = 3;
				break;
			case 'E' :
				columnIndexAsInt = 4;
				break;
			case 'F' :
				columnIndexAsInt = 5;
				break;
			case 'G' :
				columnIndexAsInt = 6;
				break;
			case 'H' :
				columnIndexAsInt = 7;
				break;
		}
		
		return columnIndexAsInt;
	}
	
	// Converts a row from its index value to its character value
	public static char convertRowToChar(int rowIndex)
	{		
		return Integer.toString(rowIndex + 1).charAt(0);
	}
	
	// Converts a column from its index value to its character value
	public static char convertColumnToChar(int columnIndex)
	{
		char columnIndexAsChar = 0;
		
		switch(columnIndex)
		{
			case 0 :
				columnIndexAsChar = 'A';
				break;
			case 1 :
				columnIndexAsChar = 'B';
				break;
			case 2 :
				columnIndexAsChar = 'C';
				break;
			case 3 :
				columnIndexAsChar = 'D';
				break;
			case 4 :
				columnIndexAsChar = 'E';
				break;
			case 5 :
				columnIndexAsChar = 'F';
				break;
			case 6 :
				columnIndexAsChar = 'G';
				break;
			case 7 :
				columnIndexAsChar = 'H';
				break;
		}
		
		return columnIndexAsChar;
	}
}
