package com.example.chatbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;


import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
FirebaseAuth firebaseAuth;
   Toolbar mtoolbar;
   ViewPager mviewpager;
   TabLayout mtablayout;
   SectionPagerAdapter mSectionPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        mtoolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Chat Box");

        mviewpager = (ViewPager)findViewById(R.id.main_tabpager);
        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        mviewpager.setAdapter(mSectionPagerAdapter);
        mtablayout= (TabLayout)findViewById(R.id.main_tabs);
        mtablayout.setupWithViewPager(mviewpager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null)
        {
            sendTostart();
        }
    }

    private void sendTostart() {
        Intent startintent = new Intent(MainActivity.this,StartActivity.class);
        startActivity(startintent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.main_menu,menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()== R.id.main_logout)
        {
            FirebaseAuth.getInstance().signOut();
            sendTostart();
        }
        if(item.getItemId() == R.id.main_accset)
        {
            Intent settingintent = new Intent(MainActivity.this,AccountSettings.class);
            startActivity(settingintent);
        }
        if(item.getItemId() == R.id.main_users)
        {
            Intent userintent = new Intent(MainActivity.this,AllUsers.class);
            startActivity(userintent);
        }
        return true;
    }
}
