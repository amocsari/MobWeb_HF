package com.example.dragon.pageflipperdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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

/**
 * pdf megjelenítéséért felelős activity
 */
public class PdfActivity extends Activity{

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
        etPageNum = findViewById(R.id.et_page_num);
        mPdfHandler = PdfHandler.get(getBaseContext(), ivContent.getWidth());

        try {
            Intent intent = getIntent();
            Uri uri = Uri.parse(intent.getExtras().getString("uri"));
            mPdfHandler.openPdf(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO: kiszervezni
        bJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: ne get-eljük minden alkalommal amikor megnyomjuk a gombot
                mPdfHandler = PdfHandler.get(getBaseContext(), ivContent.getWidth());
                //TODO: impl
                try {
                    Integer pageNo = Integer.parseInt(etPageNum.getText().toString());
                    mPdfHandler.activatePage(pageNo);
                }catch (NumberFormatException e){
                    return;
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
    }
}
