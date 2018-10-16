package generatebarcode.com.scanhotelmenu.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;

import generatebarcode.com.scanhotelmenu.R;
import generatebarcode.com.scanhotelmenu.utils.Helpers;
import generatebarcode.com.scanhotelmenu.utils.IBarcode;

public class MainActivity extends AppCompatActivity {
    AlertDialog dialog;
    private Barcode barcodeResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initActionbar("Menu List");
        Helpers.darkenStatusBar(MainActivity.this,R.color.colorPrimary);
    }

    private void initActionbar(String title) {
        ActionBar actionBar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.action_bar_tittle_text_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(viewActionBar, params);
        TextView actionbarTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        actionbarTitle.setText(title);
        //  actionbarTitle.setTypeface(TypefaceCache.get(getAssets(), 3));
        actionbarTitle.setTextSize(16);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(true);
    }

    public void add(View view){
        startScan();
    }

    private void startScan() {
        /**
         * Build a new MaterialBarcodeScanner
         */
        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(MainActivity.this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withCenterTracker()
                .withText("Scanning...")
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) {
                        barcodeResult = barcode;
                        System.out.println(barcode.rawValue);
                        Toast.makeText(MainActivity.this, "Barcode Type  " + barcodeScan(barcode.format, barcode.rawValue), Toast.LENGTH_SHORT).show();
                        // result.setText(barcode.rawValue);
                        String input = barcode.rawValue;
                        int code_type = barcode.format;
                        Bitmap bitmap = null;
                        if (code_type== IBarcode.EAN13){
                            bitmap = Helpers.barcodeBitmap(code_type, input.substring(0,input.length()-1), 900, 700);
                        }else {
                            bitmap = Helpers.barcodeBitmap(code_type, input, 900, 700);
                        }
                        showScanBarcode(bitmap, code_type, input);
                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }

    String barcodeScan(int code_type, String value) {
        String bool = "";
        if (code_type == IBarcode.CODE39) {
            bool = "CODE-39 = " + value;
        } else if (code_type == IBarcode.CODE93) {
            bool = "CODE-93 = " + value;
        } else if (code_type == IBarcode.EAN13) {
            bool = "EAN-13 = " + value;
        } else if (code_type == IBarcode.EAN8) {
            bool = "EAN-8 = " + value;
        } else if (code_type == IBarcode.UPCE) {
            bool = "UPC-E = " + value;
        } else if (code_type == IBarcode.UPCA) {
            bool = "UPC-A = " + value;
        } else if (code_type == IBarcode.ITF) {
            bool = "ITF = " + value;
        } else if (code_type == IBarcode.QR_CODE) {
            bool = "QR-CODE = " + value;
        } else if (code_type == IBarcode.PDF417) {
            bool = "PDF417 = " + value;
        } else if (code_type == IBarcode.AZTEC) {
            bool = "AZTEC = " + value;
        } else if (code_type == IBarcode.DATA_MATRIX) {
            bool = "DATA-MATRIX = " + value;
        } else if (code_type == IBarcode.CODE128) {
            bool = "CODE-128 = " + value;
        } else if (code_type == IBarcode.CODABAR) {
            bool = "CODABAR = " + value;
        }
        return bool;
    }

    void showScanBarcode(Bitmap bitmap, int code_type, String code_value) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogForm = inflater.inflate(R.layout.barcode_image_view_dialog, null, false);
        ImageView mImageView = (ImageView) dialogForm.findViewById(R.id.image_view);
        LinearLayout cancel = (LinearLayout) dialogForm.findViewById(R.id.cancel);
        TextView buttonTab = (TextView) dialogForm.findViewById(R.id.buttonTab);
        TextView codeNameTxt = (TextView) dialogForm.findViewById(R.id.codeNameTxt);
        TextView description = (TextView) dialogForm.findViewById(R.id.description);
        buttonTab.setText("Scaned Barcode");
        if (code_type == IBarcode.DATA_MATRIX) {
            mImageView.getLayoutParams().width = 900;
            mImageView.getLayoutParams().height = 500;
        }

        mImageView.setImageBitmap(bitmap);
        String arr[] = barcodeScan(code_type,code_value).split("=");
        description.setText(arr[0]);
        codeNameTxt.setText(code_value);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogForm);
        builder.create();
        codeNameTxt.setVisibility(View.VISIBLE);
        if (bitmap != null) {
            if (code_value.contains("https")){
                String value = "<html><a href=\""+code_value+"\">"+code_value+"</a></html>";
                codeNameTxt.setText(Html.fromHtml(value));
                codeNameTxt.setMovementMethod(LinkMovementMethod.getInstance());
            }else {
                codeNameTxt.setText(code_value);
            }
        } else {
            description.setText("Image is empty!");
        }
        dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
