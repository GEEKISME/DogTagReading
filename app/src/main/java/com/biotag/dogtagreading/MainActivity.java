package com.biotag.dogtagreading;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.biotag.commons.MacInfoItemBean;
import com.biotag.commons.MacInfoItemBean_;
import com.biotag.commons.ObjectBoxIni;
import com.biotag.commons.utils.CommonUtils;

import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import androidx.core.app.ActivityCompat;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.Query;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class MainActivity extends BaseNFCActivity  {

    private ImageView iv_readdogtag;
    public static final int REQUEST_PERMISSION_CAMERA = 20, REQUEST_BARCODE = 21;
    private String dogtagUrl;
    private Tag detecttag;
    private String dogtag_fromcamera, dogtag_fromnfc;
    private TextView tv_tip;
    private TextView tv_tip2;
    private Vibrator vibrator;
    private String result, temp;
    private TextView ttv_camera;
    private TextView ttv_nfc, tv_test_result;
//    private EditText et_macdes;
//    private Button btn_sure;
    private Box<MacInfoItemBean> macinfoDB;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        initDB();

        int versionCode = CommonUtils.getVersionCode(this);
        Toast.makeText(this, "当前版本号:"+versionCode, Toast.LENGTH_SHORT).show();
    }

    private void initDB() {
        BoxStore boxStore = ObjectBoxIni.getBoxStore();
        macinfoDB = boxStore.boxFor(MacInfoItemBean.class);
    }

    private void initView() {
        iv_readdogtag = (ImageView) findViewById(R.id.iv_readdogtag);
        iv_readdogtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
                    return;
                }
                ttv_camera.setText("");
                ttv_nfc.setText("");
                tv_test_result.setText("");
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivityForResult(intent, REQUEST_BARCODE);
                ttv_camera.setText("");
            }
        });

        //        String tt = "123";
        //        try {
        //            String in = ThreeDesUtils.get3DES("123");
        //            Log.i("tms","in is: "+in);
        //            String out = ThreeDesUtils.decode3DES(in);
        //            Log.i("tms","out is "+out);
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }

        tv_tip = (TextView) findViewById(R.id.tv_tip);
        tv_tip2 = (TextView) findViewById(R.id.tv_tip2);
        ttv_camera = (TextView) findViewById(R.id.ttv_camera);
        ttv_nfc = (TextView) findViewById(R.id.ttv_nfc);
        tv_test_result = (TextView) findViewById(R.id.tv_test_result);
        String tt = "123bcdjbsj";
        String en = CreateEncryptByte(tt, Constants.DES3KEY);
        Log.i("tms", "加密后是： " + en);
        String tts = CreateDecryptByte(en, Constants.DES3KEY);
        Log.i("tms", "解密后是： " + tts);

//        et_macdes = (EditText) findViewById(R.id.et_macdes);
//
//        btn_sure = (Button) findViewById(R.id.btn_sure);
//        btn_sure.setOnClickListener(val->{
//            String s = et_macdes.getText().toString();
//            if(s.equals("")){
//                Toast.makeText(this, "内容为空~", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            temp = CreateDecryptByte(s, Constants.DES3KEY);
//            temp = temp.toLowerCase();
//            Log.i("tms", "temp is:  " + temp);
//            ttv_camera.setText(new StringBuilder().append("二维码:     ").append(temp).toString());
//            iv_readdogtag.setVisibility(View.GONE);
//            tv_tip.setVisibility(View.GONE);
//            tv_tip2.setVisibility(View.VISIBLE);
//        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BARCODE && resultCode == RESULT_OK) {
            if (data != null) {
                dogtagUrl = data.getStringExtra("result");
                dogtag_fromcamera = dogtagUrl.substring(dogtagUrl.indexOf("="));
                dogtag_fromcamera = dogtag_fromcamera.substring(1);
                Log.i("tms", "dogtag_fromcamera is:  " + dogtag_fromcamera);
                temp = CreateDecryptByte(dogtag_fromcamera, Constants.DES3KEY);
                temp = temp.toLowerCase();
                Log.i("tms", "temp is:  " + temp);
                ttv_camera.setText(new StringBuilder().append("二维码:     ").append(temp).toString());
                Toast.makeText(this, "获取犬牌号成功，请将犬牌靠近手机以读取芯片号", Toast.LENGTH_SHORT).show();
                iv_readdogtag.setVisibility(View.GONE);
                tv_tip.setVisibility(View.GONE);
                tv_tip2.setVisibility(View.VISIBLE);
                Log.i("tms", "dogtag_fromcamera is:  " + dogtag_fromcamera);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        detecttag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        byte[] b = detecttag.getId();
        result = Utils.bytesToHexString(b);
        Log.i("tms", "result is: " + result);
        ttv_nfc.setText(new StringBuilder().append("NFC    :    ").append(result).toString());
        if (temp != null && !temp.equals("") && temp.equals(result)) {
            Toast.makeText(this, "犬牌验证成功！", Toast.LENGTH_SHORT).show();
            tv_tip2.setVisibility(View.GONE);
            tv_tip.setVisibility(View.VISIBLE);
            iv_readdogtag.setVisibility(View.VISIBLE);
            dogtag_fromcamera = "";
            dogtag_fromnfc = "";
            tv_test_result.setText("匹配成功");
            putTheMacIntoDB(result);
        } else {
            vibrator.vibrate(400);
            Toast.makeText(this, "二维码扫描结果与nfc读取结果不一致！", Toast.LENGTH_SHORT).show();
            tv_tip2.setVisibility(View.GONE);
            tv_tip.setVisibility(View.VISIBLE);
            iv_readdogtag.setVisibility(View.VISIBLE);
            dogtag_fromcamera = "";
            dogtag_fromnfc = "";
            tv_test_result.setText("匹配失败");

        }
        //        dogtag_fromnfc = CreateEncryptByte(result, Constants.DES3KEY);
        //                ttv_nfc.setText(dogtag_fromnfc);
        //        if (dogtag_fromcamera.equals(dogtag_fromnfc)) {
        //            Toast.makeText(this, "犬牌验证成功！", Toast.LENGTH_SHORT).show();
        //            tv_tip2.setVisibility(View.GONE);
        //            tv_tip.setVisibility(View.VISIBLE);
        //            iv_readdogtag.setVisibility(View.VISIBLE);
        //            dogtag_fromcamera = "";
        //            dogtag_fromnfc = "";
        //        } else {
        //            vibrator.vibrate(400);
        //            Toast.makeText(this, "二维码扫描结果与nfc读取结果不一致！", Toast.LENGTH_SHORT).show();
        //            tv_tip2.setVisibility(View.GONE);
        //            tv_tip.setVisibility(View.VISIBLE);
        //            iv_readdogtag.setVisibility(View.VISIBLE);
        //            dogtag_fromcamera = "";
        //            dogtag_fromnfc = "";
        //        }
    }

    private void putTheMacIntoDB(String result) {
        //=首先检索该Mac地址在数据库中是否已经存在
        Query<MacInfoItemBean> queryResult =
                macinfoDB.query().equal(MacInfoItemBean_.mac, result).build();
        List<MacInfoItemBean> macInfoItemBeans = queryResult.find();
        if(macInfoItemBeans.size()>0){//说明已经曾经已被入库过一次了
            MacInfoItemBean temp = macInfoItemBeans.get(0);
            Toast.makeText(this, temp.getMac()+"已于"+temp.getDate()+"入库过了~", Toast.LENGTH_SHORT).show();
            return;
        }
        //===============下面存储这条数据到DB
        MacInfoItemBean macInfoItemBean = new MacInfoItemBean();
        macInfoItemBean.setMac(result);
        macInfoItemBean.setDate(formatter.format(new Date()));
        long putid = macinfoDB.put(macInfoItemBean);
        Toast.makeText(this, "数据存储成功,id为:"+putid, Toast.LENGTH_SHORT).show();
    }

    private String CreateEncryptByte(String regNo, String keystr) {
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] keyBytes = decoder.decodeBuffer(keystr);
            DESedeKeySpec keySpec = null;
            AlgorithmParameterSpec iv = null;
            SecretKeyFactory keyFactory = null;
            Key key = null;
            keySpec = new DESedeKeySpec(keyBytes);
            keyFactory = SecretKeyFactory.getInstance("DESede");
            key = keyFactory.generateSecret(keySpec);
            Cipher enCipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            enCipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] pasByte = enCipher.doFinal(regNo.getBytes());
            BASE64Encoder encoder = new BASE64Encoder();
            String result = encoder.encode(pasByte);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String CreateDecryptByte(String regNo_encrypt, String keystr) {
        try {
            //准备好cipher
            byte[] orikey = Base64.decode(keystr, Base64.DEFAULT);
            DESedeKeySpec dks = new DESedeKeySpec(orikey);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
            SecretKey secretKey = keyFactory.generateSecret(dks);
            Cipher deCipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            deCipher.init(Cipher.DECRYPT_MODE, secretKey);
            //准备待解密的byte[]
            byte[] regno_temp = Base64.decode(regNo_encrypt, Base64.DEFAULT);
            //下面用decipher解密regno_temp这个byte[]
            byte[] regno_temp1 = deCipher.doFinal(regno_temp);
            //            byte[] regno_temp2 = new String(regno_temp1).getBytes("GB2312");
            //            String result = new String(regno_temp1, "GB2312");
            String result = new String(regno_temp1, "GB2312");
            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private byte[] forMatebyte(byte[] arr) {
        int i = 0;
        for (; i < arr.length; i++) {
            if (arr[i] == new Byte("0")) {
                break;
            }
        }
        byte[] result = new byte[i];
        for (int j = 0; j < i; j++) {
            result[j] = arr[j];
        }
        return result;
    }

    native int nativeTest();




}
