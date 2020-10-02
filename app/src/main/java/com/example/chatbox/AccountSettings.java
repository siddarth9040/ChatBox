package com.example.chatbox;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class AccountSettings extends AppCompatActivity {
DatabaseReference muserdatabse;

FirebaseUser mcurrentuser;
CircleImageView imageView;
TextView mname,mstatus;
Button chngimg,chngstat;
    String current_user;
    ProgressDialog pd;
    String down_url,thumb_url;
static final int Gallery_pick = 1;
StorageReference mimagestorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        mcurrentuser = FirebaseAuth.getInstance().getCurrentUser();
        current_user = mcurrentuser.getUid();
        muserdatabse = FirebaseDatabase.getInstance().getReference().child("users").child(current_user);
        muserdatabse.keepSynced(true);
        imageView = (CircleImageView)findViewById(R.id.setting_img);
        mname = (TextView)findViewById(R.id.setting_disp);
        mimagestorage = FirebaseStorage.getInstance().getReference();
        mstatus =(TextView)findViewById(R.id.setting_status);
        chngimg = (Button)findViewById(R.id.changeimg);
        chngstat = (Button)findViewById(R.id.changests);
        chngimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryintent = new Intent();
                galleryintent.setType("image/*");
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryintent,"SELECT IMAGE"),Gallery_pick);
            }
        });
        chngstat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = mstatus.getText().toString();
                Intent statusintent = new Intent(AccountSettings.this,StatusChange.class);
                statusintent.putExtra("status",status);
                startActivity(statusintent);
            }
        });
        muserdatabse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String thumb_img = dataSnapshot.child("thumb_img").getValue().toString();
                mname.setText(name);
                mstatus.setText(status);
                if (!image.equals("default")) {
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_img).into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(image).placeholder(R.drawable.default_img).into(imageView);
                        }
                    });
                }
            }
            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Gallery_pick && resultCode == RESULT_OK)
        {
            Uri imageuri = data.getData();
            CropImage.activity(imageuri)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                pd = new ProgressDialog(this);
                pd.setTitle("Uploading Image");
                pd.setMessage("Please Wait");
                pd.setCanceledOnTouchOutside(false);
                pd.show();
                final Uri resultUri = result.getUri();
                File thumb_filepath= new File(resultUri.getPath());

                Bitmap thumb_bitmap = null;
                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(70)
                            .compressToBitmap(thumb_filepath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte = baos.toByteArray();

                final StorageReference filepath = mimagestorage.child("profile_images").child(current_user+".jpg");
                final StorageReference thumb_file = mimagestorage.child("profile_images").child("thumbs").child(current_user+".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete( Task<UploadTask.TaskSnapshot> task) {
                       if( task.isSuccessful())
                       {
                           down_url  = filepath.getDownloadUrl().toString();
                           UploadTask uploadTask = thumb_file.putBytes(thumb_byte);
                           uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                               @Override
                               public void onComplete( Task<UploadTask.TaskSnapshot> thumb_task) {
                                   thumb_url = thumb_file.getDownloadUrl().toString();
                                if(thumb_task.isSuccessful())
                                {
                                    thumb_file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            thumb_url = uri.toString();
                                            muserdatabse.child("thumb_img").setValue(thumb_url);
                                        }
                                    });
                                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            down_url = uri.toString();
                                            muserdatabse.child("image").setValue(down_url);
                                        }
                                    });
                                    pd.dismiss();
                                    Toast.makeText(AccountSettings.this, "Uploading Successful", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    pd.dismiss();
                                    Toast.makeText(AccountSettings.this, "Error in Uploading Thumbnail", Toast.LENGTH_SHORT).show();
                                }
                               }
                           });

                       }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                pd.dismiss();
            }
        }
    }
}
