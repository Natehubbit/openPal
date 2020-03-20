package com.walkerstechbase.openpal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.walkerstechbase.openpal.General.Constansts;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddGroupMembers extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView FindFriendsRecyclerList;
    private DatabaseReference UsersRef, memberRef, groupRef, userGroupRef;
    private ArrayList<UserObject> members;
    private ArrayList userGroupList;
    private ArrayList userGroupList2;
    FloatingActionButton addMembersFab;

    int getTotalContactList = 0;

    String intentGroupName, intentAdminID, intentGroupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        //getting values from previous activity
        if (getIntent().getExtras() != null){
            intentAdminID = getIntent().getExtras().getString("adminID");
            intentGroupID = getIntent().getExtras().getString("groupID");
            intentGroupName = getIntent().getExtras().getString("groupName");
        }


        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        groupRef = FirebaseDatabase.getInstance().getReference().child(Constansts.GROUP_REF);
        memberRef = groupRef.child(intentGroupID).child("members");

        members = new ArrayList<>();
        userGroupList = new ArrayList();
        userGroupList2 = new ArrayList();

        addMembersFab = findViewById(R.id.add_members_fab);
        FindFriendsRecyclerList = findViewById(R.id.add_members_recycler_list);
        FindFriendsRecyclerList.setLayoutManager(new LinearLayoutManager(this));


        mToolbar = findViewById(R.id.find_friends_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add members");

        FirebaseRecyclerOptions<UserObject> options =
                new FirebaseRecyclerOptions.Builder<UserObject>()
                        .setQuery(UsersRef, UserObject.class)
                        .build();

        FirebaseRecyclerAdapter<UserObject, AddGroupMembers.AddGroupMembersViewHolder> adapter =
                new FirebaseRecyclerAdapter<UserObject, AddGroupMembers.AddGroupMembersViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull final AddGroupMembers.AddGroupMembersViewHolder holder, final int position, @NonNull final UserObject model)
                    {
                        //get the item count of the children in the recycler view
                        getTotalContactList = getItemCount();
                        Log.d("total item count ", String.valueOf(getTotalContactList));

                        UserObject userObject = getItem(position);

                        //checking to see if user is already a member of the group
                        memberRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                if (dataSnapshot.exists()){
                                    String getTheId = dataSnapshot.child("uid").getValue().toString();
                                    if (getTheId.equals(model.getUid())){
                                        holder.checkBox.setChecked(true);
                                    }else {

                                    }
                                }else{
                                    Toast.makeText(AddGroupMembers.this, "Add members", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        holder.userName.setText(model.getName());
                        holder.userStatus.setText(model.getStatus());
                        Picasso.get().load(model.getImage()).placeholder(R.drawable.imgplaceholder).networkPolicy(NetworkPolicy.OFFLINE).into(holder.profileImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(model.getImage()).into(holder.profileImage);

                            }
                        });

                        //opening the user profile
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                String visit_user_id = getRef(position).getKey();

                                Intent profileIntent = new Intent(AddGroupMembers.this, ProfileActivity.class);
                                profileIntent.putExtra("visit_user_id", visit_user_id);
                                startActivity(profileIntent);
                            }
                        });


                        ProgressDialog progressDialog = new ProgressDialog(AddGroupMembers.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle("Adding members");

                        addMembersFab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //showing progress dialog
                                progressDialog.show();

                                //Adding group id to user node to tell the groups the user belongs to
                                //this is outside of the for loop to prevent the group id from posting multiple times
                                userGroupList.add(intentGroupID);
                                Log.d("group list", " list are " + userGroupList);
                                //Add group to admin only

//                                String idd = model.getUid();
//                                userGroupList2.add(idd);
                                for (int k = 0; k < userGroupList2.size(); k++) {
                                    //checking if user was already a member of the group so it does not create the group twice in the database when we push to the child

                                    String eyeDee =userGroupList2.get(k).toString();
                                    Log.d("adminGroups", eyeDee);


//                                    UsersRef.child(eyeDee).child("userGroups").push().setValue(userGroupList);

//                                    else{
                                        int finalK = k;
                                        UsersRef.child(eyeDee).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChild("userGroups")){
                                                    UsersRef.child(eyeDee).child("userGroups").addChildEventListener(new ChildEventListener() {
                                                        @Override
                                                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                            String getID = dataSnapshot.child("0").getValue().toString();
                                                            Log.d("child","Added");
                                                            Log.d(intentGroupID,getID);

                                                            //ids required will be for on group about to created and selected group
                                                            Log.d("intentGroupId",intentGroupID);
                                                            if(intentGroupID.equals(getID)){
                                                                for (int i=0; i<userGroupList2.size(); i++) {
                                                                    if(userGroupList2.get(i) == eyeDee){
                                                                        userGroupList2.remove(i);
                                                                    }
                                                                    Log.d("userList",userGroupList2.toString());
                                                                }
                                                                Log.d("id","GroupID equal");
                                                            }else if(userGroupList2.contains(eyeDee)){
                                                                UsersRef.child(eyeDee).child("userGroups").push().setValue(userGroupList);
                                                                Log.d("GroupAdded to",eyeDee);
                                                                userGroupList2.clear();
                                                                userGroupList.clear();
                                                            }else{
                                                                Log.d("Nothing","added");
                                                            }
                                                        }

                                                        @Override
                                                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                                        }

                                                        @Override
                                                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                                        }

                                                        @Override
                                                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });

                                                }else{
                                                    Log.d("initialData","Added");
                                                    UsersRef.child(eyeDee).child("userGroups").push().setValue(userGroupList);
                                                    userGroupList2.clear();
                                                    userGroupList.clear();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
//                                    }


                                }
                                //looping through array list and pushing to database
                                for (int j = 0; j < members.size(); j++){
                                    Log.d("AddGroupMembers : " , members.get(j).getName());

                                    //getting user id of selected users
                                    String eyeDee = members.get(j).getUid();

                                    //pushing the member list to database
                                    memberRef.setValue(members).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //dismissing progress dialog
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Group created successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(AddGroupMembers.this, MainActivity.class));
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //dismissing progress dialog
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });



                        //adding member to group list
                        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                if (compoundButton.isChecked()){
                                    members.add(userObject);

//                                    for (int i = 0; i < members.size(); i++){
//                                        Log.i("gadeee " , String.valueOf(i));
                                        userObject.setName(model.getName());
                                        userObject.setImage(model.getImage());
                                        userObject.setStatus(model.getStatus());
                                        userObject.setUid(model.getUid());
                                    userGroupList2.add(model.getUid());

//                                    }



                                            Log.d("AddGroupMembers : " , "member size " + members.size());
                                }else{
                                    members.remove(userObject);
                                }
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AddGroupMembers.AddGroupMembersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.members_display_layout, viewGroup, false);
                        AddGroupMembers.AddGroupMembersViewHolder viewHolder = new AddGroupMembers.AddGroupMembersViewHolder(view);
                        return viewHolder;
                    }
                };

        FindFriendsRecyclerList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class AddGroupMembersViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userStatus;
        CircleImageView profileImage;
        CheckBox checkBox;


        public AddGroupMembersViewHolder(@NonNull View itemView)
        {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}
