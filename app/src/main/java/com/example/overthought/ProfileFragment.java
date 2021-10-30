package com.example.overthought;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Adapter.MyPhotosAdapter;
import Model.Post;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    CircleImageView profile_image;
    TextView username, fullname, bio, followersNo, postsNo, followersNo_tv;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    ImageView add_post, options, followers_image;

    String profileid;

    Button editProfile;

    RecyclerView recyclerView;
    MyPhotosAdapter myPhotosAdapter;
    List<Post> postList;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        profile_image = v.findViewById(R.id.image_profile);
        username = v.findViewById(R.id.username);
        fullname = v.findViewById(R.id.fullname);
        bio = v.findViewById(R.id.bio);
        options = v.findViewById(R.id.options);
        add_post = v.findViewById(R.id.add_image);
        followersNo = v.findViewById(R.id.followersNo);
        postsNo = v.findViewById(R.id.postsNo);
        editProfile = v.findViewById(R.id.editProfile);
        followersNo_tv = v.findViewById(R.id.followersNo_tv);
        followers_image = v.findViewById(R.id.followers_image);

        recyclerView = v.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        myPhotosAdapter = new MyPhotosAdapter(getContext(), postList);
        recyclerView.setAdapter(myPhotosAdapter);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid","none");

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        Toolbar  toolbar = v.findViewById(R.id.toolbar);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), OptionsActivity.class);
                startActivity(i);
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = editProfile.getText().toString();

                if(btn.equals("Edit Profile")){
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                } else if (btn.equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);

                } else if (btn.equals("following")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getImageurl().equals("default")){
                    profile_image.setImageResource(R.drawable.profile_icon);
                } else{
                    Glide.with(ProfileFragment.this).load(user.getImageurl()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


        add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PostActivity.class);
                startActivity(i);
                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });

        userInfo();
        getFollowers();
        getPostsNo();
        myPhotos();

            if (profileid.equals(firebaseUser.getUid())){
                editProfile.setText("Edit Profile");
            } else {
                checkFollow();
                add_post.setVisibility(View.GONE);
                options.setVisibility(View.GONE);
            }

            followersNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), FollowersActivity.class);
                    i.putExtra("id", profileid);
                    i.putExtra("title", "followers");
                    startActivity(i);
                }
            });

        followers_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), FollowersActivity.class);
                i.putExtra("id", profileid);
                i.putExtra("title", "followers");
                startActivity(i);
            }
        });

        followersNo_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), FollowersActivity.class);
                i.putExtra("id", profileid);
                i.putExtra("title", "followers");
                startActivity(i);
            }
        });

        return v;
    }

    private void userInfo(){

        if (profileid != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (getContext() == null) {
                        return;
                    }

                    User user = snapshot.getValue(User.class);

                    Glide.with(getContext()).load(user.getImageurl()).into(profile_image);
                    username.setText(user.getUsername());
                    fullname.setText(user.getFullname());
                    bio.setText(user.getBio());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(profileid).exists()){
                    editProfile.setText("following");
                } else {
                    editProfile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers(){

        if (profileid != null) {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid).child("followers");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    followersNo.setText("" + snapshot.getChildrenCount());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });

        } }

    private void getPostsNo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if(post.getPublisher().equals(profileid)){
                        i++;
                    }
                }

                postsNo.setText(""+ i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void myPhotos(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if(post.getPublisher().equals(profileid)){
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                myPhotosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
