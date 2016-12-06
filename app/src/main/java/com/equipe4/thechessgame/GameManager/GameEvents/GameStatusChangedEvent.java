package com.equipe4.thechessgame.GameManager.GameEvents;

import com.equipe4.thechessgame.GameManager.GameStatusType;

/**
 * Created by Alexis on 2016-11-20.
 */

public class GameStatusChangedEvent implements IGameEvent {
    private GameStatusType newStatus;
    private GameStatusType oldStatus;

    public GameStatusChangedEvent(GameStatusType oldStatus, GameStatusType newStatus){
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
    public GameStatusType getNewStatus(){ return newStatus; }
    public GameStatusType getOldStatus(){ return oldStatus; }
}
