package com.equipe4.thechessgame.MainGame;

import android.view.View;

import com.equipe4.thechessgame.GameManager.GameEvents.GameManagerEventListener;
import com.equipe4.thechessgame.GameManager.GameEvents.PlayerChangedEvent;
import com.equipe4.thechessgame.GameManager.GameManagerResponseListener;
import com.equipe4.thechessgame.GameManager.GameManagerSingleTabletMode;
import com.equipe4.thechessgame.GameManager.Player;

/**
 * Created by Alexis on 2016-11-26.
 */

class MainGameControllerSingleDevice extends MainGameController {


    protected MainGameControllerSingleDevice(MainGameActivity activity, ChessboardView view, ChessInfoView infoView) {
        super(activity, view, infoView);
        this.model = new GameManagerSingleTabletMode();
        init();
    }
    @Override
    protected void init() {
        super.init();
        model.addGameStateListener(new GameManagerEventListener() {
            @Override
            public void onPlayerChanged(PlayerChangedEvent event) {
                onPlayerChangedRoutine(event.getNewPlayer());
            }
        });
    }

    @Override
    public void onFinish() {
        super.onFinish();
        infoView.stopCountdown();
    }

    private void onPlayerChangedRoutine(final Player newPlayer) {
        if(newPlayer.getNumeroJoueur() == 1) infoView.showHeaderState(ChessInfoView.TURN_1ST_PLAYER);
        else if(newPlayer.getNumeroJoueur() == 2) infoView.showHeaderState(ChessInfoView.TURN_2ND_PLAYER);

        //update the time remaining for the player, then start the countdown
        model.updateRemainingTime(newPlayer, new GameManagerResponseListener() {
            @Override
            public void onRequestSuccess() {
                //player has updated remaining time
                infoView.setTimeTextVisibilty(View.VISIBLE);
                if (newPlayer.getPlayerTime().getTimeRemaining().getTime() > 0)
                    infoView.setRemainingTime(ChessInfoView.NORMAL_TIME, newPlayer.getPlayerTime().getTimeRemaining());
                else
                    infoView.setRemainingTime(ChessInfoView.OVERTIME, newPlayer.getPlayerTime().getOvertimeRemaining());
                infoView.resumeCountdown();
            }
            @Override
            public void onRequestError(String mess) {
                infoView.setTimeTextVisibilty(View.INVISIBLE);
            }
        });

    }

}
