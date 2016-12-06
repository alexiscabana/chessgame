package com.equipe4.thechessgame.Settings;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.NumberPicker;

import com.equipe4.thechessgame.Configuration.GameConfiguration;
import com.equipe4.thechessgame.R;


public class TimeKeepingConfigActivity extends Activity {

    public static final String ALL_PARAMS_MESSAGES = "param";
    public static final String ALL_MAXES_MESSAGES = "max";
    public static final String ALL_UNITS_MESSAGES = "unit";
    public static int[] params_max = { 180, 300, 150, 180, 300 };
    String[] params_mess;
    String[] params_unit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        params_mess = new String[]{
                getString(R.string.total_time_each_player_mess),
                getString(R.string.max_time_each_turn_mess),
                getString(R.string.turn_limit_each_player_mess),
                getString(R.string.total_overtime_each_player_mess),
                getString(R.string.total_overtime_each_turn_player_mess),
        };

        params_unit = new String[]{
                getString(R.string.minutes_units),
                getString(R.string.seconds_unit),
                getString(R.string.turn_unit),
                getString(R.string.minutes_units),
                getString(R.string.seconds_unit)
        };

        TimeConfigDialogFragmentWindow w = new TimeConfigDialogFragmentWindow();

        Bundle b = new Bundle();
        b.putStringArray(ALL_PARAMS_MESSAGES, params_mess);
        b.putIntArray(ALL_MAXES_MESSAGES, params_max);
        b.putStringArray(ALL_UNITS_MESSAGES, params_unit);

        w.setArguments(b);
        w.show(getFragmentManager(),"");
    }

    public static class TimeConfigDialogFragmentWindow extends DialogFragment {

        Activity act;
        CustomViewPager mPager;
        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            act = activity;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.time_keeping_config, container);

            //get the current parameters
            GameConfiguration.TimerFormat current =
                    (GameConfiguration.TimerFormat) GameConfiguration.getInstance().
                            getParam(GameConfiguration.Parameter.timerFormat);

            //set the numpickers depending on the vurrent parameters
            mPager = (CustomViewPager) view.findViewById(R.id.pager);
            TimeConfigSlidePagerAdapter mPagerAdapter = new TimeConfigSlidePagerAdapter(
                    getChildFragmentManager(),
                    getArguments().getStringArray(ALL_PARAMS_MESSAGES),
                    getArguments().getIntArray(ALL_MAXES_MESSAGES),
                    getArguments().getStringArray(ALL_UNITS_MESSAGES),
                    current.toIntArray());
            mPager.storeAdapter(mPagerAdapter);
            mPager.setOffscreenPageLimit(current.toIntArray().length);

            if(getDialog().getWindow() != null)
                getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            return view;
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            CustomViewPager v = (CustomViewPager) getView();
            if(v == null || v.getAdapter() == null) return;

            TimeConfigSlidePagerAdapter adapter = (TimeConfigSlidePagerAdapter) v.getAdapter();
            int[] timerFormatArgs = new int[ adapter.getCount() ];
            for(int i = 0; i < adapter.getCount(); i++){
                timerFormatArgs[i] = ((NumberPicker)adapter.getItem(i).getView().findViewById(R.id.picker)).getValue();
            }
            GameConfiguration.getInstance().setParam(GameConfiguration.Parameter.timerFormat,
                    new GameConfiguration.TimerFormat(
                            timerFormatArgs[0],
                            timerFormatArgs[1],
                            timerFormatArgs[2],
                            timerFormatArgs[3],
                            timerFormatArgs[4]));
            super.onDismiss(dialog);
            act.finish();
        }
    }
}
