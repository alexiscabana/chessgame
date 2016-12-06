package com.equipe4.thechessgame.GameManager;

/**
 * The handler interface for handling success or fails from the model. As the GameManager uses
 * asynchroneous responses, this interface is used to standardize what should be done in the event
 * of a failed com.equipe4.androidappinf3995_04.request and in the event of success.
 *
 * Created by Alexis on 2016-10-17.
 */

public abstract class GameManagerResponseListener {
    public void onRequestSuccess(){}
    public void onRequestError(String mess){}
}
