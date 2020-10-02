package com.example.chatbox;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {
    RecyclerView Friendlist;
    DatabaseReference friendsdatabase,usersdatabase;
    FirebaseAuth mAuth;
    String mCurrent_user;
    View mMainview;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainview = inflater.inflate(R.layout.fragment_friends, container, false);
        Friendlist = (RecyclerView) mMainview.findViewById(R.id.friendslist);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user = mAuth.getCurrentUser().getUid();
        friendsdatabase = FirebaseDatabase.getInstance().getReference().child("friends").child(mCurrent_user);
        friendsdatabase.keepSynced(true);
        usersdatabase = FirebaseDatabase.getInstance().getReference().child("users");
        usersdatabase.keepSynced(true);
        Friendlist.setHasFixedSize(true);
        Friendlist.setLayoutManager(new LinearLayoutManager(getContext()));
        return mMainview;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<friends> options =

                new FirebaseRecyclerOptions.Builder<friends>()
                        .setQuery(friendsdatabase, friends.class)
                        .build();

        FirebaseRecyclerAdapter friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<friends, FriendsViewHolder>(options) {


            @Override

            protected void onBindViewHolder( final FriendsViewHolder friendsViewHolder, int i, friends friends) {
                friendsViewHolder.setDate(friends.getDate());

                final String list_user_id = getRef(i).getKey();
                usersdatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_img").getValue().toString();

                        if(dataSnapshot.hasChild("online")) {

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            friendsViewHolder.setUserOnline(userOnline);

                        }

                        friendsViewHolder.setName(userName);
                        friendsViewHolder.setUserImage(userThumb, getContext());
                        // When Click to Friends View
                        friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                CharSequence options[] = new CharSequence[]{"Open Profile", "Send message"};

                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        //Click Event for each item.
                                        if(i == 0){

                                            Intent profileIntent = new Intent(getContext(), profileActivity.class);
                                            profileIntent.putExtra("user_id", list_user_id);
                                            startActivity(profileIntent);

                                        }

                                        if(i == 1){

                                            Intent chatIntent = new Intent(getContext(), .class);
                                            chatIntent.putExtra("user_id", list_user_id);
                                            chatIntent.putExtra("user_name", userName);
                                            startActivity(chatIntent);

                                        }

                                    }
                                });

                                builder.show();

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }



            @Override
            public FriendsViewHolder onCreateViewHolder( ViewGroup parent, int i) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);
                return new FriendsViewHolder(view);
            }


        };

        Friendlist.setAdapter(friendsRecyclerViewAdapter);
        friendsRecyclerViewAdapter.startListening();

    }
    


    public class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDate(String date){

            TextView userStatusView = mView.findViewById(R.id.user_status);
            userStatusView.setText(date);

        }

        public void setName(String name){

            TextView userNameView = mView.findViewById(R.id.user_name);
            userNameView.setText(name);

        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = mView.findViewById(R.id.user_img);
            Picasso.get().load(thumb_image).placeholder(R.drawable.default_img).into(userImageView);

        }

        public void setUserOnline(String online_status) {

            ImageView userOnlineView =mView.findViewById(R.id.user_single_online_icon);

            if(online_status.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }


    }


}