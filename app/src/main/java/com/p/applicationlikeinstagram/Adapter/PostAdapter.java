package com.p.applicationlikeinstagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.p.applicationlikeinstagram.CommentActivity;
import com.p.applicationlikeinstagram.FollowersActivity;
import com.p.applicationlikeinstagram.Fragments.PostDetailFragment;
import com.p.applicationlikeinstagram.Fragments.ProfileFragment;
import com.p.applicationlikeinstagram.Model.Post;
import com.p.applicationlikeinstagram.Model.User;
import com.p.applicationlikeinstagram.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> mPosts;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Post> mPosts) {
        this.mContext = context;
        this.mPosts = mPosts;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mPosts.get(position);

        Picasso.get().load(post.getImageUrl()).into(holder.ivPostImage);
        holder.sTxtDescription.setText(post.getDescription());

        FirebaseDatabase.getInstance().getReference().child("users").child(post.getPublisher())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user.getImageurl().equals("default")){
                    holder.ivImageProfile.setImageResource(R.mipmap.ic_launcher);
                } else{
                    Picasso.get().load(user.getImageurl()).into(holder.ivImageProfile);
                }

                holder.txtUsername.setText(user.getUsername());
                holder.txtAuthor.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        isLiked(post.getPostId(), holder.ivLike);
        noOfLikes(post.getPostId(), holder.txtNoOfLikes);
        getComments(post.getPostId(), holder.txtNoOfComments);
        isSaved(post.getPostId(), holder.ivSave);

        holder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.ivLike.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId())
                            .child(firebaseUser.getUid()).setValue(true);

                    addNotification(post.getPostId(), post.getPublisher());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("authorId",post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.txtNoOfComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("authorId",post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.ivSave.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("Saves")
                            .child(firebaseUser.getUid()).child(post.getPostId()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Saves")
                            .child(firebaseUser.getUid()).child(post.getPostId()).removeValue();
                }
            }
        });

        holder.ivImageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit()
                        .putString("profileId", post.getPublisher()).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

        holder.txtUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit()
                        .putString("profileId", post.getPublisher()).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

        holder.txtAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit()
                        .putString("profileId", post.getPublisher()).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

         holder.ivPostImage.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                         .putString("postId", post.getPostId()).apply();
                 ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                         .replace(R.id.fragment_container, new PostDetailFragment()).commit();
             }
         });

        holder.txtNoOfLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id", post.getPublisher());
                intent.putExtra("title", "likes");
                mContext.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView ivImageProfile, ivPostImage, ivLike, ivComment, ivSave, ivMore;
        public TextView txtUsername, txtNoOfLikes, txtAuthor, txtNoOfComments;
        SocialTextView sTxtDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivImageProfile = itemView.findViewById(R.id.image_profile);
            ivComment = itemView.findViewById(R.id.comment);
            ivLike = itemView.findViewById(R.id.like);
            ivMore = itemView.findViewById(R.id.more);
            ivPostImage = itemView.findViewById(R.id.post_image);
            ivSave = itemView.findViewById(R.id.save);

            txtUsername = itemView.findViewById(R.id.username);
            txtNoOfComments = itemView.findViewById(R.id.no_of_comments);
            txtAuthor = itemView.findViewById(R.id.author);
            txtNoOfLikes = itemView.findViewById(R.id.no_of_likes);

            sTxtDescription = itemView.findViewById(R.id.description);
        }
    }

    private void isSaved(String postId, ImageView imageView){
        FirebaseDatabase.getInstance().getReference().child("Saves").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postId).exists()){
                    imageView.setImageResource(R.drawable.ic_save_black);
                    imageView.setTag("saved");
                } else {
                    imageView.setImageResource(R.drawable.ic_save);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isLiked(String postId, ImageView imageView){
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void noOfLikes (String postId, TextView textView){
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textView.setText(snapshot.getChildrenCount() + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getComments(String postId, TextView textView){
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textView.setText("View All " + snapshot.getChildrenCount() + " comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addNotification(String postId, String publisherId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", publisherId);
        map.put("text", "liked your post.");
        map.put("postId", postId);
        map.put("isPost" , true);

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(firebaseUser.getUid())
                .push().setValue(map);
    }

}
