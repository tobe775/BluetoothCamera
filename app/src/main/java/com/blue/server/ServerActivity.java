package com.blue.server;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.blue.manager.Info;
import com.example.forestlive.blecomera.R;


public class ServerActivity extends ActionBarActivity implements OnItemClickListener, OnCancelListener {

    /**
     * @author 接続関連
     */
    private ServiceConnect mServiceConnect = null;

    /**
     * @param Layout
     */
    private ImageView iv_image = null;
    private Boolean divConnected = false;

    // 待機中のレイアウト
    private ProgressDialog waitDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.server_main);
        setUpLayout();
        setServerAction();
        setWaitShow();
    }

    private void setUpLayout() {
        iv_image = (ImageView) findViewById(R.id.iv_image);

        /**
         * Set Action Bar Color
         */
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.client_bar)));
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void setServerAction() {
        mServiceConnect = new ServiceConnect(this, mHandler);
        mServiceConnect.acceptstart();
    }


    private void setWaitShow() {
        waitDialog = new ProgressDialog(this);
        waitDialog.setMessage("connection wait...");
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitDialog.setCancelable(true);
        waitDialog.setOnCancelListener(this);
        waitDialog.show();
    }

    private void setpicPhotoShow() {
        waitDialog = new ProgressDialog(this);
        waitDialog.setMessage("Photo data wait...");
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitDialog.setCancelable(false);
        waitDialog.setOnCancelListener(this);
        waitDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void cancel() {
        if (mServiceConnect != null) {
            mServiceConnect.cancel();
            mServiceConnect = null;
        }
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case Info.HANDLER_ACCEPT_INFO:
                    if (Info.HANDLER_ACCEPT_INFO_CONNECTEND == msg.arg1) {
                        Log.d("OUT", "ACCEPT_INFO_CONNECTEND");

                        // Dialogの削除
                        waitDialog.dismiss();
                        setpicPhotoShow();
                        divConnected = true;

                    }
                    break;

                case Info.HANDLER_DEVICE_INFO:
                    if (Info.HANDLER_DEVICE_INFO_LIST == msg.arg1) {

                    }
                    break;

                case Info.HANDLER_READ_INFO:
                    if (Info.HANDLER_READ_INFO_END == msg.arg1) {
                        if (waitDialog != null) {
                            waitDialog.dismiss();
                            waitDialog = null;
                        }

                        byte[] image = (byte[]) msg.obj;
                        Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
                        iv_image.setImageBitmap(bmp);
                    }
                    break;

                case Info.HANDLER_DELETE_MESSAGE:
                    finishAcitivity();
                    break;
            }
        }
    };

    private void finishAcitivity() {
        cancel();
        finish();
    }

    private void showFinishMessage() {

        AlertDialog.Builder finishDia = new AlertDialog.Builder(this);
        finishDia.setTitle("終了処理");
        finishDia.setMessage("システムを終了しますか");
        finishDia.setPositiveButton("いいえ",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!divConnected) {
                            setWaitShow();
                        }
                    }
                });
        finishDia.setNegativeButton("はい",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAcitivity();
                    }
                });

        AlertDialog adFinish = finishDia.create();
        adFinish.show();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (e.getAction() == KeyEvent.ACTION_UP) {
                if (divConnected) {
                    Toast.makeText(this, "Client端末から終了して下さい", Toast.LENGTH_SHORT).show();
                } else {
                    showFinishMessage();
                }
                return false;
            }
        }
        return super.dispatchKeyEvent(e);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        showFinishMessage();
    }
}
