package com.plantopia.plantopia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.Replaceable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth uAuth;
    FloatingActionButton btnPost;
    SharedPreferences sp;
    HomeFragment homeFragment;
    CommunityFragment communityFragment;
    ProfileFragment profileFragment;
    SearchFragment searchFragment;


    BottomNavigationView loginBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sp = getSharedPreferences("p1", MODE_PRIVATE);
        uAuth=FirebaseAuth.getInstance();
        loginBottom=findViewById(R.id.loginBottom);
        btnPost=findViewById(R.id.btnPost);


        //Fragements

        homeFragment=new HomeFragment();
        searchFragment=new SearchFragment();
        profileFragment=new ProfileFragment();
        communityFragment=new CommunityFragment();
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),NewPostActivity.class);
                startActivity(i);
            }
        });

        replaceFargement(homeFragment);
        loginBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch(menuItem.getItemId())
                {
                    case R.id.nav_home:
                        replaceFargement(homeFragment);
                        return true;
                    case R.id.nav_community:
                        replaceFargement(communityFragment);
                        return true;
                    case R.id.nav_search:
                        replaceFargement(searchFragment);
                        return true;
                    case R.id.nav_profile:
                        replaceFargement(profileFragment);
                        return true;
                    default:
                        return false;
                }


            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options,menu);
        return  true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.nav_home)
        {

        }
        if(item.getItemId()==R.id.nav_search)
        {
//            Intent i=new Intent(LoginActivity.this,SettingActivityP.class);
//            startActivity(i);
        }
        if(item.getItemId()==R.id.nav_community)
        {
//            Intent i=new Intent(LoginActivity.this,SettingActivityP.class);
//            startActivity(i);
        }
        if(item.getItemId()==R.id.nav_profile)
        {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("email", "");
            editor.apply();
            uAuth.signOut();
          Intent i=new Intent(getApplicationContext(),MainActivity.class);
          startActivity(i);
          finish();
        }
        return  true;

    }

    private void replaceFargement(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragement,fragment);
        fragmentTransaction.commit();

    }

}
