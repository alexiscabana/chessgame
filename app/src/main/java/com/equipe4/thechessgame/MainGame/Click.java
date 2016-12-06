package com.equipe4.thechessgame.MainGame;

/**
 * Class used to represent the span of a touch
 *
 * Created by Alexis on 2016-10-16.
 */

public class Click {
    private TouchPosition origin = null, ending = null;

    public Click(TouchPosition origin){
        this.origin = origin;
    }
    public Click(TouchPosition origin, TouchPosition ending){
        this.origin = origin;
        this.origin = ending;
    }

    public float distanceOriginToEnding(){
        if(ending == null)
            return 0;
        return ((float)Math.sqrt(
                Math.pow(origin.getX() - ending.getX(), 2) +
                Math.pow(origin.getY() - ending.getY(), 2)
        ));
    }
    public void setEnding(TouchPosition ending){
        this.ending = ending;
    }
    public TouchPosition getOrigin(){ return this.origin; }
    public TouchPosition getEnding(){ return this.ending; }
}
