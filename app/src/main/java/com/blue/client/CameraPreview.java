package com.blue.client;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.blue.manager.Info;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class CameraPreview extends SurfaceView implements
        SurfaceHolder.Callback, Camera.PreviewCallback {

    private Camera mCam = null;
    private Handler mHandler = null;

    /**
     * コンストラクタ
     */
    public CameraPreview(Context context, Camera cam, Handler mHandler) {
        super(context);

        this.mHandler = mHandler;
        this.mCam = cam;
        mCam.setDisplayOrientation(90);
        // サーフェスホルダーの取得とコールバック通知先の設定
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * SurfaceView 生成
     */
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("OUT", "surfaceCreated ");
        try {
            // カメラインスタンスに、画像表示先を設定
            mCam.setPreviewDisplay(holder);
            mCam.setPreviewCallback(this);
            // プレビュー開始
            mCam.startPreview();
        } catch (IOException e) {
            //
            Log.d("OUT", "surfaceCreated error ", e);
        }
    }

    public void stopCamera() {
        if (mCam != null) {

            mCam.release();
            mCam = null;
        }
    }

    /**
     * SurfaceHolder が変化したときのイベント
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (Info.sendImage) {
            Info.sendImage = false;
            if (data != null) {

                // 取得するデータ
                Camera.Parameters parameters = camera.getParameters();
                Size size = parameters.getPreviewSize();

                // Cameraから取得したデータとカメラサイズからimage画像を作成する．
                YuvImage image = new YuvImage(data,
                        parameters.getPreviewFormat(), size.width, size.height,
                        null);

                // Pathの作成
                String filePath = Environment.getExternalStorageDirectory()
                        + "/camera_data.jpg";

                // データの保存
                File file = new File(filePath);
                FileOutputStream filecon;
                try {
                    filecon = new FileOutputStream(file);
                    image.compressToJpeg(
                            new Rect(0, 0, image.getWidth(), image.getHeight()),
                            90, filecon);
                    mHandler.obtainMessage(Info.HANDLER_CAMERA_INFO,
                            Info.HANDLER_CAMERA_INFO_SAVE_DATA, 0)
                            .sendToTarget();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}