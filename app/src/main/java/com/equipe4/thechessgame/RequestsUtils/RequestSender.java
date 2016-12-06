package com.equipe4.thechessgame.RequestsUtils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Alexis on 2016-10-11.
 *
 * Classe singleton se chargeant d'envoyer des requêtes au serveur. Wrapper pour Requestqueue
 */
public class RequestSender {

    //singleton instance
    private static RequestSender ourInstance;
    public static RequestSender getInstance() {
        return ourInstance;
    }
    public static void createInstance(Context context){
        ourInstance = new RequestSender(context);
    }

    private RequestQueue rqueue;

    private RequestSender(Context context) {
        rqueue = Volley.newRequestQueue(context);
    }
    public void addRequest(Request req){
        rqueue.add(req);
    }

}
