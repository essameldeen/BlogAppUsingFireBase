package com.example.toshiba.blogappusingfirebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.toshiba.blogappusingfirebase.Fragment.accountFragment;
import com.example.toshiba.blogappusingfirebase.Fragment.homeFragment;
import com.example.toshiba.blogappusingfirebase.Fragment.notificationFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemReselectedListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private  Toolbar mainToolBar;
    private  FirebaseAuth auth;
    private  FloatingActionButton bt_addPost;
    private  String currentUserId;
    private  FirebaseFirestore firestore;
    private BottomNavigationView  bt_navigationView;
    private homeFragment home;
    private notificationFragment notification;
    private accountFragment account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainToolBar=(Toolbar)findViewById(R.id.mainToolBar);
        bt_addPost = (FloatingActionButton)findViewById(R.id.bt_addPost);
        bt_navigationView=(BottomNavigationView)findViewById(R.id.mainNavigation);

        auth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        //Fragments
         home = new homeFragment();
         notification=new notificationFragment();
         account=new accountFragment();
         //
         replaceFrgment(home);
         //
        setSupportActionBar(mainToolBar);
        bt_addPost.setOnClickListener(this);
        bt_navigationView.setOnNavigationItemSelectedListener(this);


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
    private void replaceFrgment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainContainer,fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.home_menu){
            replaceFrgment(home);
            return  true;
        }else if(item.getItemId()==R.id.notifiaction_menu){
            replaceFrgment(notification);
            return  true;
        }else if(item.getItemId()==R.id.account_menu){
            replaceFrgment(account);
            return  true;
        }
        return false;
    }
}
