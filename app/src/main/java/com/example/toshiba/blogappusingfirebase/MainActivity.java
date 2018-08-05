package com.example.toshiba.blogappusingfirebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar mainToolBar;
    private  FirebaseAuth auth;
    private FloatingActionButton bt_addPost;
    private  String currentUserId;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainToolBar=(Toolbar)findViewById(R.id.mainToolBar);
        bt_addPost = (FloatingActionButton)findViewById(R.id.bt_addPost);

        setSupportActionBar(mainToolBar);
        bt_addPost.setOnClickListener(this);

        auth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();


    }

    private void setSupportActionBar(Toolbar mainToolBar) {
        mainToolBar.setTitle("Blog App");
        mainToolBar.inflateMenu(R.menu.main_menu);

        mainToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.logout_menu:
                        LogOut();
                        return true;
                    case R.id.setting_menu:
                       Intent setupAccount= new Intent(MainActivity.this,activity_setup.class);
                       startActivity(setupAccount);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            ToLogIn();
        }else {
            currentUserId=auth.getCurrentUser().getUid();
            firestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                         if(task.isSuccessful()){
                             if(!task.getResult().exists()){
                                 Intent setupPage=new Intent(MainActivity.this,activity_setup.class);
                                 startActivity(setupPage);
                                 finish();

                             }else {



                             }


                         }else {

                             Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                         }
                }
            });


        }
    }

    private void LogOut() {
        auth.signOut();
        ToLogIn();
    }

    private void ToLogIn() {
        Intent logInPage=new Intent(MainActivity.this,activity_logIn.class);
        startActivity(logInPage);
        finish();
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.bt_addPost){
            Intent newPostIntent =  new Intent(MainActivity.this ,activity_newPost.class);
            startActivity(newPostIntent);
        }
    }
}
