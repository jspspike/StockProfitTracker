package com.example.jspspike.stockprofittracker;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.jjoe64.graphview.series.DataPoint;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by jspspike on 12/29/2016.
 */

public class UpdateHistoryActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        MainActivity.updateQuotes();

        MainActivity.profit = MainActivity.calculateTotalProfit();
        Date date = Calendar.getInstance().getTime();

        MainActivity.profitHistory.add(new DataPoint(date, MainActivity.profit));

        preferences = getApplicationContext().getSharedPreferences("storage", MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();

        String profitHistoryStorage = gson.toJson(MainActivity.profitHistory);

        editor.putString("ProfitHistory", profitHistoryStorage);
        editor.commit();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon_stocks)
                        .setContentTitle("Stock Profit")
                        .setContentText("Profit " + MainActivity.profit);

        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, mBuilder.build());

        finish();
    }
}
