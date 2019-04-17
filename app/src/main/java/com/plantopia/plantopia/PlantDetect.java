package com.plantopia.plantopia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class PlantDetect extends AppCompatActivity {
    ImageView mImageView;
    TextView textViewResult,tvScientificNamef,tvDescriptionf,tvPoisonf,tvMedicinalUse,tvSoilf,tvTemperaturef,tvEdiblef;
    private static final String MODEL_PATH = "optimized_graph.lite";
    private static final boolean QUANT = false;
    private static final String LABEL_PATH = "retrained_labels.txt";
    private static final int INPUT_SIZE = 224;
    private Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();
    Bitmap bmp;

    String nameOfPlant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_detect); initTensorFlowAndLoadModel();
        mImageView = findViewById(R.id.iv_plant);
        textViewResult = findViewById(R.id.tv_namePlant);
        tvScientificNamef=findViewById(R.id.tv_sci_namef);
        tvDescriptionf=findViewById(R.id.tv_descriptionf);
        tvPoisonf=findViewById(R.id.tv_poisonf);
        tvMedicinalUse=findViewById(R.id.tv_medicinalf);
        tvSoilf=findViewById(R.id.tv_soilf);
        tvTemperaturef=findViewById(R.id.tv_temperaturef);
        tvEdiblef=findViewById(R.id.tv_ediblef);


        Bundle extras = getIntent().getExtras();
        String cameraPath = extras.getString("pat");
        String galPath = extras.getString("gal");


        //=============================================================================
        //CODE FOR PIC SELECTED FROM GALLERY
        //===============================================================================
        if (cameraPath.equals("nothing")) {
            Uri galpath = Uri.parse(galPath);
            //Toast.makeText(getApplicationContext(), galPath, Toast.LENGTH_LONG).show();
            mImageView.setImageURI(galpath);
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), galpath);
                classify(bmp);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
        }

        //==============================================================================
        //CODE FOR PIC CLICKED FROM CAMERA
        //===============================================================================
        else {
            Uri path = Uri.parse(cameraPath);
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                mImageView.setImageURI(path);
                classify(bmp);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    //==============================================================
    //INITIALIZE TENSORFLOW
    //==============================================================
    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = com.plantopia.plantopia.TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_PATH,
                            LABEL_PATH,
                            INPUT_SIZE,
                            QUANT);
                    Log.d("/////", classifier.toString());

                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

    //================================================================
    //CLASSIFY IMG
    //================================================================
    private void classify(Bitmap bmp) {
        if (bmp == null) {
            Toast.makeText(getApplicationContext(), "No image", Toast.LENGTH_SHORT).show();
        } else {
            try {
                bmp = Bitmap.createScaledBitmap(bmp, INPUT_SIZE, INPUT_SIZE, false);
                final List<Classifier.Recognition> results = classifier.recognizeImage(bmp);
                Classifier.Recognition obj = results.get(0);

                //FETCH NAME AND CONFIDENCE OF THE PLANT, DISPLAY ONLY IF CONFIDENCE>85
                nameOfPlant = obj.getTitle();
                float confidence = obj.getConfidence() * 100;
                if (confidence < 85.00) {
                    Toast.makeText(getApplicationContext(), "No plant identified!", Toast.LENGTH_LONG).show();
                    //HIDE ALL FIELDS IF NO PLANT DETECTED
                    LinearLayout layout = (LinearLayout) findViewById(R.id.my_layout);
                    
                    for (int i = 0; i < layout.getChildCount(); i++) {
                        View child = layout.getChildAt(i);
                        child.setVisibility(View.GONE);
                    }
                } else {
                    textViewResult.setVisibility(View.VISIBLE);
                    //Toast.makeText(getApplicationContext(), Float.toString(confidence), Toast.LENGTH_SHORT).show();
                    textViewResult.setText(nameOfPlant);
                    //DB CODE
                    InputStream is = getResources().openRawResource(R.raw.plants);
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(is, Charset.forName("UTF-8")));
                    String line = "";
                    int cnt=1;
                    try {
                        while ((line = reader.readLine()) != null) {
                            // Split the line into different tokens (using the comma as a separator).
                            String[] tokens = line.split(";");
                            StringBuilder[] arr=new StringBuilder[15];
                            //Log.d("------------",tokens[0]);
                            String plantName=tokens[1].toLowerCase().substring(1);

                            if(nameOfPlant.equals(plantName)) {
                                Character ch = null;
                               for(int i=0;i<tokens.length;i++){
                                    arr[i]=new StringBuilder(tokens[i].replace("\"",""));
                                    Log.d("(((("+i,arr[i].toString());
                                }

                                textViewResult.setText(nameOfPlant.substring(0, 1).toUpperCase() + nameOfPlant.substring(1));
                                if (arr[2].substring(0).charAt(0) == ',')
                                    tvScientificNamef.setText(arr[2].substring(1));
                                else
                                    tvScientificNamef.setText(arr[2].substring(0));

                                if (arr[3].substring(0).charAt(0) == ',')
                                    tvDescriptionf.setText(arr[3].substring(1));
                                else
                                    tvDescriptionf.setText(arr[3].substring(0));

                                if (arr[4].substring(0).charAt(0) == ',')
                                    tvPoisonf.setText(arr[4].substring(1));
                                else
                                    tvPoisonf.setText(arr[4].substring(0));

                                if (arr[5].substring(0).charAt(0) == ',')
                                    tvMedicinalUse.setText(arr[5].substring(1));
                                else
                                    tvMedicinalUse.setText(arr[5].substring(0));
                                if (arr[7].substring(0).charAt(0) == ',')
                                    tvSoilf.setText(arr[7].substring(1));
                                else
                                    tvSoilf.setText(arr[7].substring(0));
                                if(arr[8].substring(0).charAt(0)==',')
                                    tvTemperaturef.setText(arr[8].substring(1));
                                else
                                    tvTemperaturef.setText(arr[8].substring(0));
                                if(arr[9].substring(0).charAt(0)==',')
                                    tvEdiblef.setText(arr[9].substring(1));
                                else
                                    tvEdiblef.setText(arr[9].substring(0));
                                break;
                            }

                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
