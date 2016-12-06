package com.equipe4.thechessgame.Settings;

import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

/**
 * Created by Alexis on 2016-11-23.
 */

public class TimeConfigSlidePagerAdapter extends FragmentStatePagerAdapter {

    String[] params_mess;
    int[] params_max, params_current_value;
    String[] params_unit;
    TimeConfigSlideFragment[] slides;

    public static final String FRAG_MESSAGE = "mess";
    public static final String FRAG_MAX_VALUE = "max";
    public static final String FRAG_UNIT = "unit";
    public static final String FRAG_CURRENT_VALUE = "current";



    public TimeConfigSlidePagerAdapter(FragmentManager fm,
                                       String[] params_mess,
                                       int[] params_max,
                                       String[] params_unit,
                                       int[] params_current_value) {
        super(fm);
        this.params_mess = params_mess;
        this.params_max = params_max;
        this.params_unit = params_unit;
        this.params_current_value = params_current_value;

        this.slides = new TimeConfigSlideFragment[params_mess.length];
        for(int i = 0; i < params_mess.length; i++){
            slides[i] = new TimeConfigSlideFragment();

            Bundle b = new Bundle();
            b.putString(FRAG_MESSAGE, params_mess[i]);
            b.putInt(FRAG_MAX_VALUE, params_max[i]);
            b.putString(FRAG_UNIT, params_unit[i]);
            b.putInt(FRAG_CURRENT_VALUE, params_current_value[i]);
            slides[i].setArguments(b);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return slides[position];
    }

    @Override
    public int getCount() {
        return slides.length;
    }
}
