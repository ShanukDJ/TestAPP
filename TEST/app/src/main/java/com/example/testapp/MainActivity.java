package com.example.testapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testapp.Profile.Home;
import com.example.testapp.Profile.ProfileDetails;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {


    EditText loginEmail;
    EditText loginPassword;
    Button login;
    Button fbSignUpButton;
    TextView registerLink;
    private FirebaseAuth mAuth;
    private static final String EMAIL = "email";
    CallbackManager mcallbackManager;
    FirebaseUser user;
    private static final String TAG = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mcallbackManager = CallbackManager.Factory.create();

        initViews();
        setListners();


        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(MainActivity.this, Registration.class);
                startActivity(in);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AllowUserToLogin();

                Intent in = new Intent(MainActivity.this, ProfileDetails.class);
                startActivity(in);
            }
        });
    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI();
                        }

                    }
                });
    }

    private void updateUI() {

        Intent in = new Intent(MainActivity.this, ProfileDetails.class);
        startActivity(in);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mcallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

    }


    private void AllowUserToLogin() {


        String loginemail = loginEmail.getText().toString();
        String loginpassword = loginPassword.getText().toString();


        if (TextUtils.isEmpty(loginemail) & (TextUtils.isEmpty(loginpassword))){
            Toast.makeText(this, "Please fill out the login details!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(loginemail)){
            Toast.makeText(this, "Please enter your email!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(loginpassword)){
            Toast.makeText(this, "Please enter the password!", Toast.LENGTH_SHORT).show();
        }
        else{

            mAuth.signInWithEmailAndPassword(loginemail,loginpassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                SendUserToProfile();
                                Toast.makeText(MainActivity.this,"Login Success! ",Toast.LENGTH_SHORT).show();

                            }else{
                                String message = task.getException().toString();
                                Toast.makeText(MainActivity.this, "Login Error! : "+message, Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        }
    }

    private void SendUserToProfile() {

        Intent mainIntent = new Intent(MainActivity.this, ProfileDetails.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }

    private void initViews() {

        loginEmail = findViewById((R.id.LoginEmail));
        loginPassword = findViewById((R.id.LoginPassword));
        login = findViewById((R.id.loginButton));
        registerLink = findViewById((R.id.RegisterLink));
        fbSignUpButton = findViewById((R.id.FbSignInButton));

    }

    private void setListners() {

        fbSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(mcallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());

                    }

                    @Override
                    public void onCancel() {

                        Toast.makeText(getApplicationContext(), "User Cancelled it", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    ;
                });




            }
        });
    }
}
