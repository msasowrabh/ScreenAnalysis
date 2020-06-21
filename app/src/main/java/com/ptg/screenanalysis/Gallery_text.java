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
        int vib=getIntent().getExtras().getInt("vibrant");
        int dark_vib=getIntent().getExtras().getInt("dark_vibrant");
        int light_vib=getIntent().getExtras().getInt("light_vibrant");
        int muted=getIntent().getExtras().getInt("muted");
        int dark_muted=getIntent().getExtras().getInt("dark_muted");
        int light_muted=getIntent().getExtras().getInt("light_muted");


        TextView display=findViewById(R.id.textDisplay);
        TextView specialChar=findViewById(R.id.special_char_dispalay);
        TextView vibrant= findViewById(R.id.vibrant_color);
        vibrant.setBackgroundColor(vib);
        TextView dark_vibrant=findViewById(R.id.dark_vibrant_color);
        dark_vibrant.setBackgroundColor(dark_vib);

        TextView dark_mutedview=findViewById(R.id.dark_muted_color);
        dark_mutedview.setBackgroundColor(dark_muted);

        TextView light_mutedview=findViewById(R.id.light_muted_color);
        light_mutedview.setBackgroundColor(light_muted);
        TextView light_vibrant=findViewById(R.id.light_vibrant_color);
        light_vibrant.setBackgroundColor(light_vib);
        TextView mutedview=findViewById(R.id.muted_color);
        mutedview.setBackgroundColor(muted);

        String normalText=findNormalText(result);
        String specialCharText=findSpecialChar(result);
        specialChar.setText(specialCharText);
        display.setText(normalText);
    }

    private String findSpecialChar(String text) {
        String result="";
        result = text.replaceAll("[a-zA-Z0-9\n]", "");


        return result;
    }

    private String findNormalText(String text) {
        String result="";
        result = text.replaceAll("[^a-zA-Z0-9]", "");

        return result;
    }

}