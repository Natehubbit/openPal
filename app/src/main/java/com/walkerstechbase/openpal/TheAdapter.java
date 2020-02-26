package com.walkerstechbase.openpal;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.walkerstechbase.openpal.General.Constansts;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class TheAdapter extends RecyclerView.Adapter<TheAdapter.theHolder> {
    private List<Groupie> groupList;
    private FirebaseAuth mAuth;

    public TheAdapter(List<Groupie> groupList) {
        this.groupList = groupList;
    }

    @NonNull
    @Override
    public TheAdapter.theHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_list_items, parent, false);

        mAuth = FirebaseAuth.getInstance();

        return new TheAdapter.theHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TheAdapter.theHolder holder, int position) {
        Groupie groupie = groupList.get(position);
        String messageSenderId = mAuth.getCurrentUser().getUid();

        if (groupie != null){
            String groupAdminID = groupie.getAdminId();
            String groupID = groupie.getGroupId();
            String groupName = groupie.getGroupName();
            String groupImage = groupie.getGroupImage();

            DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child(Constansts.GROUP_REF).child(groupID);

            holder.title.setText(groupName);
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String textGroupName = groupName;

                    Intent goToPrayers = new Intent(view.getContext(), GroupChatActivity.class);
                    goToPrayers.putExtra("groupName" , textGroupName);
                    goToPrayers.putExtra("groupIDD", groupID);
                    goToPrayers.putExtra("groupImagee", groupImage);
                    goToPrayers.putExtra("groupAdminIDD", groupAdminID);
                    view.getContext().startActivity(goToPrayers);
                }
            });


            if (groupImage.equals("")){
                holder.circleImageView.setImageResource(R.drawable.team);
                Log.d("exec", "empty was executed");
            }
            else if (!groupImage.equals("")){
                Log.d("44", "44 is executed");
                Picasso.get().load(groupImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.team).into(holder.circleImageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(groupImage).into(holder.circleImageView);
                    }
                });
            }

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(Constansts.USER_REF);
            userRef.child(groupAdminID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String adminName = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                    holder.adminName.setText(String.format("created by %s", adminName));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

//        else {
//            groupRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.hasChild("groupImage")) {
//                        final String receiverImage = dataSnapshot.child("groupImage").getValue().toString();
//                        Log.d("pathh" ,"pathh " + receiverImage);
//
//                        Picasso.get().load(receiverImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.team).into(holder.circleImageView, new Callback() {
//                            @Override
//                            public void onSuccess() {
//
//                            }
//
//                            @Override
//                            public void onError(Exception e) {
//                                Picasso.get().load(receiverImage).into(holder.circleImageView);
//
//                            }
//                        });
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//        }
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public class theHolder extends RecyclerView.ViewHolder {
        TextView title, adminName;
        CircleImageView circleImageView;
        RelativeLayout relativeLayout;

        public theHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.group_profile_name);
            circleImageView = itemView.findViewById(R.id.group_profile_image);
            relativeLayout = itemView.findViewById(R.id.gro_lay);
            adminName = itemView.findViewById(R.id.group_admin_name);
        }
    }
}
