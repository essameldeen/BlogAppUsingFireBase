package com.example.toshiba.blogappusingfirebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class activity_register extends AppCompatActivity implements View.OnClickListener {
    private EditText et_email;
    private EditText et_passWord;
    private EditText et_confirmPassWord;
    private Button bt_register;
    private Button bt_logIn;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
            et_email=(EditText)findViewById(R.id.et_mail_register);
            et_passWord=(EditText)findViewById(R.id.et_password_register);
            et_confirmPassWord=(EditText)findViewById(R.id.et_confrimpassword_register);
            progressBar=(ProgressBar)findViewById(R.id.register_progress);
             bt_logIn=(Button)findViewById(R.id.bt_LogIn_register);
             bt_register=(Button)findViewById(R.id.bt_createAccount);

             auth=FirebaseAuth.getInstance();
             //
             bt_register.setOnClickListener(this);
             bt_logIn.setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if(user!=null){
            SentToMain();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.bt_LogIn_register){
            finish();
        }else if(view.getId()==R.id.bt_createAccount){
             String email=et_email.getText().toString();
             String passWord=et_passWord.getText().toString();
             String confirmPassWord=et_confirmPassWord.getText().toString();

             if(!(TextUtils.isEmpty(email))&&!(TextUtils.isEmpty(passWord))&&!(TextUtils.isEmpty(confirmPassWord))){
                 progressBar.setVisibility(View.VISIBLE);
                 if(passWord.equals(confirmPassWord)){
                     auth.createUserWithEmailAndPassword(email,passWord).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {
                               if(task.isSuccessful()){
                                   progressBar.setVisibility(View.INVISIBLE);
                                   Intent toSetupPage = new Intent(activity_register.this,activity_setup.class);
                                   startActivity(toSetupPage);
                                   finish();
                               }else {
                                   progressBar.setVisibility(View.INVISIBLE);
                                   Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                               }
                         }
                     });

                 }else {
                     progressBar.setVisibility(View.INVISIBLE);
                     Toast.makeText(this, "Please Enter The Same Password", Toast.LENGTH_SHORT).show();
                 }
             }else {
                 Toast.makeText(this, "Please Fill All The Data", Toast.LENGTH_SHORT).show();
             }



        }
    }

    private void SentToMain() {
        Intent toMainPage = new Intent(activity_register.this,MainActivity.class);
        startActivity(toMainPage);
        finish();
    }
}
