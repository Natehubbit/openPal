package com.walkerstechbase.openpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CounsellingActivity extends AppCompatActivity {
    //Adapter ArrayLists
    List<Counsel> listItems;
    List<Counsel> listKeys;
    RecyclerView recyclerView;
    //CourseCodeAdapter adapter;
    private Boolean itemSelected = false;
    private int selectedPosition = 0;

    //CounsellorsAdapter adapter;
    Counsel counsel;
    //private RecyclerView FirebaseRecyclerView;
    private LinearLayoutManager linearLayoutManager;
   // private CounsellorsAdapter coursecodeAdapter;
    private String name, date, time, number, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counselling);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.counselling_activity_recycler_view);

        listItems = new ArrayList<>();
        listKeys = new ArrayList<>();


                recyclerView.setLayoutManager(new LinearLayoutManager(this));


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Counsels");
        Query query = ref;
        FirebaseRecyclerOptions<Counsel> options =
                new FirebaseRecyclerOptions.Builder<Counsel>()
                        .setQuery(query, Counsel.class)
                        .setLifecycleOwner(this)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Counsel, CounselViewHolder>(options) {


            @NonNull
            @Override
            public CounselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LinearLayout.inflate(getApplicationContext(), R.layout.counsel_list, parent);
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.counsel_list, parent, false);

                return new CounselViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CounselViewHolder counselViewHolder, int i, @NonNull Counsel counsel) {
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot childsnap : dataSnapshot.getChildren()){

                            name = counsel.getName();
                            date = counsel.getDate();
                            time = counsel.getTime();
                            number = counsel.getPhoneNumber();
                            message = counsel.getMessage();

                            //make message field invisible if message is null
                            if (message == null){
                                counselViewHolder.message.setVisibility(View.GONE);
                            }

                            counselViewHolder.name.setText(name);
                            counselViewHolder.date.setText(String.format("Date : %s", date));
                            counselViewHolder.time.setText(String.format("Time : %s",time));
                            counselViewHolder.number.setText(String.format("Phone : %s" , number));
                            counselViewHolder.message.setText(String.format("Reason : %s", message));

                        }

                        counselViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(getApplicationContext(), "name "+ name, Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(new Intent(getApplicationContext(), BookCounselling.class));
//                                intent.putExtra("counsellor_id",id);
//                                startActivity(intent);
                            }
                        });

                        counselViewHolder.lottieAnimationView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                counselViewHolder.lottieObj.playAnimation();
                                counselViewHolder.accept.setText("Accepted");
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };



recyclerView.setAdapter(adapter);






                //recyclerView = findViewById(R.id.home_recyclerView);
//        recyclerView.setHasFixedSize(true);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//        adapter = new CounsellorsAdapter();
//        recyclerView.setAdapter(adapter);

    }


    private void firebaseDB() {
        //Initializing the databaseReference
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("counsellors");
        //uploadsDatabaseReference = FirebaseDatabase.getInstance().getReference("users/"+ getUDBKey + "/uploads");


        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //below code takes data from DB
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    return;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("AddNewActivity : ", "Failed to read value.", databaseError.toException());
            }
        });

    }

}
