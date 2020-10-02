package com.example.chatbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {
Button startreg,haveacnt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        startreg = (Button)findViewById(R.id.startreg);
        haveacnt = (Button)findViewById(R.id.haveacnt);
        startreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regintent = new Intent(StartActivity.this,Register.class);
                startActivity(regintent);
            }
        });
        haveacnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logintent = new Intent(StartActivity.this,login.class);
                startActivity(logintent);
            }
        });
    }
}
