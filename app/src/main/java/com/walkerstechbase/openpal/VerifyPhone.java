package com.walkerstechbase.openpal;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class VerifyPhone extends AppCompatActivity {
    private ImageButton VerifyButton;
    private EditText InputVerificationCode;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_number);

        VerifyButton = findViewById(R.id.verify_button);
        InputVerificationCode = findViewById(R.id.verification_code_input);

        mAuth = FirebaseAuth.getInstance();

        mVerificationId = getIntent().getExtras().getString("verification_id");
        //mResendToken = getIntent().getExtras().get("token");

        VerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                String verificationCode = InputVerificationCode.getText().toString();

                if (TextUtils.isEmpty(verificationCode))
                {
                    Toast.makeText(VerifyPhone.this, "Please write verification code first...", Toast.LENGTH_SHORT).show();
                }
                else
                {
//                    loadingBar.setTitle("Verification Code");
//                    loadingBar.setMessage("please wait, while we are verifying verification code...");
//                    loadingBar.setCanceledOnTouchOutside(false);
//                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            //loadingBar.dismiss();
                            Toast.makeText(VerifyPhone.this, "Congratulations, you're logged in successfully...", Toast.LENGTH_SHORT).show();
                            SendUserToMainActivity();
                        }
                        else
                        {
                            String message = task.getException().toString();
                            Toast.makeText(VerifyPhone.this, "Error : "  +  message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(VerifyPhone.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
