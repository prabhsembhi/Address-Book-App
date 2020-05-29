package com.example.address_book;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class add extends AppCompatActivity {
    DatabaseReference myRef;
    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    //for file storage
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    ImageView  imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
         myRef = database.getReference("Contacts");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //buttons
        Button add_contact = findViewById(R.id.add);
        Button search = findViewById(R.id.search);
        Button home = findViewById(R.id.home);
        Button btnChoose = (Button) findViewById(R.id.btnChoose);
        Button  btnUpload = (Button) findViewById(R.id.btnUpload);

        imageView =  findViewById(R.id.image);


        final EditText fName = findViewById(R.id.fName);
        final EditText lName = findViewById(R.id.lName);
        final EditText email = findViewById(R.id.email);
        final EditText phone = findViewById(R.id.phone);

        
        //Strings for validating
       final  String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
       final String pattern = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$";


        //Choose Button
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        //Upload Button
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });




            //home button
        home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(add.this, MainActivity.class);
                startActivity(intent);
            }
        });




        
        
        //Adding a contact
        add_contact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                // validation of Phone Number
                Matcher m = null;
                Pattern r = Pattern.compile(pattern);
                if (!phone.getText().toString().isEmpty()) {
                    m = r.matcher(phone.getText().toString().trim());
                } else {
                    Toast.makeText(add.this, "Please enter mobile number ", Toast.LENGTH_LONG).show();
                }
                if (m.find()) {
                    Toast.makeText(add.this, "Valid Phone Number", Toast.LENGTH_LONG).show();

                    //Validation of phone Number
                    if ( email.getText().toString().trim().matches(emailPattern)) {
                        Toast.makeText(getApplicationContext(), "valid email address", Toast.LENGTH_SHORT).show();
                        User contact = new User(email.getText().toString(), fName.getText().toString(), lName.getText().toString(), phone.getText().toString());
                        String key = myRef.push().getKey();
                        myRef.child(key).setValue(contact).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(add.this, "chat saved", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(add.this, "chat not saved", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(add.this, "Invalid Phone Number", Toast.LENGTH_LONG).show();
                }
            }
        });




//Search button that takes us to the search page
        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Write a message to the database
                Intent intent = new Intent(add.this, search.class);
                startActivity(intent);
            }
        });


    }

    //This function is to choose files from the cloud
    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


//This part is to upload the files
    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(add.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(add.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }
}
