package com.example.forestlive.blecomera;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.blue.client.ClientActivity;
import com.blue.manager.Info;
import com.blue.server.ServerActivity;

public class MainActivity extends Activity implements View.OnClickListener{

    private BluetoothAdapter mAdapter;
    private ProgressDialog waitDialog;

    private IntentFilter mFilter = null;

    private RelativeLayout rl_server = null;
    private RelativeLayout rl_client = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initLayout();
        setFilter();
        setUpBlue();

    }



    private void initLayout() {
        rl_server = (RelativeLayout)findViewById(R.id.rl_server);
        rl_client = (RelativeLayout)findViewById(R.id.rl_client);
    }

    private void setFilter() {
        mFilter = new IntentFilter();
        mFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, mFilter);
    }

    private void setUpBlue() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mAdapter.isEnabled()) {
            setEnableBluetooth();
        }else {
            cheackPerInfo();
        }
    }

    private void setEnableBluetooth() {
        waitDialog = new ProgressDialog(this);
        waitDialog.setMessage("Bluetoothの起動中...");
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitDialog.setCancelable(false);
        waitDialog.show();

        mAdapter.enable();
    }

    private Boolean cheackPerInfo(){
        if (mAdapter.getBondedDevices().size() == 0) {
            Toast.makeText(getApplicationContext(),
                    "ペアリング情報がありません。接続する端末とペアリングを行ってください。",
                    Toast.LENGTH_LONG).show();

            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivityForResult(intent, Info.REQUEST_BLUE_SETTING);

            return false;
        }
        return true;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (mAdapter.isEnabled()) {
                    waitDialog.dismiss();
                    waitDialog = null;
                    cheackPerInfo();
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case Info.REQUEST_BLUE_SETTING:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()){
            case R.id.rl_server:
                Toast.makeText(this,"Next : " + "server",Toast.LENGTH_LONG).show();

                intent = new Intent(this, ServerActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_client:
                Toast.makeText(this, "Next : " + "client", Toast.LENGTH_LONG).show();
                intent = new Intent(this, ClientActivity.class);
                startActivity(intent);
                break;
        }
    }
}
