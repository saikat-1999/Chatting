package com.applex.chatting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.applex.chatting.LinkPreview.ApplexLinkPreviewShort;
import com.applex.chatting.LinkPreview.ViewListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static java.lang.Boolean.TRUE;

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

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView senderMessageText, receiverMessageText, senderTime, receiverTime, docName;
        TextView docSenderTime, docReceiverTime;
        public ImageView messageSenderPicture, messageReceiverPicture;
        public CardView senderPicCard, recPicCard;
        LinearLayout senderDocCard, recDocCard;
        public LinearLayout send;
        public LinearLayout receive;
        ImageView seen;
        ApplexLinkPreviewShort senderLink, receiverLink;

        public MessageViewHolder(@NonNull View itemView, OnLongClickListener listener)
        {
            super(itemView);
            senderLink = itemView.findViewById(R.id.LinkPreViewSender);
            receiverLink = itemView.findViewById(R.id.LinkPreViewReceiver);
            senderMessageText = itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_text);
            senderDocCard= itemView.findViewById(R.id.senderDocCard);
            recDocCard =  itemView.findViewById(R.id.recDocCard);
            docName = itemView.findViewById(R.id.docname);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
            senderTime = itemView.findViewById(R.id.sender_timestamp);
            receiverTime = itemView.findViewById(R.id.receiver_timestamp);

            docReceiverTime = itemView.findViewById(R.id.doc_receiver_timestamp);
            docSenderTime = itemView.findViewById(R.id.doc_sender_timestamp);

            send = itemView.findViewById(R.id.send);
            receive = itemView.findViewById(R.id.receive);

            senderPicCard = itemView.findViewById(R.id.send_card_pic);
            recPicCard =  itemView.findViewById(R.id.receive_card_pic);

            seen = itemView.findViewById(R.id.seen);

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

        holder.senderLink.setVisibility(View.GONE);
        holder.receiverLink.setVisibility(View.GONE);
        holder.recDocCard.setVisibility(View.GONE);
        holder.recDocCard.setVisibility(View.GONE);
        holder.send.setVisibility(View.GONE);
        holder.receive.setVisibility(View.GONE);
        holder.senderPicCard.setVisibility(View.GONE);
        holder.recPicCard.setVisibility(View.GONE);

        if (fromMessageType.equals("text"))
        {
            if (fromUserID.matches(messageSenderID))
            {
                SimpleDateFormat sfd = new SimpleDateFormat("hh:mm a, dd MMMM");
                String date = sfd.format(messages.getTimestamp().toDate());
                holder.senderTime.setText(date);
                holder.send.setVisibility(View.VISIBLE);
                holder.senderMessageText.setVisibility(View.VISIBLE);
                if(messages.getSeen() == 0){
                    holder.seen.setVisibility(View.GONE);
                }
                else {
                    holder.seen.setVisibility(View.VISIBLE);
                }
                holder.senderMessageText.setText(messages.getMessage());
                if(holder.senderMessageText.getUrls().length>0){
                    URLSpan urlSnapItem = holder.senderMessageText.getUrls()[0];
                    String url = urlSnapItem.getURL();
                    if(url.contains("http")){
                        holder.senderLink.setVisibility(View.VISIBLE);
                        holder.senderLink.setLink(url ,new ViewListener() {
                            @Override
                            public void onSuccess(boolean status) {

                            }

                            @Override
                            public void onError(Exception e) {
                            }
                        });
                    }

                }
            }
            else
            {
                SimpleDateFormat sfd = new SimpleDateFormat("hh:mm a, dd MMMM");
                String date = sfd.format(messages.getTimestamp().toDate());
                holder.receiverTime.setText(date);
                holder.receive.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setText(messages.getMessage());
                if(holder.receiverMessageText.getUrls().length>0){
                    URLSpan urlSnapItem = holder.receiverMessageText.getUrls()[0];
                    String url = urlSnapItem.getURL();
                    if(url.contains("http")){
                        holder.receiverLink.setVisibility(View.VISIBLE);
                        holder.receiverLink.setLink(url ,new ViewListener() {
                            @Override
                            public void onSuccess(boolean status) {

                            }

                            @Override
                            public void onError(Exception e) {
                            }
                        });
                    }
                }
                //holder.receiverTime.setText(messages.getTimestamp().toDate().toString());
            }
        }
        else if (fromMessageType.equals("image"))
        {
            if (fromUserID.equals(messageSenderID))
            {
                SimpleDateFormat sfd = new SimpleDateFormat("hh:mm a, dd MMMM");
                String date = sfd.format(messages.getTimestamp().toDate());
                holder.senderTime.setText(date);
                holder.send.setVisibility(View.VISIBLE);
                holder.senderPicCard.setVisibility(View.VISIBLE);
                holder.messageSenderPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getImage()).into(holder.messageSenderPicture);

                if(messages.getMessage()!=null){
                    holder.receiverMessageText.setVisibility(View.VISIBLE);
                    holder.senderMessageText.setText(messages.getMessage());
                }
                else
                    holder.senderMessageText.setVisibility(View.GONE);

                holder.messageSenderPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(holder.messageSenderPicture.getContext(), ImageViewerActivity.class);
                        intent.putExtra("url", userMessagesList.get(position).getImage());
                        holder.messageSenderPicture.getContext().startActivity(intent);
                    }
                });

                if(messages.getSeen() == 0){
                    holder.seen.setVisibility(View.GONE);
                }
                else {
                    holder.seen.setVisibility(View.VISIBLE);
                }

            }
            else
            {
                SimpleDateFormat sfd = new SimpleDateFormat("hh:mm a, dd MMMM");
                String date = sfd.format(messages.getTimestamp().toDate());
                holder.receiverTime.setText(date);
                holder.receive.setVisibility(View.VISIBLE);
                holder.recPicCard.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getImage()).into(holder.messageReceiverPicture);
                if(messages.getMessage()!=null){
                    holder.receiverMessageText.setVisibility(View.VISIBLE);
                    holder.receiverMessageText.setText(messages.getMessage());
                }
                else
                    holder.receiverMessageText.setVisibility(View.GONE);

                holder.messageReceiverPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(holder.messageReceiverPicture.getContext(), ImageViewerActivity.class);
                        intent.putExtra("url", userMessagesList.get(position).getImage());
                        holder.messageReceiverPicture.getContext().startActivity(intent);
                    }
                });

            }
        }
        else if (fromMessageType.equals("pdf") || fromMessageType.equals("docx"))
        {
            if (fromUserID.equals(messageSenderID)) {
                SimpleDateFormat sfd = new SimpleDateFormat("hh:mm a, dd MMMM");
                String date = sfd.format(messages.getTimestamp().toDate());
                holder.docSenderTime.setText(date);
                holder.senderDocCard.setVisibility(View.VISIBLE);

                holder.senderDocCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getDocument()));
                        holder.itemView.getContext().startActivity(intent);
                    }
                });

                if(messages.getSeen() == 0){
                    holder.seen.setVisibility(View.GONE);
                }
                else {
                    holder.seen.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                SimpleDateFormat sfd = new SimpleDateFormat("hh:mm a, dd MMMM");
                String date = sfd.format(messages.getTimestamp().toDate());
                holder.docReceiverTime.setText(date);
                holder.recDocCard.setVisibility(View.VISIBLE);

                holder.recDocCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getDocument()));
                        holder.recDocCard.getContext().startActivity(intent);
                    }
                });
            }
        }

   }



    @Override
    public int getItemCount()
    {
        return userMessagesList.size();
    }

}
