package com.walkerstechbase.openpal;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment
{
    private View groupFragmentView;
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_groups = new ArrayList<>();

    private DatabaseReference groupRef;
    FloatingActionButton addNewGroupFab;
    private String currentUserID;
    private FirebaseAuth mAuth;

    //new fields for new group layout
    ImageView testimonies, prayers;
    RelativeLayout advices;

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

        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");


        IntializeFields();


        RetrieveAndDisplayGroups();


        addNewGroupFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //group stuff
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Creating group");

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                EditText groupNameET = new EditText(getActivity());
                builder.setTitle("Group Name");
                builder.setView(groupNameET);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //show progress dialog
                        progressDialog.show();

                        String groupID = groupRef.push().getKey();
                        String groupName = groupNameET.getText().toString();
                        String adminID = currentUserID;

                        //creating the group node to get the groupID for the next intent
                        Map<String , String> map = new HashMap();
                        map.put("adminId", adminID);
                        map.put("groupId", groupID);
                        map.put("groupName", groupName);

                        groupRef.child(Objects.requireNonNull(groupID)).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                Toast.makeText(getActivity(), "Failed creating group", Toast.LENGTH_SHORT).show();
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

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                String currentGroupName = adapterView.getItemAtPosition(position).toString();

                Intent groupChatIntent = new Intent(getContext(), GroupChatActivity.class);
                groupChatIntent.putExtra("groupName" , currentGroupName);
                startActivity(groupChatIntent);
            }
        });


        return groupFragmentView;
    }



    private void IntializeFields()
    {
        list_view =  groupFragmentView.findViewById(R.id.list_view);
        addNewGroupFab = groupFragmentView.findViewById(R.id.add_group_fab);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list_of_groups);
        list_view.setAdapter(arrayAdapter);
    }




    private void RetrieveAndDisplayGroups()
    {
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Set<String> set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();

                while (iterator.hasNext())
                {
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }

                list_of_groups.clear();
                list_of_groups.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
