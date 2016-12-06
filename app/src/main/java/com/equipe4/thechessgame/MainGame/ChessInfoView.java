package com.equipe4.thechessgame.MainGame;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.equipe4.thechessgame.R;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Alexis on 2016-11-22.
 */

public class ChessInfoView extends RelativeLayout {

    public static final int NOT_YOUR_TURN = 0;
    public static final int YOUR_TURN = 1;
    public static final int TURN_1ST_PLAYER = 2;
    public static final int TURN_2ND_PLAYER = 3;
    public static final int NORMAL_TIME = 4;
    public static final int OVERTIME = 5;
    protected Date remainingTime;
    protected volatile boolean isInCountdown = false;
    protected int currentCountdownState = NORMAL_TIME;


    public ChessInfoView(Context context) {
        this(context, null, 0);
    }
    public ChessInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public ChessInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        remainingTime = new Time(0);
        inflate(getContext(), R.layout.chess_info_layout, this);
    }

    private void showRemainingTimeForPlayer(Date t){
        if(t == null) return;
        StringBuilder sb = new StringBuilder();
        switch (currentCountdownState){
            case(OVERTIME): sb.append("PROLONGATION : "); break;
            default:
        }
        SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        sb.append(f.format(t));
        TextView timeText = (TextView) findViewById(R.id.info_time_player);
        timeText.setText(sb.toString());
        timeText.invalidate();
    }

    public void setTimeTextVisibilty(int visibility){
        findViewById(R.id.info_time_player).setVisibility(visibility);
        invalidate();
    }
    public void setRemainingTime(int id, Date t){
        this.remainingTime = t;
        this.currentCountdownState = id;
        showRemainingTimeForPlayer(t);
    }
    public void showHeaderState(int id){
        String h = "";
        switch (id){
            case(YOUR_TURN):
                h = "C'est à votre tour";
                break;
            case(NOT_YOUR_TURN):
                h = "Veuillez attendre votre tour";
                break;
            case(TURN_1ST_PLAYER):
                h = "C'est au tour au joueur blanc";
                break;
            case(TURN_2ND_PLAYER):
                h = "C'est au tour au joueur noir";
            default:
        }
        ((TextView) findViewById(R.id.info_player_playing)).setText(h);
        invalidate();
    }
    public void resumeCountdown(){
        if(isInCountdown)
            return;
        isInCountdown = true;
        final Activity activity = (Activity)getContext();
        new Thread(new Runnable() {
            @Override
            public void run() {
            while(isInCountdown){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                remainingTime.setTime(Math.max(remainingTime.getTime() - 1000, 0));
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showRemainingTimeForPlayer(remainingTime);
                    }
                });

                if(remainingTime.getTime() <= 0) isInCountdown = false;
            }
            }
        }).start();
    }
    public void stopCountdown(){
        isInCountdown = false;
    }
}
