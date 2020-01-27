package com.walkerstechbase.openpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BookCounselling extends AppCompatActivity {
    Button bookCounsellingBtn;
    DatabaseReference ContactsRef;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
//    private String receiverUserID;
    private String  senderUserID, Current_State;
    private String saveCurrentTime, saveCurrentDate;

    TextView dateTV, timeTV;
    EditText userName, userPhone;
    final Calendar myCalendar = Calendar.getInstance();
    private String date, time, name, phone, counselMessage;
    Toolbar toolbar;
    DatabaseReference counselsRef;
    private ProgressDialog loadingBar;
    EditText bookCounselMessageET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_counselling);

        mAuth = FirebaseAuth.getInstance();
//        messageSenderID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        init();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Book Counselling");

        DatePickerDialog.OnDateSetListener datee = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

//        receiverUserID = getIntent().getExtras().getString("counsellor_id");
        senderUserID = mAuth.getCurrentUser().getUid();
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        counselsRef = FirebaseDatabase.getInstance().getReference().child("Counsels");

        bookCounsellingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userName.length() < 1){
                    userName.setError("Please provide your name");
                }else if (userPhone.length() < 10){
                    userPhone.setError("Phone is invalid");
                }else if(dateTV.getText() == null){
                    Toast.makeText(BookCounselling.this, "Define a date", Toast.LENGTH_SHORT).show();
                }else if (timeTV == null){
                    Toast.makeText(BookCounselling.this, "Define a time", Toast.LENGTH_SHORT).show();
                }else if (userName!=null && userPhone.length() > 9 && dateTV != null && timeTV != null){
                    loadingBar.setMessage("Booking...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    time = timeTV.getText().toString();
                    date = dateTV.getText().toString();
                    phone = userPhone.getText().toString();
                    name = userName.getText().toString();
                    counselMessage = bookCounselMessageET.getText().toString();

                    Counsel counsel = new Counsel();
                    counsel.setTime(time);
                    counsel.setDate(date);
                    counsel.setPhoneNumber(phone);
                    counsel.setName(name);
                    counsel.setMessage(counselMessage);

                    counselsRef.push().setValue(counsel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            loadingBar.dismiss();
                            Toast.makeText(getApplicationContext(), "Booked Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });


//                    ContactsRef.child(senderUserID).child(receiverUserID)
//                            .child("Contacts").setValue("Saved")
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    SendMessage();
//                                }
//                            });
                }

            }
        });

        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                new DatePickerDialog(BookCounselling.this, datee, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        timeTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;

                mTimePicker = new TimePickerDialog(BookCounselling.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if(selectedHour>=0 && selectedHour<12){
                            time = selectedHour + " : " + selectedMinute + " AM";
                        } else {
                            if(selectedHour == 12){
                                time = selectedHour + " : " + selectedMinute + " PM";
                            } else{
                                selectedHour = selectedHour -12;
                                time = selectedHour + " : " + selectedMinute + " PM";
                            }
                        }

                        timeTV.setText(time);
                    }
                }, hour, minute, false);//No 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

    }



    private void init() {
        bookCounsellingBtn = findViewById(R.id.book_counselling_btn);
        dateTV = findViewById(R.id.book_counselling_date);
        timeTV = findViewById(R.id.book_counselling_time);
        userName = findViewById(R.id.book_counselling_name);
        userPhone = findViewById(R.id.book_counselling_phone_number);
        toolbar = findViewById(R.id.book_counselling_toolbar);
        bookCounselMessageET = findViewById(R.id.book_counsel_message);

        loadingBar = new ProgressDialog(this);
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateTV.setText(sdf.format(myCalendar.getTime()));
        date = dateTV.getText().toString();
    }

    private void SendMessage()
    {
//        time = timeTV.getText().toString();
//        date = dateTV.getText().toString();
//        phone = userPhone.getText().toString();
//        name = userName.getText().toString();
//
//        String messageText = "Hello, i am " + name + ", looking to seek help from you as a counsellor. \n I will like to know if you would be available on "+ date + " at " + time + " . Thank You. \n You can also reach me on " + phone;
//
//        if (TextUtils.isEmpty(messageText))
//        {
//            Toast.makeText(this, "first write your message...", Toast.LENGTH_SHORT).show();
//        }
//        else
//        {
//            String messageSenderRef = "Messages/" + senderUserID + "/" + receiverUserID;
//            String messageReceiverRef = "Messages/" + receiverUserID + "/" + senderUserID;
//
//            DatabaseReference userMessageKeyRef = RootRef.child("Messages")
//                    .child(senderUserID).child(receiverUserID).push();
//
//            String messagePushID = userMessageKeyRef.getKey();
//
//            Map messageTextBody = new HashMap();
//            messageTextBody.put("message", messageText);
//            messageTextBody.put("type", "text");
//            messageTextBody.put("from", senderUserID);
////            messageTextBody.put("to", receiverUserID);
//            messageTextBody.put("messageID", messagePushID);
//            messageTextBody.put("time", saveCurrentTime);
//            messageTextBody.put("date", saveCurrentDate);
//
//            Map messageBodyDetails = new HashMap();
//            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
//            messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);
//
//            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
//                @Override
//                public void onComplete(@NonNull Task task)
//                {
//                    if (task.isSuccessful())
//                    {
//                        Toast.makeText(getApplicationContext(), "Booked", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(BookCounselling.this, MainActivity.class));
//                        finish();
//                        //Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
//                    }
//                    else
//                    {
//                        Toast.makeText(BookCounselling.this, "Error", Toast.LENGTH_SHORT).show();
//                    }
//                  //  MessageInputText.setText("");
//                }
//            });
//        }
    }
}
