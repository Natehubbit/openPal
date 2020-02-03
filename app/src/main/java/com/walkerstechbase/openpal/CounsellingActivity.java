package com.walkerstechbase.openpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private String name, date, time, number, message, receiverId;
    DatabaseReference Rootref;
    DatabaseReference ContactsRef;
    DatabaseReference counselRef;
    private String  senderUserID;
    private FirebaseAuth mAuth;
    private String saveCurrentTime, saveCurrentDate;
    String counselKeyID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counselling);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.counselling_activity_recycler_view);

        listItems = new ArrayList<>();
        listKeys = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Rootref = FirebaseDatabase.getInstance().getReference();
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        senderUserID = mAuth.getCurrentUser().getUid();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        counselRef = FirebaseDatabase.getInstance().getReference().child("Counsels");
        Query query = counselRef;
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
                counselRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot childsnap : dataSnapshot.getChildren()){

                            name = counsel.getName();
                            date = counsel.getDate();
                            time = counsel.getTime();
                            number = counsel.getPhoneNumber();
                            message = counsel.getMessage();
                            receiverId = counsel.getUserID();


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
                                Toast.makeText(getApplicationContext(), "Click Accept to counsel user", Toast.LENGTH_SHORT).show();


                                //getting the ID for the clicked item to be deleted after counsellor accepts
                                counselKeyID = getRef(counselViewHolder.getAdapterPosition()).getKey();
//                                Intent intent = new Intent(new Intent(getApplicationContext(), BookCounselling.class));
//                                intent.putExtra("counsellor_id",id);
//                                startActivity(intent);
                            }
                        });

                        counselViewHolder.lottieAnimationView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                //getting the ID for the clicked item to be deleted after counsellor accepts
                                counselKeyID = getRef(counselViewHolder.getAdapterPosition()).getKey();

                                //animate lottie item
                                counselViewHolder.lottieObj.playAnimation();

                                //setting textView to "accepted"
                                counselViewHolder.accept.setText("Accepted");

                                //automatically saving number and sending message to user who booked counselling session
                                ContactsRef.child(senderUserID).child(receiverId)
                                .child("Contacts").setValue("Saved")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    SendMessage();
                                }
                            });

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

    private void SendMessage()
    {
        String messageText = "Hello, " + name + "! I have accepted to counsel you over your issue '" + message + "'";

        if (TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "first write your message...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String messageSenderRef = "Messages/" + senderUserID + "/" + receiverId;
            String messageReceiverRef = "Messages/" + receiverId + "/" + senderUserID;

            DatabaseReference userMessageKeyRef = Rootref.child("Messages")
                    .child(senderUserID).child(receiverId).push();

            String messagePushID = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", senderUserID);
            messageTextBody.put("to", receiverId);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);

            Rootref.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(CounsellingActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();

                        //Deleting accepted bookings from the list
                        counselRef.child(counselKeyID).removeValue();
                    }
                    else
                    {
                        Toast.makeText(CounsellingActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

}
