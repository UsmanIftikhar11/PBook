package com.example.pbook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    private EditText mInput_name , mInput_pass , mInput_email , mInput_phone ;
    private ImageButton btn_create ;

    private FirebaseAuth mAuth ;
    private ProgressDialog mprogress ;
    private DatabaseReference mDatabase ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mprogress = new ProgressDialog(this);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);


        mInput_name = findViewById(R.id.et_name);
        mInput_pass = findViewById(R.id.et_pass1);
        mInput_email = findViewById(R.id.et_email1);
        mInput_phone = findViewById(R.id.et_phone);
        btn_create = findViewById(R.id.btn_signup);

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });
    }

    private void startRegister() {

        final String nameString = mInput_name.getText().toString().trim();
        String passString = mInput_pass.getText().toString().trim();
        String emailString = mInput_email.getText().toString().trim();
        final String phoneString = mInput_phone.getText().toString().trim();


        if(!TextUtils.isEmpty(nameString) && !TextUtils.isEmpty(passString)  && !TextUtils.isEmpty(emailString) && !TextUtils.isEmpty(phoneString)){

            mprogress.setMessage("Creating account...");
            mprogress.show();

            mAuth.createUserWithEmailAndPassword(emailString , passString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = mDatabase.child(user_id);
                        current_user_db.child("name").setValue(nameString);
                        current_user_db.child("phone").setValue(phoneString);

                        mprogress.dismiss();
                        Intent intent = new Intent(SignUp.this , Home.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                }
            });

        }
        else {

            Toast.makeText(getApplicationContext() , "Fill all fields!!!" , Toast.LENGTH_LONG).show();
        }

    }
}
