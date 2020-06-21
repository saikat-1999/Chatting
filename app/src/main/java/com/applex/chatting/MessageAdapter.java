package com.applex.chatting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    public MessageAdapter(List<Messages> userMessagesList)
    {
        this.userMessagesList = userMessagesList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessageText, receiverMessageText, senderTime, receiverTime;
        //public CircleImageView receiverProfileImage;
        public ImageView messageSenderPicture, messageReceiverPicture;
        public LinearLayout sender;
        public LinearLayout receiver;

        public MessageViewHolder(@NonNull View itemView)
        {
            super(itemView);

            senderMessageText = (TextView) itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            //receiverProfileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_image);
            messageReceiverPicture = (ImageView) itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderPicture = (ImageView) itemView.findViewById(R.id.message_sender_image_view);
            senderTime = itemView.findViewById(R.id.sender_timestamp);
            receiverTime = itemView.findViewById(R.id.receiver_timestamp);

            sender = itemView.findViewById(R.id.sender);
            receiver = itemView.findViewById(R.id.receiver);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_messages_layout, parent, false);
        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position)
    {
        String messageSenderID = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
//                if (dataSnapshot.hasChild("image"))
//                {
//                    String receiverImage = dataSnapshot.child("image").getValue().toString();
//                    Picasso.get().load(receiverImage).placeholder(R.drawable.ic_baseline_person_24).into(holder.receiverProfileImage);
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


//        holder.receiverMessageText.setVisibility(View.GONE);
//        holder.receiverProfileImage.setVisibility(View.GONE);
//        holder.senderMessageText.setVisibility(View.GONE);
        holder.messageSenderPicture.setVisibility(View.GONE);
        holder.messageReceiverPicture.setVisibility(View.GONE);
        holder.sender.setVisibility(View.GONE);
        holder.receiver.setVisibility(View.GONE);

        if (fromMessageType.equals("text"))
        {
            if (fromUserID.equals(messageSenderID))
            {
                holder.sender.setVisibility(View.VISIBLE);
                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                holder.senderMessageText.setText(messages.getMessage());
                //holder.senderTime.setText(messages.getTimestamp());

            }
//            else
//            {
////dara call ese6 ekta RUN KORE DEKH
//                holder.receiver.setVisibility(View.VISIBLE);
//                //holder.receiverProfileImage.setVisibility(View.VISIBLE);
//                holder.receiverMessageText.setVisibility(View.VISIBLE);
//                holder.receiverMessageText.setText(messages.getMessage());
//                holder.receiverTime.setText(messages.getTime() + " - " + messages.getDate());
//
//            }
        }
//        else if (fromMessageType.equals("image"))
//        {
//            if (fromUserID.equals(messageSenderID))
//            {
//                holder.messageSenderPicture.setVisibility(View.VISIBLE);
//
//                Picasso.get().load(messages.getMessage()).into(holder.messageSenderPicture);
//            }
//            else
//            {
//                //holder.receiverProfileImage.setVisibility(View.VISIBLE);
//                holder.messageReceiverPicture.setVisibility(View.VISIBLE);
//
//                Picasso.get().load(messages.getMessage()).into(holder.messageReceiverPicture);
//            }
//        }
//        else if (fromMessageType.equals("pdf") || fromMessageType.equals("docx"))
//        {
//            if (fromUserID.equals(messageSenderID))
//            {
//                holder.messageSenderPicture.setVisibility(View.VISIBLE);
//
//                holder.messageSenderPicture.setBackgroundResource(R.drawable.ic_baseline_insert_drive_file_24);
//
//            }
//            else
//            {
//                //holder.receiverProfileImage.setVisibility(View.VISIBLE);
//                holder.messageReceiverPicture.setVisibility(View.VISIBLE);
//
//                holder.messageReceiverPicture.setBackgroundResource(R.drawable.ic_baseline_insert_drive_file_24);
//            }
        }

//        if (fromUserID.equals(messageSenderID))
//        {
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v)
//                {
//                    if (userMessagesList.get(position).getType().equals("pdf") || userMessagesList.get(position).getType().equals("docx"))
//                    {
//                        CharSequence options[] = new  CharSequence[]
//                                {
//                                        "Delete for me",
//                                        "Download and View This Document",
//                                        "Cancel",
//                                        "Delete for Everyone"
//                                };
//                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
//                        builder.setTitle("Delete Message?");
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which)
//                            {
//                                if (which == 0)
//                                {
//                                    deleteSentMessages(position, holder);
//                                    Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                    holder.itemView.getContext().startActivity(intent);
//                                }
//                                else if (which == 1)
//                                {
//                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
//                                    holder.itemView.getContext().startActivity(intent);
//                                }
//                                else if (which == 3)
//                                {
//                                    deleteMessageForEveryone(position, holder);
//                                    Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                    holder.itemView.getContext().startActivity(intent);
//                                }
//
//                            }
//                        });
//                        builder.show();
//                    }
//                    else if (userMessagesList.get(position).getType().equals("text"))
//                    {
//                        CharSequence options[] = new  CharSequence[]
//                                {
//                                        "Delete for me",
//                                        "Cancel",
//                                        "Delete for Everyone"
//                                };
//                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
//                        builder.setTitle("Delete Message?");
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which)
//                            {
//                                if (which == 0)
//                                {
//                                    deleteSentMessages(position, holder);
//                                    Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                    holder.itemView.getContext().startActivity(intent);
//                                }
//                                else if (which == 2)
//                                {
//                                    deleteMessageForEveryone(position, holder);
//                                    Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                    holder.itemView.getContext().startActivity(intent);
//                                }
//
//                            }
//                        });
//                        builder.show();
//                    }
//                    else if (userMessagesList.get(position).getType().equals("image"))
//                    {
//                        CharSequence options[] = new  CharSequence[]
//                                {
//                                        "Delete for me",
//                                        "View This Image",
//                                        "Cancel",
//                                        "Delete for Everyone"
//                                };
//                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
//                        builder.setTitle("Delete Message?");
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which)
//                            {
//                                if (which == 0)
//                                {
//                                    deleteSentMessages(position, holder);
//                                    Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                    holder.itemView.getContext().startActivity(intent);
//                                }
//                                else if (which == 1)
//                                {
//                                    Intent intent = new Intent(holder.itemView.getContext(), ImageViewerActivity.class);
//                                    intent.putExtra("url", userMessagesList.get(position).getMessage());
//                                    holder.itemView.getContext().startActivity(intent);
//                                }
//                                else if (which == 3)
//                                {
//                                    deleteMessageForEveryone(position, holder);
//                                    Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                    holder.itemView.getContext().startActivity(intent);
//                                }
//
//                            }
//                        });
//                        builder.show();
//                    }
//                }
//            });
//        }
//        else
//        {
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v)
//                {
//                    if (userMessagesList.get(position).getType().equals("pdf") || userMessagesList.get(position).getType().equals("docx"))
//                    {
//                        CharSequence options[] = new  CharSequence[]
//                                {
//                                        "Delete for me",
//                                        "Download and View This Document",
//                                        "Cancel"
//                                };
//                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
//                        builder.setTitle("Delete Message?");
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which)
//                            {
//                                if (which == 0)
//                                {
//                                    deleteReceivedMessages(position, holder);
//                                    Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                    holder.itemView.getContext().startActivity(intent);
//                                }
//                                else if (which == 1)
//                                {
//                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
//                                    holder.itemView.getContext().startActivity(intent);
//                                }
//
//                            }
//                        });
//                        builder.show();
//                    }
//                    else if (userMessagesList.get(position).getType().equals("text"))
//                    {
//                        CharSequence options[] = new  CharSequence[]
//                                {
//                                        "Delete for me",
//                                        "Cancel"
//                                };
//                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
//                        builder.setTitle("Delete Message?");
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which)
//                            {
//                                if (which == 0)
//                                {
//                                    deleteReceivedMessages(position, holder);
//                                    Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                    holder.itemView.getContext().startActivity(intent);
//                                }
//
//                            }
//                        });
//                        builder.show();
//                    }
//                    else if (userMessagesList.get(position).getType().equals("image"))
//                    {
//                        CharSequence options[] = new  CharSequence[]
//                                {
//                                        "Delete for me",
//                                        "View This Image",
//                                        "Cancel"
//                                };
//                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
//                        builder.setTitle("Delete Message?");
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which)
//                            {
//                                if (which == 0)
//                                {
//                                    deleteReceivedMessages(position, holder);
//                                    Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                    holder.itemView.getContext().startActivity(intent);
//                                }
//                                else if (which == 1)
//                                {
//                                    Intent intent = new Intent(holder.itemView.getContext(), ImageViewerActivity.class);
//                                    intent.putExtra("url", userMessagesList.get(position).getMessage());
//                                    holder.itemView.getContext().startActivity(intent);
//                                }
//
//                            }
//                        });
//                        builder.show();
//                    }
//                }
//            });
//        }
//    }



    @Override
    public int getItemCount()
    {
        return userMessagesList.size();
    }

//    private void deleteSentMessages(final int position , final MessageViewHolder holder)
//    {
//        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//        rootRef.child("Messages").child(userMessagesList.get(position).getFrom()).child(userMessagesList.get(position).getTo()).child(userMessagesList.get(position).getMessageID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task)
//            {
//                if (task.isSuccessful())
//                {
//                    Toast.makeText(holder.itemView.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
//                }
//                else
//                {
//                    Toast.makeText(holder.itemView.getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//    }

//    private void deleteReceivedMessages(final int position , final MessageViewHolder holder)
//    {
//        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//        rootRef.child("Messages").child(userMessagesList.get(position).getTo()).child(userMessagesList.get(position).getFrom()).child(userMessagesList.get(position).getMessageID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task)
//            {
//                if (task.isSuccessful())
//                {
//                    Toast.makeText(holder.itemView.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
//                }
//                else
//                {
//                    Toast.makeText(holder.itemView.getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//    }

//    private void deleteMessageForEveryone(final int position , final MessageViewHolder holder)
//    {
//        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//        rootRef.child("Messages").child(userMessagesList.get(position).getTo()).child(userMessagesList.get(position).getFrom()).child(userMessagesList.get(position).getMessageID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task)
//            {
//                if (task.isSuccessful())
//                {
//                    rootRef.child("Messages").child(userMessagesList.get(position).getFrom()).child(userMessagesList.get(position).getTo()).child(userMessagesList.get(position).getMessageID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task)
//                        {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(holder.itemView.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//
//                }
//                else
//                {
//                    Toast.makeText(holder.itemView.getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//    }


}
