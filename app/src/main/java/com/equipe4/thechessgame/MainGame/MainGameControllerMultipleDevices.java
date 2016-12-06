package com.equipe4.thechessgame.MainGame;

import android.graphics.Bitmap;
import android.view.View;

import com.equipe4.thechessgame.ChessboardUtils.Piece;
import com.equipe4.thechessgame.ChessboardUtils.PieceKey;
import com.equipe4.thechessgame.Configuration.ApparenceConfiguration;
import com.equipe4.thechessgame.Configuration.GameConfiguration;
import com.equipe4.thechessgame.GameManager.GameEvents.GameManagerEventListener;
import com.equipe4.thechessgame.GameManager.GameEvents.PlayerChangedEvent;
import com.equipe4.thechessgame.GameManager.GameManagerResponseListener;
import com.equipe4.thechessgame.GameManager.GameManagerTwoTabletsMode;
import com.equipe4.thechessgame.GameManager.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexis on 2016-11-26.
 */

class MainGameControllerMultipleDevices extends MainGameController{

    private final Player playerOnThisDevice;

    protected MainGameControllerMultipleDevices(final MainGameActivity activity, ChessboardView view, ChessInfoView infoView, Player player) {
        super(activity, view, infoView);
        this.playerOnThisDevice = player;
        this.model = new GameManagerTwoTabletsMode(
                playerOnThisDevice,
                (Integer) GameConfiguration.getInstance().getParam(GameConfiguration.Parameter.treadPollingInterval),
                new GameManagerResponseListener() {
                    @Override
                    public void onRequestError(String mess) {
                        activity.showToast("Impossible d'avoir les paramètres de partie");
                    }
                });
        init();
    }

    @Override
    protected void init() {
        super.init();
        model.addGameStateListener(new GameManagerEventListener() {
            @Override
            public void onPlayerChanged(final PlayerChangedEvent event) {
                playerChangedRoutine(event.getNewPlayer());
            }
        });
    }
    private void playerChangedRoutine(final Player newPlayer){
        if(newPlayer == playerOnThisDevice){
            infoView.showHeaderState(ChessInfoView.YOUR_TURN);
            view.unlock();
            model.updateRemainingTime(newPlayer, new GameManagerResponseListener() {
                @Override
                public void onRequestSuccess() {
                    infoView.setTimeTextVisibilty(View.VISIBLE);
                    if (newPlayer.getPlayerTime().getTimeRemaining().getTime() > 0) {
                        infoView.setRemainingTime(ChessInfoView.NORMAL_TIME, newPlayer.getPlayerTime().getTimeRemaining());
                        infoView.resumeCountdown();
                    } else if (newPlayer.getPlayerTime().getOvertimeRemaining().getTime() > 0) {
                        infoView.setRemainingTime(ChessInfoView.OVERTIME, newPlayer.getPlayerTime().getOvertimeRemaining());
                        infoView.resumeCountdown();
                    } else{ //if both time and overtime == 0, there's either no countdown or the game has ended
                        infoView.setTimeTextVisibilty(View.INVISIBLE);
                        infoView.stopCountdown();
                    }
                }
                @Override
                public void onRequestError(String mess) {
                    infoView.setTimeTextVisibilty(View.INVISIBLE);
                }
            });
        } else {
            infoView.showHeaderState(ChessInfoView.NOT_YOUR_TURN);
            infoView.setTimeTextVisibilty(View.INVISIBLE);
            view.lock();
        }
    }
    @Override
    public Map<Bitmap, Object> getPossiblePiecesLook(){
        Map<Bitmap, Object> ret = new HashMap<>();
        for(ApparenceConfiguration.PieceTag tag : ApparenceConfiguration.PieceTag.values()){
            ret.put((Bitmap) ApparenceConfiguration.getInstance().
                    getCachedParam(new PieceKey(Piece.PieceType.king, playerOnThisDevice.getType()), tag),tag);
        }
        return ret;
    }

    @Override
    public void onFinish() {
        super.onFinish();
        infoView.stopCountdown();
    }
}
