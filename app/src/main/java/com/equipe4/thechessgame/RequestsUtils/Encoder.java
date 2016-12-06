package com.equipe4.thechessgame.RequestsUtils;

import android.util.Base64;

/**
 * Utils class used when for encoding a String.
 *
 * Created by Alexis on 2016-10-31.
 */

public class Encoder {

    public static String getCodeBase64(String s){
        return Base64.encodeToString(s.getBytes(), Base64.DEFAULT);
    }
}
