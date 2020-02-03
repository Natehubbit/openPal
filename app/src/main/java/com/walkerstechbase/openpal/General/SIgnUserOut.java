package com.walkerstechbase.openpal.General;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.walkerstechbase.openpal.PhoneLoginActivity;

public class SIgnUserOut extends AppCompatActivity{

    public void signOut(){
        Intent loginIntent = new Intent(getApplicationContext(), PhoneLoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }
}
