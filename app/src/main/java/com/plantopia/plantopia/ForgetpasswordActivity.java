package com.plantopia.plantopia;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetpasswordActivity extends AppCompatActivity {
    EditText etEmailForget;
    Button btnSendLink;
    FirebaseAuth uAuth;
    ProgressBar pbForgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);

        etEmailForget=(EditText)findViewById(R.id.etEmailForget);
        btnSendLink=(Button)findViewById(R.id.btnSendLink);
        pbForgetPassword=(ProgressBar)findViewById(R.id.pbForgetPassword);
        pbForgetPassword.setVisibility(View.GONE);

        uAuth=FirebaseAuth.getInstance();

        btnSendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email=etEmailForget.getText().toString().trim();
                if (Email.isEmpty()) {
                    etEmailForget.setError("Email Required");
                    etEmailForget.requestFocus();
                    return;
                }



                if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                    etEmailForget.setError("Enter a Valid Email-id");
                    etEmailForget.requestFocus();
                    return;
                }
                pbForgetPassword.setVisibility(View.VISIBLE);
                uAuth.sendPasswordResetEmail(etEmailForget.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pbForgetPassword.setVisibility(View.GONE);
                        if(task.isSuccessful())
                        {
                            Toast.makeText(ForgetpasswordActivity.this, "Reset Link Has Been send to your email address", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(ForgetpasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
    }
}
