package com.blue.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.blue.manager.Info;
import com.blue.manager.ManagingThread;

public class ClientConnectService {

    private int count = 0;

    private Context mContext = null;
    private Handler mHandler = null;

    private static ManagingThread mManagingThread = null;

    private final UUID MY_UUID = UUID
            .fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    private ConnectThread mConnectThread = null;

    /**
     * @param 初期処理
     */
    public ClientConnectService(Context mContext, Handler mHandler) {

        this.mContext = mContext;
        this.mHandler = mHandler;

        init();
    }

    private void init() {

        if (mConnectThread != null) {
            mConnectThread.cansel();
            mConnectThread = null;
        }

    }

    public void connectstart(BluetoothDevice device) {
        if (mConnectThread != null) {
            mConnectThread.cansel();
            mConnectThread = null;
        }

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
    }

    /**
     * @param 接続後の処理
     */
    private void addManaging(BluetoothSocket socket, Boolean myServer) {
        Log.d("OUT", "addManaging");
        mManagingThread = new ManagingThread(socket, mHandler, myServer, mContext);

        connectInfoShow();
    }

    /**
     * @param　接続後のデータ取得と転送処理
     */
    private void connectInfoShow() {
        ArrayList<String> connectList = new ArrayList<String>();
        mHandler.obtainMessage(Info.HANDLER_DEVICE_INFO,
                Info.HANDLER_DEVICE_INFO_LIST, 0, connectList).sendToTarget();
    }

    public void sendData() {
        if (mManagingThread != null) {
            Log.d("OUT", mManagingThread.deviceName + "に転送いたします");
            mManagingThread.startWriteThread();
        }
    }


    /**
     * @param 再接続処理
     */
    public void reconnect(BluetoothDevice mDevice) {
        if (mConnectThread != null) {
            mConnectThread.cansel();
            mConnectThread = null;
        }

        mConnectThread = new ConnectThread(mDevice);
        mConnectThread.start();

    }

    public void cansel() {

        if (mManagingThread != null) {
            mManagingThread.cancel();
        }

        if (mConnectThread != null) {
            mConnectThread.cansel();
            mConnectThread = null;
        }
    }

    private class ConnectThread extends Thread {

        private BluetoothDevice mDevice = null;
        private BluetoothSocket mSocket = null;

        public ConnectThread(BluetoothDevice mDevice) {
            setName("ConnectThread " + mDevice.getName());
            this.mDevice = mDevice;

            try {
                mSocket = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            super.run();
            count++;
            try {
                mHandler.obtainMessage(Info.HANDLER_CONNECT_INFO,
                        Info.HANDLER_CONNECT_INFO_START, count).sendToTarget();
                this.mSocket.connect();
                mHandler.obtainMessage(Info.HANDLER_CONNECT_INFO,
                        Info.HANDLER_CONNECT_INFO_CONNECTEND, 0).sendToTarget();
                addManaging(mSocket, true);
                return;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("OUT", "connect error", e);
                mHandler.obtainMessage(Info.HANDLER_CONNECT_INFO,
                        Info.HANDLER_CONNECT_INFO_ERROR, 0).sendToTarget();
            }
        }

        public void cansel() {
        }
    }
}
