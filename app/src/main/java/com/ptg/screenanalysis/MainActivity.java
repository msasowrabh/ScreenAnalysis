package com.ptg.screenanalysis;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.icu.lang.UCharacter;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import androidx.camera.extensions.*;
import androidx.palette.graphics.Palette;


public class MainActivity extends AppCompatActivity {
    private int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE"};
    TextureView textureView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private Preview preview;
    private ImageCapture imageCapture;
    private  ImageAnalysis imageAnalysis;
    private Button b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        previewView = findViewById(R.id.viewFinder);
        Button openGallery=(Button) findViewById(R.id.open_gallery);
        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent opengallerywindow=new Intent(MainActivity.this,Gallery_image.class);
                startActivity(opengallerywindow);

            }
        });


        if(allPermissionsGranted()){
            startCamera();
            Toast.makeText(this, "camera started", Toast.LENGTH_SHORT).show();


        }
        else{
            ActivityCompat.requestPermissions(this,REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            Toast.makeText(this, "request permission", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                startCamera();
            } else{
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                cameraProvider.unbindAll();
                preview = new Preview.Builder()
                        .build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();
                b=(Button) findViewById(R.id.camera_capture_button);
                imageCapture=new ImageCapture.Builder().build();
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        File f = new File(Environment.getExternalStorageDirectory() + "/" +"Pictures/"+ System.currentTimeMillis() + ".png");
                        ImageCapture.OutputFileOptions outputFileOptions =
                                new ImageCapture.OutputFileOptions.Builder(f).build();

                        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(MainActivity.this), new ImageCapture.OnImageSavedCallback() {
                            @Override
                            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                                String msg = "Pic captured at " + f.getAbsolutePath();
                                Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(@NonNull ImageCaptureException exception) {
                                Toast.makeText(MainActivity.this,String.valueOf(exception),Toast.LENGTH_SHORT).show();
                            }
                        });



                    }
                });
                imageAnalysis=new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280,720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
                    @Override
                    public void analyze(@NonNull ImageProxy image) {
                        Bitmap bitmap=toBitmap(image);

                        setPalletecolors(bitmap);
                        RecogniseText(image);
                        //Toast.makeText(MainActivity.this,"analysis",Toast.LENGTH_SHORT).show();
                        image.close();




                    }
                });




                Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector,imageAnalysis,imageCapture, preview);
                preview.setSurfaceProvider(previewView.createSurfaceProvider(camera.getCameraInfo()));

            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));


    }

    private void RecogniseText(ImageProxy imageProxy) {
        int rotationDegree = 90;
        @SuppressLint("UnsafeExperimentalUsageError") Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image =
                    InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());


            TextRecognizer detector = TextRecognition.getClient();
            detector.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
                @Override
                public void onSuccess(Text text) {
                    Toast.makeText(MainActivity.this, text.getText(), Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(String.valueOf(e),String.valueOf(e));
                }
            });
        }
    }

    private void setPalletecolors(Bitmap bitmap) {

        int c=1;
        try {

            Palette p = Palette.from(bitmap).generate();
            int vib=p.getVibrantColor(Color.TRANSPARENT);
            int light_vib=p.getLightVibrantColor(Color.TRANSPARENT);
            int dark_vib=p.getDarkVibrantColor(Color.TRANSPARENT);
            int muted=p.getMutedColor(Color.TRANSPARENT);
            int light_muted=p.getLightMutedColor(Color.TRANSPARENT);
            int dark_muted=p.getDarkMutedColor(Color.TRANSPARENT);
            TextView t=(TextView) findViewById(R.id.vibrant_color_camera);
            t.setBackgroundColor(vib);
            t=(TextView)findViewById(R.id.dark_vibrant_color_camera);
            t.setBackgroundColor(dark_vib);
            t=(TextView)findViewById(R.id.dark_muted_color_camera);
            t.setBackgroundColor(dark_muted);
            t=(TextView)findViewById(R.id.light_muted_color_camera);
            t.setBackgroundColor(light_muted);
            t=(TextView)findViewById(R.id.light_muted_color_camera);
            t.setBackgroundColor(light_muted);
            t=(TextView)findViewById(R.id.light_vibrant_color_camera);
            t.setBackgroundColor(light_vib);
            t=(TextView)findViewById(R.id.muted_color_camera);
            t.setBackgroundColor(muted);

        } catch (Exception e) {
            e.printStackTrace();
        }



    }
    //function to convert Image proxy to bitmap
    private Bitmap toBitmap(ImageProxy image) {
        ImageProxy.PlaneProxy planes[] = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];
        //U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

        byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    private boolean allPermissionsGranted() {
        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
}

