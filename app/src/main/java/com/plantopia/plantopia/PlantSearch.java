package com.plantopia.plantopia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PlantSearch extends AppCompatActivity {
    ImageView mImageView;
    TextView textViewResult, tvScientificNamef, tvDescriptionf, tvPoisonf, tvMedicinalUse, tvSoilf, tvTemperaturef, tvEdiblef;
    Bitmap bmp;
    String nameOfPlant;
    ArrayList<Drawable> imgsList;
    int[] imgIds;
    HashMap<String, String> plantInfoMap;
    boolean exists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
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
        nameOfPlant = extras.getString("plantName").toLowerCase();
        exists=false;
        plantInfoMap = new HashMap<>();
        plantInfoMap.put("aglaonema",Integer.toString(R.drawable.pp1_aglaonema));
        plantInfoMap.put("areca plant",Integer.toString(R.drawable.pp2_areca_plant));
        plantInfoMap.put("bougainvillea",Integer.toString(R.drawable.pp3_bougainvillea));
        plantInfoMap.put("hibiscus",Integer.toString(R.drawable.pp4_hibiscus));
        plantInfoMap.put("mango",Integer.toString(R.drawable.pp5_mango));
        plantInfoMap.put("money plant",Integer.toString(R.drawable.pp6_money_plant));
        plantInfoMap.put("neem",Integer.toString(R.drawable.pp7_neem));
        plantInfoMap.put("nerium oleander",Integer.toString(R.drawable.pp8_nerium_oleander));
        plantInfoMap.put("tabernaemontana",Integer.toString(R.drawable.pp9_tabernaemontana));
        plantInfoMap.put("tobacco",Integer.toString(R.drawable.pp10_tobacco));
        plantInfoMap.put("tulsi",Integer.toString(R.drawable.pp11_tulsi));
        plantInfoMap.put("white snakeroot",Integer.toString(R.drawable.pp12_white_snakeroot));

        InputStream is = getResources().openRawResource(R.raw.plants);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = "";
        String[] plants=new String[50];
        int cnt=0;

        //FILL THE ARRAY
        try {
            while ((line = reader.readLine()) != null) {
                // Split the line into different tokens (using the comma as a separator).
                String[] tokens = line.split(";");
                //Log.d("------------", tokens[0]);
                String plantName = tokens[1].toLowerCase().substring(1);
                plants[cnt]=plantName;
                Log.d("^^^^^^^",plants[cnt]);
            }

        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }

        is = getResources().openRawResource(R.raw.plants);
        reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      //line = "";
        try {
            while ((line = reader.readLine()) != null) {
                // Split the line into different tokens (using the comma as a separator).
                String[] tokens = line.split(";");
                StringBuilder[] arr=new StringBuilder[15];
                String plantName=tokens[1].toLowerCase().substring(1);

                if(nameOfPlant.equals(plantName)) {
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

                    mImageView.setImageResource(Integer.parseInt(plantInfoMap.get(plantName)));
                    exists=true;
                     break;
                }

            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
        if(!exists){
            Toast.makeText(getApplicationContext(),"No plant found",Toast.LENGTH_LONG).show();
            finish();
        }
    }
}