package com.walkerstechbase.openpal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity
{
    private ImageButton SendVerificationCodeButton;
    //private Button  VerifyButton;
    private EditText InputPhoneNumber;
            //InputVerificationCode
    private static final int GalleryPick = 1;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth mAuth;

    private ProgressDialog loadingBar;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
//    private StorageReference UserProfileImagesRef;
//    private DatabaseReference RootRef;
//    String currentUserID;
    //RelativeLayout picLay;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        mAuth = FirebaseAuth.getInstance();


        SendVerificationCodeButton = findViewById(R.id.send_ver_code_button);
        //VerifyButton = (Button) findViewById(R.id.verify_button);
        InputPhoneNumber = (EditText) findViewById(R.id.phone_nnumber_input);
        //InputVerificationCode = (EditText) findViewById(R.id.verification_code_input);
       // picLay = findViewById(R.id.pic_lay);
        loadingBar = new ProgressDialog(this);

//        RootRef = FirebaseDatabase.getInstance().getReference();
//        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

//        picLay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent galleryIntent = new Intent();
//                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//                galleryIntent.setType("image/*");
//                startActivityForResult(galleryIntent, GalleryPick);
//            }
//        });

        SendVerificationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String phoneNumber = InputPhoneNumber.getText().toString();

                if (TextUtils.isEmpty(phoneNumber))
                {
                    Toast.makeText(PhoneLoginActivity.this, "Please enter your phone number first...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingBar.setTitle("Phone Verification");
                    loadingBar.setMessage("please wait, while we are authenticating your phone...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks
                }
            }
        });


//        VerifyButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                SendVerificationCodeButton.setVisibility(View.INVISIBLE);
//                InputPhoneNumber.setVisibility(View.INVISIBLE);
//
//                String verificationCode = InputVerificationCode.getText().toString();
//
//                if (TextUtils.isEmpty(verificationCode))
//                {
//                    Toast.makeText(PhoneLoginActivity.this, "Please write verification code first...", Toast.LENGTH_SHORT).show();
//                }
//                else
//                {
//                    loadingBar.setTitle("Verification Code");
//                    loadingBar.setMessage("please wait, while we are verifying verification code...");
//                    loadingBar.setCanceledOnTouchOutside(false);
//                    loadingBar.show();
//
//                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
//                    signInWithPhoneAuthCredential(credential);
//                }
//            }
//        });


        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {
                //signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e)
            {
                loadingBar.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Invalid Phone Number, Please enter correct phone number with your country code...", Toast.LENGTH_SHORT).show();

//                SendVerificationCodeButton.setVisibility(View.VISIBLE);
//                InputPhoneNumber.setVisibility(View.VISIBLE);

//                VerifyButton.setVisibility(View.INVISIBLE);
//                InputVerificationCode.setVisibility(View.INVISIBLE);
            }

            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token)
            {
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                loadingBar.dismiss();
                Toast.makeText(getApplicationContext(), "Code has been sent, please check and verify...", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(PhoneLoginActivity.this, VerifyPhone.class);
                i.putExtra("verification_id", mVerificationId);
                i.putExtra("token", mResendToken);
                startActivity(i);
//                VerifyButton.setVisibility(View.VISIBLE);
//                InputVerificationCode.setVisibility(View.VISIBLE);
            }
        };
    }




//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful())
//                        {
//                            loadingBar.dismiss();
//                            Toast.makeText(PhoneLoginActivity.this, "Congratulations, you're logged in successfully...", Toast.LENGTH_SHORT).show();
//                            SendUserToMainActivity();
//                        }
//                        else
//                        {
//                            String message = task.getException().toString();
//                            Toast.makeText(PhoneLoginActivity.this, "Error : "  +  message, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }




    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(PhoneLoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
        {
            SendUserToMainActivity();
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
//    {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
//        {
//            Uri ImageUri = data.getData();
//
//            CropImage.activity()
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .setAspectRatio(1, 1)
//                    .start(this);
//        }
//
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
//        {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//
//            if (resultCode == RESULT_OK)
//            {
//                loadingBar.setTitle("Set Profile Image");
//                loadingBar.setMessage("Please wait, your profile image is updating...");
//                loadingBar.setCanceledOnTouchOutside(false);
//                loadingBar.show();
//
//                Uri resultUri = result.getUri();
//
//
//                final StorageReference filePath = UserProfileImagesRef.child(currentUserID + ".jpg");
//
//
//                filePath.putFile(resultUri)
//                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                Toast.makeText(PhoneLoginActivity.this, "Profile Image uploaded Successfully...", Toast.LENGTH_SHORT).show();
//
//                                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                    @Override
//                                    public void onSuccess(Uri uri) {
//                                        String downloaedUrl = uri.toString();
//
//                                        RootRef.child("Users").child(currentUserID).child("image")
//                                                .setValue(downloaedUrl)
//                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<Void> task)
//                                                    {
//                                                        if (task.isSuccessful())
//                                                        {
//                                                            Toast.makeText(PhoneLoginActivity.this, "Image save in Database, Successfully...", Toast.LENGTH_SHORT).show();
//                                                            loadingBar.dismiss();
//                                                        }
//                                                        else
//                                                        {
//                                                            String message = task.getException().toString();
//                                                            Toast.makeText(PhoneLoginActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
//                                                            loadingBar.dismiss();
//                                                        }
//                                                    }
//                                                });
//
//                                    }
//                                });
//                                //final String downloaedUrl = task.getResult().getDownloadUrl().toString();
//                                //final String downloaedUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
//                                //final String downloaedUrl = taskSnapshot.getUploadSessionUri().toString();
//
//
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(PhoneLoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        loadingBar.dismiss();
//                    }
//                });
//
//            }
//        }
//    }
}
