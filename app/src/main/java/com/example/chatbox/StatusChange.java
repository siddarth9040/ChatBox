package com.example.chatbox;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusChange extends AppCompatActivity {
Toolbar mtoolbar;
TextInputLayout mstatus;
DatabaseReference mdatabse;
FirebaseUser muser;
ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_change);
        mtoolbar =(Toolbar)findViewById(R.id.status_appbar);
        setSupportActionBar(mtoolbar);
        String status_value = getIntent().getStringExtra("status");
        muser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = muser.getUid();
        mdatabse = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mstatus = (TextInputLayout)findViewById(R.id.status);
        mstatus.getEditText().setText(status_value);
        Button save = (Button)findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd= new ProgressDialog(StatusChange.this);
                pd.setTitle("Saving Changes");
                pd.setMessage("Please Wait");
                pd.show();
                String stat = mstatus.getEditText().getText().toString();
                mdatabse.child("status").setValue(stat).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete( Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        pd.dismiss();
                    }
                    else
                    {
                        Toast.makeText(StatusChange.this, "Error Occured", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                    }
                });
            }
        });
    }
}
