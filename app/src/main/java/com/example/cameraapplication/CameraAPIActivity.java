package com.example.cameraapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class CameraAPIActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private Camera camera;
    private TextureView textureView;
    private String currentPath = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_a_p_i);
        textureView = findViewById(R.id.textureView2);
        textureView.setSurfaceTextureListener(this);

        Button button = findViewById(R.id.button3);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               takePic();
            }
        });

    }

    private void takePic() {

        if(camera != null)
        camera.takePicture(null,null,pictureCallback);
    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(System.currentTimeMillis());
            String fileName = "IMG_"+timestamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                File imageFile = File.createTempFile(fileName,".jpg",storageDir);
                currentPath = imageFile.getAbsolutePath();
                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                fileOutputStream.write(data);
                fileOutputStream.close();
                Toast.makeText(CameraAPIActivity.this, "Image saved in "+ currentPath, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

            if(checkPermissions())
            {
                camera = Camera.open();
                if(camera != null)
                {

                    try {
                        camera.setPreviewTexture(surface);
                        camera.startPreview();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if(camera != null)
        {
            camera.stopPreview();
            camera.release();
            camera = null;
            return true;
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private boolean checkPermissions()
    {
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void requestPermissions()
    {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1000)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                camera = Camera.open();
                if(camera != null)
                {
                    try {
                        camera.setPreviewTexture(textureView.getSurfaceTexture());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    camera.startPreview();
                }
            }
            else
            {
                requestPermissions();
            }
        }
    }
}