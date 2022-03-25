package com.borham.simplebrowser;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Locale;

import static com.borham.simplebrowser.MainActivity.bhdao;
import static com.borham.simplebrowser.MainActivity.editor;
import static com.borham.simplebrowser.MainActivity.fdao;
import static com.borham.simplebrowser.MainActivity.preferences;
import static com.borham.simplebrowser.MainActivity.shdao;
import static com.borham.simplebrowser.SBrowserHelper.Arabic_Code;
import static com.borham.simplebrowser.SBrowserHelper.convertToArabic;
import static com.borham.simplebrowser.SBrowserHelper.createAlertDialog;
import static com.borham.simplebrowser.SBrowserHelper.getCurrentDate;
import static com.borham.simplebrowser.SBrowserHelper.getCurrentTime;
import static com.borham.simplebrowser.SBrowserHelper.openWebLink;
import static com.borham.simplebrowser.SBrowserHelper.pixelsToSp;
import static com.borham.simplebrowser.SBrowserHelper.storeClickForBrowseHistoryAndMostVisited;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    public static String searchLinkOfRecycleItem = "";
    public ArrayList<RecycleBrowseHistoryFormat> localDataSetCopy = new ArrayList<>();
    public ArrayList<RecycleFavouriteFormat> FavlocalDataSetCopy = new ArrayList<>();
    public ArrayList<RecycleSearchHistoryFormat> SearchlocalDataSetCopy = new ArrayList<>();
    Context context = null;
    private ArrayList<RecycleBrowseHistoryFormat> localDataSet = null;
    private ArrayList<RecycleFavouriteFormat> FavlocalDataSet = null;
    private ArrayList<RecycleSearchHistoryFormat> SearchlocalDataSet = null;
    private float CurrentViewSize = 0f;
    private float fontSize = 0f;

    public CustomAdapter(ArrayList<RecycleBrowseHistoryFormat> dataSet) {
        localDataSet = dataSet;
        localDataSetCopy.addAll(dataSet);
    }

    public CustomAdapter(ArrayList<RecycleFavouriteFormat> dataSetFav, int i) {
        FavlocalDataSet = dataSetFav;
        FavlocalDataSetCopy.addAll(dataSetFav);
    }

    public CustomAdapter(ArrayList<RecycleSearchHistoryFormat> dataSetSearch, long l) {
        SearchlocalDataSet = dataSetSearch;
        SearchlocalDataSetCopy.addAll(dataSetSearch);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.text_row_item, viewGroup, false);
        context = view.getContext();
        fontSize = MainActivity.prefSettings.getFloat("FontSize", 0f);
        TextView RVItemTextView = view.findViewById(R.id.BHDetail);
        float CurrentTextViewSize = pixelsToSp(context, RVItemTextView.getTextSize());
        RVItemTextView.setTextSize(CurrentTextViewSize + fontSize);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if (localDataSet != null) {
            viewHolder.BHDetail.setText(localDataSet.get(position).getDetail());
            viewHolder.BHIcon.setImageDrawable(localDataSet.get(position).getIcon());
        } else if (SearchlocalDataSet != null) {
            viewHolder.BHDetail.setText(SearchlocalDataSet.get(position).getDetail());
            viewHolder.BHIcon.setImageDrawable(SearchlocalDataSet.get(position).getIcon());
        } else {
            viewHolder.BHDetail.setText(FavlocalDataSet.get(position).getName());
            viewHolder.BHIcon.setImageDrawable(FavlocalDataSet.get(position).getIcon());
        }
        viewHolder.BHRemoveBtn.setOnClickListener(v -> {
            AlertDialog dialog = createAlertDialog(context, android.R.drawable.ic_dialog_alert, context.getString(R.string.alert_title));
            Button PosBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button NegBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            TextView message = dialog.findViewById(android.R.id.message);
            TextView title = dialog.findViewById(R.id.title_template).findViewById(R.id.alertTitle);
            CurrentViewSize = pixelsToSp(context, PosBtn.getTextSize());
            PosBtn.setTextSize(CurrentViewSize + fontSize);
            NegBtn.setTextSize(CurrentViewSize + fontSize);
            CurrentViewSize = pixelsToSp(context, message.getTextSize());
            message.setTextSize(CurrentViewSize + fontSize);
            CurrentViewSize = pixelsToSp(context, title.getTextSize());
            title.setTextSize(CurrentViewSize + fontSize);
            PosBtn.setOnClickListener(v1 -> {
                if (localDataSet != null) {
                    RecycleBrowseHistoryFormat recycleBrowseHistoryFormat = localDataSet.remove(position);
                    bhdao.delete(recycleBrowseHistoryFormat.getRef());
                    localDataSetCopy.remove(recycleBrowseHistoryFormat);
                } else if (SearchlocalDataSet != null) {
                    RecycleSearchHistoryFormat recycleSearchHistoryFormat = SearchlocalDataSet.remove(position);
                    shdao.delete(recycleSearchHistoryFormat.getRef());
                    SearchlocalDataSetCopy.remove(recycleSearchHistoryFormat);
                } else {
                    RecycleFavouriteFormat recycleFavouriteFormat = FavlocalDataSet.remove(position);
                    fdao.delete(recycleFavouriteFormat.getRef());
                    FavlocalDataSetCopy.remove(recycleFavouriteFormat);
                }
                notifyDataSetChanged();
                dialog.dismiss();
            });
        });
        viewHolder.itemView.setOnClickListener(v -> {
            if (SearchlocalDataSet != null) {
                searchLinkOfRecycleItem = SearchlocalDataSet.get(position).getRef().getSearch_link();
                ((MainActivity) context).navigationView.setSelectedItemId(R.id.webSearch_btn);
            } else {
                String site_key, link;
                if (localDataSet != null) {
                    String site_name = localDataSet.get(position).getDetail().substring(0, localDataSet.get(position).getDetail().indexOf("\n"));
                    String site_data_time = getCurrentDate() + "," + getCurrentTime();
                    site_key = localDataSet.get(position).getRef().getBrowsing_name();
                    if (Locale.getDefault().getLanguage().equals(Arabic_Code))
                        site_data_time = convertToArabic(site_data_time);
                    RecycleBrowseHistoryFormat browseHistoryFormat = new RecycleBrowseHistoryFormat(site_name + "\n" + site_data_time, localDataSet.get(position).getIcon(), localDataSet.get(position).getRef());
                    localDataSet.add(0, browseHistoryFormat);
                    localDataSetCopy.add(0, browseHistoryFormat);
                    notifyDataSetChanged();
                } else {
                    site_key = FavlocalDataSet.get(position).getRef().getFav_name();
                }
                link = MainActivity.prefMostVisitedSitesLinks.getString(site_key, "");
                openWebLink(context, link);
                storeClickForBrowseHistoryAndMostVisited(site_key, editor, preferences, bhdao);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (localDataSet != null) {
            return localDataSet.size();
        } else if (SearchlocalDataSet != null) {
            return SearchlocalDataSet.size();
        } else {
            return FavlocalDataSet.size();
        }
    }

    public void filter(String text) {
        text = text.trim().toLowerCase();
        if (localDataSet != null) {
            localDataSet.clear();
            if (text.isEmpty()) {
                localDataSet.addAll(localDataSetCopy);
            } else {
                for (RecycleBrowseHistoryFormat item : localDataSetCopy) {
                    int lastIndexOfHistoryName = item.getDetail().length() - 20;
                    String HistoryName = item.getDetail().substring(0, lastIndexOfHistoryName).toLowerCase();
                    if (HistoryName.contains(text)) {
                        localDataSet.add(item);
                    }
                }
            }
        } else if (SearchlocalDataSet != null) {
            SearchlocalDataSet.clear();
            if (text.isEmpty()) {
                SearchlocalDataSet.addAll(SearchlocalDataSetCopy);
            } else {
                for (RecycleSearchHistoryFormat item : SearchlocalDataSetCopy) {
                    int lastIndexOfHistoryName = item.getDetail().length() - 20;
                    String HistoryName = item.getDetail().substring(0, lastIndexOfHistoryName).toLowerCase();
                    if (HistoryName.contains(text)) {
                        SearchlocalDataSet.add(item);
                    }
                }
            }
        } else {
            FavlocalDataSet.clear();
            if (text.isEmpty()) {
                FavlocalDataSet.addAll(FavlocalDataSetCopy);
            } else {
                for (RecycleFavouriteFormat item : FavlocalDataSetCopy) {
                    if (item.getName().toLowerCase().contains(text)) {
                        FavlocalDataSet.add(item);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView BHIcon;
        TextView BHDetail;
        ImageButton BHRemoveBtn;

        public ViewHolder(View view) {
            super(view);
            view.setClickable(true);
            BHIcon = view.findViewById(R.id.BHIcon);
            BHDetail = view.findViewById(R.id.BHDetail);
            BHRemoveBtn = view.findViewById(R.id.BHRemoveBtn);
        }
    }

}