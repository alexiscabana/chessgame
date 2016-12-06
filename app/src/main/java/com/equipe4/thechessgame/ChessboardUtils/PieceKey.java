package com.equipe4.thechessgame.ChessboardUtils;

import com.equipe4.thechessgame.GameManager.Player;

/**
 * Class used to represent a hashable version of a Piece passed in parameter. It is a utilities
 * class used to put a Piece as a key in a map, for example.
 *
 * Created by Alexis on 2016-10-26.
 */

public class PieceKey {

    Piece.PieceType ptype;
    Player.PlayerType jtype;

    public PieceKey(Piece p) {
        this(p.getType(), p.getJoueurProprietaire().getType());
    }
    public PieceKey(Piece.PieceType ptype, Player.PlayerType jtype){
        this.ptype = ptype;
        this.jtype = jtype;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof PieceKey))
            return false;
        PieceKey other = (PieceKey) obj;
        return other.jtype == this.jtype && other.ptype == this.ptype;
    }

    @Override
    public int hashCode() {
        int hash = 47;
        hash = 89 * hash + (this.ptype != null ? this.ptype.hashCode() : 0);
        hash = 89 * hash + (this.jtype != null ? this.jtype.hashCode() : 0);
        return hash;
    }
}

