package com.equipe4.thechessgame.GameManager.GameEvents;

/**
 * Listener class for the GameManager. Every new listener to notify from the GameManager should
 * extend this class.
 *
 * Created by Alexis on 2016-11-20.
 */

public abstract class GameManagerEventListener {
    public void onChessboardChanged(ChessboardChangedEvent event){}
    public void onPlayerChanged(PlayerChangedEvent event){}
    public void onGameStarted(GameStartedEvent event){}
    public void onGameEnded(GameEndedEvent event){}
    public void onGameStatusChanged(GameStatusChangedEvent event){}
    public void onPawnPromotion(PawnPromotionEvent event){}
}
