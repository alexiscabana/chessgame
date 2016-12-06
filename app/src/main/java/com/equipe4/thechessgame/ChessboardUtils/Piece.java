package com.equipe4.thechessgame.ChessboardUtils;

import com.equipe4.thechessgame.GameManager.Player;

/**
 * Created by Alexis on 2016-10-11.
 *
 * Classe qui représente une piece aux échec
 */

public class Piece{

    //private fields
    private PieceType type;
    private Player appartenance;

    public Piece(PieceType type, Player player){
        this.type = type;
        this.appartenance = player;
    }

    public Player getJoueurProprietaire() {
        return appartenance;
    }
    public PieceType getType() {
        return type;
    }
    public Piece clonePiece(){
        return new Piece(this.type, this.appartenance);
    }

    public enum PieceType{
        king,
        queen,
        rook,
        knight,
        bishop,
        pawn
    }
}
