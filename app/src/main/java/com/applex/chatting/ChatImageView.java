package com.applex.chatting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class ChatImageView extends AppCompatActivity {

    ImageView imageView, crop, back;
    EditText editText;
    byte[] pic;
    FloatingActionButton send;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_image);

        imageView= findViewById(R.id.imageView);
        crop= findViewById(R.id.crop);
        editText= findViewById(R.id.edittext);
        send= findViewById(R.id.send);
        back= findViewById(R.id.back);

        byte[] byteArray = getIntent().getByteArrayExtra("Imageuri");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        imageView.setImageBitmap(bmp);

        crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity(getImageUri(getApplicationContext(), bmp))
                        .setActivityTitle("Crop Image")
                        .setAllowRotation(TRUE)
                        .setAllowCounterRotation(TRUE)
                        .setAllowFlipping(TRUE)
                        .setAspectRatio(1,1)
                        .setAutoZoomEnabled(TRUE)
                        .setMultiTouchEnabled(FALSE)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ChatImageView.this);
            }
        });

       send.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               loadingBar = new ProgressDialog(ChatImageView.this);
               loadingBar.setTitle("Sending File");
               loadingBar.setMessage("Please wait, we are sending that file...");
               loadingBar.setCanceledOnTouchOutside(false);
               loadingBar.show();

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");

                Long tsLong = System.currentTimeMillis();
                String ts = tsLong.toString();

                final StorageReference reference = storageReference.child(ts + "." + "jpg");
                reference.putBytes(pic)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                reference.getDownloadUrl().addOnSuccessListener(uri -> {
                                    Uri downloadUri = uri;
                                    String generatedFilePath = downloadUri.toString();

                                    String messageText = editText.getText().toString();
//                                    myUrl = downloadUrl.toString();

                                    Messages messages = new Messages();
                                    messages.setImage(generatedFilePath);
                                    messages.setMessage(messageText);
                                    messages.setFromUid(FirebaseAuth.getInstance().getUid());
                                    messages.setSeen(0);
                                    messages.setType("image");

                                    FirebaseFirestore.getInstance().collection("Rooms/" + getIntent().getStringExtra("ID") + "/Messages/").document()
                                            .set(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                loadingBar.dismiss();
                                                Toast.makeText(ChatImageView.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                                                ChatImageView.super.onBackPressed();
                                            } else {
                                                loadingBar.dismiss();
                                                Toast.makeText(ChatImageView.this, "Error", Toast.LENGTH_SHORT).show();
                                                ChatImageView.super.onBackPressed();
                                            }

                                        }

                                    });

                                });
                            }
                        });
           }
       });

       back.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               ChatImageView.super.onBackPressed();
           }
       });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri resultUri = result.getUri();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            pic = baos.toByteArray();

            Bitmap bitmap2 = BitmapFactory.decodeByteArray(pic, 0 ,pic.length);
            imageView.setImageBitmap(bitmap2);

        }


    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}