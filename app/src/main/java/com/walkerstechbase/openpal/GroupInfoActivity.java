package com.walkerstechbase.openpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jgabrielfreitas.core.BlurImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.walkerstechbase.openpal.General.Constansts;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupInfoActivity extends AppCompatActivity {
    CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    AppBarLayout appBarLayout;
    BlurImageView groupInfoImg2;
    ImageView groupInfoImgView;
    String getGroupProfileImage = "https://firebasestorage.googleapis.com/v0/b/openpal-1c7e8.appspot.com/o/Profile%20Images%2F69C8zFSi0BNiQnZdPzqO69tPzeR2.jpg?alt=media&token=9cb73570-e5eb-4f94-be25-cba5387bbfe4";

    private RecyclerView GroupInfosRecyclerList;
    String name, groupId, groupAdmin, groupImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        //init
        init();

        GroupInfosRecyclerList.setLayoutManager(new LinearLayoutManager(this));


        name = getIntent().getStringExtra("theGroupName");
        groupId = getIntent().getStringExtra("theGroupID");
        groupAdmin = getIntent().getStringExtra("theGroupAdmin");
        groupImage = getIntent().getStringExtra("theGroupImage");

        //toolbar
        topToolbar();

        //collapsible toolbar
        collapsibleToolbar();

        //get users into fireBase recycler view
        firebaseRecycler();

        if (getGroupProfileImage.isEmpty()){
        }else if (!getGroupProfileImage.isEmpty()){
            Picasso.get().load(getGroupProfileImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.team).into(groupInfoImgView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(getGroupProfileImage).into(groupInfoImgView);
                }
            });

            groupInfoImg2.setBackground(getResources().getDrawable(R.drawable.team));
            Picasso.get().setIndicatorsEnabled(false);
            Picasso.get().load(getGroupProfileImage).networkPolicy(NetworkPolicy.OFFLINE).into(groupInfoImg2, new Callback() {
                @Override
                public void onSuccess() {
                    groupInfoImg2.setBlur(25);
                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().setIndicatorsEnabled(false);
                    Picasso.get().load(getGroupProfileImage).into(groupInfoImgView);
                }
            });
        }
    }

    private void init() {
        collapsingToolbarLayout = findViewById(R.id.group_info_collapsetoolbar);
        toolbar = findViewById(R.id.group_info_toolbar);
        appBarLayout = findViewById(R.id.group_info_appbar);
        groupInfoImg2 = findViewById(R.id.group_info_blur_img2);
        groupInfoImgView = findViewById(R.id.group_info_imgview);
        GroupInfosRecyclerList = findViewById(R.id.group_info_recyclerview);

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
}
