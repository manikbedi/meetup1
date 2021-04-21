package com.example.meetup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class setup extends AppCompatActivity {
    private EditText userName, fullName, countryName;
    private Button Save;
    private CircleImageView profileImage;
    private FirebaseAuth mauth;
    private DatabaseReference usersref;
    private ProgressDialog loadingbar;
    String currentuserid;
    final static int gallery_pic = 1;
    private StorageReference userprofileref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        userName = (EditText) findViewById(R.id.setup_username);
        fullName = (EditText) findViewById(R.id.setup_fullname);
        countryName = (EditText) findViewById(R.id.setup_country);
        Save = (Button) findViewById(R.id.setup_save);
        mauth = FirebaseAuth.getInstance();
        userprofileref = FirebaseStorage.getInstance().getReference().child("profile images");
        loadingbar = new ProgressDialog(this);
        currentuserid = mauth.getCurrentUser().getUid();
        usersref = FirebaseDatabase.getInstance("https://meet-up-5c59e-default-rtdb.firebaseio.com/").getReference().child("users");
        profileImage = (CircleImageView) findViewById(R.id.setup_profile_image);

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveAccountSetupInformation();
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent = new Intent();
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent, gallery_pic);
            }
        });

        usersref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if (dataSnapshot.hasChild("profileimage"))
                    {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.with(setup.this).load(image).placeholder(R.drawable.profile).into(profileImage);
                    }
                    else
                    {
                        Toast.makeText(setup.this, "Please select profile image first.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void SaveAccountSetupInformation() {
        String username = userName.getText().toString();
        String fullname = fullName.getText().toString();
        String country = countryName.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please write your username...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(fullname)) {
            Toast.makeText(this, "Please write your full name...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(country)) {
            Toast.makeText(this, "Please write your country...", Toast.LENGTH_SHORT).show();
        } else {
            loadingbar.setTitle("Saving Information");
            loadingbar.setMessage("Please wait, while we are creating your new Account...");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);

            HashMap userMap = new HashMap();
            userMap.put("username", username);
            userMap.put("fullname", fullname);
            userMap.put("country", country);
            userMap.put("status", "Hey there");
            userMap.put("gender", "none");
            userMap.put("dob", "none");
            userMap.put("relationshipstatus", "none");
            usersref.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        sendusertomainactivity();
                        Toast.makeText(setup.this, "your Account is created Successfully.", Toast.LENGTH_LONG).show();
                        loadingbar.dismiss();
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(setup.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                    }
                }
            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == gallery_pic && resultCode == RESULT_OK && data != null) {
            Uri imageuri = data.getData();
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this);
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK) {
                    loadingbar.setTitle("Profile Image");
                    loadingbar.setMessage("Please wait, while we updating your profile image...");
                    loadingbar.show();
                    loadingbar.setCanceledOnTouchOutside(true);

                    Uri resultUri = result.getUri();

                    StorageReference filePath = userprofileref.child(currentuserid + ".jpg");

                    filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(setup.this, "Profile Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();

                                final String downloadUrl = task.getResult().getStorage().getDownloadUrl().toString();

                                usersref.child("profileimage").setValue(downloadUrl)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Intent selfIntent = new Intent(setup.this, setup.class);
                                                    startActivity(selfIntent);

                                                    Toast.makeText(setup.this, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
                                                    loadingbar.dismiss();
                                                } else {
                                                    String message = task.getException().getMessage();
                                                    Toast.makeText(setup.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                                    loadingbar.dismiss();
                                                }
                                            }
                                        });
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "Error Occured: Image can not be cropped. Try Again.", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }
            }
        }


    }

    private void sendusertomainactivity() {
        Intent mainIntent = new Intent(setup.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}


