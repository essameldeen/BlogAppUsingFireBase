package com.example.toshiba.blogappusingfirebase.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.toshiba.blogappusingfirebase.Adapter.AdapterBlogList;
import com.example.toshiba.blogappusingfirebase.Model.BlogData;
import com.example.toshiba.blogappusingfirebase.Model.User;
import com.example.toshiba.blogappusingfirebase.R;
import com.example.toshiba.blogappusingfirebase.activity_logIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class homeFragment extends Fragment {
   private RecyclerView recyclerView;
   private AdapterBlogList adapterBlogList;
   private List<BlogData> blogDataList;
   private List<User>users;
   private FirebaseFirestore storage;
   private DocumentSnapshot last_blog;

    public homeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
              View view = inflater.inflate(R.layout.fragment_home, container, false);
              recyclerView = (RecyclerView)view.findViewById(R.id.blogListView);
              blogDataList=new ArrayList<>();
              users=new ArrayList<>();
              FetchDataFromServer();
              recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
              adapterBlogList=new AdapterBlogList(getContext(),blogDataList,users);
              recyclerView.setHasFixedSize(true);
              recyclerView.setAdapter(adapterBlogList);


              recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                  @Override
                  public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                      super.onScrollStateChanged(recyclerView, newState);

                      boolean load = !recyclerView.canScrollVertically(1);
                      if(load){
                          LoadMoreData();
                      }
                  }
              });

            return  view;
     }

    private void FetchDataFromServer() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            storage = FirebaseFirestore.getInstance();
            Query firstQuery = storage.collection("Posts").orderBy("timeStamp",Query.Direction.DESCENDING).limit(2);

            firstQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (documentSnapshots != null && !(documentSnapshots.isEmpty())) {

                            last_blog=documentSnapshots.getDocuments().get(documentSnapshots.size()-1);


                        for (final DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                 final String blogId=doc.getDocument().getId();
                                 String blogUserId =doc.getDocument().getString("user_id");
                                 storage.collection("Users").document(blogUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                     @Override
                                     public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                     if(task.isSuccessful()){
                                                         User user = task.getResult().toObject(User.class);
                                                         users.add(user);
                                                         BlogData blogData = doc.getDocument().toObject(BlogData.class).withId(blogId);
                                                         blogDataList.add(blogData);
                                                         adapterBlogList.notifyDataSetChanged();

                                                     }else {
                                                         Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                     }
                                     }
                                 });

                            }
                        }

                    }


                }
            });
        }
    }
      private  void LoadMoreData(){

          Query nextQuere = storage.collection("Posts").orderBy("timeStamp",Query.Direction.DESCENDING).startAfter(last_blog).limit(2);

          nextQuere.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
              @Override
              public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                  if (documentSnapshots != null && !(documentSnapshots.isEmpty())) {
                      last_blog=documentSnapshots.getDocuments().get(documentSnapshots.size()-1);
                      for (final DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                          if (doc.getType() == DocumentChange.Type.ADDED) {
                              final String blogId=doc.getDocument().getId();
                              String blogUserId =doc.getDocument().getString("user_id");
                              storage.collection("Users").document(blogUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                  @Override
                                  public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                      if(task.isSuccessful()){
                                          User user = task.getResult().toObject(User.class);
                                          users.add(user);
                                          BlogData blogData = doc.getDocument().toObject(BlogData.class).withId(blogId);
                                          blogDataList.add(blogData);
                                          adapterBlogList.notifyDataSetChanged();

                                      }else {
                                          Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                      }
                                  }
                              });


                          }
                      }
                  }


              }
          });
      }
}
