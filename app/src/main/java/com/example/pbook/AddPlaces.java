package com.example.pbook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class AddPlaces extends AppCompatActivity {

    private ImageButton img_btn_select;

    private EditText editText_title , editText_desc , editText_price , editText_adress , editText_category , editText_categoryType;
    private Button btn_submit ;
    MaterialSpinner spinner , spinner1 , spinner2 ;

    private Uri imageUri = null ;

    private StorageReference mStorage ;
    private DatabaseReference mDatabse ;
    private DatabaseReference mDatabseUsers ;
    private FirebaseAuth mAuth ;
    private FirebaseUser mCurrentUser ;
    private ProgressDialog mProgress ;


    private static final int GALLERY_REQUEST = 1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_places);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabse = FirebaseDatabase.getInstance().getReference().child("Places");

        mDatabseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());


        spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setItems("Choose category" , "For Rent", "For Sale");

        spinner1 = (MaterialSpinner)findViewById(R.id.spinner1);

        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                if(item.toString()=="For Rent")
                {
                    editText_category.setText("For Rent");
                    editText_category.setClickable(false);
                    spinner1.setItems("Choose Type" , "Available" , "Not Available" );

                    spinner1.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {

                            if(item.toString() == "Available"){
                                editText_categoryType.setText("Available");
                                editText_categoryType.setClickable(false);
                            }
                            else if(item.toString() == "Not Available"){
                                editText_categoryType.setText("Not Available");
                                editText_categoryType.setClickable(false);
                            }
                            else{
                                editText_categoryType.setText("");
                                editText_categoryType.setClickable(false);
                            }
                        }
                    });
                }
                else if (item.toString()=="For Sale")
                {
                    editText_category.setText("For Sale");
                    editText_category.setClickable(false);
                    spinner1.setItems("Choose Type" , "Available" , "Not Available");

                    spinner1.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {

                            if(item.toString() == "Available"){
                                editText_categoryType.setText("Available");
                                editText_categoryType.setClickable(false);
                            }
                            else if(item.toString() == "Not Available"){
                                editText_categoryType.setText("Not Available");
                                editText_categoryType.setClickable(false);
                            }
                            else{
                                editText_categoryType.setText("");
                                editText_categoryType.setClickable(false);
                            }
                        }
                    });

                }
                else {
                    editText_category.setText("");
                    editText_category.setClickable(false);
                    spinner1.setItems("None");

                    editText_categoryType.setText("");
                    editText_categoryType.setClickable(false);
                }
            }
        });


        mProgress = new ProgressDialog(this);

        img_btn_select = (ImageButton)findViewById(R.id.imageSelect);
        editText_title = (EditText)findViewById(R.id.input_title);
        editText_desc = (EditText)findViewById(R.id.input_desc);
        editText_price = (EditText)findViewById(R.id.input_price);
        editText_adress = (EditText)findViewById(R.id.input_adress);
        editText_category = (EditText)findViewById(R.id.input_category);
        editText_categoryType = (EditText)findViewById(R.id.input_categoryType);
        btn_submit = (Button)findViewById(R.id.submit_post);

        img_btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent , GALLERY_REQUEST);

            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPosting();
            }
        });
    }

    private void startPosting() {

        mProgress.setMessage("Adding Product...");


        final String title_string = editText_title.getText().toString().toLowerCase().trim();
        final String desc_string = editText_desc.getText().toString().trim();
        final String price_string = editText_price.getText().toString().trim();
        final String aderss_string = editText_adress.getText().toString().trim();
        final String category_string = editText_category.getText().toString().trim();
        final String categoryType_string = editText_categoryType.getText().toString().trim();


        if(!TextUtils.isEmpty(title_string) && !TextUtils.isEmpty(desc_string) && !TextUtils.isEmpty(price_string) && imageUri != null && !TextUtils.isEmpty(category_string) && !TextUtils.isEmpty(categoryType_string) && !TextUtils.isEmpty(aderss_string)){

            mProgress.show();

            StorageReference filePath = mStorage.child("Company_Products").child(imageUri.getLastPathSegment());
            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    @SuppressWarnings("VisibleForTests") final Uri downlaodUrl = taskSnapshot.getDownloadUrl();
                    //editText_title.setText(null);

                    final DatabaseReference newProduct = mDatabse.push();

                    mDatabseUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // String s = String.valueOf(dataSnapshot.child("name").getValue());
                            // final DatabaseReference newProduct = mDatabse.push();



                            newProduct.child("Title").setValue(title_string);
                            newProduct.child("Description").setValue(desc_string);
                            newProduct.child("Price").setValue(price_string);
                            newProduct.child("Address").setValue(aderss_string);
                            newProduct.child("Image").setValue(downlaodUrl.toString());
                            newProduct.child("UserId").setValue(mCurrentUser.getUid());
                            newProduct.child("Category").setValue(category_string);
                            newProduct.child("Type").setValue(categoryType_string);
                            newProduct.child("UserName").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        //startActivity(new Intent(CompanyAddProduct.this  , CompanyHome.class));
                                        Intent intent = new Intent(AddPlaces.this  , Home.class);
                                        startActivity(intent);

                                    }
                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    //  newProduct.child("CompanyName").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {

                    mProgress.dismiss();


                }
            });
        }
        else {
            Toast.makeText(getApplicationContext() , "fill all fields" , Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();
            /*img_btn_select.setImageURI(imageUri);*/
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(4 , 3)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                img_btn_select.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
