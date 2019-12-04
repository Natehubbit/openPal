package com.walkerstechbase.openpal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class JobPostingsDetails extends AppCompatActivity {

    TextView titleTV, contentTV;
    String getTitle, getContent, getTimeStanp, getImg, getPostedBy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_postings_details);

        //init views
        titleTV = findViewById(R.id.title_TV);
        contentTV = findViewById(R.id.content_TV);

        //get intent
        getTitle = getIntent().getExtras().getString("jobTitle");
        getContent =  getIntent().getExtras().getString("jobContent");

        //setting texts to text views
        titleTV.setText(getTitle);
        contentTV.setText(getContent);
    }
}
