package com.example.toshiba.blogappusingfirebase.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.toshiba.blogappusingfirebase.Model.BlogData;
import com.example.toshiba.blogappusingfirebase.Model.User;
import com.example.toshiba.blogappusingfirebase.R;
import com.example.toshiba.blogappusingfirebase.activityComment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterBlogList extends RecyclerView.Adapter<AdapterBlogList.viewHolder> {
      private List<BlogData> Blogs;
       private List<User> user;
      private Context context;
       private FirebaseFirestore firestore;
       private FirebaseAuth auth;

    public AdapterBlogList(Context context, List<BlogData> blogs, List<User> users) {
         Blogs = blogs;
        this.context=context;
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        this.user=users;

    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
             View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_post,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, final int position) {
            holder.setIsRecyclable(false);
               final String blogPostId= Blogs.get(position).BlogPostId;
               String ownerPost = Blogs.get(position).user_id;
               final String currentUserId=auth.getCurrentUser().getUid();
               holder.tv_dec.setText(Blogs.get(position).getDesc());


               if(ownerPost.equals(currentUserId)){

                   holder.bt_delete.setVisibility(View.VISIBLE);
                   holder.bt_delete.setEnabled(true);
               }



               if(Blogs.get(position).getTimeStam()!=null){
                   holder.tv_time.setText(Blogs.get(position).getTimeStam());
               }

                   holder.tv_name.setText(user.get(position).getName());
                   RequestOptions requestOptions =new RequestOptions();
                   requestOptions.placeholder(R.drawable.profile);
                   Glide.with(context).applyDefaultRequestOptions(requestOptions).load(user.get(position).getImage()).into(holder.profilePicture);


                     RequestOptions requestOptions2 =new RequestOptions();
                     requestOptions.placeholder(R.drawable.blank);

                      Glide.with(context).applyDefaultRequestOptions(requestOptions2).load(Blogs.get(position).getImage_url()).
                               thumbnail(Glide.with(context).load(Blogs.get(position).getImage_thumb()))
                              .into(holder.blogImage);

               // get like Counts
        firestore.collection("Posts/"+blogPostId+"/Likes").addSnapshotListener((Activity) context,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(!documentSnapshots.isEmpty()){
                    int count = documentSnapshots.size();
                    holder.tv_likeCount.setText(count + " Likes");

                }else {
                    holder.tv_likeCount.setText("0 " + "Likes");
                }
            }
        });

        // get comments Counts
        firestore.collection("Posts/"+blogPostId+"/Comments").addSnapshotListener((Activity) context,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(!documentSnapshots.isEmpty()){
                    int count = documentSnapshots.size();
                    holder.tv_commentCount.setText(count + " comments");

                }else {
                    holder.tv_commentCount.setText("0 " + "comments");
                }
            }
        });



        // get Likes
        firestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).addSnapshotListener((Activity)context,new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                              if(documentSnapshot.exists()){
                                  holder.iv_like.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_fav));

                              }else {
                                  holder.iv_like.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_name));

                              }
            }
        });

              //  Like Post and Save in database
        holder.iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()){
                            Map<String ,Object> like_map  = new HashMap<>();
                            // create String from TimeStamp
                            int time = (int) (System.currentTimeMillis());
                            Timestamp tsTemp = new Timestamp(time);
                            long s = tsTemp.getTime();
                            String timestamp = android.text.format.DateFormat.format("dd/mm/yyyy",new Date(s)).toString();
                            //
                            like_map.put("timeStamp",timestamp);
                            firestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).set(like_map);
                            holder.iv_like.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_fav));
                        }else {
                            firestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).delete();
                            holder.iv_like.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_name));

                        }
                    }
                });
            }
        });
       //Comment Page
        holder.iv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goCommentPage=new Intent(context,activityComment.class);
                goCommentPage.putExtra("post_id",blogPostId);
                context.startActivity(goCommentPage);
            }
        });

        //Delete Post
        holder.bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("Posts").document(blogPostId).delete();
                Blogs.remove(position);
                user.remove(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return Blogs.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
                TextView tv_dec;
                TextView tv_time;
                TextView tv_name;
                CircleImageView profilePicture;
                ImageView iv_like;
                TextView tv_likeCount;
                ImageView blogImage;
                ImageView iv_comment;
                TextView tv_commentCount;
                Button bt_delete;
           public viewHolder(View itemView) {
                  super(itemView);
                  tv_dec=(TextView)itemView.findViewById(R.id.tv_descriptionPost);
                  tv_time=(TextView)itemView.findViewById(R.id.tv_data);
                  tv_name=(TextView)itemView.findViewById(R.id.tv_name);
                  tv_likeCount=(TextView)itemView.findViewById(R.id.tv_likeCount);
                  iv_like=(ImageView)itemView.findViewById(R.id.iv_likePost);
                  profilePicture=(CircleImageView)itemView.findViewById(R.id.civ_imageProfile);
                  blogImage=(ImageView)itemView.findViewById(R.id.iv_imagePost);
                  iv_comment=(ImageView)itemView.findViewById(R.id.iv_coment);
                   bt_delete=(Button)itemView.findViewById(R.id.bt_deletePost);
                  tv_commentCount=(TextView)itemView.findViewById(R.id.tv_commentCounter);


            }
      }

}
