package com.borham.simplebrowser;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;
import static com.borham.simplebrowser.MainActivity.bhdao;
import static com.borham.simplebrowser.MainActivity.editor;
import static com.borham.simplebrowser.MainActivity.prefSettings;

public class HomeFragment extends Fragment implements View.OnClickListener {


    LinearLayout MostVisitedRoot = null;
    Button menuBtn = null;
    private boolean Is_PopupMenu_Created = false;
    private android.widget.PopupMenu popupMenu = null;
    private int MostVisitedIconsColorBorder = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SBrowserHelper.current_nav_id = R.id.home_btn;
        if (prefSettings.getBoolean("LastSelectedMode", false)) {
            MostVisitedIconsColorBorder = Color.WHITE;
        } else {
            MostVisitedIconsColorBorder = Color.BLACK;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View Homeview = inflater.inflate(R.layout.fragment_home, container, false);
        MostVisitedRoot = Homeview.findViewById(R.id.MostVisitedRoot);
        menuBtn = Homeview.findViewById(R.id.menuBtn);
        menuBtn.setOnClickListener(this);
        return Homeview;
    }

    @Override
    public void onStart() {
        super.onStart();
        CircleImageView MostVisitedInstance;
        String MostVisitedKey;
        Drawable MostVisitedIcon;
        for (int i = 0; i < MostVisitedRoot.getChildCount(); i++) {
            MostVisitedKey = MainActivity.prefMostVisitedSites.getString(getString(R.string.Site_App) + (i + 1), "");
            MostVisitedInstance = (CircleImageView) MostVisitedRoot.getChildAt(i);
            if (MostVisitedKey.isEmpty()) {
                MostVisitedInstance.setBorderColor(MostVisitedIconsColorBorder);
            } else {
                MostVisitedIcon = SBrowserHelper.StringToDrawable(getContext(), MainActivity.prefMostVisitedSitesIcons.getString(MostVisitedKey, ""));
                MostVisitedInstance.setTag(MainActivity.prefMostVisitedSitesLinks.getString(MostVisitedKey, ""));
                MostVisitedInstance.setImageDrawable(MostVisitedIcon);
                MostVisitedInstance.setOnClickListener(this);
                MostVisitedInstance.setContentDescription(MostVisitedKey);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.menuBtn) {
            if (!Is_PopupMenu_Created) {
                popupMenu = SBrowserHelper.createPopupMenu(getContext(), menuBtn, R.menu.home_option_menu);
                Is_PopupMenu_Created = true;
            }
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(item -> {
                getParentFragmentManager().beginTransaction().replace(R.id.container_fragments, new HomeBrowsingFragment()).commit();
                switch (item.getItemId()) {
                    case R.id.websites:
                        SBrowserHelper.fragmentType = "Websites";
                        break;
                    case R.id.android_apps:
                        SBrowserHelper.fragmentType = "Apps";
                        break;
                }
                return false;
            });
        } else if (v.getTag() != null) {
            SBrowserHelper.openWebLink(getContext(), v.getTag().toString());
            SBrowserHelper.storeClickForBrowseHistoryAndMostVisited(v.getContentDescription().toString(), editor, MainActivity.preferences, bhdao);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SBrowserHelper.setCustomActionBar((MainActivity) getContext(), R.string.app_name, R.drawable.home_icon);
    }
}