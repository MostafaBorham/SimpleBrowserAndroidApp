package com.borham.simplebrowser;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.borham.simplebrowser.MyRoom.BrowseHistoryFormat;
import com.borham.simplebrowser.MyRoom.FavouriteFormat;
import com.borham.simplebrowser.MyRoom.SearchHistoryFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.borham.simplebrowser.MainActivity.AppsNames;
import static com.borham.simplebrowser.MainActivity.EnglishAppsNames;
import static com.borham.simplebrowser.MainActivity.EnglishWebsitesNames;
import static com.borham.simplebrowser.MainActivity.WebsitesNames;
import static com.borham.simplebrowser.MainActivity.bhdao;
import static com.borham.simplebrowser.MainActivity.fdao;
import static com.borham.simplebrowser.MainActivity.prefMostVisitedSitesIcons;
import static com.borham.simplebrowser.MainActivity.shdao;
import static com.borham.simplebrowser.SBrowserHelper.Arabic_Code;
import static com.borham.simplebrowser.SBrowserHelper.DBKey_extra;
import static com.borham.simplebrowser.SBrowserHelper.convertToArabic;
import static com.borham.simplebrowser.SBrowserHelper.createAlertDialog;
import static com.borham.simplebrowser.SBrowserHelper.getEnglishString;
import static com.borham.simplebrowser.SBrowserHelper.is_configs_change;
import static com.borham.simplebrowser.SBrowserHelper.pixelsToSp;

public class HistoryFragment extends Fragment implements View.OnClickListener, TabHost.OnTabChangeListener, SearchView.OnQueryTextListener {

    private float CurrentViewSize = 0f;
    private TabHost tabHost = null;
    private TabWidget tabWidget = null;
    private TextView BHTextView, SHTextView, FAVTextView = null;
    private SearchView historySearchView = null;
    private RecyclerView recyclerViewBrowse, recyclerViewFav, recyclerViewSearch = null;
    private Button clearAllBHBtn, clearAllFavBtn, clearAllSearchBtn = null;
    private CustomAdapter adapterBrowse, adapterFav, adapterSearch = null;
    private ArrayList<RecycleBrowseHistoryFormat> browseHistoryFormats = null;
    private ArrayList<RecycleFavouriteFormat> favouriteFormats = null;
    private ArrayList<RecycleSearchHistoryFormat> searchHistoryFormats = null;
    private List<BrowseHistoryFormat> allDatabaseListBrowse = null;
    private List<FavouriteFormat> allDatabaseListFav = null;
    private List<SearchHistoryFormat> allDatabaseListSearch = null;
    private String english_unique_key_app = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        english_unique_key_app = getEnglishString(getContext(), R.string.UniqueKeyApp);
        is_configs_change = true;
        SBrowserHelper.current_nav_id = R.id.history_btn;
        browseHistoryFormats = new ArrayList<>();
        favouriteFormats = new ArrayList<>();
        searchHistoryFormats = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View historyView = inflater.inflate(R.layout.fragment_history, container, false);
        initializeHistoryViews(historyView);
        initializeTabHost();
        implementBrowseHistoryRecycleView();
        adjustFontSize();
        return historyView;
    }

    @Override
    public void onClick(View v) {
        AlertDialog dialog = createAlertDialog(getContext(), android.R.drawable.ic_dialog_alert, getString(R.string.alert_title));
        Button PosBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button NegBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        TextView message = dialog.findViewById(android.R.id.message);
        TextView title = dialog.findViewById(R.id.title_template).findViewById(R.id.alertTitle);
        CurrentViewSize = pixelsToSp(getContext(), PosBtn.getTextSize());
        PosBtn.setTextSize(CurrentViewSize + MainActivity.fontSize);
        NegBtn.setTextSize(CurrentViewSize + MainActivity.fontSize);
        CurrentViewSize = pixelsToSp(getContext(), message.getTextSize());
        message.setTextSize(CurrentViewSize + MainActivity.fontSize);
        CurrentViewSize = pixelsToSp(getContext(), title.getTextSize());
        title.setTextSize(CurrentViewSize + MainActivity.fontSize);
        if (v.getId() == R.id.clearAllBH) {
            PosBtn.setOnClickListener(v1 -> {
                allDatabaseListBrowse = bhdao.getAll();
                bhdao.delete(allDatabaseListBrowse.toArray(new BrowseHistoryFormat[]{}));
                browseHistoryFormats.clear();
                adapterBrowse.localDataSetCopy.clear();
                adapterBrowse.notifyDataSetChanged();
                dialog.dismiss();
            });
        } else if (v.getId() == R.id.clearAllFav) {
            PosBtn.setOnClickListener(v12 -> {
                fdao.delete(allDatabaseListFav.toArray(new FavouriteFormat[]{}));
                favouriteFormats.clear();
                adapterFav.FavlocalDataSetCopy.clear();
                adapterFav.notifyDataSetChanged();
                dialog.dismiss();
            });
        } else {
            PosBtn.setOnClickListener(v13 -> {
                shdao.delete(allDatabaseListSearch.toArray(new SearchHistoryFormat[]{}));
                searchHistoryFormats.clear();
                adapterSearch.SearchlocalDataSetCopy.clear();
                adapterSearch.notifyDataSetChanged();
                dialog.dismiss();
            });
        }
    }

    @Override
    public void onTabChanged(String tabId) {
        historySearchView.setQueryHint(getString(R.string.mai_hint_keyword) + tabHost.getCurrentTabTag());
        if (tabId.equals(getString(R.string.favourite_tab))) {
            if (recyclerViewFav.getAdapter() == null) {
                allDatabaseListFav = fdao.getAll();
                for (FavouriteFormat favourite : allDatabaseListFav) {
                    String Fav_name = favourite.getFav_name();
                    Drawable icon = SBrowserHelper.StringToDrawable(getContext(), prefMostVisitedSitesIcons.getString(Fav_name, ""));
                    if (Fav_name.contains(english_unique_key_app)) {
                        DBKey_extra = getString(R.string.UniqueKeyApp);
                        if (Locale.getDefault().getLanguage().equals(Arabic_Code)) {
                            int pos = Arrays.asList(EnglishAppsNames).indexOf(Fav_name.replaceAll(english_unique_key_app, ""));
                            Fav_name = AppsNames[pos] + DBKey_extra;
                        }
                    } else {
                        DBKey_extra = "";
                        if (Locale.getDefault().getLanguage().equals(Arabic_Code)) {
                            int pos = Arrays.asList(EnglishWebsitesNames).indexOf(Fav_name.replaceAll(english_unique_key_app, ""));
                            Fav_name = WebsitesNames[pos] + DBKey_extra;
                        }
                    }
                    favouriteFormats.add(new RecycleFavouriteFormat(Fav_name, icon, favourite));
                }
                adapterFav = new CustomAdapter(favouriteFormats, 0);
                recyclerViewFav.setAdapter(adapterFav);
            } else if (historySearchView.getQuery().toString().equals("")) {
                favouriteFormats.clear();
                favouriteFormats.addAll(adapterFav.FavlocalDataSetCopy);
                adapterFav.notifyDataSetChanged();
            }

        } else if (tabId.equals(getString(R.string.search_tab))) {
            if (recyclerViewSearch.getAdapter() == null) {
                allDatabaseListSearch = shdao.getAll();
                Collections.reverse(allDatabaseListSearch);
                for (SearchHistoryFormat searchHistoryFormat : allDatabaseListSearch) {
                    String detail;
                    if (searchHistoryFormat.getSearch_name().length() < 25) {
                        detail = searchHistoryFormat.getSearch_name() + "\n" + convertToArabic(searchHistoryFormat.getSearch_date_time());
                    } else {
                        detail = searchHistoryFormat.getSearch_name().substring(0, 24) + "...";
                        detail = detail + "\n" + convertToArabic(searchHistoryFormat.getSearch_date_time());
                    }
                    int icon_id = getContext().getResources().getIdentifier(searchHistoryFormat.getSearch_engine_name(), "drawable", getContext().getPackageName());
                    searchHistoryFormats.add(new RecycleSearchHistoryFormat(detail, getContext().getResources().getDrawable(icon_id), searchHistoryFormat));
                }
                adapterSearch = new CustomAdapter(searchHistoryFormats, 0L);
                recyclerViewSearch.setAdapter(adapterSearch);
            } else if (historySearchView.getQuery().toString().equals("")) {
                searchHistoryFormats.clear();
                searchHistoryFormats.addAll(adapterSearch.SearchlocalDataSetCopy);
                adapterSearch.notifyDataSetChanged();
            }

        } else {
            ((MainActivity) getContext()).navigationView.setSelectedItemId(R.id.history_btn);
        }
        historySearchView.setQuery("", false);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (tabHost.getCurrentTabTag().equals(getString(R.string.browse_tab))) {
            adapterBrowse.filter(newText);
        } else if (tabHost.getCurrentTabTag().equals(getString(R.string.search_tab))) {
            adapterSearch.filter(newText);
        } else {
            adapterFav.filter(newText);
        }
        return false;
    }

    private void initializeHistoryViews(View historyView) {
        historySearchView = historyView.findViewById(R.id.historySearchView);
        clearAllBHBtn = historyView.findViewById(R.id.clearAllBH);
        clearAllFavBtn = historyView.findViewById(R.id.clearAllFav);
        clearAllSearchBtn = historyView.findViewById(R.id.clearAllSearch);
        recyclerViewBrowse = historyView.findViewById(R.id.RV);
        recyclerViewFav = historyView.findViewById(R.id.RV_Fav);
        recyclerViewSearch = historyView.findViewById(R.id.RV_Search);
        tabHost = historyView.findViewById(R.id.historyTabHost);
        tabWidget = historyView.findViewById(android.R.id.tabs);
        historySearchView.setOnQueryTextListener(this);
        historySearchView.setIconifiedByDefault(false);
        clearAllBHBtn.setOnClickListener(this);
        clearAllFavBtn.setOnClickListener(this);
        clearAllSearchBtn.setOnClickListener(this);
        recyclerViewBrowse.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewFav.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void initializeTabHost() {
        tabHost.setup();
        TabHost.TabSpec tabBrowseHistory = tabHost.newTabSpec(getString(R.string.browse_tab));
        TabHost.TabSpec tabSearchHistory = tabHost.newTabSpec(getString(R.string.search_tab));
        TabHost.TabSpec tabFavSites = tabHost.newTabSpec(getString(R.string.favourite_tab));
        tabBrowseHistory.setContent(R.id.tab1);
        tabSearchHistory.setContent(R.id.tab2);
        tabFavSites.setContent(R.id.tab3);
        tabFavSites.setIndicator(getString(R.string.favourite_tab));
        tabBrowseHistory.setIndicator(getString(R.string.browse_tab));
        tabSearchHistory.setIndicator(getString(R.string.search_tab));
        tabHost.addTab(tabBrowseHistory);
        tabHost.addTab(tabSearchHistory);
        tabHost.addTab(tabFavSites);
        BHTextView = tabWidget.getChildTabViewAt(0).findViewById(android.R.id.title);
        SHTextView = tabWidget.getChildTabViewAt(1).findViewById(android.R.id.title);
        FAVTextView = tabWidget.getChildTabViewAt(2).findViewById(android.R.id.title);
        BHTextView.setAllCaps(false);
        SHTextView.setAllCaps(false);
        FAVTextView.setAllCaps(false);
        tabHost.setOnTabChangedListener(this);
        historySearchView.setQueryHint(getString(R.string.mai_hint_keyword) + tabHost.getCurrentTabTag());
    }

    private void adjustFontSize() {
        CurrentViewSize = pixelsToSp(getContext(), BHTextView.getTextSize());
        BHTextView.setTextSize(CurrentViewSize + MainActivity.fontSize);
        SHTextView.setTextSize(CurrentViewSize + MainActivity.fontSize);
        FAVTextView.setTextSize(CurrentViewSize + MainActivity.fontSize);
        CurrentViewSize = pixelsToSp(getContext(), clearAllBHBtn.getTextSize());
        clearAllSearchBtn.setTextSize(CurrentViewSize + MainActivity.fontSize);
        clearAllBHBtn.setTextSize(CurrentViewSize + MainActivity.fontSize);
        clearAllFavBtn.setTextSize(CurrentViewSize + MainActivity.fontSize);
        int id = historySearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textSearchView = historySearchView.findViewById(id);
        CurrentViewSize = pixelsToSp(getContext(), textSearchView.getTextSize());
        textSearchView.setTextSize(CurrentViewSize + MainActivity.fontSize);
    }

    private void implementBrowseHistoryRecycleView() {
        allDatabaseListBrowse = bhdao.getAll();
        Collections.reverse(allDatabaseListBrowse);
        for (BrowseHistoryFormat history : allDatabaseListBrowse) {
            String history_name = history.getBrowsing_name();
            if (history_name.contains(english_unique_key_app)) {
                DBKey_extra = getString(R.string.UniqueKeyApp);
                if (Locale.getDefault().getLanguage().equals(Arabic_Code)) {
                    int pos = Arrays.asList(EnglishAppsNames).indexOf(history.getBrowsing_name().replaceAll(english_unique_key_app, ""));
                    history_name = AppsNames[pos] + DBKey_extra;
                }
            } else {
                DBKey_extra = "";
                if (Locale.getDefault().getLanguage().equals(Arabic_Code)) {
                    int pos = Arrays.asList(EnglishWebsitesNames).indexOf(history.getBrowsing_name().replaceAll(english_unique_key_app, ""));
                    history_name = WebsitesNames[pos] + DBKey_extra;
                }
            }
            String details = history_name + "\n" + convertToArabic(history.getBrowsing_date());
            Drawable icon = SBrowserHelper.StringToDrawable(getContext(), prefMostVisitedSitesIcons.getString(history.getBrowsing_name(), ""));
            browseHistoryFormats.add(new RecycleBrowseHistoryFormat(details, icon, history));
        }
        adapterBrowse = new CustomAdapter(browseHistoryFormats);
        recyclerViewBrowse.setAdapter(adapterBrowse);
    }

    @Override
    public void onResume() {
        super.onResume();
        SBrowserHelper.setCustomActionBar((MainActivity) getContext(), R.string.history_nav, R.drawable.history_icon);
    }
}