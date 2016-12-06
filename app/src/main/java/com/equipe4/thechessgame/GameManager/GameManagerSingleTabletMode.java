package com.equipe4.thechessgame.GameManager;

import android.support.annotation.NonNull;

import com.equipe4.thechessgame.ChessboardUtils.ChessboardPosition;

/**
 * Created by Alexis on 2016-11-20.
 */

public class GameManagerSingleTabletMode extends GameManagerWithServer{

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
        //once the move is done, switch player
        doMove(playerCurrentlyPlaying, source, dest, new GameManagerResponseListener() {
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
    public void onFinish() {}

}
