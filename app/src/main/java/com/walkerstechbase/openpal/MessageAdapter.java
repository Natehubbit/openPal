package com.walkerstechbase.openpal;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.comix.rounded.RoundedCornerImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


//TODO make seperate adapter for group messages for delete functions to work
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    //private DatabaseReference usersRef;


    public MessageAdapter (List<Messages> userMessagesList)
    {
        this.userMessagesList = userMessagesList;
    }



    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessageText,senderMessageTime, receiverMessageText, receiverMessageTime, receiverName;
        public CircleImageView receiverProfileImage;
        public RoundedCornerImageView messageSenderPicture, messageReceiverPicture;
        RecyclerView recyclerView;

        public MessageViewHolder(@NonNull View itemView)
        {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.sender_messsage_text);
            receiverProfileImage = itemView.findViewById(R.id.message_profile_image);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_text);
            receiverMessageTime = itemView.findViewById(R.id.receiver_text_time);
            senderMessageTime = itemView.findViewById(R.id.sender_text_time);
            recyclerView = itemView.findViewById(R.id.private_messages_list_of_users);
            receiverName = itemView.findViewById(R.id.receiver_name);
        }
    }




    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_messages_layout, viewGroup, false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, final int position)
    {
        String messageSenderId = mAuth.getCurrentUser().getUid();
        final Messages messages = userMessagesList.get(position);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.hasChild("image"))
                {
                    final String receiverImage = dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(receiverImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.imgplaceholder).into(messageViewHolder.receiverProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(receiverImage).into(messageViewHolder.receiverProfileImage);

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        messageViewHolder.receiverName.setVisibility(View.GONE);
        messageViewHolder.receiverMessageText.setVisibility(View.GONE);
        messageViewHolder.receiverMessageTime.setVisibility(View.GONE);
        messageViewHolder.receiverProfileImage.setVisibility(View.GONE);
        messageViewHolder.senderMessageText.setVisibility(View.GONE);
        messageViewHolder.senderMessageTime.setVisibility(View.GONE);
        messageViewHolder.messageSenderPicture.setVisibility(View.GONE);
        messageViewHolder.messageReceiverPicture.setVisibility(View.GONE);


        if (fromMessageType.equals("text"))
        {
            if (fromUserID.equals(messageSenderId))
            {
                messageViewHolder.senderMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.senderMessageTime.setVisibility(View.VISIBLE);

//                messageViewHolder.senderMessageText.setBackgroundResource(R.drawable.ic_launcher_background);
                messageViewHolder.senderMessageText.setTextColor(Color.BLACK);
                messageViewHolder.senderMessageTime.setTextColor(Color.BLACK);
                messageViewHolder.senderMessageText.setText(messages.getMessage());
                messageViewHolder.senderMessageTime.setText(messages.getTime() + " - " + messages.getDate());
            }
            else
            {
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.receiverMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.receiverName.setVisibility(View.VISIBLE);
                messageViewHolder.receiverMessageTime.setVisibility(View.VISIBLE);

//                messageViewHolder.receiverMessageText.setBackgroundResource(R.drawable.ic_attach_file_black_24dp);
                messageViewHolder.receiverMessageText.setTextColor(Color.BLACK);
                messageViewHolder.receiverName.setTextColor(Color.BLACK);
                messageViewHolder.receiverMessageTime.setTextColor(Color.BLACK);
                messageViewHolder.receiverMessageText.setText(messages.getMessage());
                messageViewHolder.receiverName.setText(messages.getFromName());
                messageViewHolder.receiverMessageTime.setText(messages.getTime() + " - " + messages.getDate());
            }
        }
        else if (fromMessageType.equals("image")){
            if (fromUserID.equals(messageSenderId)){
                messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);

                Picasso.get().load(messages.getMessage()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.photoplaceholder).into(messageViewHolder.messageSenderPicture, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageSenderPicture);
                    }
                });
            }else {
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);

                Picasso.get().load(messages.getMessage()).networkPolicy(NetworkPolicy.OFFLINE).into(messageViewHolder.messageReceiverPicture, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageReceiverPicture);
                    }
                });
            }
        }
        else if (fromMessageType.equals("pdf") || fromMessageType.equals("docx")){
            if (fromUserID.equals(messageSenderId)) {
                messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);
                messageViewHolder.messageSenderPicture.setBackgroundResource(R.drawable.file);


            }
            else{
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);

                messageViewHolder.messageReceiverPicture.setBackgroundResource(R.drawable.file);

            }
        }


        if (fromUserID.equals(messageSenderId)){
            messageViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (userMessagesList.get(position).getType().equals("pdf") || userMessagesList.get(position).getType().equals("docx")){
                        CharSequence options[] = new CharSequence[]{"Delete for me", "Download and View Document", "Cancel", "Delete for everyone"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0){
                                    deleteSentMessages(position, messageViewHolder);
                                }
                                else if (i == 1){
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                                    messageViewHolder.itemView.getContext().startActivity(intent);

                                }
                                else if (i == 2){

                                }
                                else if (i == 3){
                                    deleteMessageForEveryone(position, messageViewHolder);

                                }
                            }
                        });
                        builder.show();
                    }

                    else if (userMessagesList.get(position).getType().equals("text")){
                        CharSequence options[] = new CharSequence[]{"Delete for me", "Cancel", "Delete for everyone"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0){
                                    deleteSentMessages(position, messageViewHolder);
                                }
                                else if (i == 1){
                                    //do nothing
                                }
                                else if (i == 2){
                                    deleteMessageForEveryone(position, messageViewHolder);
                                }
                            }
                        });
                        builder.show();
                    }

                    else if (userMessagesList.get(position).getType().equals("image")){
                        CharSequence options[] = new CharSequence[]{"Delete for me", "View Image", "Cancel", "Delete for everyone"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0){
                                    deleteSentMessages(position, messageViewHolder);
                                }
                                else if (i == 1){
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(), ImageVIewActivity.class);
                                    intent.putExtra("url", userMessagesList.get(position).getMessage());
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                                else if (i == 2){

                                }
                                else if (i == 3){
                                    deleteMessageForEveryone(position, messageViewHolder);
                                }
                            }
                        });
                        builder.show();
                    }
                    return true;
                }
            });
//            messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (userMessagesList.get(position).getType().equals("pdf") || userMessagesList.get(position).getType().equals("docx")){
//                        CharSequence options[] = new CharSequence[]{"Delete for me", "Download and View Document", "Cancel", "Delete for everyone"};
//
//                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
//                        builder.setTitle("Delete Message?");
//
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                if (i == 0){
//                                    deleteSentMessages(position, messageViewHolder);
//                                }
//                                else if (i == 1){
//                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
//                                    messageViewHolder.itemView.getContext().startActivity(intent);
//
//                                }
//                                else if (i == 2){
//
//                                }
//                                else if (i == 3){
//                                    deleteMessageForEveryone(position, messageViewHolder);
//
//                                }
//                            }
//                        });
//                        builder.show();
//                    }
//
//                    else if (userMessagesList.get(position).getType().equals("text")){
//                        CharSequence options[] = new CharSequence[]{"Delete for me", "Cancel", "Delete for everyone"};
//
//                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
//                        builder.setTitle("Delete Message?");
//
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                if (i == 0){
//                                    deleteSentMessages(position, messageViewHolder);
//                                }
//                                else if (i == 1){
//                                    //do nothing
//                                }
//                                else if (i == 2){
//                                    deleteMessageForEveryone(position, messageViewHolder);
//                                }
//                            }
//                        });
//                        builder.show();
//                    }
//
//                    else if (userMessagesList.get(position).getType().equals("image")){
//                        CharSequence options[] = new CharSequence[]{"Delete for me", "View Image", "Cancel", "Delete for everyone"};
//
//                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
//                        builder.setTitle("Delete Message?");
//
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                if (i == 0){
//                                    deleteSentMessages(position, messageViewHolder);
//                                }
//                                else if (i == 1){
//                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(), ImageVIewActivity.class);
//                                    intent.putExtra("url", userMessagesList.get(position).getMessage());
//                                    messageViewHolder.itemView.getContext().startActivity(intent);
//                                }
//                                else if (i == 2){
//
//                                }
//                                else if (i == 3){
//                                    deleteMessageForEveryone(position, messageViewHolder);
//                                }
//                            }
//                        });
//                        builder.show();
//                    }
//                }
//            });
        }

        else {
            messageViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (userMessagesList.get(position).getType().equals("pdf") || userMessagesList.get(position).getType().equals("docx")){
                        CharSequence options[] = new CharSequence[]{"Delete for me", "Download and View Document", "Cancel"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0){
                                    deleteReceivedMessages(position, messageViewHolder);

                                }
                                else if (i == 1){
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                                    messageViewHolder.itemView.getContext().startActivity(intent);

                                }
                                else if (i == 2){

                                }
                            }
                        });
                        builder.show();
                    }

                    else if (userMessagesList.get(position).getType().equals("text")){
                        CharSequence options[] = new CharSequence[]{"Delete for me", "Cancel"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0){
                                    deleteReceivedMessages(position, messageViewHolder);
                                }
                                else if (i == 1){
                                    //do nothing
                                }
                            }
                        });
                        builder.show();
                    }

                    else if (userMessagesList.get(position).getType().equals("image")){
                        CharSequence options[] = new CharSequence[]{"Delete for me", "View Image", "Cancel"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0){
                                    deleteReceivedMessages(position, messageViewHolder);
                                }
                                else if (i == 1){
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(), ImageVIewActivity.class);
                                    intent.putExtra("url", userMessagesList.get(position).getMessage());
                                    messageViewHolder.itemView.getContext().startActivity(intent);

                                }
                                else if (i == 2){

                                }
                            }
                        });
                        builder.show();
                    }
                    return true;
                }
            });
//            messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (userMessagesList.get(position).getType().equals("pdf") || userMessagesList.get(position).getType().equals("docx")){
//                        CharSequence options[] = new CharSequence[]{"Delete for me", "Download and View Document", "Cancel"};
//
//                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
//                        builder.setTitle("Delete Message?");
//
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                if (i == 0){
//                                    deleteReceivedMessages(position, messageViewHolder);
//
//                                }
//                                else if (i == 1){
//                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
//                                    messageViewHolder.itemView.getContext().startActivity(intent);
//
//                                }
//                                else if (i == 2){
//
//                                }
//                            }
//                        });
//                        builder.show();
//                    }
//
//                    else if (userMessagesList.get(position).getType().equals("text")){
//                        CharSequence options[] = new CharSequence[]{"Delete for me", "Cancel"};
//
//                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
//                        builder.setTitle("Delete Message?");
//
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                if (i == 0){
//                                    deleteReceivedMessages(position, messageViewHolder);
//                                }
//                                else if (i == 1){
//                                    //do nothing
//                                }
//                            }
//                        });
//                        builder.show();
//                    }
//
//                    else if (userMessagesList.get(position).getType().equals("image")){
//                        CharSequence options[] = new CharSequence[]{"Delete for me", "View Image", "Cancel"};
//
//                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
//                        builder.setTitle("Delete Message?");
//
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                if (i == 0){
//                                    deleteReceivedMessages(position, messageViewHolder);
//                                }
//                                else if (i == 1){
//                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(), ImageVIewActivity.class);
//                                    intent.putExtra("url", userMessagesList.get(position).getMessage());
//                                    messageViewHolder.itemView.getContext().startActivity(intent);
//
//                                }
//                                else if (i == 2){
//
//                                }
//                            }
//                        });
//                        builder.show();
//                    }
//                }
//            });
        }
    }




    @Override
    public int getItemCount()
    {
        return userMessagesList.size();
    }

    private void deleteSentMessages(final int position, final MessageViewHolder holder){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(holder.itemView.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(holder.itemView.getContext(), "Error occured", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void deleteReceivedMessages(final int position, final MessageViewHolder holder){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(holder.itemView.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(holder.itemView.getContext(), "Error occured", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteMessageForEveryone(final int position, final MessageViewHolder holder){
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                rootRef.child("Messages")
                        .child(userMessagesList.get(position).getFrom())
                        .child(userMessagesList.get(position).getTo())
                        .child(userMessagesList.get(position).getMessageID())
                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(holder.itemView.getContext(), "Deleted", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(holder.itemView.getContext(), "Error occured", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
