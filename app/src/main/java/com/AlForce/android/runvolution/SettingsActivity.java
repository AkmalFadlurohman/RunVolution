package com.AlForce.android.runvolution;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

import java.util.List;

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
 */
public class SettingsActivity extends AppCompatPreferenceActivity {




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
     *
     */
    /*private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        SharedPreferences preferences = getSharedPreferences(getString(R.string.sharedpref_file), MODE_PRIVATE);
        //sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, preferences.getString(preference.getKey(),""));
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onHeaderClick(Header header, int position) {
        if(header.fragmentArguments == null)
        {
            header.fragmentArguments = new Bundle();
        }
        SharedPreferences preferences = getSharedPreferences(getString(R.string.sharedpref_file), MODE_PRIVATE);
        String name = preferences.getString("name","John Doe");
        String email = preferences.getString("email", "johndoe@email.com");
        header.fragmentArguments.putString("name", name);
        header.fragmentArguments.putString("email", email);
        super.onHeaderClick(header, position);
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
                || AccountPreferenceFragment.class.getName().equals(fragmentName)
                || PetPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AccountPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_account);
            setHasOptionsMenu(true);

            SharedPreferences preferences = AccountPreferenceFragment.this.getActivity().getSharedPreferences(getString(R.string.sharedpref_file), MODE_PRIVATE);
            String name = preferences.getString("name", "John Doe");
            String email = preferences.getString("email", "johndoe@example.com");

            Preference accountName = findPreference("account_name");
            Preference accountEmail = findPreference("account_email");

            accountName.setSummary(name);
            accountEmail.setSummary(email);

            Preference signoutPref = findPreference("action_signout");
            signoutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SharedPreferences preferences = AccountPreferenceFragment.this.getActivity().getSharedPreferences(getString(R.string.sharedpref_file), MODE_PRIVATE);
                    preferences.edit().remove("logged").apply();
                    preferences.edit().remove("name").apply();
                    preferences.edit().remove("email").apply();
                    Intent signoutIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(signoutIntent);
                    return true;
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class PetPreferenceFragment extends PreferenceFragment  {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Preference.OnPreferenceChangeListener bindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    String stringValue = value.toString();
                    SharedPreferences preferences = PetPreferenceFragment.this.getActivity().getSharedPreferences(getString(R.string.sharedpref_file), MODE_PRIVATE);
                    preferences.edit().putString(preference.getKey(),stringValue).apply();
                    preference.setSummary(stringValue);
                    return true;
                }
            };
            addPreferencesFromResource(R.xml.pref_pet);
            setHasOptionsMenu(true);

            SharedPreferences preferences = PetPreferenceFragment.this.getActivity().getSharedPreferences(getString(R.string.sharedpref_file), MODE_PRIVATE);
            String petName = preferences.getString("petName","Bobby");

            Preference petNamePref = findPreference("petName");
            petNamePref.setSummary(petName);
            petNamePref.setOnPreferenceChangeListener(bindPreferenceSummaryToValueListener);
            bindPreferenceSummaryToValueListener.onPreferenceChange(petNamePref, preferences.getString("petName",""));
            /*petNamePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    petNamePref.setSummary(petNamePref.get);
                }
            }*/
        }
        public Boolean isPetNameValid(String petName) {
            return true;
        }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
