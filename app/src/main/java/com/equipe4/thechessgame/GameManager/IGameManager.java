package com.equipe4.thechessgame.GameManager;

import android.support.annotation.NonNull;

import com.equipe4.thechessgame.ChessboardUtils.ChessBoardObject;
import com.equipe4.thechessgame.ChessboardUtils.ChessboardPosition;
import com.equipe4.thechessgame.ChessboardUtils.Piece;
import com.equipe4.thechessgame.GameManager.GameEvents.GameManagerEventListener;

/**
 * All GameManager should extend this class to manage a game.
 *
 * Created by Alexis on 2016-11-20.
 */

public interface IGameManager {

    //Game requests
    void addGameStateListener(GameManagerEventListener newlist);
    void deleteGameStateListener(GameManagerEventListener newlist);
    void moveChessPiece(@NonNull final ChessboardPosition source,
                        @NonNull final ChessboardPosition dest,
                        final GameManagerResponseListener list);
    void startGame(final GameManagerResponseListener list);
    void endGame(final GameEndType reason, final GameManagerResponseListener list);
    void updateGameStatus(final GameManagerResponseListener list);
    void updateRemainingTime(final Player j, final GameManagerResponseListener list);
    void updateStatusBoard(boolean post, final GameManagerResponseListener list);
    void promotePiece(Piece.PieceType piece, Player p, final GameManagerResponseListener list);

    void determineNextPlayer();

    boolean isGameInProgress();
    GameStatusType getGameStatus();
    Player getPlayerCurrentlyPlaying();
    ChessBoardObject getBoard();
    Integer getTotalNoMove();
    String getLastMoveCase();

    void onFinish();
}
