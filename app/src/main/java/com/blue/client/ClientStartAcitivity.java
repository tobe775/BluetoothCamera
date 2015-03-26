package com.blue.client;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blue.manager.Info;
import com.example.forestlive.blecomera.R;


public class ClientStartAcitivity extends ActionBarActivity implements
        OnItemClickListener, OnClickListener {

    /**
     * Bluetooth
     */
    private BluetoothAdapter mAdapter = null;
    private ClientConnectService mClientConnectService = null;

    /**
     * Layout incident
     */
    private ListView lv_info;
    private ArrayAdapter<String> adapter = null;
    private ProgressBar pb_line;
    private Button bt_send;
    private FrameLayout cameraPreview;

    /**
     * Camera関連
     */
    private CameraPreview mCamPreview;
    private Camera mCam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.client_connect_main);

        setUpLayout();
        setUpBlue();
        getReceiveData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Info.finish = true;
        if (mClientConnectService != null) {
            mClientConnectService.cansel();
        }
    }

    private void setUpLayout() {
        lv_info = (ListView) findViewById(R.id.lv_info);
        lv_info.setOnItemClickListener(this);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);

        pb_line = (ProgressBar) findViewById(R.id.pb_line);
        pb_line.setMax(2);

        lv_info.setAdapter(adapter);

        bt_send = (Button) findViewById(R.id.bt_send);
        bt_send.setOnClickListener(this);
        bt_send.setVisibility(View.GONE);
        cameraPreview = (FrameLayout) findViewById(R.id.cameraPreview);

        /**
         * Set Action Bar Color
         */
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.server_bar)));
    }

    private void setUpBlue() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void getReceiveData() {
        String connectDev = getIntent().getStringExtra("name");
        for (BluetoothDevice device : mAdapter.getBondedDevices()) {
            if (device.getName().equals(connectDev)) {
                mClientConnectService = new ClientConnectService(this, mHandler);
                mClientConnectService.connectstart(device);
                pb_line.setProgress(1);
                return;
            }
        }
    }

    private void setUpCamera() {
        // カメラインスタンスの取得
        try {
            mCam = Camera.open();
            Camera.Parameters parameters = mCam.getParameters();
            parameters.setPreviewSize(640, 480);
            mCam.setParameters(parameters);
            mCamPreview = new CameraPreview(this, mCam, mHandler);
            cameraPreview.addView(mCamPreview);
        } catch (Exception e) {
            // エラー
            Log.d("OUT", "error", e);
        }
    }

    private void cancel() {

        if (mCamPreview != null) {
            mCamPreview.stopCamera();
            cameraPreview = null;
        }

        if (mClientConnectService != null) {
            mClientConnectService.cansel();
            mClientConnectService = null;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case Info.HANDLER_CONNECT_INFO:
                    if (Info.HANDLER_CONNECT_INFO_START == msg.arg1) {
                    } else if (Info.HANDLER_CONNECT_INFO_CONNECTEND == msg.arg1) {
                        adapter.add("接続しました");
                        pb_line.setProgress(3);
                        bt_send.setVisibility(View.VISIBLE);
                    } else if (Info.HANDLER_CONNECT_INFO_ERROR == msg.arg1) {
                        adapter.add("接続エラー");
                        pb_line.setProgress(0);
                        Toast.makeText(getApplicationContext(), "接続できませんでした．再度，接続処理をしてください．", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    break;

                case Info.HANDLER_CAMERA_INFO:
                    if (Info.HANDLER_CAMERA_INFO_SAVE_DATA == msg.arg1) {
                        mClientConnectService.sendData();
                    }
                    break;

                case Info.HANDLER_READ_INFO:
                    if (Info.HANDLER_READ_INFO_START == msg.arg1) {
                        pb_line.setProgress(3);
                    }
                    break;

                case Info.HANDLER_SEND_INFO:
                    if (Info.HANDLER_SEND_INFO_END == msg.arg1) {
                    }
                    break;

                case Info.HANDLER_DELETE_MESSAGE:
                    String deletedevise = (String) msg.obj;
                    finish();
                    break;

            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

    }

    @Override
    public void onClick(View v) {

        if (bt_send.getText().equals("TransferStart")) {
            bt_send.setBackgroundColor(Color.RED);
            bt_send.setText("TransferStop");
            setUpCamera();
            Toast.makeText(this, "転送開始", Toast.LENGTH_SHORT).show();
        } else if (bt_send.getText().equals("TransferStop")) {
            showFinishMessage();
        }

    }

    private void showFinishMessage() {
        AlertDialog.Builder finishDia = new AlertDialog.Builder(this);
        finishDia.setTitle("終了処理");
        finishDia.setMessage("システムを終了しますか");
        finishDia.setPositiveButton("いいえ",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        finishDia.setNegativeButton("はい",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancel();
                        finish();
                    }
                });

        AlertDialog adFinishi = finishDia.create();
        adFinishi.show();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (e.getAction() == KeyEvent.ACTION_UP) {
                showFinishMessage();
                return false;
            }
        }
        return super.dispatchKeyEvent(e);
    }
}
