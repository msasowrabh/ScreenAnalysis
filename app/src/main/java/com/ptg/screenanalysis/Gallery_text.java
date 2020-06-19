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
        int vib=getIntent().getExtras().getInt("vib");
        int dark_vib=getIntent().getExtras().getInt("dark_vib");
        int light_vib=getIntent().getExtras().getInt("light_vib");
        int muted=getIntent().getExtras().getInt("muted");
        int dark_muted=getIntent().getExtras().getInt("dark_muted");
        int light_muted=getIntent().getExtras().getInt("light_muted");


        TextView display=findViewById(R.id.textDisplay);
        TextView vibrant= findViewById(R.id.vibrant_color);
        vibrant.setBackgroundColor(vib);
        TextView dark_vibrant=findViewById(R.id.dark_vibrant_color);
        vibrant.setBackgroundColor(dark_vib);

        TextView dark_mutedview=findViewById(R.id.dark_muted_color);
        vibrant.setBackgroundColor(dark_muted);

        TextView light_mutedview=findViewById(R.id.light_muted_color);
        vibrant.setBackgroundColor(light_muted);
        TextView light_vibrant=findViewById(R.id.light_vibrant_color);
        vibrant.setBackgroundColor(light_vib);
        TextView mutedview=findViewById(R.id.muted_color);
        vibrant.setBackgroundColor(muted);
        display.setText(result);
    }
}