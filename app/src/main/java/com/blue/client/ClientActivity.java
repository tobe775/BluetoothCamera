package com.blue.client;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.forestlive.blecomera.R;


public class ClientActivity extends ActionBarActivity implements OnItemClickListener {

    private BluetoothAdapter mAdapter = null;

    /**
     * Layout
     */
    private ListView lv_list = null;
    private ArrayAdapter<String> adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.select_device);
        setUpLayout();
        setUpBlue();


    }

    private void setUpLayout() {

        /**
         * Layout
         */
        lv_list = (ListView) findViewById(R.id.lv_list);
        lv_list.setOnItemClickListener(this);

        /**
         * Set Adapter
         */
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
        lv_list.setAdapter(adapter);

        /**
         * Set Action Bar Color
         */
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.server_bar)));

    }

    private void setUpBlue() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        setMyListData();
    }

    private void setMyListData() {
        adapter.clear();
        for (BluetoothDevice devices : mAdapter.getBondedDevices()) {
            adapter.add(devices.getName());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Log.d("OUT",
                "point ->" + position + " name -> " + adapter.getItem(position));

        Intent intent = new Intent(this, ClientStartAcitivity.class);
        intent.putExtra("name", adapter.getItem(position));
        startActivity(intent);
        finish();
    }
}
