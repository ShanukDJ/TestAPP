package com.example.testapp.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testapp.MainActivity;
import com.example.testapp.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileDetails extends AppCompatActivity {


    TextView displayName;
    TextView displayEmail;
    Button logOut;
    private CircleImageView userProfileImage;
    private FirebaseAuth mAuth;
    private static final int GALLERY_PIC = 1;
    private StorageReference UserProfileImageRef;
    private DatabaseReference reference;
    private String currentUserId;
//    FirebaseUser currentuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);


        mAuth = FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference();
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        initViews();
        setListners();
        RetriveUserInfo();


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUserId ==null)
        {
            SendUserToLoginPge();
        }

    }
    private void SendUserToLoginPge()
    {
        Intent LoginIntent=new Intent(ProfileDetails.this,MainActivity.class);
        startActivity(LoginIntent);
    }

        private void RetriveUserInfo() {

            reference.child("Users").child(currentUserId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Email") && (dataSnapshot.hasChild("image")))) {
                                String FName = dataSnapshot.child("First Name").getValue().toString();
                                String LName = dataSnapshot.child("Last Name").getValue().toString();
                                String Email = dataSnapshot.child("Email").getValue().toString();
                                String ProfileImage = dataSnapshot.child("image").getValue().toString();

                                String name = FName + LName;

                                displayName.setText(name);
                                displayEmail.setText(Email);

                                Picasso.get().load(ProfileImage).into(userProfileImage);

                            } else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Email"))) {
                                String FName = dataSnapshot.child("First Name").getValue().toString();
                                String LName = dataSnapshot.child("Last Name").getValue().toString();
                                String Email = dataSnapshot.child("Email").getValue().toString();

                                String name = FName + LName;

                                displayName.setText(name);
                                displayEmail.setText(Email);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PIC && resultCode == RESULT_OK && data != null) {

            Uri ImageUri = data.getData();

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                final Uri resultUri = result.getUri();

                final StorageReference filePath = UserProfileImageRef.child(currentUserId + ".jpg");

                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                final String downloadUri =  uri.toString();

                                reference.child("Users").child(currentUserId).child("image").setValue(downloadUri)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(ProfileDetails.this, "Successfully updated!", Toast.LENGTH_SHORT).show();
                                                } else{
                                                    String message = task.getException().toString();
                                                    Toast.makeText(ProfileDetails.this, "Error!: "+ message, Toast.LENGTH_SHORT).show();


                                                }
                                            }
                                        });
                            }
                        });
                    }
                });
            }
        }
    }


    private void updateUI () {

            Intent in = new Intent(ProfileDetails.this, MainActivity.class);
            startActivity(in);
        }

        private void initViews () {

            displayName = findViewById((R.id.DisplayName));
            displayEmail = findViewById((R.id.DisplayEmail));
            logOut = findViewById((R.id.Logout));
            userProfileImage = findViewById(R.id.set_profile_image);

        }
        private void setListners () {

            logOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mAuth.signOut();
                    LoginManager.getInstance().logOut();
                    updateUI();

                }
            });


            userProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, GALLERY_PIC);
                }
            });
        }



}


