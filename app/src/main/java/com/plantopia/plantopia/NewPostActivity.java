package com.plantopia.plantopia;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import io.grpc.Compressor;

public class NewPostActivity extends AppCompatActivity {

    ImageView imgNewPost;
    private static final int MAX_LENGTH=100;
    ProgressBar prgBar;
    EditText etPostDescription;
    Button btnPost;
    Uri postImage;
    StorageReference storageReference;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth uAuth;
    String current_user;
    String downloadUri;
Bitmap compressedImageFile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        storageReference=FirebaseStorage.getInstance().getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();
        uAuth=FirebaseAuth.getInstance();
        current_user=uAuth.getCurrentUser().getUid();
        etPostDescription=findViewById(R.id.etPostDescription);
        prgBar=findViewById(R.id.prgBar);
        imgNewPost=findViewById(R.id.imgNewPost);
        btnPost=findViewById(R.id.btnPost);
        imgNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(NewPostActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                } else {

                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setMaxCropResultSize(512,512)
                            .setAspectRatio(1,1)
                            .start(NewPostActivity.this);

                }
            }
            else
        {

        }

            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String desc=etPostDescription.getText().toString();

             if(!TextUtils.isEmpty(desc) && imgNewPost!=null)
             {
                 prgBar.setVisibility(View.VISIBLE);
                 imgNewPost.setVisibility(View.VISIBLE);
                 final String randomName=UUID.randomUUID().toString();

                 StorageReference filepath=storageReference.child("post_images").child(randomName+ ".jpg");
                 filepath.putFile(postImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                         // downloadUri=task.getResult().getMetadata().getReference().getDownloadUrl().toString();

                         if(task.isSuccessful())
                         {
//                             Toast.makeText(NewPostActivity.this, downloadUri, Toast.LENGTH_SHORT).show();
                             File newImageFile=new File(postImage.getPath());
                             try {
                                 compressedImageFile=new id.zelory.compressor.Compressor(NewPostActivity.this).
                                         setMaxHeight(100).setMaxWidth(100).setQuality(10).compressToBitmap(newImageFile);

                             }
                             catch (IOException e)
                             {

                                 e.printStackTrace();
                             }
                             ByteArrayOutputStream baos =new ByteArrayOutputStream();
                             compressedImageFile.compress(Bitmap.CompressFormat.JPEG,100,baos);
                             byte[] data =baos.toByteArray();
                             UploadTask uploadTask =storageReference.child("post_images/thumbs").child(randomName +".jpg").putBytes(data);
                             uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                                 public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                     downloadUri=uri.toString();
                                        String downloadthumb=taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                                        Map<String,Object> postMap=new HashMap<>();
                                        postMap.put("image_url",downloadUri);
                                        postMap.put("thumb",downloadthumb);
                                        postMap.put("desc",desc);
                                        postMap.put("user_id",current_user);
                                        postMap.put("timestamp",FieldValue.serverTimestamp());
                                        firebaseFirestore.collection("Post").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if(task.isSuccessful())
                                                {
                                                    Toast.makeText(NewPostActivity.this, "Post Added", Toast.LENGTH_SHORT).show();
                                                    Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                                                    startActivity(i);

                                                }
                                                else
                                                {
                                                    prgBar.setVisibility(View.INVISIBLE);

                                                }

                                            }
                                        });


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

//                               String downloadthumb=taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
//                                Map<String,Object> postMap=new HashMap<>();
//                                postMap.put("image_url",downloadUri);
//                                postMap.put("thumb",downloadthumb);
//                                postMap.put("desc",desc);
//                                postMap.put("user_id",current_user);
//                                postMap.put("timestamp",FieldValue.serverTimestamp());
//                                firebaseFirestore.collection("Post").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<DocumentReference> task) {
//                                        if(task.isSuccessful())
//                                        {
//                                            Toast.makeText(NewPostActivity.this, "Post Added", Toast.LENGTH_SHORT).show();
//                                            Intent i=new Intent(getApplicationContext(),LoginActivity.class);
//                                            startActivity(i);
//
//                                        }
//                                        else
//                                        {
//                                            prgBar.setVisibility(View.INVISIBLE);
//
//                                        }
//
//                                    }
//                                });

                                  }
                                    }).addOnFailureListener(new OnFailureListener() {
                                         @Override
                                         public void onFailure(@NonNull Exception e) {

                                         }
                                        });



                         }
                         else
                         {

                             prgBar.setVisibility(View.INVISIBLE);

                         }
                     }
                 });
             }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                postImage = result.getUri();
                imgNewPost.setImageURI(postImage);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }




}

