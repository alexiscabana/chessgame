package com.equipe4.thechessgame.MainGame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.equipe4.thechessgame.ChessboardUtils.ChessboardPosition;
import com.equipe4.thechessgame.ChessboardUtils.Piece;
import com.equipe4.thechessgame.ChessboardUtils.PieceKey;
import com.equipe4.thechessgame.Configuration.ApparenceConfiguration;
import com.equipe4.thechessgame.Configuration.BitmapContentProvider;
import com.equipe4.thechessgame.Configuration.GameConfiguration;
import com.equipe4.thechessgame.DialogUtils.ImageChooserDialog;
import com.equipe4.thechessgame.GameManager.GameEndType;
import com.equipe4.thechessgame.GameManager.Player;
import com.equipe4.thechessgame.R;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The Android Activity the represents the main game.
 *
 * Created by Alexis
 */
public class MainGameActivity extends AppCompatActivity {

    //parameter that every intent should possess to specify if the current player
    public static final String IS_FIRST_PLAYER_PARAM = "isFirstPlayer";

    //-------------------------PRIVATE FIELDS-------------------------------------------------------
    MainGameController controller;
    private ChessboardView cbView;
    private ChessInfoView  infoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);

        //init the chessboard view
        cbView = (ChessboardView) findViewById(R.id.chessboardView);
        infoView = (ChessInfoView) findViewById(R.id.chess_info_main_game);
        //set the content provider for this view
        cbView.setBitmapContentProvider(ApparenceConfiguration.getInstance().bitmapContentProviderFactory());
        cbView.setCaseSelectedColor(Color.RED);
        cbView.setCaseSelectedAlpha(120);
        cbView.setOnPieceMoveListener(new ChessboardView.PieceMoveListener() {
            @Override
            public void onPieceMove(ChessboardPosition source, ChessboardPosition dest) {
                controller.newMove(source, dest);
            }
        });
        cbView.lock();

        //init the controller based on the configuration (1 or 2 devices)
        boolean isFirstPlayer = getIntent().getBooleanExtra(IS_FIRST_PLAYER_PARAM, true);
        if((Boolean) GameConfiguration.getInstance().getParam(GameConfiguration.Parameter.isTwoTabletsMode))
            controller = MainGameController.abstractFactory(this, cbView, infoView,
                    isFirstPlayer ? Player.getInstancePlayerWhite() : Player.getInstancePlayerBlack());
        else
            controller = MainGameController.abstractFactory(this, cbView, infoView, null);

        //Init the startgame button (only for the first player)
        if(!isFirstPlayer){
            removeStartGameButton();
        } else {
            Button startb = (Button)findViewById(R.id.main_game_start_button);
            startb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedStartGame();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_game_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case(R.id.board_config_submenu):{
                showBoardLookConfigDialog();
                break;
            }
            case(R.id.pieces_confg_submenu):{
                showPiecesLookConfigDialog();
                break;
            }
            case(R.id.update_submenu):{
                updateboard();
                break;
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        controller.onFinish();
        super.onDestroy();
    }

    //-------------------------PRIVATE METHODS------------------------------------------------------
    private void clickedStartGame(){
        controller.startGame();
    }
    private void clickedEndGame(){
        showEndGameChooserDialog();
    }
    private void showBoardLookConfigDialog() {
        AlertDialog dialog = new ImageChooserDialog(this, controller.getPossibleBoardsLook(),
                new ImageChooserDialog.OnImageClickListener() {
            @Override
            public void onImageClickListener(Object tag) {
                ApparenceConfiguration.getInstance().setGroupAsCurrentParams(tag);
                cbView.redraw();
            }
        });
        dialog.setTitle(R.string.config_chessboard_look);
        dialog.show();
    }
    private void showPiecesLookConfigDialog() {
        AlertDialog dialog = new ImageChooserDialog(this, controller.getPossiblePiecesLook(),
                new ImageChooserDialog.OnImageClickListener() {
            @Override
            public void onImageClickListener(Object tag) {
                ApparenceConfiguration.getInstance().setGroupAsCurrentParams(tag);
                cbView.redraw();
            }
        });
        dialog.setTitle(R.string.pieces_look_config);
        dialog.show();
    }
    private void updateboard() {
        controller.updateBoard();
    }
    //-------------------------PUBLIC API-----------------------------------------------------------
    /**
     * Method to call when the activity should display a Toast
     *
     * @param mess the error message to show
     */
    public void showToast(String mess) {
        Toast.makeText(getApplicationContext(), mess,Toast.LENGTH_LONG).show();
    }
    public void showEndGameChooserDialog(){
        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        alertbox.setMessage("Voulez-vous vraiment quitter la partie?");
        // set a positive/yes button and create a listener
        alertbox.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {
                controller.endGame();
            }
        });
        alertbox.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {}
        });
        // display box
        alertbox.show();
    }
    public void forceGameEndDialog(GameEndType endType){
        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        alertbox.setMessage("La partie s'est terminée! Raison : " + endType.toString());
        final Activity thisObject = this;
        // set a positive/yes button and create a listener
        alertbox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {
                controller.onFinish();
                arg0.dismiss();
                thisObject.finish();
            }
        });
        // display box
        alertbox.show();
    }
    public void showAlertDialog(String mess){
        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        alertbox.setMessage(mess);
        // set a positive/yes button and create a listener
        alertbox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        // display box
        alertbox.show();
    }
    public void removeStartGameButton(){
        Button b = (Button) findViewById(R.id.main_game_start_button);
        if(b == null)
            return;
        ViewGroup layout = (ViewGroup) b.getParent();
        if(layout != null)
            layout.removeView(b);
    }
    public void addEndGameButton() {
        findViewById(R.id.main_game_end_button).setVisibility(View.VISIBLE);
        findViewById(R.id.main_game_end_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedEndGame();
            }
        });
    }
    public void showPawnPromotionDialog(BitmapContentProvider contentProvider, final Player p, ChessboardPosition promotePos) {
        Piece.PieceType[] typesOfPromotion =
                {
                        Piece.PieceType.queen,
                        Piece.PieceType.bishop,
                        Piece.PieceType.rook,
                        Piece.PieceType.knight
                };
        Map<Bitmap, Object> map = new LinkedHashMap<>();

        for(Piece.PieceType pType : typesOfPromotion)
            map.put(contentProvider.getBitmapFor(new PieceKey(pType, p.getType())), pType);

        final AlertDialog dialog = new ImageChooserDialog(this, map, new ImageChooserDialog.OnImageClickListener() {
            @Override
            public void onImageClickListener(Object tag) {
                controller.promotePawnTo((Piece.PieceType)tag, p);
            }
        });
        // set the message to display
        dialog.setTitle(R.string.pwan_promotion_dialog_title);
        dialog.setMessage("Promouvoir le pion en case " + promotePos.toString() + " vers:");

        dialog.show();
    }
}
