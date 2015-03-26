package com.blue.manager;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ManagingThread {

    private BluetoothSocket mmSocket = null;
    public String deviceName = "";
    private Handler mHandler = null;
    public Boolean isServer = true;
    private Context mContext = null;

    // Socketの生成
    private InputStream in = null;
    private OutputStream out = null;
    private DataInputStream dataIn = null;
    private DataOutputStream dataOut = null;

    // 送受信用のクラス
    private ReadThread mReadThread = null;
    private WriteThread mWriteThread = null;

    public ManagingThread(BluetoothSocket mmSocket, Handler mHandler,
                          Boolean isServer, Context mContext) {
        Log.d("OUT", "ManagingThread");
        this.mmSocket = mmSocket;
        this.mHandler = mHandler;
        this.isServer = isServer;
        this.mContext = mContext;
        this.deviceName = mmSocket.getRemoteDevice().getName();

        init();
        setUpStream();

    }

    private void init() {
        if (mReadThread != null) {
            mReadThread.cancel();
            mReadThread = null;
        }

        if (mWriteThread != null) {
            mWriteThread.cancel();
            mWriteThread = null;
        }
    }

    private void setUpStream() {
        Log.d("OUT", "setUpStream");

        try {
            in = this.mmSocket.getInputStream();
            out = this.mmSocket.getOutputStream();

            dataIn = new DataInputStream(in);
            dataOut = new DataOutputStream(out);

            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("OUT", "Managing setUpStream error", e);
        }
    }

    public synchronized void startWriteThread() {
        mWriteThread = new WriteThread();
        mWriteThread.start();
    }

    public void cancel() {
        if (mReadThread != null) {
            mReadThread.cancel();
            mReadThread = null;
        }

        if (mWriteThread != null) {
            mWriteThread.cancel();
            mWriteThread = null;
        }
    }

    private class ReadThread extends Thread {

        private Boolean cheack = true;

        public ReadThread() {
            Log.d("OUT", "ReadThread");
            setName("ReadThread");
            setName("readThread " + deviceName);
        }

        @Override
        public void run() {
            super.run();
            Log.d("OUT", "READTRHEAD START RUN");
            while (cheack) {
                try {

                    int type = dataIn.readInt();
                    switch (type) {
                        case Info.MESSAGE_TYPE_SENDDATA:
                            imageRead();
                            break;
                        case Info.MESSAGE_TYPE_READFINISH:
                            readfinish();
                            break;

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.obtainMessage(Info.HANDLER_DELETE_MESSAGE,
                            deviceName).sendToTarget();
                    return;
                }

            }
        }

        private void imageRead() throws IOException {
            mHandler.obtainMessage(Info.HANDLER_READ_INFO,
                    Info.HANDLER_READ_INFO_START, 0).sendToTarget();
            int total = dataIn.readInt();
            mHandler.obtainMessage(Info.HANDLER_READ_INFO,
                    Info.HANDLER_READ_INFO_START, 0).sendToTarget();

            byte[] buff = new byte[1024];
            int len = 0;
            int progress = 0;
            ByteArrayOutputStream imageOut = null;

            imageOut = new ByteArrayOutputStream();

            while (total > progress) {
                len = in.read(buff);
                imageOut.write(buff, 0, len);
                progress += len;
            }

            dataOut.writeInt(Info.MESSAGE_TYPE_READFINISH);

            mHandler.obtainMessage(Info.HANDLER_READ_INFO,
                    Info.HANDLER_READ_INFO_END, 0,
                    imageOut.toByteArray()).sendToTarget();
        }

        private void readfinish() {
            Info.sendImage = true;
        }

        public void cancel() {
            cheack = false;
            if (dataOut != null) {
                try {
                    dataOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dataOut = null;
            }
        }
    }

    private class WriteThread extends Thread {

        private byte[] bytes = null;
        private int total = 0;

        public WriteThread() {
            setName("WriteThread");
        }

        @Override
        public void run() {
            super.run();
            try {
                mHandler.obtainMessage(Info.HANDLER_SEND_INFO,
                        Info.HANDLER_SEND_INFO_START, 0).sendToTarget();
                createData();
                dataOut.writeInt(Info.MESSAGE_TYPE_SENDDATA);
                dataOut.writeInt(total);

                byte[] buff = new byte[1024];
                int len = -1;
                ByteArrayInputStream imageIn = new ByteArrayInputStream(bytes,
                        0, total);
                while ((len = imageIn.read(buff)) != -1) {
                    out.write(buff, 0, len);
                }

                // 送信通知
                mHandler.obtainMessage(Info.HANDLER_SEND_INFO,
                        Info.HANDLER_SEND_INFO_END, 0).sendToTarget();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void createData() {
            BufferedInputStream in = null;
            FileInputStream file;
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            try {
                file = new FileInputStream(
                        Environment.getExternalStorageDirectory()
                                + "/camera_data.jpg");
                in = new BufferedInputStream(file);

                Log.d("LoadVolumefile", "openFileInput");

                // 読み込み
                while (true) {
                    int len = 0;
                    try {
                        len = in.read(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (len < 0) {
                        break;
                    }

                    bao.write(buffer, 0, len);
                }

                // データの情報を抽出
                this.bytes = bao.toByteArray();
                this.total = bao.size();
            } catch (FileNotFoundException e) {
                Log.d("FILE not found", "not found");

            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
