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
    public static String resultText;

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
                Intent intent=new Intent(Gallery_image.this,Gallery_text.class);
                int vib=p.getVibrantColor(Color.TRANSPARENT);
                int light_vib=p.getLightVibrantColor(Color.TRANSPARENT);
                int dark_vib=p.getDarkVibrantColor(Color.TRANSPARENT);
                int muted=p.getMutedColor(Color.TRANSPARENT);
                int light_muted=p.getLightMutedColor(Color.TRANSPARENT);
                int dark_muted=p.getDarkMutedColor(Color.TRANSPARENT);
                intent.putExtra("vibrant",vib);
                intent.putExtra("dark_vibrant",dark_vib);
                intent.putExtra("light_vibrant",light_vib);
                intent.putExtra("muted",muted);
                intent.putExtra("dark_muted",dark_muted);
                intent.putExtra("light_muted",light_muted);
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
                                resultText=VisionText.getText();
                                Log.d("Text from Image",resultText);
                                Intent intent=new Intent(Gallery_image.this,Gallery_text.class);
                                intent.putExtra("ResultText",resultText);
                                startActivity(intent);


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