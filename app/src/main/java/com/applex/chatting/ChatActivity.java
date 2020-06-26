package com.applex.chatting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.type.Date;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String messageReceiverID,messageReceiverName,messageReceiverImage,messageSenderID,ts;
    private TextView userName,userLastSeen;
    private CircleImageView userImage;
    private Toolbar chattoolbar;
    private ImageButton SendMessageButton, SendFilesButton;
    private EditText MessageInputText;
    private FirebaseAuth mAuth;
    private byte[] pic;
    private ImageCompressor imageCompressor;

    //private DatabaseReference RootRef;

    private final List<Messages>  messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;

    private MessageAdapter messageAdapter;

    private RecyclerView userMessagesList;

    private String saveCurrentTime, saveCurrentDate;
    private String checker = "", myUrl = "";
    private StorageTask uploadTask;
    private Uri fileUri;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();

       InitializeControllers();

        MessageInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                RoomsModel roomsModel = new RoomsModel();
                roomsModel.setTyping((Map<String, Long>) new HashMap<>().put("Saikat",1));
                roomsModel.setBlock((Map<String, Long>) new HashMap<>().put("Saikat",0));
                FirebaseFirestore.getInstance().collection("Rooms").document(getIntent().getStringExtra("ID")).set(roomsModel);
            }
        });

       userName.setText(getIntent().getStringExtra("Name"));
       Picasso.get().load(getIntent().getStringExtra("DP")).placeholder(R.drawable.ic_baseline_person_24).into(userImage);
       DisplayLastSeen();

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

        SendFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CharSequence options[] = new CharSequence[]
                        {
                               "Images",
                               "PDF Files",
                               "Ms Word Files"
                        };

                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Select the File");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (which == 0)
                        {
                            checker = "image";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent, "Select Image"), 438);
                        }
                        if (which == 1)
                        {
                            checker = "pdf";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            startActivityForResult(intent.createChooser(intent, "Select PDF File"), 438);
                        }
                        if (which == 2)
                        {
                            checker = "docx";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/msword");
                            startActivityForResult(intent.createChooser(intent, "Select Ms Word File"), 438);
                        }
                    }
                });
                builder.show();
            }
        });

        FirebaseFirestore.getInstance().collection("Rooms/"+ getIntent().getStringExtra("ID")+"/Messages")
                .orderBy("timestamp")
                .addSnapshotListener(ChatActivity.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
                    {
                        if (e!=null)
                        {
                            Log.w("TAG", "listen:error", e);
                            return;
                        }
                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges())
                            {
                                switch (dc.getType()) {
                                    case ADDED:
                                        Messages messages = dc.getDocument().toObject(Messages.class);
                                        messages.setDocID(dc.getDocument().getId());
                                        FirebaseFirestore.getInstance().collection("Rooms")
                                                .document(getIntent().getStringExtra("ID"))
                                                .collection("Messages")
                                                .document(messages.getDocID())
                                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task)
                                            {
                                                Messages messages1 = task.getResult().toObject(Messages.class);
                                                if (!messages1.getFromUid().matches(mAuth.getUid()) && messages1.getSeen() == 0)
                                                {
                                                    FirebaseFirestore.getInstance().collection("Rooms")
                                                            .document(getIntent().getStringExtra("ID"))
                                                            .collection("Messages")
                                                            .document(messages.getDocID())
                                                            .update("seen",1);
                                                }
                                            }
                                        });
                                        messagesList.add(messages);
                                        messageAdapter.notifyDataSetChanged();
                                        userMessagesList.clearOnScrollListeners();
                                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                                        break;
                                    case MODIFIED:
                                        messageAdapter.notifyDataSetChanged();
                                        break;
                                    case REMOVED:
                                        Messages messagesDel = dc.getDocument().toObject(Messages.class);
                                        messagesDel.setDocID(dc.getDocument().getId());
                                        messagesList.remove(messagesDel);
                                        messageAdapter.notifyDataSetChanged();
                                        break;

                                }
                            }
                    }
                });

        if(getIntent().getStringExtra("fromChatImageView")!=null){

            loadingBar = new ProgressDialog(ChatActivity.this);
            loadingBar.setTitle("Sending File");
            loadingBar.setMessage("Please wait, we are sending that file...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");

            Long tsLong = System.currentTimeMillis();
            String ts = tsLong.toString();

            pic = getIntent().getByteArrayExtra("pic");

//            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            final StorageReference reference = storageReference.child(ts + "." + "jpg");
            reference.putBytes(pic)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(uri -> {
                                Uri downloadUri = uri;
                                String generatedFilePath = downloadUri.toString();
                                Messages messages= new Messages();
                                if(getIntent().getStringExtra("text")!=null){

                                    messages.setImage(generatedFilePath);
                                    messages.setMessage(getIntent().getStringExtra("text"));
                                    messages.setFromUid(FirebaseAuth.getInstance().getUid());
                                    messages.setSeen(0);
                                    messages.setType("image");
                                }
                                else{

                                    messages.setImage(generatedFilePath);
                                    messages.setFromUid(FirebaseAuth.getInstance().getUid());
                                    messages.setSeen(0);
                                    messages.setType("image");
                                }


                                FirebaseFirestore.getInstance().collection("Rooms/" + getIntent().getStringExtra("ID") + "/Messages/").document()
                                        .set(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            loadingBar.dismiss();
                                            Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();

                                        } else {
                                            loadingBar.dismiss();
                                            Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();

                                        }

                                    }

                                });

                            });
                        }
                    });
        }

    }

    @SuppressLint("WrongViewCast")
    private void InitializeControllers() {

        chattoolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(chattoolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView =  layoutInflater.inflate(R.layout.custom_chat_bar,null);
        actionBar.setCustomView(actionBarView);

        userImage = findViewById(R.id.custom_profile_IMAGE);
        userName = findViewById(R.id.custom_profile_name);
        userLastSeen = findViewById(R.id.custom_user_last_seen);
        SendMessageButton = findViewById(R.id.send_message_btn);
        SendFilesButton = findViewById(R.id.send_files_btn);
        MessageInputText = findViewById(R.id.input_message);

        messageAdapter = new MessageAdapter(messagesList, getIntent().getStringExtra("ID"));
        userMessagesList = (RecyclerView) findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);

        messageAdapter.onLongClickListener((position) ->
        {
            FirebaseFirestore.getInstance().document("Rooms/" + getIntent().getStringExtra("ID")+"/Messages/" + messagesList.get(position).getDocID())
                    .delete();

            messagesList.remove(position);
            messageAdapter.notifyItemRemoved(position);
        });

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());
        loadingBar = new ProgressDialog(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 438 && resultCode == RESULT_OK && data!= null && data.getData()!= null)
        {
            
            fileUri = data.getData();


            if (!checker.equals("image"))
            {
                loadingBar.setTitle("Sending File");
                loadingBar.setMessage("Please wait, we are sending that file...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Chat/"+getIntent().getStringExtra("ID")+"/Images");
                Long tsLong = System.currentTimeMillis();
                ts = tsLong.toString();

                final StorageReference filePath = storageReference.child(ts + "." + checker);

                filePath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri)
                            {
                                String downloadUrl = uri.toString();
                                Messages messages = new Messages();
                                messages.setDocument(downloadUrl);
                                messages.setFromUid(FirebaseAuth.getInstance().getUid());
                                messages.setSeen(0);
                                messages.setType(checker);
                                FirebaseFirestore.getInstance().collection("Rooms/" + getIntent().getStringExtra("ID") + "/Messages/").document()
                                        .set(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            loadingBar.dismiss();
                                            Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                                        } else {
                                            loadingBar.dismiss();
                                            Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                        }
                                        MessageInputText.setText("");

                                    }
                                });

                                loadingBar.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                loadingBar.dismiss();
                                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot)
                    {
                        double p = (100.0*taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        loadingBar.setMessage((int) p + " % Uploading...");
                    }
                });
            }

            else if (checker.equals("image"))
            {
                try {
                    fileUri = data.getData();
//                    finalUri = filePath;
                    if(fileUri!=null) {

//                        postimage.setVisibility(View.VISIBLE);
                        final BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        options.inSampleSize = 2;
                        options.inJustDecodeBounds = false;
                        options.inTempStorage = new byte[16 * 1024];

                        InputStream input = this.getContentResolver().openInputStream(fileUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                        pic = baos.toByteArray();

                        imageCompressor = new ImageCompressor(pic);
                        imageCompressor.execute();
                        bitmap.recycle();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                uploadTask = filePath.putFile(fileUri);

//                uploadTask.continueWithTask(new Continuation() {
//                    @Override
//                    public Object then(@NonNull Task task) throws Exception
//                    {
//                        if (!task.isSuccessful())
//                        {
//                            throw task.getException();
//                        }
//
//                        return filePath.getDownloadUrl();
//                    }
//                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Uri> task)
//                    {
//                        if (task.isSuccessful())
//                        {
//                            Uri downloadUrl = task.getResult();
//                            myUrl = downloadUrl.toString();
//
//                            Messages messages = new Messages();
//                            messages.setMessage(myUrl);
//                            messages.setFrom(FirebaseAuth.getInstance().getUid());
//                            messages.setName("Saikat");
//                            messages.setSeen(false);
//                            messages.setType(checker);
//                            FirebaseFirestore.getInstance().collection("Rooms/"+ getIntent().getStringExtra("ID")+"/Messages/").document()
//                                    .set(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if(task.isSuccessful()){
//                                        loadingBar.dismiss();
//                                        Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
//                                    }
//                                    else{
//                                        loadingBar.dismiss();
//                                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
//                                    }
//                                    MessageInputText.setText("");
//
//                                }
//                            });
//
//                        }
//                    }
//                });
            }
            else
            {
                loadingBar.dismiss();
                Toast.makeText(this,"Nothing Selected, Error.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void DisplayLastSeen(){
        FirebaseFirestore.getInstance().collection("Users").document(getIntent().getStringExtra("Uid"))
                .addSnapshotListener(ChatActivity.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e!=null) {
                            Log.w("TAG", "listen:error", e);
                            return;
                        }
                        if(documentSnapshot != null && documentSnapshot.exists()) {
                            UserModel userModel = documentSnapshot.toObject(UserModel.class);
                            if (userModel.getIsOnline() == 1)
                            {
                                userLastSeen.setText("Online");
                            }
                            else if (userModel.getIsOnline() == 1)
                            {
                                userLastSeen.setText("Typing");
                            }
                            else {
                                SimpleDateFormat sfd = new SimpleDateFormat("hh:mm a, dd MMMM");
                                String date = sfd.format(userModel.getLastSeen().toDate());
                                userLastSeen.setText(date);
                            }
                            Picasso.get().load(userModel.getImage()).placeholder(R.drawable.ic_account_circle_black_24dp).into(userImage);

                        }
                        else {
                            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseFirestore.getInstance().document("Users/"+FirebaseAuth.getInstance().getUid()+"/ChatRooms/"+getIntent().getStringExtra("Uid"))
                .update("lastMessage", messagesList.get(messagesList.size()-1).getMessage(), "timestamp", messagesList.get(messagesList.size()-1).getTimestamp());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (messagesList.size()>0){
            FirebaseFirestore.getInstance().document("Users/"+FirebaseAuth.getInstance().getUid()+"/ChatRooms/"+getIntent().getStringExtra("Uid"))
                    .update("lastMessage", messagesList.get(messagesList.size()-1).getMessage(), "timestamp", messagesList.get(messagesList.size()-1).getTimestamp());

        }
     }

    private void SendMessage(){

        String messageText = MessageInputText.getText().toString();
        FirebaseFirestore.getInstance().collection("Users").document(mAuth.getUid()).update("istyping", 0);


        if(TextUtils.isEmpty(messageText)){
            Toast.makeText(this, "first write your message...", Toast.LENGTH_SHORT).show();
        }
        else{
            Messages messages = new Messages();
            messages.setMessage(messageText);
            messages.setFromUid(FirebaseAuth.getInstance().getUid());
            messages.setSeen(0);
            Timestamp ts = Timestamp.now();
            messages.setTimestamp(ts);
            messages.setType("text");
            FirebaseFirestore.getInstance().collection("Rooms/"+ getIntent().getStringExtra("ID")+"/Messages/").document()
                    .set(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                   MessageInputText.setText("");

                }
            });

        }
//            String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
//            String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;
//
//            DatabaseReference userMessageKeyRef = RootRef.child("Messages")
//                    .child(messageSenderID).child(messageReceiverID).push();
//
//            String messagePushID = userMessageKeyRef.getKey();
//
//            Map messageTextBody = new HashMap();
//            messageTextBody.put("message",messageText);
//            messageTextBody.put("type","text");
//            messageTextBody.put("from",messageSenderID);
//            messageTextBody.put("to",messageReceiverID);
//            messageTextBody.put("messageID",messagePushID);
//            messageTextBody.put("time",saveCurrentTime);
//            messageTextBody.put("date",saveCurrentDate);
//
//            Map messageBodyDetails = new HashMap();
//            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
//            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);
//
//            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
//                @Override
//                public void onComplete(@NonNull Task task) {
//
//                    if(task.isSuccessful()){
//                        Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
//                    }
//                    else{
//                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
//                    }
//                   MessageInputText.setText("");
//                }
//            });
//
//
//        }

    }

    class ImageCompressor extends AsyncTask<Void, Void, byte[]> {

        private final float maxHeight = 1080.0f;
        private final float maxWidth = 720.0f;
        private byte[] pic2;


        public ImageCompressor(byte[] pic) {
            this.pic2 = pic;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        public byte[] doInBackground(Void... strings) {
            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeByteArray(pic2, 0, pic2.length, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;

            float imgRatio = (float) actualWidth / (float) actualHeight;
            float maxRatio = maxWidth / maxHeight;

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
                bmp = BitmapFactory.decodeByteArray(pic2, 0, pic2.length, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 4.0f;
            float middleY = actualHeight / 4.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 4, middleY - bmp.getHeight() / 4, new Paint(Paint.FILTER_BITMAP_FLAG));

            if(bmp!=null)
            {
                bmp.recycle();
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
            byte[] by = out.toByteArray();
            return by;
        }

        @Override
        protected void onPostExecute(byte[] picCompressed) {
            if(picCompressed!= null) {
                pic = picCompressed;
//                Toast.makeText(getApplicationContext(), ""+ pic.length/1024,Toast.LENGTH_LONG).show();
//                Bitmap bitmap = BitmapFactory.decodeByteArray(picCompressed, 0 ,picCompressed.length);
                Intent i = new Intent(ChatActivity.this, ChatImageView.class);
                i.putExtra("Imageuri", pic);
                i.putExtra("ID", getIntent().getStringExtra("ID"));
                startActivity(i);
//                postimage.setImageBitmap(bitmap);
//

                }

            }
        private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {
                final int heightRatio = Math.round((float) height / (float) reqHeight);
                final int widthRatio = Math.round((float) width / (float) reqWidth);
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            }
            final float totalPixels = width * height;
            final float totalReqPixelsCap = reqWidth * reqHeight * 4;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }

            return inSampleSize;
        }

    }

}
