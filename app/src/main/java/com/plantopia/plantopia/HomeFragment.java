package com.plantopia.plantopia;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static android.util.Base64.NO_WRAP;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    ImageView mImageView;
    String keyCamera,keyGallery;
    String[] appPermissions={android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA};
    private static final int PERMISSIONS_REQUEST_CODE=1240;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container, false);

        Button btnClick=view.findViewById(R.id.btn_click);
        Button btnChoose=view.findViewById(R.id.btn_choose);
        keyCamera="nothing";
        keyGallery="nothing";

        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appPermissions=new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA};
                if(checkAndRequestPermissions()) {
                    dispatchTakePictureIntent();
                    galleryAddPic();
                }
             
            }
        });

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appPermissions=new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE};
                if(checkAndRequestPermissions()) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 2);
                }
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
    static final int REQUEST_IMAGE_CAPTURE = 1;
    //static final int REQUEST_TAKE_PHOTO = 1;
    Uri photoURI;
    File photoFile;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
                Log.d("Img_fb***************",photoFile.toString());
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d("img_fb",ex.toString());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Log.d("img_fb","Not null");
                photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.plantopia.plantopia.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                Log.d("*-*-*-",photoURI.toString());
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("*****************","Length : "+data);
        if(requestCode==2 && data!=null){
            photoURI = data.getData();
            keyGallery=photoURI.toString();
            Log.d("=====",keyGallery);
            Intent i=new Intent(getActivity(),PlantDetect.class);
            i.putExtra("gal",keyGallery);
            i.putExtra("pat","nothing");
            getActivity().startActivity(i);
        }
        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK ) {
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoURI);
                Log.d("BITMAP","BITMAP  : "+photoURI.toString());
                //mImageView.setImageBitmap(bitmap);

                if(bitmap!=null) {
                    Intent i=new Intent(getActivity(),PlantDetect.class);
                    keyCamera=photoURI.toString();
                    i.putExtra("pat",keyCamera);
                    i.putExtra("gal","nothing");
                    getActivity().startActivity(i);
                }
                else{
                    //Toast.makeText(getApplicationContext(),"No img",Toast.LENGTH_SHORT).show();
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (Exception e){
                //Toast.makeText(HomeFragment.,e.toString(),Toast.LENGTH_LONG).show();
            }
        }
        else{
            Log.d("img_fb******","Error occurred");
            //Toast.makeText(getApplicationContext(),"Error occured",Toast.LENGTH_SHORT);
        }
    }

    String mCurrentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d("img_fbc******","Path : "+mCurrentPhotoPath);
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.getActivity().sendBroadcast(mediaScanIntent);
    }

    //===========================================================================
    //PERMISSIONS CODE
    //=============================================================================
    private boolean checkAndRequestPermissions(){
        List<String> listPermissionsNeeded=new ArrayList<>();
        for(String perm:appPermissions){
            if(ActivityCompat.checkSelfPermission(getActivity(),perm)!=PackageManager.PERMISSION_GRANTED){
                listPermissionsNeeded.add(perm);
            }
        }
        if(!listPermissionsNeeded.isEmpty()){
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),PERMISSIONS_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("//////","callback");
        if(requestCode==PERMISSIONS_REQUEST_CODE){
            HashMap<String,Integer> permissionResults=new HashMap<>();
            int deniedCount=0;

            //Gather permission grant results
            for(int i=0;i<grantResults.length;i++){
                if(grantResults[i]==PackageManager.PERMISSION_DENIED){
                    Log.d("==-=-=-=-=-","enterd");
                    permissionResults.put(permissions[i],grantResults[i]);
                    deniedCount++;
                }
            }

            if(deniedCount==0)
            {}
            else{
                for(Map.Entry<String,Integer> entry:permissionResults.entrySet()){
                    String permName=entry.getKey();
                    int permResult=entry.getValue();

                    if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),permName)){
                        showDialog("","Plantopia needs Camera and Storage permission to work correctly"
                                ,"Yes, Grant permissions",new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        checkAndRequestPermissions();
                                    }
                                },"Cancel",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        //Toast.makeText(getActivity(), "Permissions ot granted", Toast.LENGTH_LONG).show();
                                    }
                                },false);
                        break;
                    }

                    else{
                        showDialog("","You have denied some permissions, allow them in settings"
                                ,"Goto settings",new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                                ,Uri.fromParts("package",getActivity().getPackageName(),null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        //getActivity().finish();

                                    }
                                },"Cancel",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        //Toast.makeText(getActivity(), "Not granted", Toast.LENGTH_LONG).show();
                                    }
                                },true);
                        break;
                    }

                }
            }

        }
    }

    public AlertDialog showDialog(String title, String msg, String positiveLabel, DialogInterface.OnClickListener positiveClick
                                    ,String negativeLabel, DialogInterface.OnClickListener negativeOnClick, boolean isCancelable){

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setCancelable(isCancelable);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveLabel,positiveClick);
        builder.setNegativeButton(negativeLabel,negativeOnClick);

        AlertDialog alert=builder.create();
        alert.show();
        return alert;
    }
}
