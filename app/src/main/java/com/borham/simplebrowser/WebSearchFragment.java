package com.borham.simplebrowser;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.borham.simplebrowser.MyRoom.SearchHistoryFormat;

import java.util.ArrayList;
import java.util.Locale;

import static com.borham.simplebrowser.MainActivity.prefSettings;
import static com.borham.simplebrowser.SBrowserHelper.Arabic_Code;
import static com.borham.simplebrowser.SBrowserHelper.English_Code;
import static com.borham.simplebrowser.SBrowserHelper.checkInternet;
import static com.borham.simplebrowser.SBrowserHelper.getCurrentDate;
import static com.borham.simplebrowser.SBrowserHelper.getCurrentTime;
import static com.borham.simplebrowser.SBrowserHelper.getEnglishStringsArray;
import static com.borham.simplebrowser.SBrowserHelper.pixelsToSp;
import static com.borham.simplebrowser.SBrowserHelper.remove_spaces;
import static com.borham.simplebrowser.SBrowserHelper.setLocaleForApp;

public class WebSearchFragment extends Fragment implements SearchView.OnQueryTextListener {
    private final String SearchHistory_Table_Name = "searchHistory";
    ArrayList<String> SUGGESTIONS = null;
    private SimpleCursorAdapter cursorAdapter = null;
    private ProgressBar progressBar = null;
    private WebView web_search_view = null;
    private SearchView search_engine = null;
    private String search_engine_link = "";
    private float fontSize = 0f;
    private int search_engine_index;
    private int WebViewForegroundColor = 0;
    private String WebViewBackgroundColor = "";
    private int startLoading=0,endLoading=0;
    private boolean is_loaded;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SBrowserHelper.current_nav_id = R.id.webSearch_btn;
        if (prefSettings.getBoolean("LastSelectedMode", false)) {
            WebViewBackgroundColor = "#191717";
            WebViewForegroundColor = WebSettings.FORCE_DARK_ON;
        } else {
            WebViewBackgroundColor = "#8BE4E4E4";
            WebViewForegroundColor = WebSettings.FORCE_DARK_OFF;
        }
        search_engine_link = MainActivity.prefSettings.getString("SearchEngineLink", "https://www.google.ru/search?q=");
        search_engine_index = MainActivity.prefSettings.getInt("SearchEngineIndex", 0);
        fontSize = MainActivity.prefSettings.getFloat("FontSize", 0f);
        SUGGESTIONS = new ArrayList<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View SearchView = inflater.inflate(R.layout.fragment_web_search, container, false);
        progressBar = SearchView.findViewById(R.id.progressBar);
        web_search_view = SearchView.findViewById(R.id.WebSearchView);
        search_engine = SearchView.findViewById(R.id.SearchEngine);
        web_search_view.setBackgroundColor(Color.parseColor(WebViewBackgroundColor));
        WebSettings webSettings = web_search_view.getSettings();
        webSettings.setTextZoom(100 + (int) fontSize * 10);
        webSettings.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            webSettings.setForceDark(WebViewForegroundColor);
        }
        search_engine.setOnQueryTextListener(this);
        int id = search_engine.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textSearchView = search_engine.findViewById(id);
        ((AutoCompleteTextView) textSearchView).setThreshold(0);
        float CurrentViewSize = pixelsToSp(getContext(), textSearchView.getTextSize());
        textSearchView.setTextSize(CurrentViewSize + fontSize);
        if (!CustomAdapter.searchLinkOfRecycleItem.isEmpty()) {
            if (checkInternet(getContext())) {
                progressBar.setVisibility(View.VISIBLE);
                web_search_view.loadUrl(CustomAdapter.searchLinkOfRecycleItem);
                CustomAdapter.searchLinkOfRecycleItem = "";
            } else {
                web_search_view.setForeground(getResources().getDrawable(R.drawable.no_internet));
            }
        }
        web_search_view.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                is_loaded=true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (is_loaded) progressBar.setVisibility(View.GONE);
            }
        });
        return SearchView;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onQueryTextSubmit(String query) {
        if (checkInternet(getContext())) {
            progressBar.setVisibility(View.VISIBLE);
            query = remove_spaces(query);
            web_search_view.setForeground(null);
            String url = search_engine_link + query;
            web_search_view.loadUrl(url);
            search_engine.clearFocus();
            MainActivity.shdao.insert(new SearchHistoryFormat(query, url, getCurrentDate() + "," + getCurrentTime(), getEnglishStringsArray(getContext(), R.array.SearchEngineSpinner)[search_engine_index]));
        } else {
            web_search_view.setForeground(getResources().getDrawable(R.drawable.no_internet));
            web_search_view.loadUrl("about:blank");
        }

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        showSuggestions(newText);
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Locale.getDefault().getLanguage().equals(Arabic_Code)) setLocaleForApp(getContext(), Arabic_Code);
        else setLocaleForApp(getContext(), English_Code);
        search_engine.setQueryHint(getString(R.string.search_web) + getResources().getStringArray(R.array.SearchEngineSpinner)[search_engine_index]);
        SBrowserHelper.setCustomActionBar((MainActivity) getContext(), R.string.WebSearchActionbar, R.drawable.websearch_icon);
        for (SearchHistoryFormat format : MainActivity.shdao.getAll()) {
            if (!SUGGESTIONS.contains(format.getSearch_name())) {
                SUGGESTIONS.add(format.getSearch_name());
            }
        }
        cursorAdapter = new SimpleCursorAdapter(getContext(), android.R.layout.simple_list_item_1, null, new String[]{SearchHistory_Table_Name}, new int[]{android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        search_engine.setSuggestionsAdapter(cursorAdapter);
        search_engine.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) cursorAdapter.getItem(position);
                String txt = cursor.getString(cursor.getColumnIndex("searchHistory"));
                search_engine.setQuery(txt, true);
                return true;
            }
        });
    }

    private void showSuggestions(String query) {
        final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, SearchHistory_Table_Name});
        for (String s : SUGGESTIONS) {
            if (s.toLowerCase().contains(query.trim().toLowerCase()))
                c.addRow(new Object[]{SUGGESTIONS.indexOf(s), s});
        }
        cursorAdapter.changeCursor(c);
    }
}