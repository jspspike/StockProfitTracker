package com.example.jspspike.stockprofittracker;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.jjoe64.graphview.series.DataPoint;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by jspspike on 12/29/2016.
 */

public class UpdateHistoryActivity extends BroadcastReceiver {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Gson gson;

    @Override
    public void onReceive(Context context, Intent intent) {

        MainActivity.loadStoredData(context);
        MainActivity.updateQuotes();

        MainActivity.profit = MainActivity.calculateTotalProfit();
        Date date = Calendar.getInstance().getTime();

        MainActivity.profitHistory.add(new DataPoint(date, MainActivity.profit));

        MainActivity.createNoficiation(context);

        preferences = context.getSharedPreferences("storage", MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();

        String profitHistoryStorage = gson.toJson(MainActivity.profitHistory);

        editor.putString("ProfitHistory", profitHistoryStorage);
        editor.commit();


    }
}
