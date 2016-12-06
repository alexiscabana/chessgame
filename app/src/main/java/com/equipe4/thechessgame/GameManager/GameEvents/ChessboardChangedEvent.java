package com.equipe4.thechessgame.GameManager.GameEvents;

import com.equipe4.thechessgame.ChessboardUtils.ChessboardPosition;
import com.equipe4.thechessgame.ChessboardUtils.Piece;

/**
 * Created by Alexis on 2016-11-20.
 */

public class ChessboardChangedEvent implements IGameEvent {
    private ChessboardPosition eliminatedPiecePos;
    private Piece eliminatedPiece;
    public ChessboardChangedEvent(Piece eliminatedPiece, ChessboardPosition eliminatedPiecePos){
        this.eliminatedPiece = eliminatedPiece;
        this.eliminatedPiecePos = eliminatedPiecePos;
    }
    public Piece getEliminatedPiece(){
        return eliminatedPiece;
    }
    public ChessboardPosition getEliminatedPiecePos(){
        return eliminatedPiecePos;
    }
}
