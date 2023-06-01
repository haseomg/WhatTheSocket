package com.example.whatthesocket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Select extends AppCompatActivity {

    private SharedPreferences shared;
    private SharedPreferences.Editor editor;

    String TAG = "[Select CLASS]";
    Intent intent;
    String getUsername, getRoomName;
    Button jayden, oscar, taron, stella, min, bob;
    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        Log.i(TAG, "onCreate()");

        initial();
    } // onCreate END

    void initial() {
        Log.i(TAG, "initial()");
        intent = getIntent();
        getUsername = intent.getStringExtra("username");
        // and To ChatActivity send Intent

        shared = getSharedPreferences("USER", MODE_PRIVATE);
        editor = shared.edit();

        jayden = findViewById(R.id.jayden);
        oscar = findViewById(R.id.oscar);
        taron = findViewById(R.id.taron);
        stella = findViewById(R.id.stella);
        min = findViewById(R.id.min);
        bob = findViewById(R.id.bob);
        name = findViewById(R.id.userNameMain);
        name.setText(getUsername);

        setButton();
    } // initial method END

    void setButton() {

        jayden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "jayden button onClick()");

                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);

                String yourname = jayden.getText().toString();
                getRoomName = getUsername + "_" + yourname;

                Log.i(TAG, "yourname Check : " + yourname);
                Log.i(TAG, "username Check : " + getUsername);
                Log.i(TAG, "getRoomName Check : " + getRoomName);

                intent.putExtra("yourname", yourname);
                intent.putExtra("username", getUsername);
                editor.putString("room", getRoomName);
                editor.putString("name", getUsername);
                editor.commit();
                startActivity(intent);
            } // onClick END
        }); // setOnClickListener END

        oscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "oscar button onClick()");

                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);

                String yourname = oscar.getText().toString();
                getRoomName = getUsername + "_" + yourname;

                Log.i(TAG, "yourname Check : " + yourname);
                Log.i(TAG, "username Check : " + getUsername);
                Log.i(TAG, "getRoomName Check : " + getRoomName);

                intent.putExtra("yourname", yourname);
                intent.putExtra("username", getUsername);
                editor.putString("room", getRoomName);
                editor.putString("name", getUsername);
                editor.commit();
                startActivity(intent);
            } // onClick END
        }); // setOnClickListener END


        stella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "oscar button onClick()");

                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);

                String yourname = stella.getText().toString();
                getRoomName = getUsername + "_" + yourname;

                Log.i(TAG, "yourname Check : " + yourname);
                Log.i(TAG, "username Check : " + getUsername);
                Log.i(TAG, "getRoomName Check : " + getRoomName);

                intent.putExtra("yourname", yourname);
                intent.putExtra("username", getUsername);
                editor.putString("room", getRoomName);
                editor.putString("name", getUsername);
                editor.commit();
                startActivity(intent);
            } // onClick END
        }); // setOnClickListener END


        taron.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "taron button onClick()");

                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);

                String yourname = taron.getText().toString();
                getRoomName = getUsername + "_" + yourname;

                Log.i(TAG, "yourname Check : " + yourname);
                Log.i(TAG, "username Check : " + getUsername);
                Log.i(TAG, "getRoomName Check : " + getRoomName);

                intent.putExtra("yourname", yourname);
                intent.putExtra("username", getUsername);
                editor.putString("room", getRoomName);
                editor.putString("name", getUsername);
                editor.commit();
                startActivity(intent);
            } // onClick END
        }); // setOnClickListener END


        min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "min button onClick()");

                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);

                String yourname = min.getText().toString();
                getRoomName = getUsername + "_" + yourname;

                Log.i(TAG, "yourname Check : " + yourname);
                Log.i(TAG, "username Check : " + getUsername);
                Log.i(TAG, "getRoomName Check : " + getRoomName);

                intent.putExtra("yourname", yourname);
                intent.putExtra("username", getUsername);
                editor.putString("room", getRoomName);
                editor.putString("name", getUsername);
                editor.commit();
                startActivity(intent);
            } // onClick END
        }); // setOnClickListener END


        bob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "bob button onClick()");

                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);

                String yourname = bob.getText().toString();
                getRoomName = getUsername + "_" + yourname;

                Log.i(TAG, "yourname Check : " + yourname);
                Log.i(TAG, "username Check : " + getUsername);
                Log.i(TAG, "getRoomName Check : " + getRoomName);

                intent.putExtra("yourname", yourname);
                intent.putExtra("username", getUsername);
                editor.putString("room", getRoomName);
                editor.putString("name", getUsername);
                editor.commit();
                startActivity(intent);
            } // onClick END
        }); // setOnClickListener END


    } // setButton method END

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    } // onStart END

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    } // onResume END

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    } // onPause END

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    } // onStop END

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    } // onDestroy END

} // Select CLASS END