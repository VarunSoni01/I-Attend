package com.example.minorproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class SignUpActivity2 extends AppCompatActivity {
    ImageView imageView;
    String imagestring;
    Button button;

    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private boolean cameraOpened = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);

        imageView = findViewById(R.id.profile_image);
        button = findViewById(R.id.su_next);

        if (ContextCompat.checkSelfPermission(SignUpActivity2.this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Request the permission
            ActivityCompat.requestPermissions(SignUpActivity2.this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CODE);
        }

//        loadPicker();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(SignUpActivity2.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Request the permission
                    ActivityCompat.requestPermissions(SignUpActivity2.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CODE);
                } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }}
            }
        });

        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        Python python = Python.getInstance();
        PyObject pyobject = python.getModule("python");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(imagestring == null){
                    Toast.makeText(SignUpActivity2.this, "Please Select Image", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(SignUpActivity2.this,SignUpActivity1.class);
                    intent.putExtra("path",imagestring);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    private void loadPicker() {
        if (ContextCompat.checkSelfPermission(SignUpActivity2.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Request the permission
            ActivityCompat.requestPermissions(SignUpActivity2.this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CODE);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Get the captured image as a bitmap
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // Set the image in the ImageView
            imageView.setImageBitmap(imageBitmap);

            // Convert the bitmap to a Base64 encoded string
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] byteArray = baos.toByteArray();
            String imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);

            String fileName = generateFileName() + ".png";
            String imagePath = saveImageToFile(imageBitmap, fileName);

            imagestring = imagePath;
            Toast.makeText(this, "" + imagePath, Toast.LENGTH_SHORT).show();

            // Pass the imageString to the Python code
//            callPythonCode(imageString);
        }
    }

    private String generateFileName() {
        // Generate a random string of 10 characters
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String saveImageToFile(Bitmap bitmap, String fileName) {
        // Save the image to a file with the given file name
        File file = new File(getExternalFilesDir(null), fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if the camera has already been opened
        if (!cameraOpened) {
            // Open the camera
            loadPicker();

            // Set the flag to true to indicate that the camera has been opened
            cameraOpened = true;
        }
    }
}