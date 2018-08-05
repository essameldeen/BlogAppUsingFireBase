package com.example.toshiba.blogappusingfirebase;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class activity_newPost extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private ImageView iv_postImage;
    private EditText et_descriptionPost;
    private Button bt_post;
    private ProgressBar progress;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private String curentUserId;
    private Uri imageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        firestore=FirebaseFirestore.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        auth=FirebaseAuth.getInstance();
        curentUserId=auth.getCurrentUser().getUid();

        //
        toolbar=(Toolbar)findViewById(R.id.toolbar_newPost);
        progress=(ProgressBar)findViewById(R.id.post_progress);
        iv_postImage=(ImageView)findViewById(R.id.iv_imgePost);
        et_descriptionPost=(EditText)findViewById(R.id.et_descriptionPost);
        bt_post=(Button)findViewById(R.id.bt_submitPost);
        iv_postImage.setOnClickListener(this);
        bt_post.setOnClickListener(this);

        setSupportActionBar(toolbar);

    }

    private void setSupportActionBar(Toolbar toolbar) {
    toolbar.setDuplicateParentStateEnabled(true);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.bt_submitPost){
            final String description = et_descriptionPost.getText().toString();
            if(!(TextUtils.isEmpty(description))&& imageUrl!=null){
                progress.setVisibility(View.VISIBLE);
               String randomName= FieldValue.serverTimestamp().toString();
                StorageReference filePath=storageReference.child("Post_images").child(randomName+".jpg");
                filePath.putFile(imageUrl).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            String downloadUrl=task.getResult().getDownloadUrl().toString();
                            Map<String,Object> postMap=new HashMap<>();
                            postMap.put("image_url",downloadUrl);
                            postMap.put("desc",description);
                            postMap.put("user_id",curentUserId);
                            postMap.put("time",FieldValue.serverTimestamp().toString());
                            firestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if(task.isSuccessful()){
                                            progress.setVisibility(View.INVISIBLE);
                                            Toast.makeText(activity_newPost.this, "Post Added", Toast.LENGTH_SHORT).show();
                                            Intent mainPage=new Intent(activity_newPost.this,MainActivity.class);
                                            startActivity(mainPage);
                                            finish();

                                        }else {
                                            progress.setVisibility(View.INVISIBLE);
                                            Toast.makeText(activity_newPost.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                }
                            });
                        }else {
                            progress.setVisibility(View.INVISIBLE);
                            Toast.makeText(activity_newPost.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }
                });




            }else {
                Toast.makeText(this, "Please Enter The Description and Select Picture", Toast.LENGTH_SHORT).show();
            }


        }else if(view.getId()==R.id.iv_imgePost){

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMinCropResultSize(512,512)
                    .setAspectRatio(1,1)
                    .start(activity_newPost.this);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUrl= result.getUri();
                iv_postImage.setImageURI(imageUrl);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}
