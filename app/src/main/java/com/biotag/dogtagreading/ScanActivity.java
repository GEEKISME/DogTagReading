package com.biotag.dogtagreading;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import cn.simonlee.xcodescanner.core.CameraScanner;
import cn.simonlee.xcodescanner.core.GraphicDecoder;
import cn.simonlee.xcodescanner.core.NewCameraScanner;
import cn.simonlee.xcodescanner.view.AdjustTextureView;

public class ScanActivity extends AppCompatActivity {
    private AdjustTextureView mTextureView;
    private View mScannerFrameView;

    private CameraScanner mCameraScanner;
    protected GraphicDecoder mGraphicDecoder;

    protected String TAG = "XCodeScanner";
    private Button mButton_Flash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        mTextureView = findViewById(R.id.textureview);
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Log.e(TAG, getClass().getName() + ".onSurfaceTextureAvailable() width = " + width + " , height = " + height);
                mCameraScanner.setPreviewTexture(surface);
                mCameraScanner.setPreviewSize(width,height);
                mCameraScanner.openCamera(ScanActivity.this);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                Log.e(TAG, getClass().getName() + ".onSurfaceTextureSizeChanged() width = " + width + " , height = " + height);
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                Log.e(TAG, getClass().getName() + ".onSurfaceTextureDestroyed()");
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                Log.i(TAG,"每一帧画面都回调一次");
            }
        });

        mScannerFrameView = findViewById(R.id.scannerframe);

        mButton_Flash = findViewById(R.id.btn_flash);
        mButton_Flash.setOnClickListener(val->{

        });

        mCameraScanner = new NewCameraScanner(new CameraScanner.CameraListener() {
            @Override
            public void openCameraSuccess(int surfaceWidth, int surfaceHeight, int surfaceDegree) {
                //打开摄像头成功的话，开始把摄像头的数据往textureview上扔
                mTextureView.setImageFrameMatrix(surfaceWidth,surfaceHeight,surfaceDegree);
                if(mGraphicDecoder == null){
                    mGraphicDecoder = new DebugZBarDecoder(new GraphicDecoder.DecodeListener() {
                        int mCount = 0;
                        String mResult = null;
                        @Override
                        public void decodeComplete(String result, int type, int quality, int requestCode) {
                            if (result == null) return;
                            Log.i("tms","二维码是:"+result);
                            if (result.equals(mResult)) {
                                if (++mCount > 0) {//连续2次相同则显示结果（主要过滤脏数据，也可以根据条码类型自定义规则）
                                    if (quality < 10) {
//                                        Toast.makeText(ScanActivity.this, "[类型" + type + "/精度00" + quality + "]" + result, Toast.LENGTH_SHORT).show();
                                    } else if (quality < 100) {
//                                        Toast.makeText(ScanActivity.this, "[类型" + type + "/精度0" + quality + "]" + result, Toast.LENGTH_SHORT).show();
                                    } else {
//                                        Toast.makeText(ScanActivity.this, "[类型" + type + "/精度" + quality + "]" + result, Toast.LENGTH_SHORT).show();
                                    }

                                    Intent intent = new Intent();
                                    intent.putExtra("result",result);
                                    setResult(RESULT_OK,intent);
                                    finish();
                                }
                            } else {
                                mCount = 1;
                                mResult = result;
                            }
                            Log.d(TAG, getClass().getName() + ".decodeComplete() -> " + mResult);
                        }
                    }, null);
                }
                //该区域坐标为相对于父容器的左上角顶点。
                mCameraScanner.setFrameRect(mScannerFrameView.getLeft(),mScannerFrameView.getTop(),mScannerFrameView.getRight(),mScannerFrameView.getBottom());
                mCameraScanner.setGraphicDecoder(mGraphicDecoder);
            }

            @Override
            public void openCameraError() {
                Toast.makeText(ScanActivity.this, "打开相机失败~", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void noCameraPermission() {
                Toast.makeText(ScanActivity.this, "没有打开相机的权限~", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void cameraDisconnected() {
                Toast.makeText(ScanActivity.this, "与摄像头断开连接~", Toast.LENGTH_SHORT).show();
            }

            int mBrightnessCount = 0;

            @Override
            public void cameraBrightnessChanged(int brightness) {
                if (brightness <= 50) {
                    if (mBrightnessCount < 0) {
                        mBrightnessCount = 1;
                    } else {
                        mBrightnessCount++;
                    }
                } else {
                    if (mBrightnessCount > 0) {
                        mBrightnessCount = -1;
                    } else {
                        mBrightnessCount--;
                    }
                }
                if (mBrightnessCount > 4) {//连续5帧亮度低于50，显示闪光灯开关
                    mButton_Flash.setVisibility(View.VISIBLE);
                } else if(mBrightnessCount < -4 && !mCameraScanner.isFlashOpened()){//连续5帧亮度不低于50，且闪光灯未开启，隐藏闪光灯开关
                    mButton_Flash.setVisibility(View.GONE);
                }
            }
        });
    }


    @Override
    protected void onRestart() {
        if(mTextureView.isAvailable()){
            //部分机型转到后台不会走onSurfaceTextureDestroyed()，因此isAvailable()一直为true，转到前台后不会再调用onSurfaceTextureAvailable()
            //因此需要手动开启相机
            mCameraScanner.setPreviewTexture(mTextureView.getSurfaceTexture());
            mCameraScanner.setPreviewSize(mTextureView.getWidth(), mTextureView.getHeight());
            mCameraScanner.openCamera(this.getApplicationContext());
        }
        super.onRestart();
    }

    @Override
    protected void onPause() {
        mCameraScanner.closeCamera();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mCameraScanner.setGraphicDecoder(null);
        if (mGraphicDecoder != null) {
            mGraphicDecoder.setDecodeListener(null);
            mGraphicDecoder.detach();
        }
        mCameraScanner.detach();
        super.onDestroy();
    }

}
