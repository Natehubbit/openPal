package com.walkerstechbase.openpal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jgabrielfreitas.core.BlurImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.walkerstechbase.openpal.General.Constansts;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupInfoActivity extends AppCompatActivity {
    CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    AppBarLayout appBarLayout;
    BlurImageView groupInfoImg2;
    ImageView groupInfoImgView;
    Button changeGroupNameBtn, exitGroupBtn;
//    String getGroupProfileImage = "https://firebasestorage.googleapis.com/v0/b/openpal-1c7e8.appspot.com/o/Profile%20Images%2F69C8zFSi0BNiQnZdPzqO69tPzeR2.jpg?alt=media&token=9cb73570-e5eb-4f94-be25-cba5387bbfe4";

    private RecyclerView GroupInfosRecyclerList;
    String name, groupId, groupAdmin, groupImage;
    private static final int GalleryPick = 1;
    private ProgressDialog loadingBar;
    private StorageReference groupProfileImageRef;
    String downloaedUrl;
    DatabaseReference rootRef;

    private String currentUserID;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        //init
        init();

        GroupInfosRecyclerList.setLayoutManager(new LinearLayoutManager(this));


//        name = getIntent().getStringExtra("theGroupName");
        groupId = getIntent().getStringExtra("theGroupID");
//        groupAdmin = getIntent().getStringExtra("theGroupAdmin");
//        groupImage = getIntent().getStringExtra("theGroupImage");
        groupProfileImageRef = FirebaseStorage.getInstance().getReference().child("Group Icons");
        rootRef = FirebaseDatabase.getInstance().getReference();


        //toolbar
        topToolbar();

        //collapsible toolbar
        collapsibleToolbar();

        //get users into fireBase recycler view
        firebaseRecycler();

        //
        imageView();

        //change group name
        changeGroupName();

        //exit group
        exitGroup();

        //getting group picture and group name from directly from database and not from intent to avoid crashes
        rootRef.child(Constansts.GROUP_REF).child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Groupie groupie = dataSnapshot.getValue(Groupie.class);
                    assert groupie != null;
                    groupImage = groupie.getGroupImage();
                    groupAdmin = groupie.getAdminId();
                    name = groupie.getGroupName();

                    if (groupImage.isEmpty()){
                    }else if (!groupImage.isEmpty()){
                        Picasso.get().load(groupImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.team).into(groupInfoImgView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(groupImage).into(groupInfoImgView);
                            }
                        });

                        groupInfoImg2.setBackground(getResources().getDrawable(R.drawable.team));
                        Picasso.get().setIndicatorsEnabled(false);
                        Picasso.get().load(groupImage).networkPolicy(NetworkPolicy.OFFLINE).into(groupInfoImg2, new Callback() {
                            @Override
                            public void onSuccess() {
                                groupInfoImg2.setBlur(25);
                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().setIndicatorsEnabled(false);
                                Picasso.get().load(groupImage).into(groupInfoImgView);
                            }
                        });
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void init() {
        collapsingToolbarLayout = findViewById(R.id.group_info_collapsetoolbar);
        toolbar = findViewById(R.id.group_info_toolbar);
        appBarLayout = findViewById(R.id.group_info_appbar);
        groupInfoImg2 = findViewById(R.id.group_info_blur_img2);
        groupInfoImgView = findViewById(R.id.group_info_imgview);
        GroupInfosRecyclerList = findViewById(R.id.group_info_recyclerview);
        changeGroupNameBtn = findViewById(R.id.group_name_change);
        exitGroupBtn = findViewById(R.id.group_exit);
        loadingBar = new ProgressDialog(this);

    }

    private void topToolbar() {
        toolbar.getNavigationIcon();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
    }

    private void collapsibleToolbar() {

        //appBar offset to determine when collapsible toolbar is collapsed or expanded
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow =  true;
            int scrollRange = -1;

            @Override
            //i here is the vertical offset
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if (scrollRange == -1){
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + i == 0){
                    if(!name.isEmpty()){
                        collapsingToolbarLayout.setTitleEnabled(true);
                        collapsingToolbarLayout.setTitle(name);
                    }else{
                        collapsingToolbarLayout.setTitle(name);
                    }
                    isShow = true;
                }else if(isShow){
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private void imageView(){
        groupInfoImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });
    }

    private void firebaseRecycler(){
        DatabaseReference membersRef = FirebaseDatabase.getInstance().getReference().child(Constansts.GROUP_REF).child(groupId).child(Constansts.MEMBERS_REF);

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(membersRef, Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, GroupInfoActivity.GroupInfoViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, GroupInfoActivity.GroupInfoViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final GroupInfoActivity.GroupInfoViewHolder holder, final int position, @NonNull final Contacts model)
                    {
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


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                String memberId = getRef(position).getKey();



                                membersRef.child(memberId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("uid")){
                                            String uid = Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString();

                                            //sending user uid to next intent
                                            Intent profileIntent = new Intent(GroupInfoActivity.this, ProfileActivity.class);
                                            profileIntent.putExtra("visit_user_id", uid);
                                            startActivity(profileIntent);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            }
                        });
                    }

                    @NonNull
                    @Override
                    public GroupInfoActivity.GroupInfoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        GroupInfoActivity.GroupInfoViewHolder viewHolder = new GroupInfoActivity.GroupInfoViewHolder(view);
                        return viewHolder;
                    }
                };

        GroupInfosRecyclerList.setAdapter(adapter);

        adapter.startListening();


    }

    public static class GroupInfoViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userStatus;
        CircleImageView profileImage;


        public GroupInfoViewHolder(@NonNull View itemView)
        {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Change group icon");
                loadingBar.setMessage("Updating group icon...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                Uri resultUri = result.getUri();


                final StorageReference filePath = groupProfileImageRef.child(groupId + ".jpg");


                filePath.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(GroupInfoActivity.this, "Profile Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

                                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        downloaedUrl = uri.toString();



                                        rootRef.child(Constansts.GROUP_REF).child(groupId).child("groupImage")
                                                .setValue(downloaedUrl)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task)
                                                    {
                                                        if (task.isSuccessful())
                                                        {
                                                            Toast.makeText(GroupInfoActivity.this, "Image save in Database, Successfully...", Toast.LENGTH_SHORT).show();
                                                            loadingBar.dismiss();

                                                            //update the image in the imageView instantly
                                                            groupImage = downloaedUrl;
                                                            Picasso.get().load(groupImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.team).into(groupInfoImgView, new Callback() {
                                                                @Override
                                                                public void onSuccess() {
                                                                    Picasso.get().setIndicatorsEnabled(false);
                                                                    Picasso.get().load(groupImage).networkPolicy(NetworkPolicy.OFFLINE).into(groupInfoImg2, new Callback() {
                                                                        @Override
                                                                        public void onSuccess() {
                                                                            groupInfoImg2.setBlur(25);
                                                                        }

                                                                        @Override
                                                                        public void onError(Exception e) {
                                                                            Picasso.get().setIndicatorsEnabled(false);
                                                                            Picasso.get().load(groupImage).into(groupInfoImgView);
                                                                        }
                                                                    });
                                                                }

                                                                @Override
                                                                public void onError(Exception e) {
                                                                    Picasso.get().load(groupImage).into(groupInfoImgView);
                                                                }
                                                            });



                                                        }
                                                        else
                                                        {
                                                            String message = task.getException().toString();
                                                            Toast.makeText(GroupInfoActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                            loadingBar.dismiss();
                                                        }
                                                    }
                                                });

                                    }
                                });
                                //final String downloaedUrl = task.getResult().getDownloadUrl().toString();
                                //final String downloaedUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                                //final String downloaedUrl = taskSnapshot.getUploadSessionUri().toString();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GroupInfoActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                });

            }
        }
    }

    private void changeGroupName(){
        changeGroupNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog progressDialog = new ProgressDialog(GroupInfoActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Please wait...");

                AlertDialog.Builder builder = new AlertDialog.Builder(GroupInfoActivity.this);
                EditText groupNameET = new EditText(GroupInfoActivity.this);
                builder.setTitle("Change Groupie Name");
                builder.setView(groupNameET);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressDialog.show();

                        String grouptext = groupNameET.getText().toString();
                        if (!grouptext.isEmpty()){
                            Groupie groupie = new Groupie();
                            groupie.setGroupName(grouptext);
//                            HashMap<String, String> map = new HashMap<>();
//                            map.put("groupName", grouptext);

//                            DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child(Constansts.GROUP_REF).child(groupId);
                            rootRef.child(Constansts.GROUP_REF).child(groupId).child("groupName")
                                    .setValue(grouptext).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dialogInterface.dismiss();
                                    progressDialog.dismiss();

                                    //updating toolbar title
                                    toolbar.setTitle(grouptext);
                                    Toast.makeText(GroupInfoActivity.this, "Group name changed.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(GroupInfoActivity.this, "Failed, try again", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //dismiss dialog
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    private void exitGroup(){
        exitGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                    builder = new AlertDialog.Builder(GroupInfoActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(GroupInfoActivity.this);
                }
                builder.setTitle("Exit Group?\n")
                        .setMessage("Are you sure you want to leave group?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                rootRef.child(Constansts.USER_REF).child(currentUserID).child(Constansts.USER_GROUPS).addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                        String getIdToDelete = dataSnapshot.child("0").getValue().toString();
                                        if (groupId.equals(getIdToDelete)) {
                                            String nodeGroupToDelete = dataSnapshot.getKey();
                                            deleteGroupFromUserGroups(nodeGroupToDelete);
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

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //do nothing
                            }
                        }).show();

            }
        });
    }

    private void deleteGroupFromUserGroups(String key){
        rootRef.child(Constansts.USER_REF).child(currentUserID).child(Constansts.USER_GROUPS).child(key).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //deleting user from group member list
                        DatabaseReference membersRef = FirebaseDatabase.getInstance().getReference().child(Constansts.GROUP_REF).child(groupId).child(Constansts.MEMBERS_REF);
                        membersRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                String currentUserIdd = dataSnapshot.child("uid").getValue().toString();
                                Log.d("TAG", " uid : " + currentUserIdd);
                                if (currentUserID.equals(currentUserIdd)){
                                    String getMemberNodeToDelete = dataSnapshot.getKey();
                                    Log.d("TAG", "onChildAdded: " + getMemberNodeToDelete);
                                    deleteMemberFromGroup(getMemberNodeToDelete);
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
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupInfoActivity.this, "Failed, try again", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void deleteMemberFromGroup(String getMemberNodeToDelete) {
        rootRef.child(Constansts.GROUP_REF).child(groupId).child(Constansts.MEMBERS_REF).child(getMemberNodeToDelete).removeValue()
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Exited Group", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(GroupInfoActivity.this, MainActivity.class));
                finish();

            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupInfoActivity.this, "Failed, Try again", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
