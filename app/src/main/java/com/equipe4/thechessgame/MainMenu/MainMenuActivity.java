package com.equipe4.thechessgame.MainMenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.equipe4.thechessgame.CreateNewGame.CreateNewGameActivity;
import com.equipe4.thechessgame.R;

public class MainMenuActivity extends Activity {

    private MainMenuController controller;

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
    public void showToast(String mess){
        Toast.makeText(this, mess, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //setup listeners
        findViewById(R.id.boutonJoindrePartie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedJoinGame();
            }
        });

        findViewById(R.id.boutonCreerNouvellePartie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedCreateNewGame();
            }
        });

        findViewById(R.id.set_ip_addr_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setServerIPAddress(((EditText)findViewById(R.id.textedit_ip_address)).getText().toString());
            }
        });

        controller = new MainMenuController(this);
    }

    private void setServerIPAddress(String ip) {
        controller.setServerIP(ip);
    }
    protected void clickedJoinGame(){
        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);

        // set the message to display
        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        alertbox.setMessage(R.string.joindre_partie_alertdialog_message);

        alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        // set a positive/yes button and create a listener
        alertbox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {
                controller.tryJoinGame(input.getText().toString());
            }
        });
        alertbox.setView(input);

        // display box
        alertbox.show();
     }
    protected void clickedCreateNewGame(){
        Intent intent = new Intent(this, CreateNewGameActivity.class);
        startActivity(intent);
    }
}
