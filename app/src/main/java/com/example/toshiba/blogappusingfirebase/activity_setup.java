package com.example.toshiba.blogappusingfirebase;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class activity_setup extends AppCompatActivity implements View.OnClickListener {
    private static  int PICK_IMAGE = 1;
    private  Toolbar toolbar;
    private  EditText et_name;
    private CircleImageView myImage;
    private Button bt_setup;
    private String user_id;
    private ProgressBar progressBar;
    private Uri imageUrl;
    private FirebaseAuth auth;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private  boolean isChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
         imageUrl=null;
         auth=FirebaseAuth.getInstance();
         storageReference= FirebaseStorage.getInstance().getReference();
         firestore = FirebaseFirestore.getInstance();
        //
         progressBar=(ProgressBar)findViewById(R.id.setup_progress);
         et_name=(EditText)findViewById(R.id.et_name_setup);
         myImage = (CircleImageView)findViewById(R.id.profile_image_setup);
         bt_setup=(Button)findViewById(R.id.bt_setup);

         toolbar=(Toolbar)findViewById(R.id.toolbar_setup);
         setSupportActionBar(toolbar);

         bt_setup.setOnClickListener(this);
         myImage.setOnClickListener(this);

         user_id=auth.getCurrentUser().getUid();

         progressBar.setVisibility(View.VISIBLE);
         bt_setup.setEnabled(false);
         firestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
             @Override
             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                     if(task.isSuccessful()){
                         if(task.getResult().exists()){
                             String existName = task.getResult().getString("name");
                             String image_uri = task.getResult().getString("image");
                             Glide.with(activity_setup.this).load(image_uri).into(myImage);
                             et_name.setText(existName);
                             imageUrl=Uri.parse(image_uri);

                         }

                     }else {
                         Toast.makeText(activity_setup.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                     }
                 progressBar.setVisibility(View.INVISIBLE);
                 bt_setup.setEnabled(true);
             }
         });


    }

    private void setSupportActionBar(Toolbar toolbar) {
        toolbar.setTitle("Account Setup");
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.profile_image_setup){
            if(ContextCompat.checkSelfPermission(activity_setup.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity_setup.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

            }else {
                //  Get Image From Your device
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(activity_setup.this);
            }
        }else if(view.getId()==R.id.bt_setup){
            if(isChange){
                final String name = et_name.getText().toString();
                if (!(TextUtils.isEmpty(name)) && imageUrl != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    user_id = auth.getCurrentUser().getUid();
                    StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");
                    image_path.putFile(imageUrl).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                StoreInFireBasse(task);
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "Pleas Enter your Name , Select Image", Toast.LENGTH_SHORT).show();
                }
            }else {
                StoreInFireBasse(null);
            }

        }
    }

    private void StoreInFireBasse(Task<UploadTask.TaskSnapshot> task) {
        final String name = et_name.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        Uri uriDownloadImage ;
        if(task!=null){
            uriDownloadImage=task.getResult().getDownloadUrl();
        }else {
            uriDownloadImage=imageUrl;
        }

        Map<String ,Object> user_map = new HashMap<>();
        user_map.put("name",name);
        user_map.put("image",uriDownloadImage.toString());
        firestore.collection("Users").document(user_id).set(user_map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.INVISIBLE);
                    Intent mainPage= new Intent(activity_setup.this,MainActivity.class);
                    startActivity(mainPage);
                    finish();
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(activity_setup.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUrl= result.getUri();
                myImage.setImageURI(imageUrl);
                isChange=true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}
