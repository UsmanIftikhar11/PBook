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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileSetup extends AppCompatActivity {

    private EditText et_name , et_phone ;
    private ImageButton btn_login ;

    private FirebaseAuth mAuth ;
    private DatabaseReference mDatabase ;
    private ProgressDialog mProgress ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);
        mProgress = new ProgressDialog(this);

        et_name = findViewById(R.id.et_name1);
        et_phone = findViewById(R.id.et_phone1);
        btn_login = findViewById(R.id.btn_profile);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setupProfile();
            }
        });
    }

    private void setupProfile() {

        final String name = et_name.getText().toString().trim();
        final String phone = et_phone.getText().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone)) {

            final DatabaseReference newUser = mDatabase.child(mAuth.getCurrentUser().getUid());

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    newUser.child("name").setValue(name);
                    newUser.child("phone").setValue(phone).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                //startActivity(new Intent(CompanyAddProduct.this  , CompanyHome.class));
                                Intent intent = new Intent(ProfileSetup.this  , Home.class);
                                startActivity(intent);

                            }
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }else {

            Toast.makeText(getApplicationContext() , "fill all fields" , Toast.LENGTH_LONG).show();
        }
    }
}
