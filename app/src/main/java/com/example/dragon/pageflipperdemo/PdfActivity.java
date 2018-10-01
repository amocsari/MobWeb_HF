package com.example.dragon.pageflipperdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

public class PdfActivity extends Activity {

    TextView etPageNum;
    Button bJump;
    ImageView ivPage;
    Bitmap mBitmap;

    PdfConverter pdfConverter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        etPageNum = findViewById(R.id.etPageNum);
        bJump = findViewById(R.id.bJump);
        ivPage = findViewById(R.id.ivPage);
        mBitmap = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sample);

        pdfConverter = new PdfConverter();

        InputStream inputStream = getResources().openRawResource(R.raw.arifureta_shokugyou_de_sekai_saikyou_01);
        pdfConverter.openPdf(inputStream);

        bJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pageNumber;
                try {
                     pageNumber = Integer.parseInt(etPageNum.getText().toString());
                }catch (NumberFormatException e){
                    return;
                }

                if (ivPage.getDrawable() != null) {
                    ((BitmapDrawable) ivPage.getDrawable()).getBitmap().recycle();
                    ivPage.setImageDrawable(null);
                }
                ivPage.setImageBitmap(pdfConverter.getPage(pageNumber));
            }
        });
    }

    @Override
    protected void onStop(){
        ((BitmapDrawable) ivPage.getDrawable()).getBitmap().recycle();
        super.onStop();
    }
}
