package com.equipe4.thechessgame.GameManager;

import android.support.annotation.NonNull;

import com.equipe4.thechessgame.ChessboardUtils.ChessboardPosition;
import com.equipe4.thechessgame.GameManager.GameEvents.ChessboardChangedEvent;
import com.equipe4.thechessgame.GameManager.GameEvents.GameEndedEvent;
import com.equipe4.thechessgame.GameManager.GameEvents.IGameEvent;
import com.equipe4.thechessgame.GameManager.GameEvents.PlayerChangedEvent;

/**
 * Created by Alexis on 2016-11-20.
 */

public class GameManagerTwoTabletsMode extends GameManagerWithServer {

    private final Player playerOnThisDevice;
    private volatile boolean isTimePolling = false, isStatusPolling = false;
    private Integer pollingInterval = null;
    private Thread state, time;

    public GameManagerTwoTabletsMode(final Player playerOnThisDevice,
                                     Integer pollingInterval,
                                     final GameManagerResponseListener listener){
        super();
        this.playerOnThisDevice = playerOnThisDevice;
        this.pollingInterval = pollingInterval;
        //if you're not the first player, check if it's your turn
        if( this.playerOnThisDevice.getNumeroJoueur() != 1 ){
            getGameDetails(new GameManagerResponseListener() {
                @Override
                public void onRequestError(String mess) {
                    listener.onRequestError(mess);
                }
                @Override
                public void onRequestSuccess() {
                    listener.onRequestSuccess();
                }
            });
        }
        resumeStatePolling();
    }

    /**
     *
     * A call to this method is a com.equipe4.androidappinf3995_04.request to change the current com.equipe4.androidappinf3995_04.gamestate by moving piece
     * (SourceX, SourceY) => (DestX, DestY). For example, (a,2)=>(a,3)
     *
     * @param source the source poisiton of the piece
     * @param dest the destination position of the piece
     */
    public void moveChessPiece(@NonNull final ChessboardPosition source,
                               @NonNull final ChessboardPosition dest,
                               final GameManagerResponseListener list){
        if(source.equals(dest)) {
            list.onRequestSuccess();
            return;
        }
        if(!isGameInProgress()){
            list.onRequestError("La partie n'est pas commencée!");
            return;
        }
        //explicit Move
        super.doMove(playerOnThisDevice, source, dest, new GameManagerResponseListener() {
            @Override
            public void onRequestSuccess() {
                determineNextPlayer();
                list.onRequestSuccess();
            }
            @Override
            public void onRequestError(String mess) {
                list.onRequestError(mess);
            }
        });
    }

    @Override
    public void onFinish() {
        stopStatePolling();
    }
    @Override
    protected void setPlayerCurrentlyPlaying(Player j) {
        if(playerCurrentlyPlaying == j || j == null)//same Joueur
            return;
        this.playerCurrentlyPlaying = j;
        notifyListeners(new PlayerChangedEvent(this.playerCurrentlyPlaying));

        if(j == playerOnThisDevice) { // if it's our turn, update the board to get the changes
            updateStatusBoard(false, new GameManagerResponseListener() {
                @Override
                public void onRequestSuccess() {
                    notifyListeners(new ChessboardChangedEvent(null, null));
                }
            });
        }
    }
    @Override
    protected void notifyListeners(IGameEvent e) {
        if(e instanceof GameEndedEvent){
            onFinish();
        }
        super.notifyListeners(e);
    }
    protected void resumeStatePolling(){
        if(isStatusPolling)
            return;
        isStatusPolling = true;
        state = new Thread(new Runnable() {
            @Override
            public void run() {
                int n = 0;
                while(isStatusPolling){
                    try {
                        Thread.sleep(pollingInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateGameStatus(new GameManagerResponseListener() {
                        @Override
                        public void onRequestError(String mess) {
                            System.out.println("Status update error : " + mess);
                        }
                    });
                    System.out.println("Status update " + getGameStatus().toString()+ " " + ++n);
                }
            }
        });
        state.start();
    }
    protected void stopStatePolling(){
        isStatusPolling = false;
    }
}
