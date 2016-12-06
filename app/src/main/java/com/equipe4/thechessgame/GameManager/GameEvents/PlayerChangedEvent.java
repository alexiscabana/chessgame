package com.equipe4.thechessgame.GameManager.GameEvents;

import com.equipe4.thechessgame.GameManager.Player;

/**
 * Created by Alexis on 2016-11-20.
 */

public class PlayerChangedEvent implements IGameEvent {
    private Player newj;
    public PlayerChangedEvent(Player newPlayer){
        this.newj = newPlayer;
    }
    public Player getNewPlayer(){ return newj; }
}
