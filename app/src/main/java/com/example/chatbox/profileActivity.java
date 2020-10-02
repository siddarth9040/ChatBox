package com.example.chatbox;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class profileActivity extends AppCompatActivity {
    ImageView profile_img;
    TextView prof_name,prof_status,prof_friends;
    Button sendreq,declinereq;
    DatabaseReference muserdatabase;
    ProgressDialog pd;
    int mcurrent_state;
    FirebaseUser mcurrent_user;
    DatabaseReference friendreqdatabse,friendsdatabase,notificationdatabase,Rootref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String user_id = getIntent().getStringExtra("id");
        Rootref = FirebaseDatabase.getInstance().getReference();
        muserdatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        friendreqdatabse = FirebaseDatabase.getInstance().getReference().child("friend_req");
        friendsdatabase = FirebaseDatabase.getInstance().getReference().child("friends");
        notificationdatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mcurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        prof_name = (TextView)findViewById(R.id.prof_name);
        prof_status =(TextView)findViewById(R.id.prof_status);
        prof_friends = (TextView)findViewById(R.id.prof_friends);
        profile_img = (ImageView)findViewById(R.id.dispimg);
        sendreq = (Button)findViewById(R.id.sendreq);
        declinereq =(Button)findViewById(R.id.declinereq);
        mcurrent_state=0;
        if(user_id.equals(mcurrent_user.getUid()))
        {
            sendreq.setVisibility(View.INVISIBLE);
            declinereq.setVisibility(View.INVISIBLE);
            sendreq.setEnabled(false);
            declinereq.setEnabled(false);
        }

        declinereq.setVisibility(View.INVISIBLE);
        declinereq.setEnabled(false);

        // current_state = 0(not friends) 1 (friends) 2(Request Recieved) 3(Request Sent)



        pd = new ProgressDialog(this);
        pd.setTitle("Loading User Data");
        pd.setMessage("Please Wait");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        muserdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                String dispname = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                prof_name.setText(dispname);
                prof_status.setText(status);
                Picasso.get().load(image).placeholder(R.drawable.default_img).into(profile_img);

                //----------Friends LIST----------------

                friendreqdatabse.child(mcurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange( DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_id)) {
                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if (req_type.equals("received")) {
                                mcurrent_state = 2;
                                sendreq.setText("Accept Friend Request");
                                declinereq.setEnabled(true);
                                declinereq.setVisibility(View.VISIBLE);

                            } else if (req_type.equals("sent")) {
                                mcurrent_state = 3;
                                sendreq.setText("Cancel Friend Request");

                            }
                            pd.dismiss();
                        }
                            else
                            {
                                friendsdatabase.child(mcurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange( DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild(user_id))
                                        {
                                            mcurrent_state = 1;
                                            sendreq.setText(" UnFriend ");
                                            declinereq.setVisibility(View.INVISIBLE);
                                            declinereq.setEnabled(false);
                                        }
                                        pd.dismiss();

                                    }

                                    @Override
                                    public void onCancelled( DatabaseError databaseError) {
                                        pd.dismiss();
                                    }
                                });

                            }
                        }


                    @Override
                    public void onCancelled( DatabaseError databaseError) {
                        pd.dismiss();
                    }
                });
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {
                pd.dismiss();
            }
        });
        sendreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendreq.setEnabled(false);

                // --------------NOT FRIENDS -------------------

                if(mcurrent_state == 0)
                {

                    DatabaseReference newNotification = Rootref.child("notifications").child(user_id).push();
                    String NotificationID = newNotification.getKey();
                    HashMap<String, String > notificationdata = new HashMap<>();
                    notificationdata.put("from",mcurrent_user.getUid());
                    notificationdata.put("type","request");
                    Map requestmap = new HashMap();
                    requestmap.put("friend_req/"+mcurrent_user.getUid()+"/"+ user_id +"/request_type","sent");
                    requestmap.put("friend_req/"+user_id+"/"+mcurrent_user.getUid()+"/request_type","received");
                    requestmap.put("notifications/"+user_id+"/"+NotificationID,notificationdata);
                    Rootref.updateChildren(requestmap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete( DatabaseError databaseError,  DatabaseReference databaseReference) {
                            if(databaseError!= null) {
                                Toast.makeText(profileActivity.this, "Error in sending friend request", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                sendreq.setText("Cancel Friend Request");
                                mcurrent_state = 3;
                            }

                            sendreq.setEnabled(true);
                        }

                    });
                }
                // --------------Cancel Request -------------------

                if(mcurrent_state == 3)
                {

                    friendreqdatabse.child(mcurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendreqdatabse.child(user_id).child(mcurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    sendreq.setEnabled(true);
                                    mcurrent_state = 0;
                                    sendreq.setText("Send Friend Request");
                                    declinereq.setVisibility(View.INVISIBLE);
                                    declinereq.setEnabled(false);
                                }
                            });
                        }
                    });
                }
                // current_state = 0(not friends) 1 (friends) 2(Request Recieved) 3(Request Sent)
                if(mcurrent_state == 2)
                {
                    declinereq.setVisibility(View.VISIBLE);
                    declinereq.setEnabled(true);
                    final String current_date = DateFormat.getDateTimeInstance().format(new Date());
                    Map friendsmap = new HashMap();
                    friendsmap.put("friends/"+ mcurrent_user.getUid()+"/"+user_id+"/date",current_date);
                    friendsmap.put("friends/"+user_id+"/"+ mcurrent_user.getUid()+"/date",current_date);

                    friendsmap.put("friend_req/"+ mcurrent_user.getUid()+"/"+user_id,null);
                    friendsmap.put("friend_req/"+ mcurrent_user.getUid()+"/"+user_id,null);
                    Rootref.updateChildren(friendsmap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete( DatabaseError databaseError,  DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                sendreq.setEnabled(true);
                                mcurrent_state = 1;
                                sendreq.setText(" UnFriend ");
                                declinereq.setVisibility(View.INVISIBLE);
                                declinereq.setEnabled(false);

                            }
                            else
                            {
                                String Error = databaseError.getMessage();
                                Toast.makeText(profileActivity.this, Error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                if(mcurrent_state==1)
                {
                    Map unfriendsmap = new HashMap();
                    unfriendsmap.put("friends/"+ mcurrent_user.getUid()+"/"+user_id,null);
                    unfriendsmap.put("friends/"+user_id+"/"+ mcurrent_user.getUid(),null);
                    Rootref.updateChildren(unfriendsmap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete( DatabaseError databaseError,  DatabaseReference databaseReference) {
                            if (databaseError == null) {

                                mcurrent_state = 0;
                                sendreq.setText("Send Friend Request");
                                declinereq.setVisibility(View.INVISIBLE);
                                declinereq.setEnabled(false);

                            }
                            else
                            {
                                String Error = databaseError.getMessage();
                                Toast.makeText(profileActivity.this, Error, Toast.LENGTH_SHORT).show();
                            }
                            sendreq.setEnabled(true);
                        }
                    });
                }
            }
        });
        declinereq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declinereq.setEnabled(false);
                if(mcurrent_state == 2)
                {
                    friendreqdatabse.child(mcurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendreqdatabse.child(user_id).child(mcurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    sendreq.setEnabled(true);
                                    mcurrent_state = 0;
                                    sendreq.setText("Send Friend Request");
                                    declinereq.setVisibility(View.INVISIBLE);
                                    declinereq.setEnabled(false);
                                }
                            });
                        }
                    });
                }
            }
        });

    }
}
