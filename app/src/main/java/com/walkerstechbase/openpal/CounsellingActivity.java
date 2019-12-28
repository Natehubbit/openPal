package com.walkerstechbase.openpal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class CounsellingActivity extends AppCompatActivity {
    //Adapter ArrayLists
    List<Counsellor> listItems;
    List<Counsellor> listKeys;
    RecyclerView recyclerView;
    //CourseCodeAdapter adapter;
    private Boolean itemSelected = false;
    private int selectedPosition = 0;

    //CounsellorsAdapter adapter;
    Counsellor counsellor;
    //private RecyclerView FirebaseRecyclerView;
    private LinearLayoutManager linearLayoutManager;
   // private CounsellorsAdapter coursecodeAdapter;
    String name, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counselling);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.counselling_activity_recycler_view);

        listItems = new ArrayList<>();
        listKeys = new ArrayList<>();


                recyclerView.setLayoutManager(new LinearLayoutManager(this));


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("counsellors");
        Query query = ref;
        FirebaseRecyclerOptions<Counsellor> options =
                new FirebaseRecyclerOptions.Builder<Counsellor>()
                        .setQuery(query, Counsellor.class)
                        .setLifecycleOwner(this)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Counsellor, CounsellorViewHolder>(options) {


            @NonNull
            @Override
            public CounsellorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LinearLayout.inflate(getApplicationContext(), R.layout.counsellor_list, null);
                return new CounsellorViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CounsellorViewHolder counsellorViewHolder, int i, @NonNull Counsellor counsellor) {
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot childsnap : dataSnapshot.getChildren()){

//                            Toast.makeText(getApplicationContext(), "jj " + childsnap.child("counsellorName").getValue().toString(), Toast.LENGTH_SHORT).show();
                            name = counsellor.getCounsellorName();
                            id = counsellor.getCounsellorID();

                            counsellorViewHolder.counsellorName.setText(name);
                        }

                        counsellorViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(getApplicationContext(), "counsellor "+ id, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(new Intent(getApplicationContext(), BookCounselling.class));
                                intent.putExtra("counsellor_id",id);
                                startActivity(intent);
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


                    //for(Counsellor mContactIterator : childSnapshot){
                      //  if(childSnapshot.getValue().equals(.getName())){
                            counsellor.setCounsellorName("Mr Qyansah");
                     //   }
                   // }


                    return;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("AddNewActivity : ", "Failed to read value.", databaseError.toException());
            }
        });

    }

    private void getCounsellorDetails() {

    }
}
