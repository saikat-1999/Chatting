package com.applex.chatting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private String ID;
    private MessageAdapter.OnLongClickListener Listener;

    public MessageAdapter(List<Messages> userMessagesList, String id)
    {
        this.userMessagesList = userMessagesList;
        this.ID = id;
    }

    public interface OnLongClickListener {
        void onLongClickListener(final int position);
    }

    public void onLongClickListener(MessageAdapter.OnLongClickListener onLongClickListener) {
        Listener= onLongClickListener;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessageText, receiverMessageText, senderTime, receiverTime;
        //public CircleImageView receiverProfileImage;
        public ImageView messageSenderPicture, messageReceiverPicture;
        public LinearLayout send;
        public LinearLayout receive;

        public MessageViewHolder(@NonNull View itemView, OnLongClickListener listener)
        {
            super(itemView);

            senderMessageText = (TextView) itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            //receiverProfileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_image);
            messageReceiverPicture = (ImageView) itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderPicture = (ImageView) itemView.findViewById(R.id.message_sender_image_view);
            senderTime = itemView.findViewById(R.id.sender_timestamp);
            receiverTime = itemView.findViewById(R.id.receiver_timestamp);

            send = itemView.findViewById(R.id.send);
            receive = itemView.findViewById(R.id.receive);

            send.setOnLongClickListener(v -> {
                if(listener != null){
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION ){
                        listener.onLongClickListener(position);
                    }
                }
                return true;
            });
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_messages_layout, parent, false);
        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(view, Listener);
    }



    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position)
    {
        String messageSenderID = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);

        String fromUserID = messages.getFromUid();
        String fromMessageType = messages.getType();

//        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);
//
//        usersRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
//            {
////                if (dataSnapshot.hasChild("image"))
////                {
////                    String receiverImage = dataSnapshot.child("image").getValue().toString();
////                    Picasso.get().load(receiverImage).placeholder(R.drawable.ic_baseline_person_24).into(holder.receiverProfileImage);
////                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


//        holder.receiverMessageText.setVisibility(View.GONE);
//        holder.receiverProfileImage.setVisibility(View.GONE);
//        holder.senderMessageText.setVisibility(View.GONE);
        holder.messageSenderPicture.setVisibility(View.GONE);
        holder.messageReceiverPicture.setVisibility(View.GONE);
        holder.send.setVisibility(View.GONE);
        holder.receive.setVisibility(View.GONE);

        if (fromMessageType.equals("text"))
        {
            if (fromUserID.matches(messageSenderID))
            {
                holder.send.setVisibility(View.VISIBLE);
                holder.senderMessageText.setVisibility(View.VISIBLE);
                //holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                holder.senderMessageText.setText(messages.getMessage());
                //holder.senderTime.setText(messages.getTimestamp().toDate().toString());

            }
            else
            {
//dara call ese6 ekta RUN KORE DEKH
                holder.receive.setVisibility(View.VISIBLE);
                //holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setText(messages.getMessage());
                //holder.receiverTime.setText(messages.getTimestamp().toDate().toString());

            }
        }
        else if (fromMessageType.equals("image"))
        {
            if (fromUserID.equals(messageSenderID))
            {
                holder.messageSenderPicture.setVisibility(View.VISIBLE);

                Picasso.get().load(messages.getImage()).into(holder.messageSenderPicture);
            }
            else
            {
                //holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);

                Picasso.get().load(messages.getImage()).into(holder.messageReceiverPicture);
            }
        }
        else if (fromMessageType.equals("pdf") || fromMessageType.equals("docx"))
        {
            if (fromUserID.equals(messageSenderID))
            {
                holder.messageSenderPicture.setVisibility(View.VISIBLE);

                holder.messageSenderPicture.setBackgroundResource(R.drawable.ic_baseline_insert_drive_file_24);

            }
            else
            {
                //holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);

                holder.messageReceiverPicture.setBackgroundResource(R.drawable.ic_baseline_insert_drive_file_24);
            }
        }

        if (fromUserID.equals(messageSenderID))
        {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (userMessagesList.get(position).getType().equals("pdf") || userMessagesList.get(position).getType().equals("docx"))
                    {
                        CharSequence options[] = new  CharSequence[]
                                {
                                        "Download and View This Document",
                                        "Delete for Everyone"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        //builder.setTitle("Delete Message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if (which == 0)
                                {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getDocument()));
                                    holder.itemView.getContext().startActivity(intent);
                                }
                                else if (which == 1)
                                {
                                    deleteMessageForEveryone(position, holder);
                                }

                            }
                        });
                        builder.show();
                    }
                    else if (userMessagesList.get(position).getType().equals("text"))
                    {
                        CharSequence options[] = new  CharSequence[]
                                {
                                        "Delete for Everyone"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        //builder.setTitle("Delete Message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if (which == 0)
                                {
                                    deleteMessageForEveryone(position, holder);
                                }

                            }
                        });
                        builder.show();
                    }
                    else if (userMessagesList.get(position).getType().equals("image"))
                    {
                        CharSequence options[] = new  CharSequence[]
                                {
                                        "View This Image",
                                        "Delete for Everyone"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        //builder.setTitle("Delete Message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if (which == 0)
                                {
                                    Intent intent = new Intent(holder.itemView.getContext(), ImageViewerActivity.class);
                                    intent.putExtra("url", userMessagesList.get(position).getImage());
                                    holder.itemView.getContext().startActivity(intent);
                                }
                                else if (which == 1)
                                {
                                    deleteMessageForEveryone(position, holder);

                                }

                            }
                        });
                        builder.show();
                    }
                }
            });
        }
        else
        {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (userMessagesList.get(position).getType().equals("pdf") || userMessagesList.get(position).getType().equals("docx"))
                    {
                        CharSequence options[] = new  CharSequence[]
                                {
                                        "Download and View This Document"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        //builder.setTitle("Delete Message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if (which == 0)
                                {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getDocument()));
                                    holder.itemView.getContext().startActivity(intent);
                                }

                            }
                        });
                        builder.show();
                    }
//                    else if (userMessagesList.get(position).getType().equals("text"))
//                    {
//                        CharSequence options[] = new  CharSequence[]
//                                {
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
//                                    deleteMessageForEveryone(position, holder);
//                                    Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                    holder.itemView.getContext().startActivity(intent);
//                                }
//
//                            }
//                        });
//                        builder.show();
//                    }
                    else if (userMessagesList.get(position).getType().equals("image"))
                    {
                        CharSequence options[] = new  CharSequence[]
                                {
                                        "View This Image",
                                        "Download Image"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        //builder.setTitle("Delete Message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                                if (which == 0)
                                {
                                    Intent intent = new Intent(holder.itemView.getContext(), ImageViewerActivity.class);
                                    intent.putExtra("url", userMessagesList.get(position).getImage());
                                    holder.itemView.getContext().startActivity(intent);
                                }

                                else if (which == 1)
                                {
                                    //Save to Device from Campus24
                                }

                            }
                        });
                        builder.show();
                    }
                }
            });
        }
   }



    @Override
    public int getItemCount()
    {
        return userMessagesList.size();
    }


    private void deleteMessageForEveryone(final int position , final MessageViewHolder holder)
    {
        FirebaseFirestore.getInstance().document("Rooms/"+ ID+"/Messages/"+userMessagesList.get(position).getDocID())
                .delete();
    }


}
