package com.walkerstechbase.openpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class JobPostings extends AppCompatActivity {
    FloatingActionButton fab;
    ListView listView ;
    DatabaseReference databaseReference;
    private ArrayList<Jobs> mJobs = new ArrayList<Jobs>();
    FirebaseListAdapter<Jobs> firebaseListAdapter;
    Toolbar toolbar;

    String getTitle, getContent, getTimeStanp, getImg, getPostedBy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_postings);

        //init
        fab = findViewById(R.id.fab);
        listView = findViewById(R.id.job_listview);
        toolbar = findViewById(R.id.job_toolbar);


        toolbar.setTitle("Job Vacancies");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //init db ref
        databaseReference = FirebaseDatabase.getInstance().getReference().child("job postings");
        //display lists offline
        //databaseReference.keepSynced(true);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(JobPostings.this, PostAJobActivity.class));
            }
        });


        //fireBase Ui
        Query query = databaseReference;

        FirebaseListOptions<Jobs> options = new FirebaseListOptions.Builder<Jobs>()
                .setQuery(databaseReference, Jobs.class)
                .setLayout(R.layout.jobs_items)
                .build();

        firebaseListAdapter = new FirebaseListAdapter<Jobs>(options) {

            @Override
            protected void populateView(View v, Jobs model, int position) {
                final TextView title = (TextView) v.findViewById(R.id.job_item_title);
                TextView content = (TextView) v.findViewById(R.id.job_item_content);
                TextView postedBy = (TextView) v.findViewById(R.id.job_item_postedBy);
                final CircleImageView image =  v.findViewById(R.id.job_item_image);
                TextView timestamp = v.findViewById(R.id.job_item_timestamp);



                getTitle = model.getTitle();
                getImg = model.getImgUrl();
                getContent = model.getContents();
                getTimeStanp = model.getTimestamp();
                getPostedBy = model.getPostBy();

                title.setText(getTitle);
                content.setText(getContent);
                timestamp.setText(getTimeStanp);
                if (model.getPostBy() == null){
                    postedBy.setText("Anon");
                }else{
                    postedBy.setText(getPostedBy);
                }


                Picasso.get().load(getImg).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.imgplaceholder).into(image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(getImg).into(image);
                    }
                });

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(JobPostings.this, JobPostingsDetails.class);
                        intent.putExtra("jobTitle", getTitle);
                        intent.putExtra("jobContent", getContent);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public Jobs getItem(int position) {
                return super.getItem(super.getCount() - position - 1);
            }
        };
        listView.setAdapter(firebaseListAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseListAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseListAdapter.stopListening();
    }
}
