package com.biotag.dogtagreading;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Administrator on 2017-09-15.
 */

public class BaseNFCActivity extends AppCompatActivity {

    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    /**
     * 启动Activity，界面可见时
     */
    @Override
    protected void onStart() {
        super.onStart();
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter==null){//判断设备是否支持NFC功能
            Toast.makeText(this,"设备不支持NFC功能!",Toast.LENGTH_SHORT);
            return;
        }
        if (!mNfcAdapter.isEnabled()){//判断设备NFC功能是否打开
            Toast.makeText(this,"请到系统设置中打开NFC功能!",Toast.LENGTH_SHORT);
            return;
        }
        //一旦截获NFC消息，就会通过PendingIntent调用窗口
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
    }
    /**
     * 获得焦点，按钮可以点击
     */
    @Override
    protected void onResume() {
        super.onResume();
        //设置处理优于所有其他NFC的处理
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }
    /**
     * 暂停Activity，界面获取焦点，按钮可以点击
     */
    @Override
    protected void onPause() {
        super.onPause();
        //恢复默认状态
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    public static class SharedPreferencesUtils {

        public static final String SP_NAME = "config";
        private static SharedPreferences sp;

        /**
         * 保存字符串
         *
         * @param context
         * @param key
         * @param value
         */
        public static void saveString(Context context, String key, String value) {
            if (sp == null) {
                sp = context.getSharedPreferences(SP_NAME, 0);
            }

            sp.edit().putString(key, value).commit();
        }
        public static void saveInt(Context context, String key, Integer value) {
            if (sp == null) {
                sp = context.getSharedPreferences(SP_NAME, 0);
            }

            sp.edit().putInt(key, value).commit();
        }

        public static void saveBoolean(Context context, String key, boolean value) {
            if (sp == null) {
                sp = context.getSharedPreferences(SP_NAME, 0);
            }

            sp.edit().putBoolean(key, value).commit();
        }

        public static boolean getBoolean(Context context, String key,
                                         boolean defValue) {
            if (sp == null) {
                sp = context.getSharedPreferences(SP_NAME, 0);
            }

            return sp.getBoolean(key, defValue);
        }
        public static int getInt(Context context, String key,
                                 Integer defValue) {
            if (sp == null) {
                sp = context.getSharedPreferences(SP_NAME, 0);
            }

            return sp.getInt(key, 0);
        }

        /**
         * 获取字符串
         *
         * @param context
         * @param key
         * @param defValue
         * @return
         */
        public static String getString(Context context, String key, String defValue) {
            if (sp == null) {
                sp = context.getSharedPreferences(SP_NAME, 0);
            }

            return sp.getString(key, defValue);
        }

    }
}
