package com.example.umarr.chessapp.pieces;

import com.example.umarr.chessapp.chess.GameActivity;


/**
 * abstract class that represents a physical piece
 * 
 * @author Umar Rabbani
 * @author Parth Desai
 */

public abstract class Piece {
	/** x coordinate of piece */
	int x;
	/** y coordinate of piece */
	int y;
	/** color of piece */
	String color;
	
	/** constructor for piece
	 * @param x This is the x coordinate of the piece being initiated 
	 * @param y This is the y coordinate of the piece being initiated
	 * @param color This is the color of the piece being initiated
	 */
	public Piece(int x, int y, String color) {
		this.x = x; this.y = y; this.color = color;
	}

	/**
	 *  attempts to move a user specified piece to a user specified location
	 *  @param positionX This is the x coordinate of the location that the user wants to move the piece to
	 *  @param positionY This is the y coordinate of the location that the user wants to move the piece to
	 *  @return boolean This returns back to the caller letting it know if the piece was successfully moved to the desired location or not without putting the king in check
	 */
	public boolean move(int positionX, int positionY) {
		if (this.isValid(positionX, positionY)) {
			
			// incase of undo
			Piece oldPiece = GameActivity.board[positionX][positionY];
			int oldX = x;
			int oldY = y;
		
			GameActivity.board[positionX][positionY] = this;
			GameActivity.board[x][y] = null;
			this.x = positionX; this.y = positionY;
			
			//makes sure to not place own king in check
			if (!GameActivity.kingCheck(color)) {
				return true;
			}
			//need to undo changes
			this.x = oldX; this.y = oldY;
			GameActivity.board[x][y] = this;
			GameActivity.board[positionX][positionY] = oldPiece;
			
		}
		
		return false;
	}
	
	/**
	 * does the same thing as move() but then undos the move right after a successful move
	 * used to test whether or not a certain move would place (or keep) its king in check
	 * @param positionX This is the x coordinate of the target location specified by the user
	 * @param positionY This is the y coordinate of the target location specified by the user
	 * @return boolean This returns whether or not the move would be successful without keeping the king in check
	 */
	public boolean testMove(int positionX, int positionY) {
		if (this.isValid(positionX, positionY)) {
			// incase of undo
			Piece oldPiece = GameActivity.board[positionX][positionY];
			int oldX = x;
			int oldY = y;

			GameActivity.board[positionX][positionY] = this;
			GameActivity.board[x][y] = null;
			this.x = positionX;
			this.y = positionY;

			// makes sure to not place own king in check
			if (!GameActivity.kingCheck(color)) {
				// need to undo changes
				this.x = oldX;
				this.y = oldY;
				GameActivity.board[x][y] = this;
				GameActivity.board[positionX][positionY] = oldPiece;
				
				return true;
			}
			// need to undo changes
			this.x = oldX;
			this.y = oldY;
			GameActivity.board[x][y] = this;
			GameActivity.board[positionX][positionY] = oldPiece;

		}

		return false;
	}
	
	/**
	 * getter method to get the piece's color
	 * @return String This is the color of the requested piece
	 */
	public String getColor() {
		return color;
	}
	/**
	 * getter method to get the piece's y coordinate
	 * @return int This is the y coordinate of the requested piece
	 */
	public int getY() {
		return y;
	}
	/**
	 * getter method to get the piece's x coordinate
	 * @return int This is the x coordinate of the requested piece
	 */
	public int getX() {
		return x;
	}
	
	/**
	 *  checks if move is valid
	 *  @param positionX This is the x coordinate of the target position
	 *  @param positionY This is the y coordinate of the target position
	 *  @return boolean Returns whether or not the requested move is permitted
	 */
	public abstract boolean isValid(int positionX, int positionY);
	
}
