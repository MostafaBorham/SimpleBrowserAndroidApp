package com.borham.simplebrowser;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.borham.simplebrowser.MyRoom.FavouriteFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import static com.borham.simplebrowser.MainActivity.AppsNames;
import static com.borham.simplebrowser.MainActivity.EnglishAppsNames;
import static com.borham.simplebrowser.MainActivity.EnglishCategoryWebsites;
import static com.borham.simplebrowser.MainActivity.EnglishWebsitesNames;
import static com.borham.simplebrowser.MainActivity.WebsitesNames;
import static com.borham.simplebrowser.MainActivity.bhdao;
import static com.borham.simplebrowser.MainActivity.editor;
import static com.borham.simplebrowser.MainActivity.editor1;
import static com.borham.simplebrowser.MainActivity.editor2;
import static com.borham.simplebrowser.MainActivity.editor3;
import static com.borham.simplebrowser.MainActivity.fdao;
import static com.borham.simplebrowser.MainActivity.prefSettings;
import static com.borham.simplebrowser.MainActivity.preferences;
import static com.borham.simplebrowser.SBrowserHelper.Arabic_Code;
import static com.borham.simplebrowser.SBrowserHelper.DBKey_extra;
import static com.borham.simplebrowser.SBrowserHelper.DrawableToString;
import static com.borham.simplebrowser.SBrowserHelper.createDrawableFromUri;
import static com.borham.simplebrowser.SBrowserHelper.fragmentType;
import static com.borham.simplebrowser.SBrowserHelper.getEnglishString;
import static com.borham.simplebrowser.SBrowserHelper.openWebLink;
import static com.borham.simplebrowser.SBrowserHelper.pixelsToSp;
import static com.borham.simplebrowser.SBrowserHelper.sortMapList;
import static com.borham.simplebrowser.SBrowserHelper.storeClickForBrowseHistoryAndMostVisited;

public class HomeBrowsingFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final int CLICKS_NUMBER = 4;//min number of clicks that  should get to it site for be most visited site
    private final int DRAWABLE_RIGHT_INDEX = 2;
    LinearLayout SitesOrAppsRootLayout;
    private int drawableIndex = 0, CategoryTextColor = 0;
    private float fontSize = 0f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SBrowserHelper.current_nav_id = R.id.home_btn;
        if (prefSettings.getBoolean("LastSelectedMode", false)) {
            CategoryTextColor = Color.WHITE;
        } else {
            CategoryTextColor = Color.BLACK;
        }
        fontSize = prefSettings.getFloat("FontSize", 0f);
        if (Locale.getDefault().getLanguage().equals(Arabic_Code)) {
            drawableIndex = DRAWABLE_RIGHT_INDEX;
            if (fragmentType.equals("Websites")) {
                DBKey_extra = "";
            } else DBKey_extra = getEnglishString(getContext(), R.string.UniqueKeyApp);
        } else {
            if (fragmentType.equals("Websites")) {
                DBKey_extra = "";
            } else DBKey_extra = getString(R.string.UniqueKeyApp);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View HBview = inflater.inflate(R.layout.fragment_home_browsing, container, false);
        SitesOrAppsRootLayout = HBview.findViewById(R.id.SitesOrAppsRoot);
        renderingView();
        return HBview;
    }

    private void renderingView() {
        float CurrentViewSize;
        TextView cat_txt;
        TextView site_link;
        int index = 0;
        for (int x1 = 0; x1 < 22; x1++) {
            cat_txt = (TextView) ((LinearLayout) SitesOrAppsRootLayout.getChildAt(x1)).getChildAt(0);
            if (Locale.getDefault().getLanguage().equals(Arabic_Code)) {
                Drawable drawableCat = cat_txt.getCompoundDrawables()[0];
                cat_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableCat, null);
            }
            CurrentViewSize = pixelsToSp(getContext(), cat_txt.getTextSize());
            cat_txt.setTextSize(CurrentViewSize + fontSize);
            cat_txt.setTextColor(CategoryTextColor);
            for (int x2 = 1; x2 < 4; x2++) {
                for (int x3 = 0; x3 < 2; x3++) {
                    site_link = (TextView) ((LinearLayout) ((LinearLayout) SitesOrAppsRootLayout.getChildAt(x1)).getChildAt(x2)).getChildAt(x3);
                    site_link.setTextSize(CurrentViewSize + fontSize);
                    site_link.setOnClickListener(this);
                    site_link.setOnLongClickListener(this);
                    if (SBrowserHelper.fragmentType.equals("Websites")) {
                        site_link.setText(WebsitesNames[index]);
                        site_link.setContentDescription(EnglishWebsitesNames[index]);
                        if (Locale.getDefault().getLanguage().equals(Arabic_Code))
                            site_link.setCompoundDrawablesWithIntrinsicBounds(null, null, createDrawableFromUri(getContext(), EnglishCategoryWebsites[x1] + "/" + EnglishWebsitesNames[index].toLowerCase() + ".png"), null);
                        else
                            site_link.setCompoundDrawablesWithIntrinsicBounds(createDrawableFromUri(getContext(), EnglishCategoryWebsites[x1] + "/" + EnglishWebsitesNames[index].toLowerCase() + ".png"), null, null, null);
                    } else {
                        site_link.setText(AppsNames[index]);
                        site_link.setContentDescription(EnglishAppsNames[index]);
                        if (Locale.getDefault().getLanguage().equals(Arabic_Code))
                            site_link.setCompoundDrawablesWithIntrinsicBounds(null, null, createDrawableFromUri(getContext(), EnglishCategoryWebsites[x1] + "/" + EnglishAppsNames[index].toLowerCase() + ".png"), null);
                        else
                            site_link.setCompoundDrawablesWithIntrinsicBounds(createDrawableFromUri(getContext(), EnglishCategoryWebsites[x1] + "/" + EnglishAppsNames[index].toLowerCase() + ".png"), null, null, null);
                    }
                    index++;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        String link;
        String uri_site_icon = DrawableToString(((TextView) v).getCompoundDrawables()[drawableIndex]);
        if (fragmentType.equals("Websites")) {
            link = v.getTag().toString().substring(v.getTag().toString().indexOf(",") + 1);
        } else {
            link = v.getTag().toString().substring(0, v.getTag().toString().indexOf(","));
        }
        openWebLink(getContext(), link);
        storeClickForBrowseHistoryAndMostVisited(v.getContentDescription().toString() + DBKey_extra, editor, preferences, bhdao);
        editor2.putString(v.getContentDescription().toString() + DBKey_extra, link);
        editor2.apply();
        editor3.putString(v.getContentDescription().toString() + DBKey_extra, uri_site_icon);
        editor3.apply();
    }


    @Override
    public boolean onLongClick(View v) {
        boolean is_fav = false;
        List<FavouriteFormat> favouriteFormats = fdao.getAll();
        for (FavouriteFormat format : favouriteFormats) {
            if (format.getFav_name().equals(v.getContentDescription().toString() + DBKey_extra)) {
                is_fav = true;
                break;
            }
        }
        if (!is_fav) {
            final android.widget.PopupMenu popupMenu = SBrowserHelper.createPopupMenu(getContext(), v, R.menu.favourite_menu);
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(item -> {
                String link;
                String uri_site_icon = DrawableToString(((TextView) v).getCompoundDrawables()[drawableIndex]);
                if (fragmentType.equals("Websites")) {
                    link = v.getTag().toString().substring(v.getTag().toString().indexOf(",") + 1);
                } else {
                    link = v.getTag().toString().substring(0, v.getTag().toString().indexOf(","));
                }
                editor2.putString(v.getContentDescription().toString() + DBKey_extra, link);
                editor2.apply();
                editor3.putString(v.getContentDescription().toString() + DBKey_extra, uri_site_icon);
                editor3.apply();
                fdao.insert(new FavouriteFormat(v.getContentDescription().toString() + DBKey_extra));
                return true;
            });
        } else {
            Toast.makeText(getContext(), getString(R.string.favourite_toast), Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Map<String, Integer> sortedMap = sortMapList((Map<String, Integer>) MainActivity.preferences.getAll());
        int x = 1, index = 1;
        for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
            if (entry.getValue() >= CLICKS_NUMBER && sortedMap.size() - index <= 3) {
                editor1.putString(getString(R.string.Site_App) + x, entry.getKey());
                editor1.apply();
                x++;
            }
            index++;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fragmentType.equals("Websites"))
            SBrowserHelper.setCustomActionBar((MainActivity) getContext(), R.string.WebSitesActionbar, R.drawable.most_visited_icon);
        else
            SBrowserHelper.setCustomActionBar((MainActivity) getContext(), R.string.AndroidAppsActionbar, R.drawable.android_icon);
    }
}