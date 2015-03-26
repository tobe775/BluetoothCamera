package com.blue.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.blue.manager.Info;
import com.blue.manager.ManagingThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServiceConnect {

    private Context mContext = null;
    private Handler mHandler = null;

    private BluetoothAdapter mAdapter = null;
    private ArrayList<ManagingThread> managingList = null;

    private final UUID MY_UUID = UUID
            .fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private String NAME = "BLUE_NET";

    private AcceptThread mAcceptThread = null;

    public ServiceConnect(Context mContext, Handler mHandler) {

        this.mContext = mContext;
        this.mHandler = mHandler;

        mAdapter = BluetoothAdapter.getDefaultAdapter();

        init();
    }

    private void init() {
        if (mAcceptThread != null) {
            mAcceptThread.cansel();
            mAcceptThread = null;
        }
        managingList = new ArrayList<ManagingThread>();
    }

    public void acceptstart() {
        if (mAcceptThread != null) {
            mAcceptThread.cansel();
            mAcceptThread = null;
        }

        mAcceptThread = new AcceptThread();
        mAcceptThread.start();
    }

    private void addManaging(BluetoothSocket socket, Boolean myServer) {
        Log.d("OUT", "addManaging");
        ManagingThread adds = null;
        adds = new ManagingThread(socket, mHandler, myServer, mContext);
        managingList.add(adds);
        connectInfoShow();
    }

    private void connectInfoShow() {
        ArrayList<String> connectList = new ArrayList<String>();
        for (ManagingThread manag : managingList) {
            connectList.add(manag.deviceName);
        }

        mHandler.obtainMessage(Info.HANDLER_DEVICE_INFO,
                Info.HANDLER_DEVICE_INFO_LIST, 0, connectList).sendToTarget();
    }

    public void deleteDevice(String name) {
        for (ManagingThread managing : managingList) {
            if (managing.deviceName.equals(name)) {
                managing.cancel();
                if (managingList.remove(managing)) {
                    Log.d("OUT", "消去成功");
                }
                connectInfoShow();
                return;
            }
        }
        connectInfoShow();

    }

    public List<String> getConnectList() {
        List<String> deviceList = new ArrayList<String>();

        Log.d("OUT", "managingList : " + managingList.size());
        for (ManagingThread device : managingList) {
            deviceList.add(device.deviceName);
        }

        return deviceList;
    }

    public void cancel() {
        if (mAcceptThread != null) {
            mAcceptThread.cansel();
            mAcceptThread = null;
        }

        for (ManagingThread managing : managingList) {
            if (managing != null) {
                managing.cancel();
                if (managingList.remove(managing)) {
                    Log.d("OUT", "消去成功");
                }
            }
        }
    }

    private class AcceptThread extends Thread {

        private BluetoothServerSocket mmServerSocket = null;
        private BluetoothSocket mmSocket = null;

        private Boolean cheack = true;

        public AcceptThread() {
            Log.d("OUT", "mAcceptThread");
            setName("AcceptTread");
            BluetoothServerSocket tmp = null;

            try {
                tmp = mAdapter
                        .listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
                this.mmServerSocket = tmp;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            super.run();
            Log.d("OUT", "mAcceptThread run");
            while (cheack) {

                mmSocket = null;
                try {
                    mHandler.obtainMessage(Info.HANDLER_ACCEPT_INFO,
                            Info.HANDLER_ACCEPT_INFO_START, 0).sendToTarget();
                    mmSocket = mmServerSocket.accept();
                    mHandler.obtainMessage(Info.HANDLER_ACCEPT_INFO,
                            Info.HANDLER_ACCEPT_INFO_CONNECTEND, 0)
                            .sendToTarget();
                    addManaging(mmSocket, false);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("OUT", "accept Error");
                }
            }
        }

        public void cansel() {
            cheack = false;
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
