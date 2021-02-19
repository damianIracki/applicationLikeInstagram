package com.p.applicationlikeinstagram.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.p.applicationlikeinstagram.Fragments.PostDetailFragment;
import com.p.applicationlikeinstagram.Fragments.ProfileFragment;
import com.p.applicationlikeinstagram.Model.Notification;
import com.p.applicationlikeinstagram.Model.Post;
import com.p.applicationlikeinstagram.Model.User;
import com.p.applicationlikeinstagram.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mContext;
    private List<Notification> mNotifications;

    private FirebaseUser fUser;

    public NotificationAdapter(Context mContext, List<Notification> mNotifications) {
        this.mContext = mContext;
        this.mNotifications = mNotifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Notification notification = mNotifications.get(position);

        getUser(holder.ivImageProfile, holder.txtUsername, notification.getUserId());

        holder.txtComment.setText(notification.getText());

        if(notification.getIsPost()){
            holder.ivPostImage.setVisibility(View.VISIBLE);
            getPostImage(holder.ivPostImage, notification.getPostId());
        } else {
            holder.ivPostImage.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notification.getIsPost()){
                    mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                            .putString("postId", notification.getPostId()).apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new PostDetailFragment()).commit();
                } else {
                    mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit()
                            .putString("profileId", notification.getUserId()).apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ProfileFragment()).commit();
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImageProfile, ivPostImage;
        private TextView txtUsername, txtComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivImageProfile = itemView.findViewById(R.id.image_profile);
            ivPostImage = itemView.findViewById(R.id.post_image);
            txtUsername = itemView.findViewById(R.id.username);
            txtComment = itemView.findViewById(R.id.comment);
        }
    }


    private void getPostImage(ImageView imageView, String postId) {
        FirebaseDatabase.getInstance().getReference().child("Posts").child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Post post = snapshot.getValue(Post.class);
                        Picasso.get().load(post.getImageUrl()).placeholder(R.mipmap.ic_launcher)
                                .into(imageView);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void getUser(ImageView ivImageProfile, TextView txtUsername, String userId) {

        FirebaseDatabase.getInstance().getReference().child("users").child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user.getImageurl().equals("default")) {
                            ivImageProfile.setImageResource(R.mipmap.ic_launcher);
                        } else {
                            Picasso.get().load(user.getImageurl()).into(ivImageProfile);
                        }
                        txtUsername.setText(user.getUsername());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}
