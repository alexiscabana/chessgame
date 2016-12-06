package com.equipe4.thechessgame.ChessboardUtils;

/**
 * Created by Alexis on 2016-10-12.
 *
 * Class used to represent a checkboard state, in the Memento Design Pattern context.
 * It is used to "undo" or "redo" a change in the checkboard.
 */
public class ChessboardObjectMemento {
    private Piece[][] state;

    ChessboardObjectMemento(Piece[][] state){
        this.state = state;
    }
    Piece[][] getSavedState(){
        return this.state;
    }
}
