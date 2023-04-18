package com.example.minorproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

import java.util.HashMap;
import java.util.Map;

public class AttendanceStatusActivity extends AppCompatActivity {

    Boolean result;
    ImageView resultimage;
    TextView resulttext;
    String enrollment,facultyemail,name,collectionid,sub;

    Button home;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    String email,imagepath,qrstring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_status);

        result = getIntent().getBooleanExtra("facematch",false);
        qrstring = getIntent().getStringExtra("qrstring");
        resultimage = findViewById(R.id.resultimage);
        resulttext = findViewById(R.id.resulttext);
        home = findViewById(R.id.home);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        email = user.getEmail();


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AttendanceStatusActivity.this,NewActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if(result){
            resulttext.setText("Successfully Marked");
            resultimage.setImageResource(R.drawable.checked);
            datastorage();
        }
        else{
            resulttext.setText("Details Not Match");
            resultimage.setImageResource(R.drawable.cancel);
//            datastorage();
        }


    }

    private void datastorage() {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String email = auth.getCurrentUser().getEmail();
        Toast.makeText(this, "email - "+email, Toast.LENGTH_SHORT).show();
        firestore.collection("UserData").document(email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            enrollment = documentSnapshot.getString("enrollment");
                            name = documentSnapshot.getString("name");
                            String[] outputStrings = qrstring.split("\\$");

                            collectionid = outputStrings[0];
                            facultyemail = outputStrings[1];
                            sub = outputStrings[2];

                            Map<String, Object> data = new HashMap<>();
                            data.put("Name", name);
                            data.put("Enrollment_No", enrollment);
                            data.put("Faculty",facultyemail);
                            data.put("Attendance",true);
                            data.put("Subject",sub);

                            firestore.collection(collectionid).document(enrollment)
                                    .set(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Data added successfully
                                            Toast.makeText(AttendanceStatusActivity.this, "Succefully posted", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Failed to add data
                                        }
                                    });
                            // Do something with the retrieved value
                        } else {
                            // Document doesn't exist
                            Toast.makeText(AttendanceStatusActivity.this, "Document not available", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to retrieve data
                        Toast.makeText(AttendanceStatusActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });




    }
}