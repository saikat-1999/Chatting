package com.applex.chatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.media.CamcorderProfile.get;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView FindFriendsRecyclerList;
    private Query UsersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        UsersRef = FirebaseFirestore.getInstance().collection("Users");

        FindFriendsRecyclerList = (RecyclerView) findViewById(R.id.find_friends_recycler_list);
        FindFriendsRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = (Toolbar) findViewById(R.id.find_friends_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>().setQuery(UsersRef, UserModel.class).build();

        FirestoreRecyclerAdapter<UserModel, FindFriendViewHolder> adapter = new FirestoreRecyclerAdapter<UserModel, FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder findFriendViewHolder, final int i, @NonNull final UserModel userModel)
            {
                findFriendViewHolder.userName.setText(userModel.getName());
                findFriendViewHolder.userStatus.setText(userModel.getStatus());
                Picasso.get().load(userModel.getImage()).placeholder(R.drawable.ic_baseline_person_24).into(findFriendViewHolder.profileImage);

                findFriendViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
//                        String visit_user_id =;
                        ChatRoomModel chatRoomModel1 = new ChatRoomModel();
                        ChatRoomModel chatRoomModel2 = new ChatRoomModel();
                        chatRoomModel1.setLastMessage("Hello");
                        chatRoomModel1.setReceiver(userModel.getName());
                        chatRoomModel1.setReceiverDP(userModel.getImage());
                        chatRoomModel1.setReceiverUid(userModel.getUid());
                        chatRoomModel1.setRoomID("abcd123");
                        chatRoomModel2.setRoomID("abcd123");
                        chatRoomModel2.setReceiver("Sarbari");
                        chatRoomModel2.setReceiverUid(FirebaseAuth.getInstance().getUid());
                        chatRoomModel2.setReceiverDP("abc");
                        chatRoomModel2.setLastMessage("hi");
                        DocumentReference doc1 = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid()).collection("ChatRooms").document();
                        DocumentReference doc2 = FirebaseFirestore.getInstance().collection("Users").document(userModel.getUid()).collection("ChatRooms").document();
                        WriteBatch batch = FirebaseFirestore.getInstance().batch();
                        batch.set(doc1, chatRoomModel1);
                        batch.set(doc2, chatRoomModel2);
//                        batch.set(FirebaseFirestore.getInstance().collection("Rooms").document("abcd123"), null);
                        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(getApplicationContext(),RandomString.getAlphaNumericString(20), Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"nikal", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
//                        Intent profileIntent = new Intent(FindFriendsActivity.this, ProfileActivity.class);
////                        profileIntent.putExtra("visit_user_id", visit_user_id);
//                        startActivity(profileIntent);

                    }
                });
            }

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                return viewHolder;
            }
        };

        FindFriendsRecyclerList.setAdapter(adapter);

        adapter.startListening();
    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userStatus;
        CircleImageView profileImage;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }

    public static class RandomString {
        static String getAlphaNumericString (int n)
        {
            String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvwxyz";
            StringBuilder sb = new StringBuilder(n);
            for (int i=0; i<n; i++)
            {
                int index = (int) (AlphaNumericString.length()*Math.random());
                sb.append(AlphaNumericString.charAt(index));
            }
            return sb.toString();
        }
    }
}