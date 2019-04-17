package com.plantopia.plantopia;

import android.support.annotation.NonNull;
import android.support.v4.util.PatternsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    EditText etEmailSign,etPasswordSign;
    TextView tvEmailSign,tvPasswordSign;
    Button btnCreateAccount;
    FirebaseAuth uAuth;
    ProgressBar pbCreate;
    DatabaseReference databaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etEmailSign=(EditText)findViewById(R.id.etEmailSign);
        etPasswordSign=(EditText)findViewById(R.id.etPasswordSign);
        //tvEmailSign=(TextView)findViewById(R.id.tvEmailSign);
        //tvPasswordSign=(TextView)findViewById(R.id.tvPasswordSign);
        btnCreateAccount=(Button)findViewById(R.id.btnCreateAccount);
        pbCreate=(ProgressBar)findViewById(R.id.pbCreate);
        pbCreate.setVisibility(View.GONE);
        uAuth=FirebaseAuth.getInstance();


        btnCreateAccount.setOnClickListener(new View.OnClickListener() {

            private void registerUser() {
                final String Email = etEmailSign.getText().toString().trim();
                String Password = etPasswordSign.getText().toString().trim();

                if (Email.isEmpty()) {
                    etEmailSign.setError("Email Required");
                    etEmailSign.requestFocus();
                    return;
                }



                    if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                    etEmailSign.setError("Enter a Valid Email-id");
                    etEmailSign.requestFocus();
                    return;
                }
                if (Password.isEmpty()) {
                    etPasswordSign.setError("Password Required");
                    etPasswordSign.requestFocus();
                    return;
                }
                pbCreate.setVisibility(View.VISIBLE);

                uAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pbCreate.setVisibility(View.GONE);
                        if(task.isSuccessful())
                        {
                            Toast.makeText(SignupActivity.this, "User Registered", Toast.LENGTH_SHORT).show();
                            User user=new User(Email);
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(SignupActivity.this, "User Registered", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(SignupActivity.this, "error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(SignupActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            @Override
            public void onClick(View v) {
                switch (v.getId())
                {
                    case R.id.btnCreateAccount:
                        registerUser();
                        break;
                }

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(uAuth.getCurrentUser()!=null){
            //already login user
        }
    }

}
