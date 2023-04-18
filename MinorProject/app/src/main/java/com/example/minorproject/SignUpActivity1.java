package com.example.minorproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity1 extends AppCompatActivity {

    EditText name,email,enroll,pass;
    String name_str,email_str,enroll_str,pass_str;
    String path;
    Button submit;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up1);

        name = findViewById(R.id.su_name);
        email = findViewById(R.id.su_email);
        enroll = findViewById(R.id.su_enroll);
        pass = findViewById(R.id.su_pass);
        submit = findViewById(R.id.su_submit);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        path = getIntent().getStringExtra("path");


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name_str = name.getText().toString().trim();
                email_str = email.getText().toString().trim();
                enroll_str = enroll.getText().toString().trim();
                pass_str = pass.getText().toString().trim();
                Toast.makeText(SignUpActivity1.this, ""+email_str+pass_str, Toast.LENGTH_SHORT).show();

                if(name_str.equals("")||email_str.equals("")||enroll_str.equals("")||enroll_str.equals("")||pass_str.equals("")){
                    Toast.makeText(SignUpActivity1.this, "Please fill all entries", Toast.LENGTH_SHORT).show();
                }

                else{

                    auth.createUserWithEmailAndPassword(email_str,pass_str).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                Map<String, Object> userData = new HashMap<>();
                                userData.put("name", name_str);
                                userData.put("email", email_str);
                                userData.put("password", pass_str);
                                userData.put("enrollment", enroll_str);
                                userData.put("imagepath", path);

                                firestore.collection("UserData").document(email_str).set(userData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Data has been successfully stored
                                                Intent intent = new Intent(SignUpActivity1.this,NewActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // An error occurred while trying to store the data
                                                Toast.makeText(SignUpActivity1.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });


                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpActivity1.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }


            }
        });





    }
}