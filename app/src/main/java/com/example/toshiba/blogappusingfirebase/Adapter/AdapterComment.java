package com.example.toshiba.blogappusingfirebase.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.toshiba.blogappusingfirebase.Model.CommentsData;
import com.example.toshiba.blogappusingfirebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.viewHolder> {
    private Context context;
    private List<CommentsData> comments;
    private FirebaseFirestore firestore;
    public AdapterComment(Context context, List<CommentsData> comments) {
        this.context = context;
        firestore=FirebaseFirestore.getInstance();
        this.comments = comments;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlecomment,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, int position) {
        holder.comment.setText(comments.get(position).getComment());
        firestore.collection("Users").document(comments.get(position).getFrom()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String name=task.getResult().getString("name");
                    String image=task.getResult().getString("image");
                    holder.name.setText(name);
                    RequestOptions requestOptions =new RequestOptions();
                    requestOptions.placeholder(R.drawable.profile);
                    Glide.with(context).applyDefaultRequestOptions(requestOptions).load(image).into(holder.profileImage);

                }else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class  viewHolder extends  RecyclerView.ViewHolder{
        TextView name;
        TextView comment;
        CircleImageView profileImage;


        public viewHolder(View itemView) {
            super(itemView);
            name=(TextView)itemView.findViewById(R.id.tv_commentName);
            comment=(TextView)itemView.findViewById(R.id.tc_commentContent);
            profileImage=(CircleImageView)itemView.findViewById(R.id.crv_comment);
        }
    }

}
