package com.example.toshiba.blogappusingfirebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.toshiba.blogappusingfirebase.Adapter.AdapterComment;
import com.example.toshiba.blogappusingfirebase.Model.CommentsData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class activityComment extends AppCompatActivity implements View.OnClickListener {

    private EditText et_comment;
    private ImageView iv_sentComment;
    private  String post_id;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String currentUserId;
    private List<CommentsData> comments;
    private RecyclerView recyclerView;
    private AdapterComment adapterComment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
            comments=new ArrayList<>();
            firestore=FirebaseFirestore.getInstance();
            auth=FirebaseAuth.getInstance();
            currentUserId=auth.getCurrentUser().getUid();

            post_id=getIntent().getStringExtra("post_id");
            et_comment=(EditText)findViewById(R.id.et_comment);
            iv_sentComment=(ImageView)findViewById(R.id.iv_sentComment);
             recyclerView=(RecyclerView)findViewById(R.id.recycleComments);

             adapterComment=new AdapterComment(this,comments);
             recyclerView.setLayoutManager(new LinearLayoutManager(this));
             recyclerView.setHasFixedSize(true);
             recyclerView.setAdapter(adapterComment);

            iv_sentComment.setOnClickListener(this);
             fetchDataFromServer();
    }

    private void fetchDataFromServer() {
        firestore.collection("Posts/"+post_id+"/Comments").addSnapshotListener(this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                           if(!documentSnapshots.isEmpty()){
                               for (DocumentChange doc: documentSnapshots.getDocumentChanges()){
                                   if(doc.getType()==DocumentChange.Type.ADDED){
                                       CommentsData comment = doc.getDocument().toObject(CommentsData.class);
                                       comments.add(comment);
                                       adapterComment.notifyDataSetChanged();
                                   }
                               }
                           }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.iv_sentComment){
            String commentConent=et_comment.getText().toString();
            if(!TextUtils.isEmpty(commentConent)){
                saveCommentInDataBase(commentConent);
            }else {
                Toast.makeText(this,"Pleas Enter Your Comment", Toast.LENGTH_SHORT).show();
            }
            
            
        }
        
    }

    private void saveCommentInDataBase(String commentConent) {

        int time = (int) (System.currentTimeMillis());
        Timestamp tsTemp = new Timestamp(time);
        long s = tsTemp.getTime();
        String timestamp = android.text.format.DateFormat.format("dd/mm/yyyy",new Date(s)).toString();

        Map<String,Object> mapComment = new HashMap<>();


        mapComment.put("comment",commentConent);
        mapComment.put("from",currentUserId);
        mapComment.put("timeStamp",timestamp);

        firestore.collection("Posts/"+post_id+"/Comments").add(mapComment).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(activityComment.this, "Error To Posting Comment", Toast.LENGTH_SHORT).show();
                }else {
                    et_comment.setText("");
                }
            }
        });

    }

}
