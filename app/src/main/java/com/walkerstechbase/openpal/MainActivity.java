package com.walkerstechbase.openpal;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener
{
    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private String currentUserID;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //dndBLyf7ViQ:APA91bHB37i2rkxkW1OPZaYzv7gXmohvBgYDhW1prJ-_B6veunXSiD23utp0aDwmlpbNZ0O3hCSeQMZl1Nl7hCc-3Ly6BUlZf-j1ug6p6tM_hKsUHPOhwV8aDh9VzWP-YhBkR2r5jxY0

        //token for sending to specific device
        //for sending to all device using own server code subscribe your app to one topic
        Log.d("TOken ",""+ FirebaseInstanceId.getInstance().getToken());
        FirebaseMessaging.getInstance().subscribeToTopic("allDevices");


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();


        mToolbar = findViewById(R.id.main_page_toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
        setSupportActionBar(mToolbar);
        //mToolbar.setTitle("OpenPal");
        //getSupportActionBar().setTitle("OpenPal2");
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
//        actionBarDrawerToggle = navigationView.getActionBarDrawerToggle();
        //Code below opens drawer
        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });



        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);


        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        if (currentUser == null)
        {
            SendUserToLoginActivity();
        }
        else
        {
            updateUserStatus("online");

            VerifyUserExistance();
        }
    }


    @Override
    protected void onStop()
    {
        super.onStop();

        if (currentUser != null)
        {
            updateUserStatus("offline");
        }
    }



    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (currentUser != null)
        {
            updateUserStatus("offline");
        }
    }



    private void VerifyUserExistance()
    {
        String currentUserID = mAuth.getCurrentUser().getUid();

        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if ((dataSnapshot.child("name").exists()))
                {
//                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.small_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_find_friends_option)
        {
            SendUserToFindFriendsActivity();
        }

        return true;
    }


    private void RequestNewGroup()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Enter Group Name :");

        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setBackground(getResources().getDrawable(R.color.colorAccent));
        groupNameField.setHint(" ");
        groupNameField.setTextColor(getResources().getColor(android.R.color.white));
        groupNameField.setPadding(40,40, 40,40);
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                String groupName = groupNameField.getText().toString();

                if (TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(MainActivity.this, "Please write Group Name...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    CreateNewGroup(groupName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }



    private void CreateNewGroup(final String groupName)
    {
        RootRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, groupName + " Group Created Successfully...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(MainActivity.this, PhoneLoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);

    }

    private void SendUserToSettingsActivity()
    {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }


    private void SendUserToFindFriendsActivity()
    {
        Intent findFriendsIntent = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(findFriendsIntent);
    }


    private void SendUserToBooKCounsellingActivity(){
        Intent intent = new Intent(MainActivity.this, BookCounselling.class);
        startActivity(intent);
    }

    private void updateUserStatus(String state)
    {
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        RootRef.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.main_logout_option)
        {
            //close drawer
            drawerLayout.closeDrawer(GravityCompat.START);


            final AlertDialog.Builder builder;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle("Logout?\n")
                    .setMessage("Are you sure you want to logout of your account?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with logout
                            try {
                                updateUserStatus("offline");
                                mAuth.signOut();
                                SendUserToLoginActivity();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    //.setIcon(R.drawable.ic_bubble_chart_black_24dp)
                    .show();
        }
        if (id == R.id.main_settings_option)
        {
            //close drawer
            drawerLayout.closeDrawer(GravityCompat.START);

            SendUserToSettingsActivity();
        }
        if (id == R.id.counsels)
        {
            startActivity(new Intent(MainActivity.this, CounsellingActivity.class));
//            RequestNewGroup();
        }
        if (id == R.id.main_find_friends_option)
        {
            //close drawer
            drawerLayout.closeDrawer(GravityCompat.START);

            SendUserToFindFriendsActivity();
        }
        if(id == R.id.main_book_counselling_session){
            //close drawer
            drawerLayout.closeDrawer(GravityCompat.START);

            SendUserToBooKCounsellingActivity();
        }
        if (id == R.id.main_job_postings){
            //close drawer
            drawerLayout.closeDrawer(GravityCompat.START);

            startActivity(new Intent(MainActivity.this, JobPostings.class));
        }
        return true;
    }
}
