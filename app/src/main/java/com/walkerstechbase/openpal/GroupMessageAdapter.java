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

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.Groupholder> {
    private List<GroupMessages> userMessagesList;
    private FirebaseAuth mAuth;
    //private DatabaseReference usersRef;


    public GroupMessageAdapter(List<GroupMessages> userMessagesList) {
        this.userMessagesList = userMessagesList;
    }

    @NonNull
    @Override
    public GroupMessageAdapter.Groupholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_messages_layout, parent, false);

        mAuth = FirebaseAuth.getInstance();

        return new GroupMessageAdapter.Groupholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupMessageAdapter.Groupholder holder, final int position) {
        String messageSenderId = mAuth.getCurrentUser().getUid();
        final GroupMessages messages = userMessagesList.get(position);

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

                    Picasso.get().load(receiverImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.imgplaceholder).into(holder.receiverProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(receiverImage).into(holder.receiverProfileImage);

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        holder.receiverName.setVisibility(View.GONE);
        holder.receiverMessageText.setVisibility(View.GONE);
        holder.receiverMessageTime.setVisibility(View.GONE);
        holder.receiverProfileImage.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);
        holder.senderMessageTime.setVisibility(View.GONE);
        holder.messageSenderPicture.setVisibility(View.GONE);
        holder.messageReceiverPicture.setVisibility(View.GONE);


        if (fromMessageType.equals("text"))
        {
            if (fromUserID.equals(messageSenderId))
            {
                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.senderMessageTime.setVisibility(View.VISIBLE);

//                holder.senderMessageText.setBackgroundResource(R.drawable.ic_launcher_background);
                holder.senderMessageText.setTextColor(Color.BLACK);
                holder.senderMessageTime.setTextColor(Color.BLACK);
                holder.senderMessageText.setText(messages.getMessage());
                holder.senderMessageTime.setText(messages.getTime() + " - " + messages.getDate());
            }
            else
            {
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverName.setVisibility(View.VISIBLE);
                holder.receiverMessageTime.setVisibility(View.VISIBLE);

//                holder.receiverMessageText.setBackgroundResource(R.drawable.ic_attach_file_black_24dp);
                holder.receiverMessageText.setTextColor(Color.BLACK);
                holder.receiverName.setTextColor(Color.BLACK);
                holder.receiverMessageTime.setTextColor(Color.BLACK);
                holder.receiverMessageText.setText(messages.getMessage());
                holder.receiverName.setText(messages.getFromName());
                holder.receiverMessageTime.setText(messages.getTime() + " - " + messages.getDate());
            }
        }
        else if (fromMessageType.equals("image")){
            if (fromUserID.equals(messageSenderId)){
                holder.messageSenderPicture.setVisibility(View.VISIBLE);

                Picasso.get().load(messages.getMessage()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.photoplaceholder).into(holder.messageSenderPicture, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(messages.getMessage()).into(holder.messageSenderPicture);
                    }
                });
            }else {
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);

                Picasso.get().load(messages.getMessage()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.messageReceiverPicture, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(messages.getMessage()).into(holder.messageReceiverPicture);
                    }
                });
            }
        }
        else if (fromMessageType.equals("pdf") || fromMessageType.equals("docx")){
            if (fromUserID.equals(messageSenderId)) {
                holder.messageSenderPicture.setVisibility(View.VISIBLE);
                holder.messageSenderPicture.setBackgroundResource(R.drawable.file);


            }
            else{
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);

                holder.messageReceiverPicture.setBackgroundResource(R.drawable.file);

            }
        }


        if (fromUserID.equals(messageSenderId)){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userMessagesList.get(position).getType().equals("pdf") || userMessagesList.get(position).getType().equals("docx")){
                        CharSequence options[] = new CharSequence[]{"Delete for me", "Download and View Document", "Cancel", "Delete for everyone"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete Message?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0){
                                    deleteSentMessages(position, holder);
                                }
                                else if (i == 1){
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                                    holder.itemView.getContext().startActivity(intent);

                                }
                                else if (i == 2){

                                }
                                else if (i == 3){
                                    deleteMessageForEveryone(position, holder);

                                }
                            }
                        });
                        builder.show();
                    }

                    else if (userMessagesList.get(position).getType().equals("text")){
                        CharSequence options[] = new CharSequence[]{"Delete for me", "Cancel", "Delete for everyone"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete Message?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0){
                                    deleteSentMessages(position, holder);
                                }
                                else if (i == 1){
                                    //do nothing
                                }
                                else if (i == 2){
                                    deleteMessageForEveryone(position, holder);
                                }
                            }
                        });
                        builder.show();
                    }

                    else if (userMessagesList.get(position).getType().equals("image")){
                        CharSequence options[] = new CharSequence[]{"Delete for me", "View Image", "Cancel", "Delete for everyone"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete Message?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0){
                                    deleteSentMessages(position, holder);
                                }
                                else if (i == 1){
                                    Intent intent = new Intent(holder.itemView.getContext(), ImageVIewActivity.class);
                                    intent.putExtra("url", userMessagesList.get(position).getMessage());
                                    holder.itemView.getContext().startActivity(intent);
                                }
                                else if (i == 2){

                                }
                                else if (i == 3){
                                    deleteMessageForEveryone(position, holder);
                                }
                            }
                        });
                        builder.show();
                    }
                }
            });
        }

        else {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userMessagesList.get(position).getType().equals("pdf") || userMessagesList.get(position).getType().equals("docx")){
                        CharSequence options[] = new CharSequence[]{"Delete for me", "Download and View Document", "Cancel"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete Message?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0){
                                    deleteReceivedMessages(position, holder);

                                }
                                else if (i == 1){
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                                    holder.itemView.getContext().startActivity(intent);

                                }
                                else if (i == 2){

                                }
                            }
                        });
                        builder.show();
                    }

                    else if (userMessagesList.get(position).getType().equals("text")){
                        CharSequence options[] = new CharSequence[]{"Delete for me", "Cancel"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete Message?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0){
                                    deleteReceivedMessages(position, holder);
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

                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete Message?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0){
                                    deleteReceivedMessages(position, holder);
                                }
                                else if (i == 1){
                                    Intent intent = new Intent(holder.itemView.getContext(), ImageVIewActivity.class);
                                    intent.putExtra("url", userMessagesList.get(position).getMessage());
                                    holder.itemView.getContext().startActivity(intent);

                                }
                                else if (i == 2){

                                }
                            }
                        });
                        builder.show();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    public class Groupholder extends RecyclerView.ViewHolder {
        public TextView senderMessageText,senderMessageTime, receiverMessageText, receiverMessageTime, receiverName;
        public CircleImageView receiverProfileImage;
        public ImageView messageSenderPicture, messageReceiverPicture;
        RecyclerView recyclerView;

        public Groupholder(@NonNull View itemView) {
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


    private void deleteSentMessages(final int position, final GroupMessageAdapter.Groupholder holder){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Groups")
                //.child(userMessagesList.get(position).getFrom())
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


    private void deleteReceivedMessages(final int position, final GroupMessageAdapter.Groupholder holder){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Groups")
                .child(userMessagesList.get(position).getTo())
                //.child(userMessagesList.get(position).getFrom())
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

    private void deleteMessageForEveryone(final int position, final GroupMessageAdapter.Groupholder holder){
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Groups")
                .child(userMessagesList.get(position).getTo())
                //.child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                rootRef.child("Groups")
                        //.child(userMessagesList.get(position).getFrom())
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
                        Toast.makeText(holder.itemView.getContext(), "Error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
