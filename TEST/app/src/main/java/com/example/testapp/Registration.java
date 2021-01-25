package com.example.testapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testapp.Profile.ProfileDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Registration extends AppCompatActivity {


    EditText firstName;
    EditText lastName;
    EditText registerEmail;
    EditText registerPassword;
    Button register;
    TextView loginLink;
    private String currentUserId;

    FirstNameLastName firstNameLastName;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
//    FirebaseUser currentuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference();
        firstNameLastName = new FirstNameLastName();

        initViews();
        setListners();


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
        Intent LoginIntent=new Intent(Registration.this,MainActivity.class);
        startActivity(LoginIntent);
    }
    private void AllowUserToRegister() {

        String email = registerEmail.getText().toString();
        String password = registerPassword.getText().toString();

        if (TextUtils.isEmpty(email) & (TextUtils.isEmpty(password))) {
            Toast.makeText(this, "Please fill out all the fields!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter a password!", Toast.LENGTH_SHORT).show();
        } else {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(Registration.this, "Successfully Registered! ", Toast.LENGTH_LONG).show();
                                SendUserToMainActivity();


                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(Registration.this, "Error : " + message, Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

        }

    }

    private void registerdetails() {

        String regsiterFName = firstName.getText().toString();
        String regsiterLName = lastName.getText().toString();
        String registeremail = registerEmail.getText().toString();


        if (TextUtils.isEmpty(regsiterFName)) {
            Toast.makeText(this, "Please enter your first name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(regsiterLName)) {
            Toast.makeText(this, "Please enter your last name!", Toast.LENGTH_SHORT).show();
        } else {


            HashMap<String, Object> RegisterMap = new HashMap<>();

            //RegisterMap.put("id", currentUserId);
            RegisterMap.put("First Name", regsiterFName);
            RegisterMap.put("Last Name", regsiterLName);
            RegisterMap.put("Email", registeremail);


            reference.child("Users").child(String.valueOf(currentUserId)).updateChildren(RegisterMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

//                    Toast.makeText(Registration.this, "Success!", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }


    private void setListners() {

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Registration.this, MainActivity.class);
                startActivity(in);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AllowUserToRegister();
                registerdetails();
            }
        });


    }

    private void initViews() {
        firstName = findViewById((R.id.FirstName));
        lastName = findViewById((R.id.LastName));
        registerEmail = findViewById((R.id.RegisterEmail));
        registerPassword = findViewById((R.id.RegisterPassword));
        register = findViewById((R.id.Register));
        loginLink = findViewById((R.id.LoginLink));
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(Registration.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
