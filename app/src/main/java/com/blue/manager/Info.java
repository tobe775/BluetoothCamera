package com.blue.manager;

import android.os.Environment;

public class Info {

	/**
	 * Logの表示用
	 */
	public final static Boolean D = true;
	public final static Boolean I = true;
	public final static Boolean E = true;

	public final static Boolean show = true;

	public static Boolean sendImage = true;

    /**
     * Bluetoothの起動時の設定部分
     */
    public final static int REQUEST_BLUE_SETTING = 10;


	/**
	 * コネクトした場合のハンドラーメッセージ
	 **/
	public final static int HANDLER_CONNECT_INFO = 100;
	public final static int HANDLER_CONNECT_INFO_START = 101;
	public final static int HANDLER_CONNECT_INFO_CONNECTEND = 102;
	public final static int HANDLER_CONNECT_INFO_ERROR = 103;

	/**
	 * アクセプトした場合のハンドラーメッセージ
	 */
	public final static int HANDLER_ACCEPT_INFO = 200;
	public final static int HANDLER_ACCEPT_INFO_START = 201;
	public final static int HANDLER_ACCEPT_INFO_CONNECTEND = 202;
	public final static int HANDLER_ACCEPT_INFO_ERROR = 203;

	/**
	 * 時間の処理に関する部分
	 */
	public final static int HANDLER_TIME = 300;
	public final static int HANDLER_TIME_MAINTIME = 301;

	/**
	 * 転送した場合のハンドラーメッセージ
	 * */
	public final static int HANDLER_SEND_INFO = 400;
	public final static int HANDLER_SEND_INFO_START = 401;
	public final static int HANDLER_SEND_INFO_END = 402;

	/**
	 * 転送した場合のハンドラーメッセージ
	 */
	public final static int HANDLER_READ_INFO = 500;
	public final static int HANDLER_READ_INFO_START = 501;
	public final static int HANDLER_READ_INFO_END = 502;

	/**
	 * 受信データの情報
	 */
	public final static int HANDLER_DATA_BYTE = 600;
	public final static int HANDLER_DATA_BYTE_TOTAL = 601;
	public final static int HANDLER_DATA_BYTE_SERVER = 603;
	public final static int HANDLER_DATA_BYTE_CLIENT = 604;

	public final static int HANDLER_DELETE_MESSAGE = 700;

	/**
	 * 転送する画像ファイルのTYPE
	 */
	public final static int SEND_IMAGETYPE1 = 800;
	public final static int SEND_IMAGETYPE2 = 801;
	public final static int SEND_IMAGETYPE3 = 802;
	public final static int SEND_IMAGETYPE4 = 803;
	public final static String SEND_TYPE_NAME1 = "zikken/Im2000.jpg";
	public final static String SEND_TYPE_NAME2 = "zikken/Im4000.jpg";
	public final static String SEND_TYPE_NAME3 = "zikken/Im6000.jpg";
	public final static String SEND_TYPE_NAME4 = "images/ic_launcher.png";
	
	public final static int MESSAGE_TYPE_SENDDATA = 0;
	public final static int MESSAGE_TYPE_READFINISH = 1;

	/**
	 * 接続端末の情報
	 */
	public final static int HANDLER_DEVICE_INFO = 900;
	public final static int HANDLER_DEVICE_INFO_LIST = 901;

	/**
	 * 接続端末の情報
	 */
	public final static int HANDLER_CAMERA_INFO = 1000;
	public final static int HANDLER_CAMERA_INFO_SAVE_DATA = 1001;
	public final static String PATH_CAMERA = Environment
			.getExternalStorageDirectory() + "/camera_data.jpg";

	/**
	 * デバイスの選択部分
	 */
	// リザルトアクティビティを用いた場合のリクエストコード
	public final static int REQUEST_2PEERLISTVIEW = 2100;
	// Intentに格納した値のKey
	public final static String EXTRA_DEVICE_NAME = "NAME";
	// SharedPreferences のKey
	public final static String KYE_DEVICEINFO = "deviceInfo";
	// データ格納時の Key
	public final static String KYE_DEVICENAME = "deivicenames";

	/**
	 * 終了が確定した時 ロックする
	 */
	public static Boolean finish = false;

}
