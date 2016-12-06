package com.equipe4.thechessgame.Settings;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.equipe4.thechessgame.R;

/**
 * Created by Alexis on 2016-11-23.
 */

public class TimeConfigSlideFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.time_keeping_config_dialog_fragment, container, false);

        TextView text = ((TextView)rootView.getChildAt(0));
        text.setText(getArguments().getString(TimeConfigSlidePagerAdapter.FRAG_MESSAGE));

        TextView unit = ((TextView)rootView.getChildAt(1));
        unit.setText(getArguments().getString(TimeConfigSlidePagerAdapter.FRAG_UNIT));

        NumberPicker picker = ((NumberPicker)rootView.getChildAt(2));
        picker.setMinValue(0);
        picker.setMaxValue(getArguments().getInt(TimeConfigSlidePagerAdapter.FRAG_MAX_VALUE));
        picker.setValue(getArguments().getInt(TimeConfigSlidePagerAdapter.FRAG_CURRENT_VALUE));

        return rootView;
    }
}
