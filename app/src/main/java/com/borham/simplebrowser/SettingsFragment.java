package com.borham.simplebrowser;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.borham.simplebrowser.MainActivity.editorSettings;
import static com.borham.simplebrowser.MainActivity.prefSettings;
import static com.borham.simplebrowser.SBrowserHelper.Arabic_Code;
import static com.borham.simplebrowser.SBrowserHelper.SBrowserAppLinkOnStore;
import static com.borham.simplebrowser.SBrowserHelper.openWebLink;
import static com.borham.simplebrowser.SBrowserHelper.pixelsToSp;

public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private final String[] SearchEnginesLinks = {"https://www.google.ru/search?q=", "https://www.bing.com/search?q=", "https://search.yahoo.com/search?q=", "https://duckduckgo.com/?q=", "https://yandex.com/search/?text=", "https://search.aol.com/aol/search?q="};
    private Dialog dialog = null;
    private CircleImageView facebookCircleImageView, twitterCircleImageView, whatsappCircleImageView = null;
    private LinearLayout layout = null;
    private Spinner SearchEngineSpinner, LanguageSpinner = null;
    private SeekBar FontSizeSeekBar = null;
    private Switch ModeSwitch = null;
    private TextView AboutAppTxt, ShareAppTxt, general, SearchEngineTxt, LanguageTxt, style, FontSizeTxt, AppModeTxt, OurApp = null;
    private float CurrentTxtViewSize = 0;
    private String AboutAppDialogColor = "";
    private int visibility = View.VISIBLE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SBrowserHelper.current_nav_id = R.id.settings_btn;
        if (prefSettings.getBoolean("LastSelectedMode", false)) {
            AboutAppDialogColor = "#A8810B";
        } else {
            AboutAppDialogColor = "#ECB923";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View SettingView = inflater.inflate(R.layout.fragment_settings, container, false);
        initializeView(SettingView);
        setConfigs();
        return SettingView;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.SearchEngineSpinner) {
            editorSettings.putInt("SearchEngineIndex", position);
            editorSettings.putString("SearchEngineLink", SearchEnginesLinks[position]);
            editorSettings.apply();
        } else if (parent.getId() == R.id.LanguageSpinner) {
            editorSettings.putInt("LanguageIndex", position).apply();
            ((MainActivity) getContext()).finish();
            startActivity(((MainActivity) getContext()).getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar.getProgress() == seekBar.getMin()) {
            editorSettings.putFloat("FontSize", -5f);
            Toast.makeText(getContext(), getString(R.string.FontSizeChangedToSmall), Toast.LENGTH_SHORT).show();
        } else if (seekBar.getProgress() == seekBar.getMax()) {
            editorSettings.putFloat("FontSize", 5f);
            Toast.makeText(getContext(), getString(R.string.FontSizeChangedToLarge), Toast.LENGTH_SHORT).show();
        } else {
            editorSettings.remove("FontSize");
            Toast.makeText(getContext(), getString(R.string.FontSizeChangedToNormal), Toast.LENGTH_SHORT).show();
        }
        editorSettings.putInt("LastSelectedFontSize", seekBar.getProgress());
        editorSettings.commit();
        ShareAppTxt.setTextSize(CurrentTxtViewSize + MainActivity.prefSettings.getFloat("FontSize", 0f));
        AboutAppTxt.setTextSize(CurrentTxtViewSize + MainActivity.prefSettings.getFloat("FontSize", 0f));
        general.setTextSize(CurrentTxtViewSize + MainActivity.prefSettings.getFloat("FontSize", 0f));
        SearchEngineTxt.setTextSize(CurrentTxtViewSize + MainActivity.prefSettings.getFloat("FontSize", 0f));
        LanguageTxt.setTextSize(CurrentTxtViewSize + MainActivity.prefSettings.getFloat("FontSize", 0f));
        style.setTextSize(CurrentTxtViewSize + MainActivity.prefSettings.getFloat("FontSize", 0f));
        FontSizeTxt.setTextSize(CurrentTxtViewSize + MainActivity.prefSettings.getFloat("FontSize", 0f));
        AppModeTxt.setTextSize(CurrentTxtViewSize + MainActivity.prefSettings.getFloat("FontSize", 0f));
        OurApp.setTextSize(CurrentTxtViewSize + MainActivity.prefSettings.getFloat("FontSize", 0f));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.AboutAppTxt) {
            dialog = new Dialog(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert);
            View viewAbout = getLayoutInflater().inflate(R.layout.about_page, null, false);
            viewAbout.setBackgroundColor(Color.parseColor(AboutAppDialogColor));
            ImageButton cancelAbout = viewAbout.findViewById(R.id.cancelAbout);
            TextView headerAbout = viewAbout.findViewById(R.id.headerAbout);
            TextView detailAbout = viewAbout.findViewById(R.id.detailAbout);
            if (Locale.getDefault().getLanguage().equals(Arabic_Code))
                headerAbout.setCompoundDrawablesWithIntrinsicBounds(null, null, headerAbout.getCompoundDrawables()[0], null);
            headerAbout.setTextSize(pixelsToSp(getContext(), headerAbout.getTextSize()) + MainActivity.prefSettings.getFloat("FontSize", 0f));
            detailAbout.setTextSize(pixelsToSp(getContext(), detailAbout.getTextSize()) + MainActivity.prefSettings.getFloat("FontSize", 0f));
            dialog.setContentView(viewAbout);
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(layout.getMeasuredWidth(), layout.getMeasuredHeight());
            dialog.setCanceledOnTouchOutside(true);
            cancelAbout.setOnClickListener(this);
        } else if (v.getId() == R.id.ShareAppTxt) {
            facebookCircleImageView.setVisibility(visibility);
            twitterCircleImageView.setVisibility(visibility);
            whatsappCircleImageView.setVisibility(visibility);
            if (visibility == View.GONE) {
                visibility = View.VISIBLE;
            } else {
                visibility = View.GONE;
            }
        } else if (v.getId() == R.id.facebookShare) {
            openWebLink(getContext(), "https://www.facebook.com/sharer/sharer.php?u=" + SBrowserAppLinkOnStore);
        } else if (v.getId() == R.id.twitterShare) {
            openWebLink(getContext(), "http://www.twitter.com/intent/tweet?url=" + SBrowserAppLinkOnStore);
        } else if (v.getId() == R.id.whatsappShare) {
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, SBrowserAppLinkOnStore);
            try {
                startActivity(whatsappIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getContext(), "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.cancelAbout) {
            dialog.dismiss();
        }
    }


    private void initializeView(View SettingView) {
        layout = SettingView.findViewById(R.id.SettingRoot);
        facebookCircleImageView = SettingView.findViewById(R.id.facebookShare);
        twitterCircleImageView = SettingView.findViewById(R.id.twitterShare);
        whatsappCircleImageView = SettingView.findViewById(R.id.whatsappShare);
        SearchEngineSpinner = SettingView.findViewById(R.id.SearchEngineSpinner);
        LanguageSpinner = SettingView.findViewById(R.id.LanguageSpinner);
        FontSizeSeekBar = SettingView.findViewById(R.id.FontSizeSeekBar);
        ModeSwitch = SettingView.findViewById(R.id.ModeSwitch);
        AboutAppTxt = SettingView.findViewById(R.id.AboutAppTxt);
        ShareAppTxt = SettingView.findViewById(R.id.ShareAppTxt);
        general = SettingView.findViewById(R.id.general);
        SearchEngineTxt = SettingView.findViewById(R.id.SearchEngineTxt);
        LanguageTxt = SettingView.findViewById(R.id.LanguageTxt);
        style = SettingView.findViewById(R.id.style);
        FontSizeTxt = SettingView.findViewById(R.id.FontSizeTxt);
        AppModeTxt = SettingView.findViewById(R.id.AppModeTxt);
        OurApp = SettingView.findViewById(R.id.OurApp);
        SearchEngineSpinner.setSelection(MainActivity.prefSettings.getInt("SearchEngineIndex", 0), false);
        LanguageSpinner.setSelection(MainActivity.prefSettings.getInt("LanguageIndex", 0), false);
        facebookCircleImageView.setOnClickListener(this);
        twitterCircleImageView.setOnClickListener(this);
        whatsappCircleImageView.setOnClickListener(this);
        AboutAppTxt.setOnClickListener(this);
        ShareAppTxt.setOnClickListener(this);
        SearchEngineSpinner.setOnItemSelectedListener(this);
        LanguageSpinner.setOnItemSelectedListener(this);
        FontSizeSeekBar.setOnSeekBarChangeListener(this);
    }

    private void setConfigs() {
        CurrentTxtViewSize = pixelsToSp(getContext(), ShareAppTxt.getTextSize());
        ShareAppTxt.setTextSize(CurrentTxtViewSize + MainActivity.prefSettings.getFloat("FontSize", 0f));
        AboutAppTxt.setTextSize(CurrentTxtViewSize + MainActivity.prefSettings.getFloat("FontSize", 0f));
        general.setTextSize(CurrentTxtViewSize + MainActivity.prefSettings.getFloat("FontSize", 0f));
        SearchEngineTxt.setTextSize(CurrentTxtViewSize + MainActivity.prefSettings.getFloat("FontSize", 0f));
        LanguageTxt.setTextSize(CurrentTxtViewSize + MainActivity.prefSettings.getFloat("FontSize", 0f));
        style.setTextSize(CurrentTxtViewSize + MainActivity.prefSettings.getFloat("FontSize", 0f));
        FontSizeTxt.setTextSize(CurrentTxtViewSize + MainActivity.prefSettings.getFloat("FontSize", 0f));
        AppModeTxt.setTextSize(CurrentTxtViewSize + MainActivity.prefSettings.getFloat("FontSize", 0f));
        OurApp.setTextSize(CurrentTxtViewSize + MainActivity.prefSettings.getFloat("FontSize", 0f));
        FontSizeSeekBar.setProgress(MainActivity.prefSettings.getInt("LastSelectedFontSize", 50));
        ModeSwitch.setChecked(prefSettings.getBoolean("LastSelectedMode", false));
        ModeSwitch.setOnCheckedChangeListener(this);
        if (Locale.getDefault().getLanguage().equals(Arabic_Code)) {
            AboutAppTxt.setCompoundDrawablesWithIntrinsicBounds(null, null, AboutAppTxt.getCompoundDrawables()[0], null);
            ShareAppTxt.setCompoundDrawablesWithIntrinsicBounds(null, null, ShareAppTxt.getCompoundDrawables()[0], null);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        editorSettings.putBoolean("LastSelectedMode", isChecked);
        editorSettings.apply();
        if (isChecked) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    public void onResume() {
        super.onResume();
        SBrowserHelper.setCustomActionBar((MainActivity) getContext(), R.string.SettingsActionbar, R.drawable.settings_icon);
    }

}