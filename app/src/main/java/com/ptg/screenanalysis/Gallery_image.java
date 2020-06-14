package com.ptg.screenanalysis;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.io.IOException;

public class Gallery_image extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_image);
        opengallery();
    }
    private void opengallery() {
        Intent gallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){

            Uri imageUri = data.getData();
            ImageView imageView=(ImageView)findViewById(R.id.gallery_image);
            imageView.setImageURI(imageUri);
            try {
                Bitmap bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
                Palette p = Palette.from(bitmap).generate();
                int vib=p.getVibrantColor(Color.TRANSPARENT);
                int light_vib=p.getLightVibrantColor(Color.TRANSPARENT);
                int dark_vib=p.getDarkVibrantColor(Color.TRANSPARENT);
                int muted=p.getMutedColor(Color.TRANSPARENT);
                int light_muted=p.getLightMutedColor(Color.TRANSPARENT);
                int dark_muted=p.getDarkMutedColor(Color.TRANSPARENT);
                TextView t=(TextView) findViewById(R.id.vibrant_color);
                t.setBackgroundColor(vib);
                t=(TextView)findViewById(R.id.dark_vibrant_color);
                t.setBackgroundColor(dark_vib);
                t=(TextView)findViewById(R.id.dark_muted_color);
                t.setBackgroundColor(dark_muted);
                t=(TextView)findViewById(R.id.light_muted_color);
                t.setBackgroundColor(light_muted);
                t=(TextView)findViewById(R.id.light_muted_color);
                t.setBackgroundColor(light_muted);
                t=(TextView)findViewById(R.id.light_vibrant_color);
                t.setBackgroundColor(light_vib);
                t=(TextView)findViewById(R.id.muted_color);
                t.setBackgroundColor(muted);
                Recognizetext(bitmap);





                // Toast.makeText(this,String.valueOf(vib),Toast.LENGTH_SHORT).show();


            } catch (IOException e) {
                e.printStackTrace();
            }

            /*try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                Palette p = Palette.from(bitmap).generate();
                TextView t=(TextView) findViewById(R.id.vibrant_color);

                Palette.Swatch vibrantSwatch = p.getVibrantSwatch();
                t.setText(String.valueOf(String.valueOf(vibrantSwatch)));
                int c=vibrantSwatch.getRgb();

                Toast.makeText(this,"color",Toast.LENGTH_SHORT).show();

                t.setBackgroundColor(c);
                t.setText(String.valueOf(c));
            }
            catch(Exception e){
                Toast.makeText(this,String.valueOf(e),Toast.LENGTH_SHORT).show();
                Log.e(String.valueOf(e),String.valueOf(e));
                TextView t=(TextView) findViewById(R.id.vibrant_color);


            }*/
        }
    }

    private void Recognizetext(Bitmap bitmap) {
       int rotationDegree=0;
        InputImage image = InputImage.fromBitmap(bitmap,rotationDegree);
        TextRecognizer detector = TextRecognition.getClient();
        Task<Text> result =
                detector.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text VisionText) {
                                String text=VisionText.getText();
                                Toast.makeText(Gallery_image.this,text,Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        e.printStackTrace();
                                        Log.e(String.valueOf(e),String.valueOf(e));
                                        Toast.makeText(Gallery_image.this,String.valueOf(e),Toast.LENGTH_SHORT).show();

                                    }
                                });

    }
}