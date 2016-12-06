package com.equipe4.thechessgame.CreateNewGame;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.equipe4.thechessgame.Configuration.GameConfiguration;
import com.equipe4.thechessgame.MainGame.MainGameActivity;
import com.equipe4.thechessgame.RequestsUtils.RequestSender;

/**
 * A controller for the CreateNewGameActivity. Part of the MVC model for the activity.
 *
 * Created by Alexis on 2016-10-16.
 */

class CreateNewGameController{

    private CreateNewGameActivity view;

    CreateNewGameController(CreateNewGameActivity view){
        this.view = view;
    }
    /**
     * This method sends the config in GameConfiguration to the server for game initialization purposes.
     */
    void sendNewGameConfig(){
        JSONObject json;
        try {
            json  = GameConfiguration.getInstance().toJSONObject();
        } catch (JSONException e){
            view.displayErrorMessage(e.toString());
            e.printStackTrace();
            return;
        }

        String URI = GameConfiguration.getInstance().getServerURL() + "/new_game";

        RequestSender.getInstance().addRequest(new JsonObjectRequest(Request.Method.POST, URI, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                startGameAs1stPlayer();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.displayErrorMessage("Impossible d'envoyer la configuration de la partie au serveur " + error.toString());
            }
        }));
    }

    private void startGameAs1stPlayer(){
        Intent intent = new Intent(view, MainGameActivity.class);
        intent.putExtra(MainGameActivity.IS_FIRST_PLAYER_PARAM, true);
        view.startActivity(intent);
    }
}
