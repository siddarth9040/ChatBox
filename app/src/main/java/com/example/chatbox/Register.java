package com.example.chatbox;


import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class Register extends AppCompatActivity {
TextInputLayout regname,regemail,regpass;
Button reg;
    ProgressDialog pd;
    FirebaseAuth mAuth;
    DatabaseReference mdatabase;
    Toolbar mtoolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);
        mtoolbar = (Toolbar)findViewById(R.id.registertoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        regname =(TextInputLayout)findViewById(R.id.regname);
        regemail =(TextInputLayout)findViewById(R.id.textInputLayout);
        regpass =(TextInputLayout)findViewById(R.id.regpass);
        reg = (Button)findViewById(R.id.button);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dispname = regname.getEditText().getText().toString();
                String email = regemail.getEditText().getText().toString();
                String password = regpass.getEditText().getText().toString();
                if (!TextUtils.isEmpty(dispname) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {


                    registeruser(dispname, email, password);
                }
            }

            private void registeruser(final String dispname, String email, String password) {
                pd.setTitle("Registering User");
                pd.setMessage("Please Wait");
                pd.setCanceledOnTouchOutside(false);
                pd.show();
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = current_user.getUid();
                            String devicetoken = FirebaseInstanceId.getInstance().getToken();
                            mdatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                            HashMap<String,String > userMap = new HashMap();
                            userMap.put("name",dispname);
                            userMap.put("status","Hi there,I am using Chat Box");
                            userMap.put("image","default");
                            userMap.put("thumb_img","default");
                            userMap.put("device_token",devicetoken);
                            mdatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete( Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        pd.dismiss();
                                        Intent mainintent = new Intent(Register.this, MainActivity.class);
                                        finish();
                                        startActivity(mainintent);
                                    }
                                }
                            });
                        }
                        else
                        {
                            pd.hide();
                            Toast.makeText(Register.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
