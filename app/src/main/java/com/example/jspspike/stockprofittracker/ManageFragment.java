package com.example.jspspike.stockprofittracker;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by jspspike on 12/21/2016.
 */
public class ManageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.manage_layout, container, false);

        ListView listView = (ListView) view.findViewById(R.id.main_list);

        listView.setAdapter(MainActivity.adapter);

        listView.setOnItemLongClickListener(MainActivity.removeListener);

        return view;
    }

}
