package com.walkerstechbase.openpal;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jgabrielfreitas.core.BlurImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity
{
    private String receiverUserID, senderUserID, Current_State;

    private BlurImageView blurImageView;
    private BlurImageView blurImageView2;
    private ImageView userProfileImage;
    private TextView userProfileName, userProfileStatus;
    private ImageButton DeclineMessageRequestButton;
    private ImageButton SendMessageRequestButton;
    private TextView justText, justText2;
    private LinearLayout declineMsgLay, sendMsgLay;

    private DatabaseReference UserRef, ChatRequestRef, ContactsRef, NotificationRef;
    private FirebaseAuth mAuth;

    private Toolbar profileToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        NotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");


        receiverUserID = getIntent().getExtras().get("visit_user_id").toString();
        senderUserID = mAuth.getCurrentUser().getUid();


        blurImageView = findViewById(R.id.blur_img3);
        blurImageView2 = findViewById(R.id.blur_img4);
        userProfileImage = findViewById(R.id.set_profile_image4);
        userProfileName =  findViewById(R.id.visit_user_name);
        userProfileStatus = findViewById(R.id.visit_profile_status);
        SendMessageRequestButton = findViewById(R.id.send_message_request_button);
        DeclineMessageRequestButton = findViewById(R.id.decline_message_request_button);
        justText = findViewById(R.id.just_text);
        justText2 = findViewById(R.id.just_text_2);
        declineMsgLay = findViewById(R.id.decline_msg_lay);
        sendMsgLay = findViewById(R.id.send_msg_lay);
        Current_State = "new";

        profileToolBar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(profileToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("");

        RetrieveUserInfo();


    }



    private void RetrieveUserInfo()
    {
        UserRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if ((dataSnapshot.exists())  &&  (dataSnapshot.hasChild("image")))
                {
                    final String userImage = dataSnapshot.child("image").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userstatus = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(userImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.imgplaceholder).into(userProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(userImage).into(userProfileImage);
                        }
                    });
                    userProfileName.setText(userName);
                    userProfileStatus.setText(userstatus);

                    blurImageView.setBackground(getResources().getDrawable(R.drawable.bg_profile));
                    Picasso.get().setIndicatorsEnabled(false);
                    Picasso.get().load(userImage).networkPolicy(NetworkPolicy.OFFLINE).into(blurImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            blurImageView.setBlur(25);
                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().setIndicatorsEnabled(false);
                            Picasso.get().load(userImage).into(blurImageView);
                        }
                    });


                    Picasso.get().setIndicatorsEnabled(false);
                    Picasso.get().load(userImage).networkPolicy(NetworkPolicy.OFFLINE).into(blurImageView2, new Callback() {
                        @Override
                        public void onSuccess() {
                            blurImageView2.setBlur(25);
                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().setIndicatorsEnabled(false);
                            Picasso.get().load(userImage).into(blurImageView2);
                        }
                    });

                    ManageChatRequests();
                }
                else
                {
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userstatus = dataSnapshot.child("status").getValue().toString();

                    userProfileName.setText(userName);
                    userProfileStatus.setText(userstatus);


                    ManageChatRequests();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    private void ManageChatRequests()
    {
        ChatRequestRef.child(senderUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.hasChild(receiverUserID))
                        {
                            String request_type = dataSnapshot.child(receiverUserID).child("request_type").getValue().toString();

                            if (request_type.equals("sent"))
                            {
                                Current_State = "request_sent";
//                                SendMessageRequestButton.setText("Cancel Chat Request");
                                justText.setText("Cancel Chat Request");
                                SendMessageRequestButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove_circle_outline_black_24dp));
                            }
                            else if (request_type.equals("received"))
                            {
                                Current_State = "request_received";
//                                SendMessageRequestButton.setText("Accept Chat Request");
                                justText.setText("Accept Chat Request");
                                SendMessageRequestButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_positive_vote));


                                //DeclineMessageRequestButton.setVisibility(View.VISIBLE);
                                declineMsgLay.setVisibility(View.VISIBLE);
                                DeclineMessageRequestButton.setEnabled(true);

                                DeclineMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        CancelChatRequest();
                                    }
                                });
                            }
                        }
                        else
                        {
                            ContactsRef.child(senderUserID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot)
                                        {
                                            if (dataSnapshot.hasChild(receiverUserID))
                                            {
                                                Current_State = "friends";
//                                                SendMessageRequestButton.setText("Remove this Contact");
                                                justText.setText("Remove Contact");
                                                SendMessageRequestButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove_circle_outline_black_24dp));

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



        if (!senderUserID.equals(receiverUserID))
        {
            SendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    SendMessageRequestButton.setEnabled(false);

                    if (Current_State.equals("new"))
                    {
                        SendChatRequest();
                    }
                    if (Current_State.equals("request_sent"))
                    {
                        CancelChatRequest();
                    }
                    if (Current_State.equals("request_received"))
                    {
                        AcceptChatRequest();
                    }
                    if (Current_State.equals("friends"))
                    {
                        RemoveSpecificContact();
                    }
                }
            });
        }
        else
        {
            SendMessageRequestButton.setVisibility(View.INVISIBLE);
            sendMsgLay.setVisibility(View.INVISIBLE);
        }
    }



    private void RemoveSpecificContact()
    {
        ContactsRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            ContactsRef.child(receiverUserID).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                SendMessageRequestButton.setEnabled(true);
                                                Current_State = "new";
//                                                SendMessageRequestButton.setText("Send Message");
                                                justText.setText("Send A Message");
                                                SendMessageRequestButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_paper_plane));


                                                DeclineMessageRequestButton.setVisibility(View.GONE);
                                                DeclineMessageRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }



    private void AcceptChatRequest()
    {
        ContactsRef.child(senderUserID).child(receiverUserID)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            ContactsRef.child(receiverUserID).child(senderUserID)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                ChatRequestRef.child(senderUserID).child(receiverUserID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if (task.isSuccessful())
                                                                {
                                                                    ChatRequestRef.child(receiverUserID).child(senderUserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    SendMessageRequestButton.setEnabled(true);
                                                                                    Current_State = "friends";
//                                                                                    SendMessageRequestButton.setText("Remove this Contact");
                                                                                    justText.setText("Remove Contact");
                                                                                    SendMessageRequestButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove_circle_outline_black_24dp));


                                                                                    DeclineMessageRequestButton.setVisibility(View.GONE);
                                                                                    DeclineMessageRequestButton.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }




    private void CancelChatRequest()
    {
        ChatRequestRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            ChatRequestRef.child(receiverUserID).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                SendMessageRequestButton.setEnabled(true);
                                                Current_State = "new";
//                                                SendMessageRequestButton.setText("Send Message");
                                                justText.setText("Send A Message");
                                                SendMessageRequestButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_paper_plane));


                                                DeclineMessageRequestButton.setVisibility(View.GONE);
                                                DeclineMessageRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }




    private void SendChatRequest()
    {
        ChatRequestRef.child(senderUserID).child(receiverUserID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            ChatRequestRef.child(receiverUserID).child(senderUserID)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                HashMap<String, String> chatNotificationMap = new HashMap<>();
                                                chatNotificationMap.put("from", senderUserID);
                                                chatNotificationMap.put("type", "request");

                                                NotificationRef.child(receiverUserID).push()
                                                        .setValue(chatNotificationMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if (task.isSuccessful())
                                                                {
                                                                    SendMessageRequestButton.setEnabled(true);
                                                                    Current_State = "request_sent";
//                                                                    SendMessageRequestButton.setText("Cancel Chat Request");
                                                                    justText.setText("Cancel Chat Request");
                                                                    SendMessageRequestButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove_circle_outline_black_24dp));

                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
