package com.walkerstechbase.openpal;

import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindUser2 extends AppCompatActivity  {

    private RecyclerView mUserList;
    private RecyclerView.Adapter mUserListAdapter;
    private RecyclerView.LayoutManager mUserListLayoutManager;
    private DatabaseReference UsersRef;
    ArrayList<UserObject2> userList, contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user2);

        contactList= new ArrayList<>();
        userList= new ArrayList<>();

        Button mCreate = findViewById(R.id.create);
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChat();
            }
        });

        initializeRecyclerView();
        getContactList();
    }

    private void createChat(){
        String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();

        DatabaseReference chatInfoDb = FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("info");
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("user");

        HashMap newChatMap = new HashMap();
        newChatMap.put("id", key);
        newChatMap.put("users/" + FirebaseAuth.getInstance().getUid(), true);

        Boolean validChat = false;
        for(UserObject2 mUser : userList){
            if(mUser.getSelected()){
                validChat = true;
                newChatMap.put("users/" + mUser.getUid(), true);
                userDb.child(mUser.getUid()).child("chat").child(key).setValue(true);
            }
        }

        if(validChat){
            chatInfoDb.updateChildren(newChatMap);
            userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);
        }

    }

    private void getContactList(){

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        FirebaseRecyclerOptions<UserObject2> options =
                new FirebaseRecyclerOptions.Builder<UserObject2>()
                        .setQuery(UsersRef, UserObject2.class)
                        .build();

        FirebaseRecyclerAdapter<UserObject2, UserListAdapter2.UserListViewHolder> adapter =
                new FirebaseRecyclerAdapter<UserObject2, UserListAdapter2.UserListViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final UserListAdapter2.UserListViewHolder holder, final int position, @NonNull final UserObject2 model)
                    {
                        holder.mName.setText(model.getName());
                        holder.mAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                Toast.makeText(compoundButton.getContext(), "you checked " + model.getName() , Toast.LENGTH_SHORT).show();
//                                userList.get(holder.getAdapterPosition()).setSelected(isChecked);
                                //holder.mAdd.setSelected(true);

                                int id = compoundButton.getId();

                                Toast.makeText(FindUser2.this, "jules "+ model.getName(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        holder.mLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(view.getContext(), "holder layout clicked", Toast.LENGTH_SHORT).show();
                            }
                        });
                       // holder.mPhone.setText(model.getStatus());
//                        Picasso.get().load(model.getImage()).placeholder(R.drawable.imgplaceholder).networkPolicy(NetworkPolicy.OFFLINE).into(holder.profileImage, new Callback() {
//                            @Override
//                            public void onSuccess() {
//
//                            }
//
//                            @Override
//                            public void onError(Exception e) {
//                                Picasso.get().load(model.getImage()).into(holder.profileImage);
//
//                            }
//                        });


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                String visit_user_id = getRef(position).getKey();

                                Toast.makeText(FindUser2.this, "was supposed to go to profile " + model.getName(), Toast.LENGTH_SHORT).show();
//                                Intent profileIntent = new Intent(FindFriendsActivity.this, ProfileActivity.class);
//                                profileIntent.putExtra("visit_user_id", visit_user_id);
//                                startActivity(profileIntent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public UserListAdapter2.UserListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user2, viewGroup, false);
                        UserListAdapter2.UserListViewHolder viewHolder = new UserListAdapter2.UserListViewHolder(view);
                        return viewHolder;
                    }

                };
        mUserList.setAdapter(adapter);

        adapter.startListening();








//        String ISOPrefix = getCountryISO();
//
//        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
//        while(phones.moveToNext()){
//            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//
//            phone = phone.replace(" ", "");
//            phone = phone.replace("-", "");
//            phone = phone.replace("(", "");
//            phone = phone.replace(")", "");
//
//            if(!String.valueOf(phone.charAt(0)).equals("+"))
//                phone = ISOPrefix + phone;
//
//            UserObject2 mContact = new UserObject2("", name, phone);
//            contactList.add(mContact);
//            getUserDetails(mContact);
//        }
    }

    private void getUserDetails(UserObject2 mContact) {
        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("Users");

        mUserDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String  phone = "",
                            name = "";
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
//                        if(childSnapshot.child("phone").getValue()!=null)
//                            phone = childSnapshot.child("phone").getValue().toString();
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            if(childSnapshot.child("name").getValue()!=null)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                name = childSnapshot.child("name").getValue().toString();


                        UserObject2 mUser = new UserObject2(childSnapshot.getKey(), name, phone);
                       // if (name.equals(phone))
                            for(UserObject2 mContactIterator : contactList){
                                if(mContactIterator.getName().equals(mUser.getName())){
                                    mUser.setName(mContactIterator.getName());
                                }
                            }

                        userList.add(mUser);
                        mUserListAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


//        Query query = mUserDB.orderByChild("phone").equalTo(mContact.getPhone());
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()){
//                    String  phone = "",
//                            name = "";
//                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
//                        if(childSnapshot.child("phone").getValue()!=null)
//                            phone = childSnapshot.child("phone").getValue().toString();
//                        if(childSnapshot.child("name").getValue()!=null)
//                            name = childSnapshot.child("name").getValue().toString();
//
//
//                        UserObject2 mUser = new UserObject2(childSnapshot.getKey(), name, phone);
//                        if (name.equals(phone))
//                            for(UserObject2 mContactIterator : contactList){
//                                if(mContactIterator.getPhone().equals(mUser.getPhone())){
//                                    mUser.setName(mContactIterator.getName());
//                                }
//                            }
//
//                        userList.add(mUser);
//                        mUserListAdapter.notifyDataSetChanged();
//                        return;
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }


//
//    private String getCountryISO(){
//        String iso = null;
//
//        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
//        if(telephonyManager.getNetworkCountryIso()!=null)
//            if (!telephonyManager.getNetworkCountryIso().toString().equals(""))
//                iso = telephonyManager.getNetworkCountryIso().toString();
//
//        return CountryToPhonePrefix.getPhone(iso);
//    }

    private void initializeRecyclerView() {
        mUserList= findViewById(R.id.userList);
        mUserList.setNestedScrollingEnabled(false);
        mUserList.setHasFixedSize(false);
        mUserListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mUserList.setLayoutManager(mUserListLayoutManager);
        mUserListAdapter = new UserListAdapter2(userList);
        mUserList.setAdapter(mUserListAdapter);
    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userStatus;
        CircleImageView profileImage;


        public FindFriendViewHolder(@NonNull View itemView)
        {
            super(itemView);

            userName = itemView.findViewById(R.id.name);
            userStatus = itemView.findViewById(R.id.phone);
           // profileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }
}
