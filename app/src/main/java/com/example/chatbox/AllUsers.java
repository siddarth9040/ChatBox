package com.example.chatbox;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsers extends AppCompatActivity {
    Toolbar mtoolbar;
    RecyclerView user_list;
    DatabaseReference mUsersDatabse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        mtoolbar = (Toolbar)findViewById(R.id.alluser_appbar);
        user_list = (RecyclerView)findViewById(R.id.users_list);
        mUsersDatabse = FirebaseDatabase.getInstance().getReference().child("users");
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user_list.setHasFixedSize(true);
        user_list.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<users> options =

                new FirebaseRecyclerOptions.Builder<users>()
                        .setQuery(mUsersDatabse, users.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<users, UsersViewHolder>(options) {

            @Override

            protected void onBindViewHolder( UsersViewHolder holder, int position, users users) {
                holder.setName(users.getName());
                holder.setRole(users.getStatus());
                holder.setImage(users.getThumb_img());
//....
                final String user_id = getRef(position).getKey();
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileintent = new Intent(AllUsers.this,profileActivity.class);
                        profileintent.putExtra("id",user_id);
                        startActivity(profileintent);
                    }
                });
            }

            @Override
            public UsersViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_layout, parent, false);
                return new UsersViewHolder(view);
            }
        };
        user_list.setAdapter(adapter);
        adapter.startListening();
    }

    @Override

    protected void onStop() {
        super.onStop();
    }


    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {

            super(itemView);
            mView =itemView;
        }

        public void setName(String name) {
            TextView userNameView = mView.findViewById(R.id.user_name);
            userNameView.setText(name);

        }
        public void setRole(String role){
            TextView userRole = mView.findViewById(R.id.user_status);
            userRole.setText(role);
        }

        public void setImage(String image){
            CircleImageView userImage = mView.findViewById(R.id.user_img);
            Picasso.get().load(image).placeholder(R.drawable.default_img).into(userImage);
        }

    }
}