package com.example.jspspike.stockprofittracker;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.NumberFormat;

/**
 * Created by jspspike on 12/21/2016.
 */
public class DashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dashboard_layout, container, false);

        TextView money = (TextView) view.findViewById(R.id.dashboard_money);
        TextView profit = (TextView) view.findViewById(R.id.dashboard_profit);
        GraphView graph = (GraphView) view.findViewById(R.id.dashboard_graph);

        NumberFormat format = NumberFormat.getCurrencyInstance();
        money.setText(format.format(MainActivity.money));
        profit.setText(format.format(MainActivity.profit));


        DataPoint[] array = new DataPoint[MainActivity.profitHistory.size()];
        array = MainActivity.profitHistory.toArray(array);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(array);

        graph.addSeries(series);

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);
        if (array.length > 0) {
            graph.getViewport().setMinX(array[0].getX());
            graph.getViewport().setMaxX(array[array.length - 1].getX());
        }
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setXAxisBoundsManual(true);

        return view;
    }
}
