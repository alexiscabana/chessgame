package com.equipe4.thechessgame.ChessboardUtils;

/**
 * Class used to represent a position a piece can have. Only a container.
 *
 * Created by Alexis on 2016-10-25.
 */

public class ChessboardPosition {

    private Character x;
    private Integer y;

    public ChessboardPosition(char x, int y){
        this.x = x;
        this.y = y;
    }
    public ChessboardPosition(String s){
        this(s.charAt(0), s.charAt(1) - '0');
    }
    public char getColumn() {
        return x;
    }
    public int getRow() {
        return y;
    }
    @Override
    public String toString() {
        return x.toString() + y.toString();
    }

    @Override
    public boolean equals(Object obj) {

        if(!(obj instanceof ChessboardPosition))
            return false;

        ChessboardPosition other = (ChessboardPosition)obj;

        return other.x.equals(this.x) && other.y.equals(this.y);
    }
}
