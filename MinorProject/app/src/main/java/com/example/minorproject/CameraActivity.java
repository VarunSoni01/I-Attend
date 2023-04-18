package com.example.minorproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class CameraActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int QR_CODE_REQUEST_CODE = 1;
    Button submit;

    // Declare a variable to store the captured image
    private Bitmap capturedImage;
    ImageView imageView;
    TextView textView;
    String imagestring;
    private static final int PERMISSION_REQUEST_CODE = 123;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    String email,profilestring,qrstring;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        imageView = findViewById(R.id.image);
        textView = findViewById(R.id.textview);
        submit = findViewById(R.id.submit_ass);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        email = user.getEmail();

        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        Python python = Python.getInstance();
        PyObject pyobject = python.getModule("python");

        ImageCalling();
        QRCalling();

        firestore.collection("UserData").document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Get the value of the "my_field" field as a String
                    profilestring = documentSnapshot.getString("imagepath");
                    // Do something with the value
                } else {
                    // Document does not exist
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // An error occurred while trying to read the document
            }
        });



        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Request the permission
            ActivityCompat.requestPermissions(CameraActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CODE);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Request the permission
                    ActivityCompat.requestPermissions(CameraActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                }


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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(imagestring == null || qrstring == null){
                    Toast.makeText(CameraActivity.this, "Please fill all entries", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(CameraActivity.this, ""+imagestring, Toast.LENGTH_SHORT).show();
                System.out.println(imagestring);
                PyObject object = pyobject.callAttr("main", imagestring, profilestring);
                Boolean facesMatch = object.toBoolean();


                    Intent intent = new Intent(CameraActivity.this,AttendanceStatusActivity.class);
                    intent.putExtra("facematch",facesMatch);
                    intent.putExtra("qrstring",qrstring);
                    startActivity(intent);
                    finish();


//                if(facesMatch){
//                    textView.setText("true");
//                }
//                else{
//                    textView.setText("false");
//                }
            }
        });
    }

    private void QRCalling() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(CameraActivity.this);
        intentIntegrator.setPrompt("Scan a barcode or QR Code");
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.initiateScan();
    }

    private void ImageCalling() {
        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Request the permission
            ActivityCompat.requestPermissions(CameraActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CODE);
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
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

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            capturedImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] byteArray = baos.toByteArray();
            String imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);

            String fileName = generateFileName() + ".png";
            String imagePath = saveImageToFile(capturedImage, fileName);

            imagestring = imagePath;
            Toast.makeText(this, "" + imagePath, Toast.LENGTH_SHORT).show();

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
                    qrstring = intentResult.getContents();
                    textView.setText(intentResult.getContents());
                    Toast.makeText(this, ""+intentResult.getContents(), Toast.LENGTH_SHORT).show();
//                textView.setText(intentResult.getFormatName());
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
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

}