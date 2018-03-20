package com.yinjunzi.zxingforandroid;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.google.zxing.ResultPoint;

import java.util.List;

import com.yinjunzi.zxinglibrary.embedded.barcodescanner.BarcodeCallback;
import com.yinjunzi.zxinglibrary.embedded.barcodescanner.BarcodeResult;
import com.yinjunzi.zxinglibrary.embedded.barcodescanner.CaptureManager;
import com.yinjunzi.zxinglibrary.embedded.barcodescanner.DecoratedBarcodeView;

public class CustomQRScanActivity extends AppCompatActivity {

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_qrscan);
        barcodeScannerView = (DecoratedBarcodeView) findViewById(R.id.zxing_barcode_scanner);
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode(new BarcodeCallback() {
            @Override
            public void barcodeResult(final BarcodeResult result) {
                try {
                    capture.getBarcodeView().pauseAndWait();
                    capture.getBeepManager().playBeepSoundAndVibrate();
                    capture.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            showDialogAndRestartScan(null, result.getText());
                        }
                    });
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {

            }
        });
    }

    private void showDialogAndRestartScan(String title, String content) {
        String title1 = "扫描结果";
        if (title != null && !"".equals(title)) {
            title1 = title;
        }

        new AlertDialog.Builder(this).setTitle(title1).setMessage(content)
                .setCancelable(false).setPositiveButton("重新扫描", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                capture.onResume();
                capture.decode();
            }
        }).setNegativeButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                CustomQRScanActivity.this.finish();
            }
        }).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}
