package com.p.applicationlikeinstagram.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.p.applicationlikeinstagram.Adapter.PhotoAdapter;
import com.p.applicationlikeinstagram.Adapter.PostAdapter;
import com.p.applicationlikeinstagram.EditProfileActivity;
import com.p.applicationlikeinstagram.Model.Post;
import com.p.applicationlikeinstagram.Model.User;
import com.p.applicationlikeinstagram.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private RecyclerView recyclerViewSaves;
    private PhotoAdapter postAdapterSaves;
    private List<Post> mSavedPosts;

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<Post> myPhotoList;

    private CircleImageView civImageProfile;
    private ImageView ivOptions, ivMyPictures, ivSavedPictures;
    private TextView txtPosts, txtFollowers, txtFollowing, txtFullName, txtBio, txtUsername;
    private Button btEditProfile;

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
        btEditProfile = view.findViewById(R.id.edit_profile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId", "none");
        if (data.equals("none")) {
            profileId = firebaseUser.getUid();
        } else {
            profileId = data;
        }

        recyclerView = view.findViewById(R.id.recycler_view_pictures);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        myPhotoList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), myPhotoList);
        recyclerView.setAdapter(photoAdapter);

        recyclerViewSaves = view.findViewById(R.id.recycler_view_saved);
        recyclerViewSaves.setHasFixedSize(true);
        recyclerViewSaves.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mSavedPosts = new ArrayList<>();
        postAdapterSaves = new PhotoAdapter(getContext(), mSavedPosts);
        recyclerViewSaves.setAdapter(postAdapterSaves);

        userInfo();
        getFollowersAndFollowingCount();
        getPostsCount();

        myPhotos();
        getSavedPosts();

        if (profileId.equals(firebaseUser.getUid())) {
            btEditProfile.setText("Edit Profile");
        } else {
            checkFollowingStatus();
        }

        btEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText = btEditProfile.getText().toString();
                if (btnText.equals("Edit Profile")) {
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                } else if (btnText.equals("follow")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(firebaseUser.getUid()).child("following").child(profileId)
                            .setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);
                } else if (btnText.equals("following")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(firebaseUser.getUid()).child("following").child(profileId)
                            .removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        recyclerView.setVisibility(View.VISIBLE);
        recyclerViewSaves.setVisibility(View.GONE);

        ivMyPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewSaves.setVisibility(View.GONE);
            }
        });

        ivSavedPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerViewSaves.setVisibility(View.VISIBLE);
            }
        });
        return view;
    }

    private void getSavedPosts() {
        List<String> savedPostsIds = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            savedPostsIds.add(dataSnapshot.getKey());
                        }

                        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                mSavedPosts.clear();

                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Post post = dataSnapshot.getValue(Post.class);
                                    for (String postsId : savedPostsIds) {
                                        if (post.getPostId().equals(postsId)) {
                                            mSavedPosts.add(post);
                                        }
                                    }
                                }
                                postAdapterSaves.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void myPhotos() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myPhotoList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);

                    if (post.getPublisher().equals(profileId)) {
                        myPhotoList.add(post);
                    }
                }
                Collections.reverse(myPhotoList);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollowingStatus() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                .child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileId).exists()) {
                    btEditProfile.setText("following");
                } else {
                    btEditProfile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPostsCount() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)) {
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
                txtFollowers.setText(snapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtFollowing.setText(snapshot.getChildrenCount() + "");
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
                        if (user.getImageurl().equals("default")) {
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