package com.walkerstechbase.openpal;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.walkerstechbase.openpal.General.Constansts;
import com.walkerstechbase.openpal.Notification.Data;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment
{
    private View groupFragmentView;
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<Groupie> list_of_groups;

    private DatabaseReference groupRef, memberRef, userRef;
    FloatingActionButton addNewGroupFab;
    private String currentUserID;
    private FirebaseAuth mAuth;

    //new fields for new group layout
    ImageView testimonies, prayers;
    RelativeLayout advices;
    String getGroupProfileImage, getGroupProfileName, getGroupProfileAdminId, getGroupProfileGroupId;
    String getID;
    Query query;
    String gID;

    ArrayList<String> gIDList;
    Long count;
    TheAdapter theAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        groupFragmentView = inflater.inflate(R.layout.fragment_groups, container, false);
        //        testimonies = groupFragmentView.findViewById(R.id.group_testimonies);
//        advices = groupFragmentView.findViewById(R.id.group_advices);
//        prayers = groupFragmentView.findViewById(R.id.group_prayers);
//
//        //setting onClick listeners for each textview
//        testimonies.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String textTestimonies = "Testimonies";
//
//                Intent goToTestimonies = new Intent(getContext(), GroupChatActivity.class);
//                goToTestimonies.putExtra("groupName" , textTestimonies);
//                startActivity(goToTestimonies);
//            }
//        });
//
//        advices.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String textAdvices = "Godly Advices";
//
//                Intent goToAdvices = new Intent(getContext(), GroupChatActivity.class);
//                goToAdvices.putExtra("groupName" , textAdvices);
//                startActivity(goToAdvices);
//            }
//        });
//
//        prayers.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String textPrayers = "Prayer Topics";
//
//                Intent goToPrayers = new Intent(getContext(), GroupChatActivity.class);
//                goToPrayers.putExtra("groupName" , textPrayers);
//                startActivity(goToPrayers);
//            }
//        });
//

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        groupRef = FirebaseDatabase.getInstance().getReference().child(Constansts.GROUP_REF);
        userRef = FirebaseDatabase.getInstance().getReference().child(Constansts.USER_REF);
        //basically trying to get the group id
//        groupRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                getID = dataSnapshot.child("groupId").getValue().toString();
//                Log.d("Group ID, " , dataSnapshot.getChildren().toString());
//                //getting the group id and making it a child to be able to read the mnember ref
//                memberRef = groupRef.child(getID).child(Constansts.MEMBERS_REF);
////                checkIfUserIsMemberOfGroup();
//                query = memberRef.orderByChild("uid").equalTo(currentUserID);
//                query.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                            Log.d("this ", dataSnapshot.getChildren().toString());
//                            Log.d("that ", dataSnapshot.getValue().toString());
//                            Log.d("there ", dataSnapshot.getRef().toString());
////                            Log.d("here ", snapshot.getRef().toString());
//
////                            RetrieveAndDisplayGroups();
//
////                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });



        IntializeFields();

//        RetrieveAndDisplayGroups();

        gIDList = new ArrayList<>();
        list_of_groups = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
//        theAdapter = new TheAdapter(list_of_groups);
//        theAdapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(linearLayoutManager);


        addNewGroupFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //group stuff
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Please wait...");

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                EditText groupNameET = new EditText(getActivity());
                builder.setTitle("Groupie Name");
                builder.setView(groupNameET);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //show progress dialog
                        progressDialog.show();

                        String groupID = groupRef.push().getKey();
                        String groupName = groupNameET.getText().toString();
                        String adminID = currentUserID;

                        //creating the groupie node to get the groupID for the next intent
                        Groupie groupie = new Groupie();
                        groupie.setAdminId(adminID);
                        groupie.setGroupId(groupID);
                        groupie.setGroupName(groupName);
                        groupie.setGroupImage("");

                        groupRef.child(Objects.requireNonNull(groupID)).setValue(groupie).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialogInterface.dismiss();
                                progressDialog.dismiss();

                                Intent intent = new Intent(getActivity(), AddGroupMembers.class);
                                intent.putExtra("groupID", groupID);
                                intent.putExtra("groupName", groupName);
                                intent.putExtra("adminID", adminID);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Failed creating groupie", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();

            }
        });


//        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
//            {
//                String currentGroupName = adapterView.getItemAtPosition(position).toString();
//
//                Intent groupChatIntent = new Intent(getContext(), GroupChatActivity.class);
//                groupChatIntent.putExtra("groupName" , currentGroupName);
//                startActivity(groupChatIntent);
//            }
//        });



        userRef.child(currentUserID).child("userGroups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                count = dataSnapshot.getChildrenCount();
                userRef.child(currentUserID).child("userGroups").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            Log.d("getChildCount " ,"child count is " + count);
                            gID = dataSnapshot.getValue().toString().replace("[", "").replace("]", "");
                            Log.d("gID " , gID);
                            gIDList.add(gID);

                            int size = 0;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                size = Math.toIntExact(count);
                            }
                            Log.d("gidLIst size", String.valueOf(gIDList.size()));

                            if (gIDList.size() == size){
                                for (int i = 0; i < size; i++){
                                    groupRef.child(gIDList.get(i)).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Groupie groupie = dataSnapshot.getValue(Groupie.class);

                                                list_of_groups.add(groupie);
                                            if (groupie != null) {
                                                Log.d("gg","gg " + groupie.groupName);
                                                Log.d("ggImage","ggImage " + groupie.groupImage);
                                            }


//                                                linearLayoutManager = new LinearLayoutManager(getActivity());
//                                                recyclerView.setHasFixedSize(true);
                                                theAdapter = new TheAdapter(list_of_groups);
                                                theAdapter.notifyDataSetChanged();
//                                                recyclerView.setLayoutManager(linearLayoutManager);
                                                recyclerView.setAdapter(theAdapter);

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }

                            }else{
                                Log.d("elseie ", "else state ");

                        }






//                groupRef = FirebaseDatabase.getInstance().getReference().child(Constansts.GROUP_REF).child(gID);





                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        theAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        theAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        theAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        theAdapter.notifyDataSetChanged();
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //getting ids of groups related to a user


        return groupFragmentView;
    }

    private void checkIfUserIsMemberOfGroup() {
        memberRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                String currentMember = dataSnapshot.child("uid").getValue().toString();
                if (currentUserID.equals(currentMember)){


                    Toast.makeText(getContext(), "Yes you are a member", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "Sorry you aren't a member", Toast.LENGTH_SHORT).show();
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


    private void IntializeFields()
    {
//        list_view =  groupFragmentView.findViewById(R.id.list_view);
        addNewGroupFab = groupFragmentView.findViewById(R.id.add_group_fab);
        recyclerView = groupFragmentView.findViewById(R.id.user_group_recyc);
//        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list_of_groups);
//        list_view.setAdapter(arrayAdapter);
    }




    private void RetrieveAndDisplayGroups()
    {
        groupRef = FirebaseDatabase.getInstance().getReference().child(Constansts.GROUP_REF);

        FirebaseListOptions<Groupie> options = new FirebaseListOptions.Builder<Groupie>()
                .setQuery(groupRef, Groupie.class)
                .setLayout(R.layout.group_list_items)
                .setLifecycleOwner(this)
                .build();

        FirebaseListAdapter<Groupie> firebaseListAdapter = new FirebaseListAdapter<Groupie>(options) {
            @Override
            protected void populateView(View v, Groupie model, int position) {
                final TextView title = v.findViewById(R.id.group_profile_name);
                final CircleImageView image = v.findViewById(R.id.group_profile_image);

                getGroupProfileImage = model.getGroupImage();
                getGroupProfileName = model.getGroupName();
                getGroupProfileAdminId = model.getAdminId();
                getGroupProfileGroupId = model.getGroupId();

                title.setText(getGroupProfileName);

                if (getGroupProfileImage.isEmpty()){
                }else if (!getGroupProfileImage.isEmpty()){
                    Picasso.get().load(getGroupProfileImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.team).into(image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(getGroupProfileImage).into(image);
                        }
                    });

                }




                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String textGroupName = model.getGroupName();
                        Intent goToPrayers = new Intent(getContext(), GroupChatActivity.class);
                        goToPrayers.putExtra("groupName" , textGroupName);
                        startActivity(goToPrayers);
                        }
                });
            }
        };

        list_view.setAdapter(firebaseListAdapter);


//        groupRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot)
//            {
//                Set<String> set = new HashSet<>();
//                Iterator iterator = dataSnapshot.getChildren().iterator();
//
//                while (iterator.hasNext())
//                {
//                    set.add(((DataSnapshot)iterator.next()).getKey());
//                }
//
//                list_of_groups.clear();
//                list_of_groups.addAll(set);
//                arrayAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }
}
