package com.equipe4.thechessgame.GameManager;


import java.sql.Time;
import java.util.Date;

/**
 * Created by Alexis on 2016-10-11.
 *
 * Class utilisée pour représenter un Joueur
 */
public class Player {

    private static Player j1 = new Player(PlayerType.WHITE,0, 1),
                            j2 = new Player(PlayerType.BLACK, 0, 2);
    public static Player getInstancePlayerWhite(){ return j1; }
    public static Player getInstancePlayerBlack(){ return j2; }
    public static Player getInstanceFromNumber(int num){
        if(num != 1 && num != 2)
            return null;
        return num == 1 ? j1 : j2;
    }

    private PlayerType type;
    private int numeroJoueur;
    private PlayerTime pTime;

    private Player(PlayerType type, int nbSecondesAuCompteur, int numeroJoueur){
        this.type = type;
        this.pTime = new PlayerTime();
        this.numeroJoueur = numeroJoueur;
    }
    public final PlayerType getType() {
        return type;
    }
    public Integer getNumeroJoueur(){
        return numeroJoueur;
    }
    public PlayerTime getPlayerTime(){
        return pTime;
    }

    public enum PlayerType {
        WHITE,
        BLACK
    }
    //Container class for the time_config_screen_layout of a Player
    public class PlayerTime{
        Date timeSecondRemain = new Time(0),
            overtimeSecRemain = new Time(0);
        public Date getTimeRemaining(){
            return timeSecondRemain;
        }
        public Date getOvertimeRemaining(){
            return overtimeSecRemain;
        }
        public void setTimeRemainingSeconds(Integer nbSec){
            this.timeSecondRemain.setTime(nbSec * 1000);
        }
        public void setOvertimeRemainingSeconds(Integer nbSec){
            this.overtimeSecRemain.setTime(nbSec * 1000);
        }
    }
}
