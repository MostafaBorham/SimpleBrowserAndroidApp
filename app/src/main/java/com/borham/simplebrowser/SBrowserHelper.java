package com.borham.simplebrowser;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.borham.simplebrowser.MyRoom.BrowseHistoryDao;
import com.borham.simplebrowser.MyRoom.BrowseHistoryFormat;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SBrowserHelper {
    public static String SBrowserAppLinkOnStore = "https://stackoverflow.com/questions/7545254";
    public static int current_nav_id = R.id.home_btn;
    public static String fragmentType = "";
    public static boolean is_configs_change;
    public static String DBKey_extra = "";
    public final static String Arabic_Code="ar";
    public final static String English_Code="en";
    public final static int NOTIFICATION_CHANNEL_ID=100;

    public static Drawable StringToDrawable(Context context, String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return new BitmapDrawable(context.getResources(), bitmap);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static String DrawableToString(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Drawable createDrawableFromUri(Context context, String uri) {
        InputStream myInput;
        try {
            myInput = context.getAssets().open(uri);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inDensity = DisplayMetrics.DENSITY_HIGH;
            return Drawable.createFromResourceStream(context.getResources(), null, myInput, null, opts);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PopupMenu createPopupMenu(Context context, View rootView, int MenuRes) {
        PopupMenu popup = new PopupMenu(context, rootView);
        popup.getMenuInflater().inflate(MenuRes, popup.getMenu());
        return popup;
    }

    public static void openWebLink(Context context, String uri) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        Intent chooser = Intent.createChooser(sendIntent, "Complete Action Using:");
        context.startActivity(chooser);
    }

    public static void storeClickForBrowseHistoryAndMostVisited(String key, SharedPreferences.Editor editor, SharedPreferences sharedPreferences, BrowseHistoryDao historyDao) {
        int visit_times = sharedPreferences.getInt(key, 0);
        editor.putInt(key, ++visit_times);
        editor.apply();

        historyDao.insert(new BrowseHistoryFormat(key, getCurrentDate() + "," + getCurrentTime()));
    }

    public static float pixelsToSp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }

    public static AlertDialog createAlertDialog(Context context, int iconId, String title) {
        return new AlertDialog.Builder(context)
                .setIcon(iconId)
                .setTitle(title)
                .setCancelable(false)
                .setMessage(context.getString(R.string.alert_message))
                .setPositiveButton(context.getString(R.string.alert_yes_btn), null)
                .setNegativeButton(context.getString(R.string.alert_no_btn), null)
                .show();
    }

    public static ArrayList<String> readFileFromAssests(Context context, int FileResId) {
        ArrayList<String> list = new ArrayList<>();
        InputStream inputStream = context.getResources().openRawResource(FileResId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        if (inputStream != null) {
            try {
                String s;
                while ((s = reader.readLine()) != null) {
                    list.add(s);
                }
                inputStream.close();
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static Map<String, Integer> sortMapList(Map<String, Integer> map) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, (o1, o2) -> (o1.getValue()).compareTo(o2.getValue()));
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    @NonNull
    public static String[] getEnglishStringsArray(Context context, int arrResId) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(new Locale(English_Code));
        return context.createConfigurationContext(configuration).getResources().getStringArray(arrResId);
    }

    public static String[] getArabicStringsArray(Context context, int arrResId) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(new Locale(Arabic_Code));
        return context.createConfigurationContext(configuration).getResources().getStringArray(arrResId);
    }

    @NonNull
    public static String getArabicString(Context context, int arrResId) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(new Locale(Arabic_Code));
        return context.createConfigurationContext(configuration).getResources().getString(arrResId);
    }

    @NonNull
    public static String getEnglishString(Context context, int arrResId) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(new Locale(English_Code));
        return context.createConfigurationContext(configuration).getResources().getString(arrResId);
    }

    public static void setLocaleForApp(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public static String remove_spaces(String text) {
        StringBuilder new_text = new StringBuilder();
        boolean is_spaced = false;
        if (text.length() > 0) {
            if (text.charAt(0) == ' ') {
                is_spaced = true;
            }
        }
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ' ' && is_spaced) {
            } else if (text.charAt(i) == ' ' && !is_spaced) {
                new_text.append(text.charAt(i));
                is_spaced = true;
            } else {
                new_text.append(text.charAt(i));
                is_spaced = false;
            }
        }
        if (new_text.toString().endsWith(" ")) {
            new_text = new StringBuilder(new_text.substring(0, new_text.length() - 1));
        }
        return new_text.toString();
    }

    public static String getCurrentDate() {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date());
    }

    public static String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date());
    }

    public static String getStringResourceByName(Context context, String aString) {
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(aString, "string", packageName);
        return context.getString(resId);
    }

    public static String convertToArabic(String englishString) {
        StringBuilder arabicString = new StringBuilder();
        if (Locale.getDefault().getLanguage().equals(Arabic_Code)) {
            for (char c : englishString.toCharArray()) {
                if (c == '0') {
                    arabicString.append((c + "").replace('0', '٠'));
                } else if (c == '1') {
                    arabicString.append((c + "").replace('1', '١'));
                } else if (c == '2') {
                    arabicString.append((c + "").replace('2', '٢'));
                } else if (c == '3') {
                    arabicString.append((c + "").replace('3', '٣'));
                } else if (c == '4') {
                    arabicString.append((c + "").replace('4', '٤'));
                } else if (c == '5') {
                    arabicString.append((c + "").replace('5', '٥'));
                } else if (c == '6') {
                    arabicString.append((c + "").replace('6', '٦'));
                } else if (c == '7') {
                    arabicString.append((c + "").replace('7', '٧'));
                } else if (c == '8') {
                    arabicString.append((c + "").replace('8', '٨'));
                } else if (c == '9') {
                    arabicString.append((c + "").replace('9', '٩'));
                } else {
                    arabicString.append(c);
                }
            }
        } else {
            arabicString = new StringBuilder(englishString);
        }
        return arabicString.toString();
    }

    public static void setCustomActionBar(AppCompatActivity activity, int titleRes, int iconRes) {
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(activity.getString(titleRes));
        actionBar.setHomeAsUpIndicator(iconRes);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public static boolean checkInternet(Context context) {
        boolean connected;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
        return connected;
    }

    public static void pushNotification(Activity activity, Context context, int nameRes, int descriptionRes, int ID, int icon, String title, String content){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(nameRes);
            String description = context.getString(descriptionRes);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(String.valueOf(ID), name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        Intent intent =activity.getIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,String.valueOf(ID))
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(ID, builder.build());
    }

}