package com.equipe4.thechessgame.MainGame;

/**
 * Class used to represent the position on a touch on-screen.
 *
 * Created by Alexis on 2016-10-16.
 */
public class TouchPosition {
    private int OriginX = 0, OriginY = 0;

    public TouchPosition(int OriginX,int OriginY){
        this.OriginX = OriginX;
        this.OriginY = OriginY;
    }

    public int getX() {
        return OriginX;
    }
    public int getY() {
        return OriginY;
    }
}
