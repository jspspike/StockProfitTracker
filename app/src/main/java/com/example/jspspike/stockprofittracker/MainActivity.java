package com.example.jspspike.stockprofittracker;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jjoe64.graphview.series.DataPoint;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import yahoofinance.quotes.stock.StockQuote;




public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public static ArrayAdapter<Stock> adapter;
    public static AdapterView.OnItemLongClickListener removeListener;

    static SharedPreferences preferences;
    static SharedPreferences.Editor editor;
    static Gson gson;

    Menu menu;

    static ArrayList<Stock> stocks;
    static ArrayList<StockQuote> quotes;
    public static ArrayList<DataPoint> profitHistory;

    public static double money;
    public static double profit;

    private PendingIntent alarmIntent;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadStoredData(getApplicationContext());

        Intent intent = new Intent(MainActivity.this, UpdateHistoryActivity.class);
        alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager == null) {
            alarmManager = (AlarmManager) MainActivity.this.getSystemService(MainActivity.this.ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 16);
            calendar.set(Calendar.MINUTE, 2);
            calendar.set(Calendar.SECOND, 0);

            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
            editor.putBoolean("AlarmStarted", true);
            editor.commit();
        }

        else {
            Log.i("Alarm", "Started");
//            editor.putBoolean("AlarmStarted", false);
        }

        updateQuotes();

        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, new DashboardFragment()).commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        removeListener = new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                String[] options = {"Sell", "Remove", "Remove Money", "Add Money"};

                new AlertDialog.Builder(MainActivity.this)
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 1) {

                                    money -= stocks.get(position).getMoney();

                                    stocks.remove(position);
                                    quotes.remove(position);

                                    String stocksStorage = gson.toJson(stocks);

                                    editor.putString("Stocks", stocksStorage);
                                    editor.putLong("Money", Double.doubleToLongBits(money));
                                    editor.commit();

                                    updateList();
                                }

                                else if (which == 0) {
                                    LayoutInflater inflater = getLayoutInflater();
                                    View sellDialog = inflater.inflate(R.layout.sell_dialog, null);

                                    final EditText sellAmount = (EditText) sellDialog.findViewById(R.id.sell_amount_dialog);

                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("Set amount being sold")
                                            .setView(sellDialog)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    try {
                                                        stocks.get(position).sellAmount(Integer.parseInt(sellAmount.getText().toString()));
                                                        String stocksStorage = gson.toJson(stocks);

                                                        editor.putString("Stocks", stocksStorage);
                                                        editor.putLong("Money", Double.doubleToLongBits(money));
                                                        editor.commit();

                                                        updateList();
                                                    } catch (InvalidInputException e) {
                                                        e.printStackTrace();
                                                        new AlertDialog.Builder(MainActivity.this)
                                                                .setTitle("Invalid stock symbol")
                                                                .setPositiveButton("OK",  new DialogInterface.OnClickListener() {

                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {

                                                                    }
                                                                });
                                                    }
                                                }
                                            })
                                            .show();
                                }

                                else if (which == 2) {
                                    LayoutInflater inflater = getLayoutInflater();
                                    View removeDialog = inflater.inflate(R.layout.remove_money_dialog, null);

                                    final EditText removeAmount = (EditText) removeDialog.findViewById(R.id.amount_remove_dialog);

                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("Set amount being removed")
                                            .setView(removeDialog)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    stocks.get(position).setMoney(stocks.get(position).getMoney() - Double.parseDouble(removeAmount.getText().toString()));
                                                    money -=  Double.parseDouble(removeAmount.getText().toString());

                                                    String stocksStorage = gson.toJson(stocks);

                                                    editor.putString("Stocks", stocksStorage);
                                                    editor.putLong("Money", Double.doubleToLongBits(money));
                                                    editor.commit();

                                                    updateList();
                                                }
                                            })
                                            .show();
                                }

                                else if (which == 3) {
                                    LayoutInflater inflater = getLayoutInflater();
                                    View addDialog = inflater.inflate(R.layout.add_money_dialog, null);

                                    final EditText addAmount = (EditText) addDialog.findViewById(R.id.amount_add_dialog);

                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("Set amount being added")
                                            .setView(addDialog)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    stocks.get(position).setMoney(stocks.get(position).getMoney() + Double.parseDouble(addAmount.getText().toString()));
                                                    money += Double.parseDouble(addAmount.getText().toString());

                                                    String stocksStorage = gson.toJson(stocks);

                                                    editor.putString("Stocks", stocksStorage);
                                                    editor.putLong("Money", Double.doubleToLongBits(money));
                                                    editor.commit();

                                                    updateList();
                                                }
                                            })
                                            .show();
                                }

                            }
                        })
                        .show();

                return true;
            }
        };
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        if (this.menu != null)
            this.menu.getItem(1).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add) {
            LayoutInflater inflater = getLayoutInflater();
            final View infoDialog = inflater.inflate(R.layout.add_stock_dialog, null);

            final EditText symbol = (EditText) infoDialog.findViewById(R.id.dialog_symbol);
            final EditText amount = (EditText) infoDialog.findViewById(R.id.dialog_amount);
            final EditText date = (EditText) infoDialog.findViewById(R.id.dialog_date);

            final Calendar calendar = Calendar.getInstance();

            date.setText(String.valueOf(calendar.get(Calendar.MONTH) + 1) + "/" + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(calendar.get(Calendar.YEAR)));
            date.setInputType(InputType.TYPE_NULL);
            date.setTextIsSelectable(true);

            final Calendar finalDate = Calendar.getInstance();

            date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar calendar = Calendar.getInstance();


                    DatePickerDialog datePicker = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            EditText date = (EditText) infoDialog.findViewById(R.id.dialog_date);

                            String dateString = String.valueOf(monthOfYear + 1) + "/" + String.valueOf(dayOfMonth) +"/"+ String.valueOf(year);

                            finalDate.set(Calendar.YEAR, year);
                            finalDate.set(Calendar.MONTH, monthOfYear);
                            finalDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            date.setText(dateString);
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    datePicker.show();
                }
            });

            new AlertDialog.Builder(this)
                    .setTitle("Add Stock")
                    .setMessage("Add stock by date or by price")
                    .setPositiveButton("Price", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("Date", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Enter Purchase Information")
                                    .setView(infoDialog)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            boolean instanceExists = false;

                                            try {

                                                for (int i = 0; i < stocks.size(); i++) {
                                                    if (symbol.getText().toString().toUpperCase().equals(stocks.get(i).getStockSymbol().toUpperCase())) {
                                                        stocks.get(i).addInstance(Integer.parseInt(amount.getText().toString()), finalDate);
                                                        instanceExists = true;
                                                        break;
                                                    }
                                                }

                                                if (!instanceExists)
                                                    stocks.add(new Stock(symbol.getText().toString(), Integer.parseInt(amount.getText().toString()), finalDate));

                                                updateQuotes();
                                                updateList();

                                                String stocksStorage = gson.toJson(stocks);

                                                editor.putString("Stocks", stocksStorage);
                                                editor.commit();
                                            }

                                            catch (InvalidInputException e) {
                                                new AlertDialog.Builder(MainActivity.this)
                                                        .setTitle("Invalid Date")
                                                        .setMessage("You have entered an invalid date or symbol")
                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                            }
                                                        })
                                                .show();
                                                e.printStackTrace();
                                            }
                                        }
                                    })
                                    .show();
                        }
                    })
                    .show();
        }

        else if (id == R.id.action_refresh) {
            updateQuotes();
            updateList();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (id == R.id.nav_dashboard) {
            profit = calculateTotalProfit();
            fragmentTransaction.replace(R.id.main_container, new DashboardFragment()).commit();
            if (menu != null)
                menu.getItem(1).setVisible(false);
        } else if (id == R.id.nav_manage) {
            adapter = new ListAdapter();
            fragmentTransaction.replace(R.id.main_container, new ManageFragment()).commit();
            if (menu != null)
                menu.getItem(1).setVisible(true);
        } else if (id == R.id.nav_share) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void createNoficiation(Context context) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.icon_stocks)
                        .setContentTitle("Stock Profit")
                        .setContentText("Profit " + MainActivity.profit);

        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, mBuilder.build());
    }

    public static void loadStoredData(Context context) {
        preferences = context.getSharedPreferences("storage", MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();

        String stocksStorage = preferences.getString("Stocks", null);

        if (stocksStorage == null) {
            stocks = new ArrayList<>();
            quotes = new ArrayList<>();
        }
        else {
            stocks = gson.fromJson(stocksStorage, new TypeToken<ArrayList<Stock>>(){}.getType());
            updateQuotes();
            Log.i("stocks", "Found stocks");
        }

        money = Double.longBitsToDouble(preferences.getLong("Money", Double.doubleToLongBits(0)));
        profit = calculateTotalProfit();

        String profitsStorage = preferences.getString("ProfitHistory", null);

        if (profitsStorage == null){
            profitHistory = new ArrayList<>();
        } else {
            profitHistory = gson.fromJson(profitsStorage, new TypeToken<ArrayList<DataPoint>>(){}.getType());
        }
    }

    public static void updateQuotes() {
        QuoteLookup lookup = new QuoteLookup();

        String[] symbols = new String[stocks.size()];

        for (int i = 0; i < stocks.size(); i++) {
            symbols[i] = stocks.get(i).getStockSymbol();
        }

        try {
            quotes = lookup.execute(symbols).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    public void updateList() {

        adapter = new ListAdapter();

        ListView list = (ListView) findViewById(R.id.main_list);

        if (list == null)
            list = (ListView) findViewById(R.id.main_list);
        if (list != null) {
            list.setAdapter(adapter);

            list.setOnItemLongClickListener(removeListener);
        }
    }

    public static double calculateTotalProfit() {

        double profit = 0;

        for (int position = 0; position < stocks.size(); position++) {
            Stock currentStock = stocks.get(position);

            double profitTotal = (currentStock.getTotalAmount() * quotes.get(position).getPrice().doubleValue()) + currentStock.getMoney();

            ArrayList<Instance> instances = stocks.get(position).getInstances();

            for (int i = 0; i < instances.size(); i++) {
                profitTotal -= (instances.get(i).getAmount() * instances.get(i).getPurchaseAmount().doubleValue());
            }

            profit += profitTotal;
        }

        return profit;
    }

    public class ListAdapter extends ArrayAdapter<Stock> {
        public ListAdapter() {
            super(MainActivity.this, R.layout.manage_item, stocks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;

            if (itemView == null)
                itemView = getLayoutInflater().inflate(R.layout.manage_item, parent, false);

            Stock currentStock = stocks.get(position);

            TextView name = (TextView) itemView.findViewById(R.id.stock_name);
            TextView symbol = (TextView) itemView.findViewById(R.id.stock_symbol);
            TextView amount = (TextView) itemView.findViewById(R.id.stock_amount);
            TextView change = (TextView) itemView.findViewById(R.id.stock_change);
            TextView changePcnt = (TextView) itemView.findViewById(R.id.stock_change_pcnt);
            TextView profit = (TextView) itemView.findViewById(R.id.stock_profit);
            TextView money = (TextView) itemView.findViewById(R.id.stock_money);

            NumberFormat format = NumberFormat.getCurrencyInstance();
            DecimalFormat decimalFormat = new DecimalFormat("#.00");

            name.setText(currentStock.getName());
            symbol.setText(currentStock.getStockSymbol().toUpperCase());
            change.setText(format.format(quotes.get(position).getChange().doubleValue()));
            changePcnt.setText(decimalFormat.format(quotes.get(position).getChangeInPercent().doubleValue()) + "%");
            money.setText(format.format(currentStock.getMoney()));

            if (quotes.get(position).getChange().doubleValue() > 0)
                change.setTextColor(Color.rgb(13, 145, 30));
            else if (quotes.get(position).getChange().doubleValue() < 0)
                change.setTextColor(Color.RED);

            if (quotes.get(position).getChangeInPercent().doubleValue() > 0)
                changePcnt.setTextColor(Color.rgb(13, 145, 30));
            else if (quotes.get(position).getChangeInPercent().doubleValue() < 0)
                changePcnt.setTextColor(Color.RED);


            double profitTotal = (currentStock.getTotalAmount() * quotes.get(position).getPrice().doubleValue()) + currentStock.getMoney();

            ArrayList<Instance> instances = stocks.get(position).getInstances();

            for (int i = 0; i < instances.size(); i++) {
                profitTotal -= (instances.get(i).getAmount() * instances.get(i).getPurchaseAmount().doubleValue());
            }

            amount.setText(currentStock.getTotalAmount() + "");
            profit.setText(format.format(profitTotal));

            notifyDataSetChanged();

            return itemView;
        }
    }
}
