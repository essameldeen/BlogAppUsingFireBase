package com.example.toshiba.blogappusingfirebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class activity_logIn extends AppCompatActivity implements View.OnClickListener {

    private EditText email_edit;
    private  EditText password_edit;
    private Button logIn_bt;
    private  Button register_bt;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
         logIn_bt=(Button)findViewById(R.id.bt_login);
          register_bt=(Button)findViewById(R.id.bt_register);
          progressBar=(ProgressBar)findViewById(R.id.login_progress);
          logIn_bt.setOnClickListener(this);
          register_bt.setOnClickListener(this);
          auth=FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.bt_login){

            email_edit=(EditText)findViewById(R.id.et_email_logIn);
            password_edit=(EditText)findViewById(R.id.et_password_logIn);

            String email = email_edit.getText().toString();
            String passWord = password_edit.getText().toString();

            if(!(TextUtils.isEmpty(email))&&!(TextUtils.isEmpty(passWord))){
                progressBar.setVisibility(View.VISIBLE);
                auth.signInWithEmailAndPassword(email,passWord).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                                 if(task.isSuccessful()){
                                     progressBar.setVisibility(View.INVISIBLE);
                                     GoTOMainActivity();
                                 }else {
                                     progressBar.setVisibility(View.INVISIBLE);
                                     Toast.makeText(getApplicationContext(),"Failed Log In Please Try Again.",Toast.LENGTH_LONG).show();
                                 }
                    }
                });


            }else {
                Toast.makeText(getApplicationContext(),"Please Fill The Data",Toast.LENGTH_LONG).show();
            }


        }else if(view.getId()==R.id.bt_register){

            Intent registerActivity = new Intent(activity_logIn.this,activity_register.class);
            startActivity(registerActivity);
            finish();

        }
    }

    private void GoTOMainActivity() {
        Intent mainActivity = new Intent(activity_logIn.this,MainActivity.class);
        startActivity(mainActivity);
        finish();
    }
}
