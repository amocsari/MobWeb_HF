package com.example.dragon.pageflipperdemo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends Activity {

    Button bPageFlipDemo;
    Button bPdfOpenDemo;
    Button bFileListDemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        bPageFlipDemo = findViewById(R.id.bPageFlipDemo);
//        bPdfOpenDemo = findViewById(R.id.bPdfOpenDemo);
        bFileListDemo = findViewById(R.id.bPdfList);

        bPageFlipDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });

//        bPdfOpenDemo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getBaseContext(), PdfActivity.class);
//                startActivity(intent);
//            }
//        });

        bFileListDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), PdfListActivity.class);
                startActivity(intent);
            }
        });
    }
}
