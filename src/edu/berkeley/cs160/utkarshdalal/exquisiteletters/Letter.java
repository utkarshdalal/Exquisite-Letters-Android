package edu.berkeley.cs160.utkarshdalal.exquisiteletters;

public class Letter {
	char mLetter;
	String mColour;
	int mPosition;
	public Letter(char letter, String colour, int position) {
		super();
		mLetter = letter;
		mColour = colour;
		mPosition = position;
	}	
	public char getLetter() {
		return mLetter;
	}
	public void setLetter(char letter) {
		mLetter = letter;
	}
	public String getColour() {
		return mColour;
	}
	public void setColour(String colour) {
		mColour = colour;
	}
	public int getPosition() {
		return mPosition;
	}
	public void setPosition(int position) {
		mPosition = position;
	}	
}
