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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {
    private EditText Useremail,userpassword,usercpassword;
    private Button createaccount;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Useremail=(EditText) findViewById(R.id.register_email);
        userpassword=(EditText) findViewById(R.id.register_password);
        usercpassword=(EditText) findViewById(R.id.register_cpassword);
        createaccount=(Button) findViewById(R.id.register_button);
        loadingbar=new ProgressDialog(this);
        mAuth =FirebaseAuth.getInstance();

        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Createnewaacount();
            }
            protected void onStart(){
                Register.super.onStart();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser==null){
                    sendusertomainactivity();
                }}
        });




    }

    private void sendusertomainactivity() {
        Intent mainIntent= new Intent(Register.this, Activity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }

    private void Createnewaacount() {
        String email = Useremail.getText().toString();
        String password=userpassword.getText().toString();
        String confirm_password= usercpassword.getText().toString();


        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please fill your email", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please fill your password", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(confirm_password)){
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();
        }
        else if (!password.equals(confirm_password)){
            Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingbar.setTitle("Creating new account");
            loadingbar.setMessage("Please wait, Your account is being created");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        SendUsertosetupactivity();
                        Toast.makeText(Register.this, "You are registered succesfully", Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                    }
                    else {
                        String message= task.getException().getMessage();
                        Toast.makeText(Register.this, "Error:"+ message, Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                    }
                }
            });
        }
    }

    private void SendUsertosetupactivity() {
        Intent setupintent=new Intent(Register.this, setup.class);
        setupintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupintent);
        finish();
    }
}