package com.walkerstechbase.openpal;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.SweepGradient;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GroupChatActivity extends AppCompatActivity
{
    private Toolbar mToolbar;
    private ImageButton SendMessageButton, sendFilesBtn, sendEmoji;
    private EmojiEditText userMessageInput;
    //private ScrollView mScrollView;
    //private TextView displayTextMessages;
    private String checker = "", myUrl = "";
    private StorageTask uploadTask;
    private ProgressDialog loadingBar;
    private Uri fileUri;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, GroupNameRef, GroupMessageKeyRef;
    private String currentGroupName,  currentUserName, currentDate, currentTime, currentUserImage;
    String currentUserID;
    String saveCurrentTime, saveCurrentDate;
    //String messageSenderID;
    DatabaseReference RootRef;

    //new new
    private final List<GroupMessages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private GroupMessageAdapter groupMessageAdapter;
    private RecyclerView groupMessagesList;
    GroupMessages messages;

    SwitchCompat mySwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);


        currentGroupName = getIntent().getExtras().get("groupName").toString();
       // Toast.makeText(GroupChatActivity.this, currentGroupName, Toast.LENGTH_SHORT).show();
        InitializeFields();
        final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(findViewById(android.R.id.content)).build(userMessageInput);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        //messageSenderID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);
        RootRef = FirebaseDatabase.getInstance().getReference();

        loadingBar = new ProgressDialog(this);

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        sendEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emojiPopup.toggle();
                emojiPopup.isShowing();
            }
        });

////get messages


        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                if (dataSnapshot.exists())
                {
                    //load image
//                    Picasso.get().load(currentUserImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.imgplaceholder).into();

                    // DisplayMessages(dataSnapshot);

                    messages = dataSnapshot.getValue(GroupMessages.class);

                    messagesList.add(messages);

                    groupMessageAdapter.notifyDataSetChanged();

                    groupMessagesList.smoothScrollToPosition(groupMessagesList.getAdapter().getItemCount());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                if (dataSnapshot.exists())
                {
//                    DisplayMessages(dataSnapshot);

                    messages = dataSnapshot.getValue(GroupMessages.class);

                    messagesList.add(messages);

                    groupMessageAdapter.notifyDataSetChanged();

                    groupMessagesList.smoothScrollToPosition(groupMessagesList.getAdapter().getItemCount());
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                groupMessageAdapter.notifyDataSetChanged();
                finish();
                startActivity(getIntent());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        sendFilesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence options[] = new CharSequence[] {"Images", "PDF Files", "Ms Word Files"};
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatActivity.this);
                builder.setTitle("Select the File");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0){
                            checker = "image";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "Select Image"), 438);
                        }
                        if (i == 1){
                            checker = "pdf";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            startActivityForResult(Intent.createChooser(intent, "Select PDF"), 438);

                        }
                        if (i == 2){
                            checker = "docx";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/msword");
                            startActivityForResult(Intent.createChooser(intent, "Select Document"), 438);

                        }
                    }
                });
                builder.show();
            }
        });



//        GroupNameRef.keepSynced(true);
//        GroupNameRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                messages = dataSnapshot.getValue(GroupMessages.class);
//
//                messagesList.add(messages);
//
//                groupMessageAdapter.notifyDataSetChanged();
//
//                groupMessagesList.smoothScrollToPosition(groupMessagesList.getAdapter().getItemCount());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//                .addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
//                    {
//                        messages = dataSnapshot.getValue(GroupMessages.class);
//
//                        messagesList.add(messages);
//
//                        groupMessageAdapter.notifyDataSetChanged();
//
//                        groupMessagesList.smoothScrollToPosition(groupMessagesList.getAdapter().getItemCount());
//                    }
//
//                    @Override
//                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(DataSnapshot dataSnapshot) {
//                        groupMessageAdapter.notifyDataSetChanged();
//                        finish();
//                        startActivity(getIntent());
//
//                    }
//
//                    @Override
//                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });




        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //SaveMessageInfoToDatabase();
                SendMessage();

               // userMessageInput.setText("");

               // mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }



//    @Override
//    protected void onStart()
//    {
//        super.onStart();
//
//        GroupNameRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s)
//            {
//                if (dataSnapshot.exists())
//                {
//                   // DisplayMessages(dataSnapshot);
//
//                    messages = dataSnapshot.getValue(GroupMessages.class);
//
//                    messagesList.add(messages);
//
//                    groupMessageAdapter.notifyDataSetChanged();
//
//                    groupMessagesList.smoothScrollToPosition(groupMessagesList.getAdapter().getItemCount());
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s)
//            {
//                if (dataSnapshot.exists())
//                {
//                    DisplayMessages(dataSnapshot);
//                }
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }


    private void InitializeFields()
    {
        mToolbar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SendMessageButton = findViewById(R.id.send_message_button);
        userMessageInput = findViewById(R.id.input_group_message);
        sendEmoji = findViewById(R.id.send_emoji_btn);
        sendFilesBtn = findViewById(R.id.send_files_btn);
//        displayTextMessages = (TextView) findViewById(R.id.group_chat_text_display);
//        mScrollView = (ScrollView) findViewById(R.id.my_scroll_view);



        linearLayoutManager = new LinearLayoutManager(this);
        groupMessageAdapter = new GroupMessageAdapter(messagesList);
        groupMessagesList = findViewById(R.id.private_messages_list_of_users);
        groupMessagesList.setLayoutManager(linearLayoutManager);
        groupMessagesList.setAdapter(groupMessageAdapter);
    }



    private void GetUserInfo()
    {
        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    currentUserName = dataSnapshot.child("name").getValue().toString();
                    currentUserImage = dataSnapshot.child("image").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void GetAnonInfo()
    {
        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    currentUserName = null;
                    currentUserImage = null;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void SaveMessageInfoToDatabase()
    {
        String message = userMessageInput.getText().toString();
        String messagekEY = GroupNameRef.push().getKey();

        if (TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "Please write message first...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());


            HashMap<String, Object> groupMessageKey = new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey);

            GroupMessageKeyRef = GroupNameRef.child(messagekEY);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
                messageInfoMap.put("name", currentUserName);
                messageInfoMap.put("message", message);
                messageInfoMap.put("date", currentDate);
                messageInfoMap.put("time", currentTime);
            GroupMessageKeyRef.updateChildren(messageInfoMap);
        }
    }



    private void DisplayMessages(DataSnapshot dataSnapshot)
    {
        Iterator iterator = dataSnapshot.getChildren().iterator();

        while(iterator.hasNext())
        {
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();



//            displayTextMessages.append(chatName + " :\n" + chatMessage + "\n" + chatTime + "     " + chatDate + "\n\n\n");
//            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.anon_switch_menu, menu);
        MenuItem item = menu.findItem(R.id.anon_switch_menuItem);
        item.setActionView(R.layout.anon_switch_layout);
        mySwitch= item.getActionView().findViewById(R.id.anon_switch);

//        mySwitch.setChecked(false);

        if (!mySwitch.isChecked()){
            GetUserInfo();
        }

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (compoundButton.isChecked()){
                    Toast.makeText(GroupChatActivity.this, "Anon Enabled", Toast.LENGTH_SHORT).show();
                    compoundButton.setChecked(true);
                    GetAnonInfo();
                }else{
                    Toast.makeText(GroupChatActivity.this, "Anon Disabled", Toast.LENGTH_SHORT).show();
                    compoundButton.setChecked(false);
                    GetUserInfo();
                }
            }
        });

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 438 && resultCode == RESULT_OK && data != null && data.getData() != null){
            loadingBar.setTitle("Sending file");
            loadingBar.setMessage("Please wait, file is sending...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();


            fileUri = data.getData();

            if (!checker.equals("image")){
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("document files");

                final String messageSenderRef = "Groups/" + currentUserID + "/" + currentGroupName;
                final String messageReceiverRef = "Groups/" + currentGroupName + "/" + currentUserID;

                DatabaseReference userMessageKeyRef = RootRef.child("Groups")
                        .child(currentUserID).child(currentGroupName).push();

                final String messagePushID = userMessageKeyRef.getKey();

                final StorageReference filesPath = storageReference.child(messagePushID + "." + checker);


                filesPath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        filesPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();
                                Map messageImageBody = new HashMap();

                                messageImageBody.put("message", downloadUrl);
                                messageImageBody.put("name", fileUri.getLastPathSegment());
                                messageImageBody.put("type", checker);
                                messageImageBody.put("from", currentUserID);
                                messageImageBody.put("to", currentGroupName);
                                messageImageBody.put("messageID", messagePushID);
                                messageImageBody.put("time", saveCurrentTime);
                                messageImageBody.put("date", saveCurrentDate);

                                Map messageBodyDetails = new HashMap();
                                messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageImageBody);
                                messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageImageBody);

                                RootRef.updateChildren(messageBodyDetails);
                                loadingBar.dismiss();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        loadingBar.dismiss();
                                        Toast.makeText(GroupChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                double p = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                loadingBar.setMessage((int) p + "% uploaded...");
                            }
                        });
            }
            else if (checker.equals("image")){
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("image files");
//
//                final String messageSenderRef = "Groups/" + currentUserID + "/" + currentGroupName;
//                final String messageReceiverRef = "Groups/" + currentGroupName + "/" + currentUserID;
                String messageSenderRef = "Groups/"  + currentGroupName;
                String messageReceiverRef = "Groups/" + currentGroupName ;

                DatabaseReference userMessageKeyRef = RootRef.child("Groups")
                        .child(currentUserID).child(currentGroupName).push();

                final String messagePushID = userMessageKeyRef.getKey();

                final StorageReference filesPath = storageReference.child(messagePushID + "." + "jpg");

                uploadTask = filesPath.putFile(fileUri);

                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()){
                            throw  task.getException();
                        }
                        return filesPath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>(){
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            Uri downloadUrl = task.getResult();
                            myUrl = downloadUrl.toString();

                            Map messageImageBody = new HashMap();
                            messageImageBody.put("message", myUrl);
                            messageImageBody.put("name", fileUri.getLastPathSegment());
                            messageImageBody.put("type", checker);
                            messageImageBody.put("from", currentUserID);
                            messageImageBody.put("fromName", currentUserName);
                            messageImageBody.put("to", currentGroupName);
                            messageImageBody.put("messageID", messagePushID);
                            messageImageBody.put("time", saveCurrentTime);
                            messageImageBody.put("date", saveCurrentDate);

                            Map messageBodyDetails = new HashMap();
                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageImageBody);
                            messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageImageBody);

                            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        loadingBar.dismiss();
                                        // Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(GroupChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                    userMessageInput.setText("");
                                }
                            });
                        }
                    }
                });
            }else{
                loadingBar.dismiss();
                Toast.makeText(this, "Select at least one image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void SendMessage()
    {
        String messageText = userMessageInput.getText().toString();

        if (TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "first write your message...", Toast.LENGTH_SHORT).show();
        }
        else
        {

            String messageSenderRef = "Groups/"  + currentGroupName;
            String messageReceiverRef = "Groups/" + currentGroupName ;


            DatabaseReference userMessageKeyRef = RootRef.child("Groups")
                    .child(currentGroupName).push();

            String messagePushID = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", currentUserID);
            messageTextBody.put("fromName", currentUserName);
            messageTextBody.put("to", currentGroupName);
            messageTextBody.put("fromImage", currentUserImage);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (task.isSuccessful())
                    {
                        //Toast.makeText(GroupChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(GroupChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    userMessageInput.setText("");
                }
            });
        }
    }
}
