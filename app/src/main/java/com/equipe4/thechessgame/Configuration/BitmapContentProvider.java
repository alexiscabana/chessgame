package com.equipe4.thechessgame.Configuration;

import android.graphics.Bitmap;

import com.equipe4.thechessgame.ChessboardUtils.Piece;
import com.equipe4.thechessgame.ChessboardUtils.PieceKey;

/**
 * Facade class for the ApparenceConfiguration. It forwards the "getters" to the approriate
 * ApparenceConfig to get the param's associated Bitmap.
 *
 * Created by Alexis on 2016-10-27.
 */


public class BitmapContentProvider {
    private ApparenceConfiguration content;
    BitmapContentProvider(ApparenceConfiguration content){
        this.content = content;
    }
    public Bitmap getBitmapFor(Object param){
        if(param instanceof Piece)
            return (Bitmap)content.getCurrentParam(new PieceKey((Piece)param));
        else
            return (Bitmap)content.getCurrentParam(param);
    }
    public Bitmap getCachedParam(Object param, Object tag){
        if(param instanceof Piece)
            return (Bitmap)content.getCachedParam(new PieceKey((Piece)param), tag);
        else
            return (Bitmap)content.getCachedParam(param, tag);
    }
}
