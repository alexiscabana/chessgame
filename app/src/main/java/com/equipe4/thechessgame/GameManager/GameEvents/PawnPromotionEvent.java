package com.equipe4.thechessgame.GameManager.GameEvents;

import com.equipe4.thechessgame.ChessboardUtils.ChessboardPosition;

/**
 * Created by Alexis on 2016-11-20.
 */

public class PawnPromotionEvent implements IGameEvent {

    private ChessboardPosition promotionPosition;
    public PawnPromotionEvent(ChessboardPosition promotionPosition){ this.promotionPosition = promotionPosition; }
    public ChessboardPosition getPromotionPosition() {
        return promotionPosition;
    }
}
