package com.equipe4.thechessgame.GameManager.GameEvents;

import com.equipe4.thechessgame.GameManager.GameEndType;

/**
 * Created by Alexis on 2016-11-20.
 */

public class GameEndedEvent implements IGameEvent {
    private GameEndType reason;
    public GameEndedEvent(GameEndType reason){
        this.reason = reason;
    }
    public GameEndType getGameEndReason(){ return reason; }
}
