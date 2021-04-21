package com.example.meetup;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.meetup.R.id.nav_profile;


public class MainActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;
    private Toolbar mToolbar;

    private CircleImageView NavProfileImage;
    private TextView NavProfileUserName;
    private ImageButton AddNewPostButton;
    private  CircleImageView ProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, PostsRef;

    String currentUserID;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
            currentUserID = mFirebaseUser.getUid();
            NavProfileImage=(CircleImageView) findViewById(R.id.profile_image);
            NavProfileUserName =(TextView)findViewById(R.id.user_name) ;


            UsersRef = FirebaseDatabase.getInstance("https://meet-up-5c59e-default-rtdb.firebaseio.com/").getReference().child("Users");
            //PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");


            mToolbar = findViewById(R.id.main_page_toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("Home");


            // AddNewPostButton = (ImageButton) findViewById(R.id.add_new_post_button);


            drawerLayout = findViewById(R.id.drawable_layout);
            actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
            drawerLayout.addDrawerListener(actionBarDrawerToggle);
            ProfileImage=(CircleImageView) findViewById(R.id.setup_profile_image);
            actionBarDrawerToggle.syncState();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            navigationView  = (NavigationView) findViewById(nav_profile);


            //postList = (RecyclerView) findViewById(R.id.all_users_post_list);
//        postList.setHasFixedSize(true);
//            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//            linearLayoutManager.setReverseLayout(true);
//            linearLayoutManager.setStackFromEnd(true);
//        postList.setLayoutManager(linearLayoutManager);

//            View navView = navigationView.inflateHeaderView(R.layout.navigation_header);

            UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.hasChild("fullname")) {
                            String fullname = dataSnapshot.child("fullname").getValue().toString();
                            NavProfileUserName.setText(fullname);
                        }
                        if (dataSnapshot.hasChild("profileimage")) {
                            String image = dataSnapshot.child("profileimage").getValue().toString();
                            Picasso.with(MainActivity.this).load(image).placeholder(R.drawable.profile).into(NavProfileImage);
                        } else {
                            Toast.makeText(MainActivity.this, "Profile name do not exists...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


//            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//                @Override
//                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                    UserMenuSelector(item);
//                    return false;
//                }
//            });


//        AddNewPostButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                SendUserToPostActivity();
//            }
//        });


        // DisplayAllUsersPosts();
    }


//    private void DisplayAllUsersPosts()
//    {
//        FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter =
//                new FirebaseRecyclerAdapter<Posts, PostsViewHolder>
//                        (
//                                Posts.class,
//                                R.layout.all_posts_layout,
//                                PostsViewHolder.class,
//                                PostsRef
//                        )
//                {
//                    @Override
//                    protected void populateViewHolder(PostsViewHolder viewHolder, Posts model, int position)
//                    {
//                        viewHolder.setFullname(model.getFullname());
//                        viewHolder.setTime(model.getTime());
//                        viewHolder.setDate(model.getDate());
//                        viewHolder.setDescription(model.getDescription());
//                        viewHolder.setProfileimage(getApplicationContext(), model.getProfileimage());
//                        viewHolder.setPostimage(getApplicationContext(), model.getPostimage());
//                    }
//                };
//        postList.setAdapter(firebaseRecyclerAdapter);
//    }


//    public static class PostsViewHolder extends RecyclerView.ViewHolder
//    {
//        View mView;
//
//        public PostsViewHolder(View itemView)
//        {
//            super(itemView);
//            mView = itemView;
//        }
//
//        public void setFullname(String fullname)
//        {
//            TextView username = (TextView) mView.findViewById(R.id.post_user_name);
//            username.setText(fullname);
//        }
//
//        public void setProfileimage(Context ctx, String profileimage)
//        {
//            CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
//            Picasso.with(ctx).load(profileimage).into(image);
//        }
//
//        public void setTime(String time)
//        {
//            TextView PostTime = (TextView) mView.findViewById(R.id.post_time);
//            PostTime.setText("    " + time);
//        }
//
//        public void setDate(String date)
//        {
//            TextView PostDate = (TextView) mView.findViewById(R.id.post_date);
//            PostDate.setText("    " + date);
//        }
//
//        public void setDescription(String description)
//        {
//            TextView PostDescription = (TextView) mView.findViewById(R.id.post_description);
//            PostDescription.setText(description);
//        }
//
//        public void setPostimage(Context ctx1,  String postimage)
//        {
//            ImageView PostImage = (ImageView) mView.findViewById(R.id.post_image);
//            Picasso.with(ctx1).load(postimage).into(PostImage);
//        }
//    }


//    private void SendUserToPostActivity()
//    {
//        Intent addNewPostIntent = new Intent(MainActivity.this, PostActivity.class);
//        startActivity(addNewPostIntent);
//    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            SendUserToLoginActivity();
        } else {
            CheckUserExistence();
        }
    }


    private void CheckUserExistence() {
        final String current_user_id = mAuth.getCurrentUser().getUid();

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(current_user_id)) {
                    SendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, setup.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }


    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, login.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_post:
                //SendUserToPostActivity();
                break;

            case nav_profile:
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_friends:
                Toast.makeText(this, "Friend List", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_find:
                Toast.makeText(this, "Find Friends", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_messages:
                Toast.makeText(this, "Messages", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_logout:
                mAuth.signOut();
                SendUserToLoginActivity();
                break;
        }
    }
}