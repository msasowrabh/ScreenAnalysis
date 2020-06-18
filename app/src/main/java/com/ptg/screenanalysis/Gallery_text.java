package com.ptg.screenanalysis;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Gallery_text extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_text2);
        String result=getIntent().getExtras().getString("ResultText");
        TextView display=findViewById(R.id.textDisplay);
        display.setText(result);
    }
}