package com.example.minorproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int QR_CODE_REQUEST_CODE = 1;

    // Declare a variable to store the captured image
    private Bitmap capturedImage;
    ImageView imageView;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        imageView = findViewById(R.id.image);
        textView = findViewById(R.id.textview);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(CameraActivity.this);
                intentIntegrator.setPrompt("Scan a barcode or QR Code");
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            // Get the captured image from the intent data
            Bundle extras = data.getExtras();
            capturedImage = (Bitmap) extras.get("data");
            // Display the captured image in the ImageView
            imageView.setImageBitmap(capturedImage);
        }
        else{
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            // if the intentResult is null then
            // toast a message as "cancelled"
            if (intentResult != null) {
                if (intentResult.getContents() == null) {
                    Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    // if the intentResult is not null we'll set
                    // the content and format of scan message
                    textView.setText(intentResult.getContents());
//                textView.setText(intentResult.getFormatName());
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }



    }

}