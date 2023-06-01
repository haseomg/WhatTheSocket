package com.example.whatthesocket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Enter extends AppCompatActivity {

    EditText name;
    Button enter;

    String TAG = "[EnterActivity]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        initialize();
        setEnter();
    } // onCreate End

    public void initialize() {
        name = findViewById(R.id.nameEditText);
        enter = findViewById(R.id.enterBtn);
    } // initialize End

    public void setEnter() {
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "enter button onClick()");

                Intent intent = new Intent(getApplicationContext(), Select.class);

                String userName = name.getText().toString();
                Log.i(TAG, "userName Check : " + userName);

                intent.putExtra("username", userName);
                startActivity(intent);
            } // onClick END
        }); // setOnClickListener END
    } // setEnter END

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

} // Enter Class END