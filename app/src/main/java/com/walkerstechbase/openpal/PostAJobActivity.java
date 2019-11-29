package com.walkerstechbase.openpal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PostAJobActivity extends AppCompatActivity {
    EditText postJobTitle, postJobContent;
    ImageButton postJobBtn;
    String jobTitle, jobContent,saveCurrentDate;
    Toolbar toolbar;

    //FireBase
    DatabaseReference databaseReference, getUserInfoReference;
    private FirebaseAuth mAuth;

    Jobs jobs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_a_job);

        //initializing
        postJobTitle = findViewById(R.id.post_a_job_title);
        postJobContent = findViewById(R.id.post_a_job_content);
        postJobBtn = findViewById(R.id.post_job_btn);
        toolbar = findViewById(R.id.post_job_toolbar);
        mAuth = FirebaseAuth.getInstance();


        toolbar.setTitle("Post Job");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //button onClick
        postJobBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jobTitle = postJobTitle.getText().toString();
                jobContent = postJobContent.getText().toString();


                final Jobs jobbies = new Jobs();
                jobbies.setTitle(jobTitle);
                jobbies.setContents(jobContent);
                jobbies.setTimestamp(saveCurrentDate);


                //getting user data
                String currentUserID = mAuth.getCurrentUser().getUid();
                getUserInfoReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
                getUserInfoReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            jobs = dataSnapshot.getValue(Jobs.class);
                            jobbies.setPostBy(jobs.getName());
                            jobbies.setImgUrl(jobs.getImage());
                            post(jobbies);
                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }
        });


    }

    private void post(Jobs jobs1){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("job postings");


        databaseReference.push().setValue(jobs1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(PostAJobActivity.this, "Posted Succesfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
