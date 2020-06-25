package com.applex.chatting;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatsFragment extends Fragment {

    private View PrivateChatsView;
    private RecyclerView chatsList;

    private CollectionReference ChatsRef;
//    private CollectionReference UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        PrivateChatsView =  inflater.inflate(R.layout.fragment_chats, container, false);
        mAuth = FirebaseAuth.getInstance();
        currentUserID =  mAuth.getCurrentUser().getUid();
        // = FirebaseFirestore.getInstance().getReference().child("Contacts").child(currentUserID);
        ChatsRef = FirebaseFirestore.getInstance().collection("Users/"+currentUserID+"/ChatRooms");

        chatsList = (RecyclerView) PrivateChatsView.findViewById(R.id.chats_list);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));


        return PrivateChatsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirestoreRecyclerOptions<ChatRoomModel> options = new FirestoreRecyclerOptions.Builder<ChatRoomModel>().setQuery(ChatsRef, ChatRoomModel.class).build();

        FirestoreRecyclerAdapter<ChatRoomModel, ChatsViewHolder> adapter = new FirestoreRecyclerAdapter<ChatRoomModel, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatsViewHolder chatsViewHolder, final int i, @NonNull final ChatRoomModel chats)
            {
//                final String usersIDs = getRef(i).getKey();
//                final String[] retImage = {"defaultimage"};

                FirebaseFirestore.getInstance().collection("Users").document(chats.getReceiverUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            UserModel userModel = task.getResult().toObject(UserModel.class);
                            if (userModel.getIsOnline() == 1)
                            {
                                chatsViewHolder.online.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                chatsViewHolder.online.setVisibility(View.GONE);
                            }

                        }
                    }
                });
                chatsViewHolder.userStatus.setText(chats.getLastMessage());
                chatsViewHolder.userName.setText(chats.getReceiver());
                SimpleDateFormat sfd = new SimpleDateFormat("hh:mm a, dd MMMM");
                String date = sfd.format(chats.getTimestamp().toDate());
                chatsViewHolder.time.setText(date);
                Picasso.get().load(chats.getReceiverDP()).placeholder(R.drawable.ic_account_circle_black_24dp).into(chatsViewHolder.profileImage);


                chatsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(),ChatActivity.class);
                        intent.putExtra("ID",chats.getRoomID());
                        intent.putExtra("Name",chats.getReceiver());
                        intent.putExtra("DP",chats.getReceiverDP());
                        intent.putExtra("Uid",chats.getReceiverUid());
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.users_display_layout, parent, false);
                return new ChatsViewHolder(view);
            }
        };

        chatsList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView profileImage;
        TextView userStatus, userName, time;
        ImageView online;


        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.users_profile_image);
            userStatus = itemView.findViewById(R.id.user_status);
            userName = itemView.findViewById(R.id.user_profile_name);
            online = itemView.findViewById(R.id.user_online_status);
            time = itemView.findViewById(R.id.time);

        }
    }

    public static String getTimeAgo(String stringtime) {
        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;
        Long time = null;
        try {
            Double a= Double.parseDouble(stringtime);
            time = Math.round(a);
        }catch (NumberFormatException e){
            System.out.println(e.getMessage());
        }

        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }
        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }
        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a min ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " mins ago";
        } else if (diff < 120 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff >= 2 * HOUR_MILLIS && diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }


}