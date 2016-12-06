package com.equipe4.thechessgame.Settings;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.equipe4.thechessgame.ChessboardUtils.Piece;
import com.equipe4.thechessgame.ChessboardUtils.PieceKey;
import com.equipe4.thechessgame.Configuration.ApparenceConfiguration;
import com.equipe4.thechessgame.Configuration.GameConfiguration;
import com.equipe4.thechessgame.DialogUtils.ImageChooserDialog;
import com.equipe4.thechessgame.GameManager.Player;
import com.equipe4.thechessgame.R;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 *
 * This UI is highly coupled and needs a big revamp
 */
public class SettingsActivity extends PreferenceActivity {

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof SwitchPreference) {
                //Set the new value in the GameConfig
                //Maybe we should map a key to a parameter?
                switch (preference.getKey()) {
                    case ("decompteDuTemps_switch"):
                        GameConfiguration.getInstance().setParam(GameConfiguration.Parameter.isTimerFormatEnabled, value);
                        break;
                    case ("twoTabletsMode_switch"):
                        GameConfiguration.getInstance().setParam(GameConfiguration.Parameter.isTwoTabletsMode, value);
                        break;
                    case ("enpassantMode_switch"):
                        GameConfiguration.getInstance().setParam(GameConfiguration.Parameter.isEnPassantMode, value);
                        break;
                }
            } else {
                preference.setSummary(stringValue);

                //Set the new value in the GameConfig
                //Maybe we should map a key to a parameter?
                switch (preference.getKey()) {
                    case ("nom_joueur_key"):
                        GameConfiguration.getInstance().setParam(GameConfiguration.Parameter.nomJoueur1, value);
                        break;
                    case ("nom_joueur_2_key"):
                        GameConfiguration.getInstance().setParam(GameConfiguration.Parameter.nomJoueur2, value);
                        break;
                    case ("ronde_key"):
                        GameConfiguration.getInstance().setParam(GameConfiguration.Parameter.rondeCourante, value);
                        break;
                    case ("mot_de_passe_key"):
                        GameConfiguration.getInstance().setParam(GameConfiguration.Parameter.secretCode, value);
                        break;
                    case ("lieu_partie_key"):
                        GameConfiguration.getInstance().setParam(GameConfiguration.Parameter.locationOfGame, value);
                        break;
                }
            }

            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        if(preference instanceof SwitchPreference){
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager
                    .getDefaultSharedPreferences(preference.getContext()).getBoolean(preference.getKey(),false));
        } else {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || ApparencePreferenceFragment.class.getName().equals(fragmentName)
                || AProposPreferenceFragment.class.getName().equals(fragmentName);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("nom_joueur_key"));
            bindPreferenceSummaryToValue(findPreference("nom_joueur_2_key"));
            bindPreferenceSummaryToValue(findPreference("ronde_key"));
            bindPreferenceSummaryToValue(findPreference("mot_de_passe_key"));
            bindPreferenceSummaryToValue(findPreference("lieu_partie_key"));
            bindPreferenceSummaryToValue(findPreference("twoTabletsMode_switch"));
            bindPreferenceSummaryToValue(findPreference("enpassantMode_switch"));
            findPreference("decompte_temp_pref").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    openTimeKeepingConfigDialog(preference.getContext());
                    return true;
                }
            });
        }

        private void openTimeKeepingConfigDialog(Context context) {
            Intent intent = new Intent(context, TimeKeepingConfigActivity.class);
            startActivity(intent);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ApparencePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_apparence);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            findPreference("style_piece").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showPiecesLookConfigDialog(getActivity());
                    return true;
                }
            });

            findPreference("style_chess").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showChessLookConfigDialog(getActivity());
                    return true;
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().finish();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        private void showChessLookConfigDialog(Context context) {
            Map<Bitmap, Object> ret = new HashMap<>();
            for (ApparenceConfiguration.BoardTag tag : ApparenceConfiguration.BoardTag.values())
                ret.put((Bitmap) ApparenceConfiguration.getInstance().
                        getCachedParam(ApparenceConfiguration.Params.chessboard_bitmap_param, tag), tag);

            AlertDialog dialog = new ImageChooserDialog(context, ret,
                    new ImageChooserDialog.OnImageClickListener() {
                        @Override
                        public void onImageClickListener(Object tag) {
                            ApparenceConfiguration.getInstance().setGroupAsCurrentParams(tag);
                        }
                    });
            dialog.setTitle(R.string.config_chessboard_look);
            dialog.show();
        }

        private static void showPiecesLookConfigDialog(Context context) {
            Map<Bitmap, Object> ret = new HashMap<>();
            for (ApparenceConfiguration.PieceTag tag : ApparenceConfiguration.PieceTag.values())
                ret.put((Bitmap) ApparenceConfiguration.getInstance().
                        getCachedParam(new PieceKey(Piece.PieceType.king, Player.PlayerType.WHITE), tag), tag);

            AlertDialog dialog = new ImageChooserDialog(context, ret,
                    new ImageChooserDialog.OnImageClickListener() {
                        @Override
                        public void onImageClickListener(Object tag) {
                            ApparenceConfiguration.getInstance().setGroupAsCurrentParams(tag);
                        }
                    });
            dialog.setTitle(R.string.pieces_look_config);
            dialog.show();
        }
    }

      @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AProposPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_apropos);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().finish();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
