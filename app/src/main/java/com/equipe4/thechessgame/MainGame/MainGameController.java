package com.equipe4.thechessgame.MainGame;

import android.graphics.Bitmap;

import com.equipe4.thechessgame.ChessboardUtils.ChessboardPosition;
import com.equipe4.thechessgame.ChessboardUtils.Piece;
import com.equipe4.thechessgame.ChessboardUtils.PieceKey;
import com.equipe4.thechessgame.Configuration.ApparenceConfiguration;
import com.equipe4.thechessgame.GameManager.GameEndType;
import com.equipe4.thechessgame.GameManager.GameEvents.ChessboardChangedEvent;
import com.equipe4.thechessgame.GameManager.GameEvents.GameEndedEvent;
import com.equipe4.thechessgame.GameManager.GameEvents.GameManagerEventListener;
import com.equipe4.thechessgame.GameManager.GameEvents.GameStartedEvent;
import com.equipe4.thechessgame.GameManager.GameEvents.GameStatusChangedEvent;
import com.equipe4.thechessgame.GameManager.GameEvents.PawnPromotionEvent;
import com.equipe4.thechessgame.GameManager.GameManagerResponseListener;
import com.equipe4.thechessgame.GameManager.GameStatusType;
import com.equipe4.thechessgame.GameManager.IGameManager;
import com.equipe4.thechessgame.GameManager.Player;
import com.equipe4.thechessgame.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller class used to update both the View (a CustomCheckBoardView) and Model (a GameState).
 * It is part of the MVC for the main game.
 *
 * Created by Alexis on 2016-10-16.
 */
abstract class MainGameController {

    //-------------------------MVC COMPONENTS-------------------------------------------------------
    protected final MainGameActivity activity;
    protected final ChessboardView view;
    protected final ChessInfoView infoView;
    protected IGameManager model;

    //-------------------------CONSTRUCTOR----------------------------------------------------------
    protected MainGameController(final MainGameActivity activity, ChessboardView view, ChessInfoView infoView){
        this.view = view;
        this.activity = activity;
        this.infoView = infoView;
    }

    public static MainGameController abstractFactory(
            MainGameActivity activity,
            ChessboardView view,
            ChessInfoView infoView,
            Player playerOnThisDevice){

        if(playerOnThisDevice == null) //should be a single device main game manager
            return new MainGameControllerSingleDevice(activity, view, infoView);
        else
            return new MainGameControllerMultipleDevices(activity, view, infoView, playerOnThisDevice);
    }

    //-------------------------PUBLIC API-----------------------------------------------------------

    /**
     * This method should be called whenever there's a new move made on the board. It will forward
     * the new move to the model
     *
     * @param source the source position of the piece
     * @param dest the destination position of the piece
     */
    void newMove(final ChessboardPosition source, final ChessboardPosition dest){
        view.lock();
        model.moveChessPiece(source, dest, new GameManagerResponseListener() {
            @Override
            public void onRequestSuccess() {
                view.unlock();
            }
            @Override
            public void onRequestError(String mess) {
                activity.showToast(activity.getString(R.string.cannot_do_move_toastmessage) + mess);
                view.unlock();
            }
        });
    }
    /**
     * This method should be called to start the game.
     */
    void startGame(){
        model.startGame(new GameManagerResponseListener() {
            @Override
            public void onRequestError(String mess) {
                activity.showToast(activity.getString(R.string.cannot_start_game_error) + mess);
            }
        });
    }

    void endGame(){
        model.endGame(GameEndType.ABANDON, new GameManagerResponseListener() {
            @Override
            public void onRequestSuccess() {
                activity.forceGameEndDialog(GameEndType.ABANDON);
            }

            @Override
            public void onRequestError(String mess) {
                activity.showToast("Impossible de finir la partie : " + mess);
            }
        });
    }

    public Map<Bitmap, Object> getPossiblePiecesLook(){
        Map<Bitmap, Object> ret = new HashMap<>();
        for(ApparenceConfiguration.PieceTag tag : ApparenceConfiguration.PieceTag.values()){
            ret.put((Bitmap) ApparenceConfiguration.getInstance().
                    getCachedParam(new PieceKey(Piece.PieceType.king, model.getPlayerCurrentlyPlaying().getType()), tag),tag);
        }
        return ret;
    }
    public Map<Bitmap,Object> getPossibleBoardsLook() {
        Map<Bitmap, Object> ret = new HashMap<>();
        for(ApparenceConfiguration.BoardTag tag : ApparenceConfiguration.BoardTag.values()){
            ret.put((Bitmap) ApparenceConfiguration.getInstance().
                    getCachedParam(ApparenceConfiguration.Params.chessboard_bitmap_param, tag),tag);
        }
        return ret;
    }
    public void promotePawnTo(Piece.PieceType piece, Player p) {
        model.promotePiece(piece, p, new GameManagerResponseListener(){
            @Override
            public void onRequestError(String mess) {
                activity.showToast(mess);
            }
        });
    }
    public void onFinish(){
        model.onFinish();
    }

    //-------------------------PRIVATE METHODS------------------------------------------------------
    protected void init(){
        model.addGameStateListener(new GameManagerEventListener() {
            @Override
            public void onChessboardChanged(ChessboardChangedEvent event) {
                //board is already set, just update the view.
                view.redraw();
            }
            @Override
            public void onGameStarted(GameStartedEvent event) {
                ((ChessboardView)activity.findViewById(R.id.chessboardView)).unlock();
                activity.showToast(activity.getString(R.string.game_started_warning));
                activity.removeStartGameButton();
                activity.addEndGameButton();
                activity.findViewById(R.id.activity_main_game).invalidate();
            }
            @Override
            public void onGameEnded(GameEndedEvent event) {
                activity.forceGameEndDialog(event.getGameEndReason());
            }
            @Override
            public void onPawnPromotion(PawnPromotionEvent event) {
                activity.showPawnPromotionDialog(
                        ApparenceConfiguration.getInstance().bitmapContentProviderFactory(),
                        model.getPlayerCurrentlyPlaying(),
                        event.getPromotionPosition()
                        );
            }
            @Override
            public void onGameStatusChanged(GameStatusChangedEvent event) {
                if(event.getNewStatus() == GameStatusType.check)
                    activity.showToast("Échec!");
                else if(event.getNewStatus() == GameStatusType.checkmate)
                    activity.showToast("Échec et mat!");
                else if(event.getNewStatus() == GameStatusType.stalemate)
                    activity.showToast("Partie nulle!");
            }
        });
        view.setChessboard(model.getBoard()); //set the chessboard that will be used to draw itself
    }


    public void updateBoard() {
        model.updateStatusBoard(false, new GameManagerResponseListener() {
            @Override
            public void onRequestSuccess() {
                view.redraw();
            }

            @Override
            public void onRequestError(String mess) {
                activity.showToast("Impossible de mettre à jour l'échiquier");
            }
        });
    }
}
