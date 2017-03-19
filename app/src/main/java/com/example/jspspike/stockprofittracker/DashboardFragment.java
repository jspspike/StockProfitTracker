package com.example.jspspike.stockprofittracker;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Date;

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
        XYPlot graph = (XYPlot) view.findViewById(R.id.dashboard_graph);

        NumberFormat format = NumberFormat.getCurrencyInstance();
        money.setText(format.format(MainActivity.money));
        profit.setText(format.format(MainActivity.profit));

        final ArrayList<Date> lineXValues = new ArrayList<>();
        ArrayList<Double> lineYValues = new ArrayList<>();

        for (int i = 0; i < MainActivity.profitHistory.size(); i++) {
            lineXValues.add(MainActivity.profitHistory.get(i).x);
            lineYValues.add(MainActivity.profitHistory.get(i).y);
        }

        XYSeries lineSeries = new SimpleXYSeries(lineYValues, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Profit");

        LineAndPointFormatter lineFormat = new LineAndPointFormatter(getActivity(), R.xml.line_point_formatter_with_labels);

        lineFormat.setInterpolationParams(new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        graph.addSeries(lineSeries, lineFormat);

        graph.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format(){

            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(lineXValues.get(i));
            }

            @Override
            public Object parseObject(String source, @NonNull ParsePosition pos) {
                return null;
            }
        });

        return view;
    }
}
