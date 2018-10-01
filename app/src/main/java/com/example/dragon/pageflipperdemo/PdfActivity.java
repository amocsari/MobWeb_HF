package com.example.dragon.pageflipperdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dragon.pageflipperdemo.Exception.PdfNotOpenException;
import com.example.dragon.pageflipperdemo.Exception.PdfPageNotFoundException;
import com.example.dragon.pageflipperdemo.PdfHandling.PdfHandler;

import java.io.IOException;

class PdfActivity extends Activity{

    PdfHandler mPdfHandler;
    Button bJump;
    ImageView ivContent;
    EditText etPageNum;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        bJump = findViewById(R.id.b_jump);
        ivContent  = findViewById(R.id.iv_content);
        mPdfHandler = PdfHandler.get(getBaseContext());
        etPageNum = findViewById(R.id.et_page_num);

        try {
            mPdfHandler.openPdf(R.raw.sample);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO: kiszervezni
        bJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: impl
                Integer pageNo = Integer.parseInt(etPageNum.getText().toString());
                try {
                    mPdfHandler.activatePage(pageNo);
                } catch (PdfNotOpenException e) {
                    e.printStackTrace();
                } catch (PdfPageNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = null;
                try {
                    bitmap = mPdfHandler.getPageAsBitmap();
                } catch (PdfNotOpenException e) {
                    e.printStackTrace();
                } catch (PdfPageNotFoundException e) {
                    e.printStackTrace();
                }
                if(ivContent.getDrawable() != null){
                    ivContent.setImageDrawable(null);
                }
                ivContent.setImageBitmap(bitmap);
            }
        });
    }
}