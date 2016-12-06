package com.equipe4.thechessgame.CreateNewGame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.equipe4.thechessgame.R;
import com.equipe4.thechessgame.Settings.SettingsActivity;

import com.equipe4.thechessgame.Configuration.GameConfiguration;

public class CreateNewGameActivity extends Activity {

    protected TextView tv;
    protected GameConfiguration.GameConfigChangedHandler textBoxUpdater;
    private CreateNewGameController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_game);
        controller = new CreateNewGameController(this);
        tv = (TextView) findViewById(R.id.summary_game_config_textbox);
        //trigger the update a first time_config_screen_layout
        tv.setBackgroundColor(Color.LTGRAY);

        //setup listeners
        textBoxUpdater = new GameConfiguration.GameConfigChangedHandler() {
            @Override
            public void onGameConfigChange(GameConfiguration.GameConfigChangedEvent event) {
                //the gameconfig changed, update on-screen text
                updateOnScreenGameConfigTextBox();
            }
        };
        GameConfiguration.getInstance().attachConfigChangedListener(textBoxUpdater);

        //force textbox update with any parameter
        textBoxUpdater.onGameConfigChange(new GameConfiguration.GameConfigChangedEvent(GameConfiguration.Parameter.isEnPassantMode,0,0));

        findViewById(R.id.boutonCreerLaPartie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedCreateTheGame();
            }
        });

        findViewById(R.id.boutonReglages).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedSettings();
            }
        });

    }

    protected void clickedSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    protected void clickedCreateTheGame(){
        controller.sendNewGameConfig();
    }
    protected void updateOnScreenGameConfigTextBox(){
        tv.setText(GameConfiguration.getInstance().toString());
        tv.invalidate();//force the view to redraw
    }
    public void displayErrorMessage(String mess) {
        Toast.makeText(getApplicationContext(), mess,Toast.LENGTH_LONG).show();
    }
}
