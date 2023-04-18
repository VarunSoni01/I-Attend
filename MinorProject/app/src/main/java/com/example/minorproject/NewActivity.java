package com.example.minorproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.FrameLayout;

public class NewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        framesetup();


    }

    private void framesetup() {
        FrameLayout frameLayout = findViewById(R.id.framelayout);

// Create a new instance of the fragment that you want to add
        HomeFragment myFragment = new HomeFragment();

// Get the FragmentManager and begin a transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

// Add the fragment to the transaction and commit it
        fragmentTransaction.add(R.id.framelayout, myFragment);
        fragmentTransaction.commit();
    }
}