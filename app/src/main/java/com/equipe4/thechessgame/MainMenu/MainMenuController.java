package com.equipe4.thechessgame.MainMenu;

import android.content.Intent;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.equipe4.thechessgame.Configuration.GameConfiguration;
import com.equipe4.thechessgame.MainGame.MainGameActivity;
import com.equipe4.thechessgame.R;
import com.equipe4.thechessgame.RequestsUtils.Encoder;
import com.equipe4.thechessgame.RequestsUtils.RequestSender;

import java.util.HashMap;
import java.util.Map;

/**
 * A controller class used as part of the MVC pattern for the main menu. It controls game joins and
 * creations.
 *
 * Created by Alexis on 2016-10-31.
 */

class MainMenuController {

    MainMenuActivity view;

    MainMenuController(MainMenuActivity view){
        this.view = view;
    }
    void tryJoinGame(final String pswd){

        final String encodedpswd = Encoder.getCodeBase64(pswd);
        String URI = GameConfiguration.getInstance().getServerURL() + "/join_game";

        StringRequest req = new StringRequest(Request.Method.POST, URI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //set the password for the game.
                GameConfiguration.getInstance().setParam(GameConfiguration.Parameter.secretCode, pswd);
                //set the device in 2TabletsMode as we are joining a game from outside
                GameConfiguration.getInstance().setParam(GameConfiguration.Parameter.isTwoTabletsMode, true);
                startGameAs2ndPlayer();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse == null){
                    view.showToast(error.toString());
                } else if(error.networkResponse.statusCode == 401){
                    view.showToast("Pas le bon mot de passe");
                } else if(error.networkResponse.statusCode == 402){
                    view.showToast("Il n'y a pas de partie sur le serveur");
                } else if(error.networkResponse.statusCode == 403){
                    view.showToast("Partie déjà en cours sur le serveur");
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> newHeaders = new HashMap<>();
                newHeaders.putAll(super.getHeaders());

                newHeaders.put("Authorization", "Basic " + encodedpswd);

                return newHeaders;
            }
        };
        RequestSender.getInstance().addRequest(req);
    }
    private void startGameAs2ndPlayer() {
        Intent intent = new Intent(view, MainGameActivity.class);
        //as we join the game, we cannot be a first player.
        intent.putExtra(MainGameActivity.IS_FIRST_PLAYER_PARAM, false);
        view.showToast(view.getString(R.string.pswd_accepted_mainmenu));
        view.startActivity(intent);
    }
    void setServerIP(String ip){
        GameConfiguration.getInstance().setParam(GameConfiguration.Parameter.serverIP,ip);
        view.showToast("Adresse IP configurée");
    }
    void setThreadPollingInterval(Integer interval) {
        GameConfiguration.getInstance().setParam(GameConfiguration.Parameter.treadPollingInterval, interval);
        view.showToast("Interval configurée");
    }
}
