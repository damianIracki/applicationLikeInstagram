package com.p.applicationlikeinstagram.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.p.applicationlikeinstagram.Model.Post;
import com.p.applicationlikeinstagram.Model.User;
import com.p.applicationlikeinstagram.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private CircleImageView civImageProfile;
    private ImageView ivOptions, ivMyPictures, ivSavedPictures;
    private TextView txtPosts, txtFollowers, txtFollowing, txtFullName, txtBio, txtUsername;

    private FirebaseUser firebaseUser;
    private String profileId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        civImageProfile = view.findViewById(R.id.image_profile);
        ivOptions = view.findViewById(R.id.options);
        ivMyPictures = view.findViewById(R.id.my_pictures);
        ivSavedPictures = view.findViewById(R.id.saved_pictures);
        txtPosts = view.findViewById(R.id.posts);
        txtFollowers = view.findViewById(R.id.followers);
        txtFollowing = view.findViewById(R.id.following);
        txtFullName = view.findViewById(R.id.full_name);
        txtBio = view.findViewById(R.id.bio);
        txtUsername = view.findViewById(R.id.username);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        profileId = firebaseUser.getUid();

        userInfo();
        getFollowersAndFollowingCount();
        getPostsCount();

        return view;
    }

    private void getPostsCount() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if(post.getPublisher().equals(profileId)){
                        counter++;
                    }
                }
                txtPosts.setText(String.valueOf(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowersAndFollowingCount() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(profileId);

        reference.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtFollowers.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtFollowing.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userInfo() {
        FirebaseDatabase.getInstance().getReference().child("users").child(profileId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user.getImageurl().equals("default")){
                    civImageProfile.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Picasso.get().load(user.getImageurl()).into(civImageProfile);
                }
                txtUsername.setText(user.getUsername());
                txtFullName.setText(user.getName());
                txtBio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}