package com.blueberry.xbag.ui.fragment.setting;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.View;

import com.blueberry.xbag.MyApplication;
import com.blueberry.xbag.R;
import com.blueberry.xbag.support.widget.RadioGroupPreference;
import com.blueberry.xbag.ui.activity.RecordFileActivity;


/**
 * @author lhq
 *         created at 2015/12/5 15:36
 */
public class SettingFragment extends BasePreferenceFragment implements OnPreferenceClickListener {

    private static final int RINGTONE_REQUEST_CODE = 0x123;

    private SwitchPreference mPreferenceNotDisturb;
    private Preference mPreferenceChooseSound;
    private Preference mPreferenceRecordHistory;
    private Preference mPreferenceVersion;
    private RadioGroupPreference mPreferenceDoubleClick;


    public static SettingFragment newInatance() {
        return new SettingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_setting);
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences preferences = getPreferenceScreen().getSharedPreferences();
        mPreferenceNotDisturb.setChecked(preferences.getBoolean(getString(R.string.preference_donnot_disturb), false));
        mPreferenceDoubleClick.setSummary(preferences.getString(getString(R.string.preference_double_click), "拍照"));
        mPreferenceChooseSound.setSummary(MyApplication.applicationContext.getSharedPreferences("Notifications", Activity.MODE_PRIVATE).getString("GlobalSound", "NoSound"));
    }

    @TargetApi(23)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPreferenceNotDisturb = findPreference(getString(R.string.preference_donnot_disturb));
        mPreferenceChooseSound = findPreference(getString(R.string.preference_choose_sound));
        mPreferenceRecordHistory = findPreference(getString(R.string.preference_record_history));
        mPreferenceVersion = findPreference(getString(R.string.preference_version));
        mPreferenceDoubleClick = findPreference(getString(R.string.preference_double_click));

        mPreferenceNotDisturb.setOnPreferenceClickListener(this);
        mPreferenceChooseSound.setOnPreferenceClickListener(this);
        mPreferenceRecordHistory.setOnPreferenceClickListener(this);
        mPreferenceVersion.setOnPreferenceClickListener(this);

        mPreferenceDoubleClick.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mPreferenceDoubleClick.setSummary(newValue.toString());
                getPreferenceScreen().getSharedPreferences().edit().putString(getString(R.string.preference_double_click), newValue.toString()).commit();
                return false;
            }
        });
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference == mPreferenceNotDisturb) {

        } else if (preference == mPreferenceChooseSound) {
            try {
                SharedPreferences preferences = MyApplication.applicationContext.getSharedPreferences("Notifications", Activity.MODE_PRIVATE);
                Intent tmpIntent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                tmpIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                tmpIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                tmpIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                Uri currentSound = null;

                String defaultPath = null;
                Uri defaultUri = Settings.System.DEFAULT_NOTIFICATION_URI;
                if (defaultUri != null) {
                    defaultPath = defaultUri.getPath();
                }

                String path = preferences.getString("GlobalSoundPath", defaultPath);
                if (path != null && !path.equals("NoSound")) {
                    if (path.equals(defaultPath)) {
                        currentSound = defaultUri;
                    } else {
                        currentSound = Uri.parse(path);
                    }
                }

                tmpIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentSound);
                startActivityForResult(tmpIntent, RINGTONE_REQUEST_CODE);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (preference == mPreferenceRecordHistory) {
            startActivity(new Intent(getActivity(), RecordFileActivity.class));
            getActivity().overridePendingTransition(0, 0);

        } else if (preference == mPreferenceVersion) {

        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            String name = null;
            if (ringtone != null) {
                Ringtone rng = RingtoneManager.getRingtone(getActivity(), ringtone);
                if (rng != null) {
                    if (ringtone.equals(Settings.System.DEFAULT_NOTIFICATION_URI)) {
                        name = getActivity().getString(R.string.SoundDefault);
                    } else {
                        name = rng.getTitle(getActivity());
                    }
                    rng.stop();
                }
            }

            SharedPreferences preferences = MyApplication.applicationContext.getSharedPreferences("Notifications", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            if (requestCode == RINGTONE_REQUEST_CODE) {
                if (name != null && ringtone != null) {
                    editor.putString("GlobalSound", name);
                    editor.putString("GlobalSoundPath", ringtone.toString());
                } else {
                    editor.putString("GlobalSound", "NoSound");
                    editor.putString("GlobalSoundPath", "NoSound");
                }
            }
            editor.commit();
        }
    }

}
