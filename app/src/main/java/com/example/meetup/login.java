package com.example.meetup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {
    private Button loginbutton;
    private EditText UserEmail, UserPassword;
    private TextView NeedNewAccount;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        NeedNewAccount = (TextView) findViewById(R.id.Register_link);
        UserEmail = (EditText) findViewById(R.id.login_email);
        UserPassword = (EditText) findViewById(R.id.login_password);
        loginbutton = (Button) findViewById(R.id.login_button);
        loadingbar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        NeedNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowigusertologin();
            }
            protected void onStart() {
            login.super.onStart();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser==null){
                    sendusertomainactivity();
                }
            }

        });
        }


    private void allowigusertologin() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
        } else {
            loadingbar.setTitle("Logging in");
            loadingbar.setMessage("Please wait, while we are logging you into your account");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        sendusertomainactivity();
                        Toast.makeText(login.this, "Your are logged in", Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(login.this, "" + message, Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                    }

                }
            });
        }
    }

    private void sendusertomainactivity() {
        Intent mainIntent= new Intent(login.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }

    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(login.this, Register.class);
        startActivity(registerIntent);

    }
}