package com.borham.simplebrowser;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.borham.simplebrowser.MyRoom.BrowseHistoryDao;
import com.borham.simplebrowser.MyRoom.BrowseHistoryDatabase;
import com.borham.simplebrowser.MyRoom.FavouriteDao;
import com.borham.simplebrowser.MyRoom.FavouriteDatabase;
import com.borham.simplebrowser.MyRoom.SearchHistoryDao;
import com.borham.simplebrowser.MyRoom.SearchHistoryDatabase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import static com.borham.simplebrowser.SBrowserHelper.Arabic_Code;
import static com.borham.simplebrowser.SBrowserHelper.English_Code;
import static com.borham.simplebrowser.SBrowserHelper.current_nav_id;
import static com.borham.simplebrowser.SBrowserHelper.getEnglishStringsArray;
import static com.borham.simplebrowser.SBrowserHelper.pushNotification;
import static com.borham.simplebrowser.SBrowserHelper.setLocaleForApp;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    public static String[] WebsitesNames, AppsNames, EnglishWebsitesNames, EnglishAppsNames, EnglishCategoryWebsites = null;
    public static SharedPreferences preferences, prefMostVisitedSites, prefMostVisitedSitesLinks, prefMostVisitedSitesIcons, prefSettings = null;
    public static SharedPreferences.Editor editor, editor1, editor2, editor3, editorSettings = null;
    public static BrowseHistoryDatabase bhdatabase = null;
    public static BrowseHistoryDao bhdao = null;
    public static FavouriteDatabase fdatabase = null;
    public static FavouriteDao fdao = null;
    public static SearchHistoryDatabase shdatabase = null;
    public static SearchHistoryDao shdao = null;
    public static float fontSize = 0;
    public BottomNavigationView navigationView = null;
    private RelativeLayout MainRoot = null;
    private FrameLayout container_fragments = null;
    private int layoutDirection = 0, textDirection = 0;
    private String NavColor = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeMySharedPreferences();
        initializeMyDatabases();
        showNotification();
        setConfigs();
        setContentView(R.layout.activity_main);
        initializeMainComponentsViews();
        applyConfigs();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.home_btn:
                fragment = new HomeFragment();
                break;
            case R.id.history_btn:
                fragment = new HistoryFragment();
                break;
            case R.id.webSearch_btn:
                fragment = new WebSearchFragment();
                break;
            case R.id.settings_btn:
                fragment = new SettingsFragment();
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container_fragments, fragment).commit();
        return true;
    }

    private void initializeMainComponentsViews() {
        MainRoot = findViewById(R.id.MainRoot);
        container_fragments = findViewById(R.id.container_fragments);
        navigationView = findViewById(R.id.bottom_nav);
    }

    private void initializeMySharedPreferences() {
        preferences = getSharedPreferences("MySharedPrefrence", Context.MODE_PRIVATE);
        editor = preferences.edit();
        prefMostVisitedSites = getSharedPreferences("MyMostVisitedSites", Context.MODE_PRIVATE);
        editor1 = prefMostVisitedSites.edit();
        prefMostVisitedSitesLinks = getSharedPreferences("MyMostVisitedSitesLinks", Context.MODE_PRIVATE);
        editor2 = prefMostVisitedSitesLinks.edit();
        prefMostVisitedSitesIcons = getSharedPreferences("MyMostVisitedSitesIcons", Context.MODE_PRIVATE);
        editor3 = prefMostVisitedSitesIcons.edit();
        prefSettings = getSharedPreferences("MySettings", Context.MODE_PRIVATE);
        editorSettings = prefSettings.edit();
    }

    private void initializeMyDatabases() {
        bhdatabase = BrowseHistoryDatabase.getInstance(this);
        bhdao = bhdatabase.getBrowseHistoryDao();
        fdatabase = FavouriteDatabase.getInstance(this);
        fdao = fdatabase.getFavouriteDao();
        shdatabase = SearchHistoryDatabase.getInstance(this);
        shdao = shdatabase.getSearchHistoryDao();
    }

    private void setConfigs() {
        fontSize = prefSettings.getFloat("FontSize", 0f);
        if (getResources().getStringArray(R.array.LanguageSpinner)[prefSettings.getInt("LanguageIndex", 0)].equals(getString(R.string.arabic_spin))) {
            layoutDirection = View.LAYOUT_DIRECTION_RTL;
            textDirection = View.TEXT_DIRECTION_RTL;
            setLocaleForApp(this, Arabic_Code);
        } else {
            textDirection = View.TEXT_DIRECTION_LTR;
            layoutDirection = View.LAYOUT_DIRECTION_LTR;
            setLocaleForApp(this,English_Code);
        }
        if (prefSettings.getBoolean("LastSelectedMode", false)) {
            NavColor = "#22527C";
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            NavColor = "#1284E8";
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void applyConfigs() {
        EnglishCategoryWebsites = getEnglishStringsArray(this, R.array.CategoryWebsites);
        EnglishWebsitesNames = getEnglishStringsArray(this, R.array.WebsitesNames);
        EnglishAppsNames = getEnglishStringsArray(this, R.array.AppsNames);
        WebsitesNames = getResources().getStringArray(R.array.WebsitesNames);
        AppsNames = getResources().getStringArray(R.array.AppsNames);
        MainRoot.setLayoutDirection(layoutDirection);
        container_fragments.setLayoutDirection(layoutDirection);
        container_fragments.setTextDirection(textDirection);
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.menu_navegation_btns);
        navigationView.setOnItemSelectedListener(this);
        navigationView.setSelectedItemId(current_nav_id);
        navigationView.setBackgroundColor(Color.parseColor(NavColor));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Runtime.getRuntime().exit(0);
    }

    private void showNotification(){
        if (bhdao.getAll().size()>10){
            pushNotification(this,this,R.string.notification_name,R.string.browsehistory_notification_desc,100,R.drawable.ic_baseline_notification_important_24,getString(R.string.browsehistory_notification_desc),getString(R.string.notification_content));
        }
        if (shdao.getAll().size()>10){
            pushNotification(this,this,R.string.notification_name,R.string.searchhistory_notification_desc,200,R.drawable.ic_baseline_notification_important_24,getString(R.string.searchhistory_notification_desc),getString(R.string.notification_content));
        }
        current_nav_id=R.id.home_btn;
    }
}