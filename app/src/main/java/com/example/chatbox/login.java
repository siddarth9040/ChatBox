package com.example.chatbox;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class login extends AppCompatActivity {
    TextInputLayout logemail,logpass;
    Button log;
    ProgressDialog pd;
    FirebaseAuth mAuth;
    Toolbar mtoolbar;
    DatabaseReference muserdatabse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        muserdatabse = FirebaseDatabase.getInstance().getReference().child("users");
        pd = new ProgressDialog(this);
        mtoolbar = (Toolbar)findViewById(R.id.logintoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        logemail =(TextInputLayout)findViewById(R.id.textInputLayout);
        logpass =(TextInputLayout)findViewById(R.id.regpass);
        log = (Button)findViewById(R.id.button);
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = logemail.getEditText().getText().toString();
                String password = logpass.getEditText().getText().toString();
                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                    loginuser(email, password);
                }
            }

            private void loginuser(String email, String password) {
                pd.setTitle("Logging In");
                pd.setMessage("Please wait while we check your Credentials.");
                pd.setCanceledOnTouchOutside(false);
                pd.show();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(    new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            pd.dismiss();
                            String user_id = mAuth.getCurrentUser().getUid();
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();

                            muserdatabse.child(user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent mainintent = new Intent(login.this, MainActivity.class);
                                    mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    finish();
                                    startActivity(mainintent);
                                }
                            });

                        } else {
                            pd.hide();
                            Toast.makeText(login.this, "Cannot Sign in. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}

