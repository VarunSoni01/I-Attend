package com.example.minorproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    Button submit;
    EditText email,pass;
    TextView signup;
//    FirebaseAuth auth;
    String em,pas;
    FirebaseAuth  auth;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submit = findViewById(R.id.loginbt);
        email = findViewById(R.id.login_name);
        pass = findViewById(R.id.login_pass);
        signup = findViewById(R.id.login_signup);
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        FirebaseUser curruser = auth.getCurrentUser();

        if(curruser != null){
            Intent in = new Intent(MainActivity.this,NewActivity.class);
            startActivity(in);
            finish();
        }

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,SignUpActivity2.class);
                startActivity(i);

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.setMessage("Loading...");

// Set whether the dialog is cancelable or not
                dialog.setCancelable(false);

// Show the progress dialog
                dialog.show();
                em = email.getText().toString().trim();
                pas = pass.getText().toString().trim();

                if(em == null || em.equals("") || pas.equals("") || pas == null){
                    Toast.makeText(MainActivity.this, "Please fill all entries", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                else {


                    auth.signInWithEmailAndPassword(em, pas).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, NewActivity.class);
                                startActivity(intent);
                                dialog.dismiss();
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

    }
}