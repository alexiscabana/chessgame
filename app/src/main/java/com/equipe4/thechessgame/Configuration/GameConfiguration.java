package com.equipe4.thechessgame.Configuration;

import com.equipe4.thechessgame.RequestsUtils.Encoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing the app's internal configuration of the game. It is a singleton class
 *
 * Created by Alexis on 2016-10-02.
 */

public class GameConfiguration{
    //singleton instance
    private static GameConfiguration instance = null;
    //Part of the Observer Design Pattern
    private List<GameConfigChangedHandler> gcChangedListeners = new ArrayList<>();
    //Container for the parameters
    private Map<Parameter, Object> params = new HashMap<>();

    private GameConfiguration() {
        //default config, ensures there's not null values hiding
        params.put(Parameter.serverIP,"40");
        params.put(Parameter.treadPollingInterval, 3000);
        params.put(Parameter.nomJoueur1, "");
        params.put(Parameter.nomJoueur2, "");
        params.put(Parameter.rondeCourante, "");
        params.put(Parameter.locationOfGame, "");
        params.put(Parameter.secretCode, "");
        params.put(Parameter.isTwoTabletsMode, false);
        params.put(Parameter.isEnPassantMode, false);
        params.put(Parameter.isTimerFormatEnabled, false);
        params.put(Parameter.timerFormat, new TimerFormat(120, 30, 0, 0, 0));
    }
    private void notifyConfigChangedListeners(GameConfigChangedEvent event){
        for(int i = 0; i < gcChangedListeners.size(); i++)
            gcChangedListeners.get(i).onGameConfigChange(event);
    }
    public static GameConfiguration getInstance() {
        if (instance == null) {
            instance = new GameConfiguration();
        }
        return instance;
    }
    @Override
    public String toString(){
        if(params.isEmpty())
            return "";
        String NEW_LINE = System.getProperty("line.separator");
        String sout = "";

        sout += "Nom du Joueur 1 : " + getParam(Parameter.nomJoueur1).toString() + NEW_LINE;
        sout += "Nom du Joueur 2 : " + getParam(Parameter.nomJoueur2).toString() + NEW_LINE;
        sout += NEW_LINE;
        sout += "Lieu où la partie se joue : " + getParam(Parameter.locationOfGame).toString() + NEW_LINE;
        sout += "Ronde courante : " + getParam(Parameter.rondeCourante).toString() + NEW_LINE;
        sout += NEW_LINE;
        sout += "Décompte du temps : ";

        sout += NEW_LINE;
        sout += getParam(Parameter.timerFormat).toString();
        sout += NEW_LINE;

        sout += NEW_LINE;
        Boolean ttactive = (Boolean)getParam(Parameter.isTwoTabletsMode);
        sout += "Mode 2 tablettes " + (ttactive ? "activé" : "désactivé") + NEW_LINE ;
        if(ttactive){
            sout += "MOT DE PASSE DE LA PARTIE : " + NEW_LINE ;
            sout += getParam(Parameter.secretCode) + NEW_LINE;
        }

        sout += NEW_LINE;
        sout += "\"En passant\" " + ((Boolean)getParam(Parameter.isEnPassantMode) ? "activé" : "désactivé") + NEW_LINE ;
        sout += NEW_LINE;

        return sout;
    }
    public JSONObject toJSONObject() throws JSONException {
        JSONObject main = new JSONObject();
        JSONObject timerformatJSON = new JSONObject();
        TimerFormat tf;
        tf = (TimerFormat)getParam(GameConfiguration.Parameter.timerFormat);

        timerformatJSON.put("time", tf.tempsTotalChaqueJoueurMin);
        timerformatJSON.put("increment", tf.incrementSec);
        timerformatJSON.put("limit", tf.limit);
        timerformatJSON.put("overtime", tf.overtimeMin);
        timerformatJSON.put("overtimeIncrement", tf.overtimeIncrementSec);

        String p1,p2,round,location,secretcode,twoTablet, enpassant;
        p1 = (String)getParam(Parameter.nomJoueur1);
        p2 = (String)getParam(Parameter.nomJoueur2);
        round = (String)getParam(Parameter.rondeCourante);
        location = (String)getParam(Parameter.locationOfGame);
        secretcode = (String)getParam(Parameter.secretCode);

        twoTablet = (Boolean)getParam(Parameter.isTwoTabletsMode) ? "yes" : "no";
        enpassant = (Boolean)getParam(Parameter.isEnPassantMode)  ? "yes" : "no";

        main.put("player1", p1); // nom du joueur 1 (jouant avec les blancs)
        main.put("player2", p2); // nom du joueur 2 (jouant avec les noirs)
        main.put("round", round); // utile pratiquement uniquement en tournoi
        main.put("location", location); // endroit de la tenue de la partie
        main.put("secret_code", Encoder.getCodeBase64(secretcode)); // pour un peu de sécurité. Encodé en base64
        main.put("twoTablet", twoTablet); // si une autre tablette est utilisée par le joueur 2
        main.put("enPassant", enpassant); // en passant permis ou pas
        main.put("timerFormat", timerformatJSON);

        return main;
    }
    public Object getParam(Parameter paramName){
        if(paramName != null)
            return params.get(paramName);
        return null;
    }
    public void setParam(Parameter paramName, Object newV){
        if(paramName != null ) {
            Object oldV = params.put(paramName, newV);
            if(!newV.equals(oldV))
                notifyConfigChangedListeners(new GameConfigChangedEvent(paramName, oldV, newV)); //notify only if the change is relevant
        }
    }
    public boolean attachConfigChangedListener(GameConfigChangedHandler list){
        if(!gcChangedListeners.contains(list)) {
            gcChangedListeners.add(list);
            return true;
        }
        return false;
    }
    public boolean dettachConfigChangedListener(GameConfigChangedHandler list){
        return gcChangedListeners.remove(list);
    }
    public String getServerURL(){
        return "http://132.207.89." + getParam(Parameter.serverIP).toString();
    }

    /*
     * Nested Classes for use only in the GameConfig context
     */
    public static class TimerFormat {
        public Integer
                tempsTotalChaqueJoueurMin = 0,
                incrementSec = 0,
                limit = 0,
                overtimeMin = 0,
                overtimeIncrementSec = 0;
        public TimerFormat(Integer tempsTotalChaqueJoueur,
                           Integer increment,
                           Integer limit,
                           Integer overtime,
                           Integer overtimeIncrement){
            this.tempsTotalChaqueJoueurMin = tempsTotalChaqueJoueur;
            this.incrementSec = increment;
            this.limit = limit;
            this.overtimeMin = overtime;
            this.overtimeIncrementSec = overtimeIncrement;
        }

        @Override
        public String toString() {
            String NEW_LINE = System.getProperty("line.separator");
            String sout = "";

            if(tempsTotalChaqueJoueurMin > 0){
                sout += "Temps total maximum de chaque joueur : " + tempsTotalChaqueJoueurMin + " minutes" + NEW_LINE;
            } else {
                sout += "Aucune limite de temps pour les joueurs" + NEW_LINE;
                return sout; //no more to show
            }

            if(incrementSec > 0){
                sout += "Délai par coup : " + tempsTotalChaqueJoueurMin + " secondes" + NEW_LINE;
                sout += "Limite de coup par joueur : " + tempsTotalChaqueJoueurMin + " coups" + NEW_LINE;
            } else {
                sout += "Aucune limite de temps par coup" + NEW_LINE;
            }

            if(overtimeMin > 0) {
                sout += "Temps total maximum de la période de prolongation : " + tempsTotalChaqueJoueurMin + " minutes" + NEW_LINE;
                sout += "Délai par coup pendant la prolongation : " + tempsTotalChaqueJoueurMin + NEW_LINE;
            } else {
                sout += "Aucune prolongation accordée" + NEW_LINE;
            }
            return sout;
        }

        public int[] toIntArray() {
            return new int[]
                {
                    tempsTotalChaqueJoueurMin,
                    incrementSec,
                    limit,
                    overtimeMin,
                    overtimeIncrementSec
                };
        }
    }
    public static class GameConfigChangedEvent{
        private Parameter paramKey;
        private Object oldValue, newValue;
        public GameConfigChangedEvent(Parameter param, Object oldValue, Object newValue){
            this.paramKey = param;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }
        public Parameter getChangedParamKey() {
            return paramKey;
        }
        public Object getOldValueParam() {
            return oldValue;
        }
        public Object getNewValueParam() {
            return newValue;
        }
    }
    public interface GameConfigChangedHandler {
        void onGameConfigChange(GameConfigChangedEvent event);
    }
    //Parameter keys
    public enum Parameter {
        serverIP,
        treadPollingInterval,
        nomJoueur1,
        nomJoueur2,
        rondeCourante,
        locationOfGame,
        secretCode,
        isTwoTabletsMode,
        isEnPassantMode,
        isTimerFormatEnabled,
        timerFormat
    }
}
