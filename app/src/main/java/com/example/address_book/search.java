package com.example.address_book;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class search extends AppCompatActivity {
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Contacts");





        final EditText name = findViewById(R.id.name);


        Button search = findViewById(R.id.search);
        Button update = findViewById(R.id.update);
        Button delete = findViewById(R.id.delete);
        Button home = findViewById(R.id.home);


        final EditText fName = findViewById(R.id.fName);
        final EditText lName = findViewById(R.id.lName);
        final EditText email = findViewById(R.id.email);
       final EditText phone = findViewById(R.id.phone);



       //home button
        home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(search.this, MainActivity.class);
                startActivity(intent);
            }
        });


        //search button
        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                ValueEventListener listen = myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot s : dataSnapshot.getChildren()) {

                          String firstName = s.child("firstName").getValue().toString();
                          String lastName = s.child("lastName").getValue().toString();
                            String userEmail = s.child("userEmail").getValue().toString();
                            String phoneNumber = s.child("phoneNumber").getValue().toString();

                            if((firstName.equals(name.getText().toString())) || (lastName.equals(name.getText().toString())) ){

                                fName.setText(firstName);
                                lName.setText(lastName);
                                email.setText(userEmail);
                                phone.setText(phoneNumber);
                            }
                            // else statement if no result is found
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


        update.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Write a message to the database

                ValueEventListener listen = myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot s : dataSnapshot.getChildren()) {

                            String firstName = s.child("firstName").getValue().toString();
                            String lastName = s.child("lastName").getValue().toString();
                            String userEmail = s.child("userEmail").getValue().toString();
                            String phoneNumber = s.child("phoneNumber").getValue().toString();


                            if ((firstName.equals(name.getText().toString())) || (lastName.equals(name.getText().toString()))) {

                                User contact = new User(email.getText().toString(), fName.getText().toString(), lName.getText().toString(), phone.getText().toString());

                                s.getRef().setValue(contact).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(search.this, "chat saved", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(search.this, "chat not saved", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            //will implement the else statement if no result is found
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });






        // Phone number is the key to delete an entry
        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Write a message to the database

                ValueEventListener listen = myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot s : dataSnapshot.getChildren()) {

                            String phoneNumber = s.child("phoneNumber").getValue().toString();

                            if(phoneNumber.equals(phone.getText().toString())){

                                s.getRef().removeValue();
                            }

                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


    }
}
