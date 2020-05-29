package com.example.address_book;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Contacts");


        //text views
        final LinearLayout contacts = findViewById(R.id.contacts_layout);

        //Edit view
        final EditText lastName = findViewById(R.id.filterText);

        //buttons
        Button add = findViewById(R.id.add);
        Button search = findViewById(R.id.search);
        Button filter = findViewById(R.id.filter);


        final LinearLayout lLayout = (LinearLayout) findViewById(R.id.contacts_layout);
        //Read from database

        ValueEventListener listen = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("msg", "Value is: " );
                for (DataSnapshot s : dataSnapshot.getChildren()) {

                    String Id = s.getKey();
                    String fName = s.child("firstName").getValue(String.class);
                    String lName = s.child("lastName").getValue(String.class);
                    String email = s.child("userEmail").getValue(String.class);
                    String phone = s.child("phoneNumber").getValue(String.class);


                    TextView tv = new TextView(MainActivity.this); // Prepare textview object programmatically
                    tv.setText("\n"+fName + " " + lName + "\n"+email+"\n"+phone+"\n");
                    tv.setTextAppearance(getApplicationContext(),R.style.textStyle);
                    // tv.setId("i" + 5);
                    lLayout.addView(tv); // Add to your ViewGroup using this method

                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //calling add activity on click
        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, add.class);
                startActivity(intent);
            }
        });

        //search button
        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, search.class);
                startActivity(intent);
            }
        });

        //Filter button for filter function
        filter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Read from database

                ValueEventListener listen = myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        lLayout.removeAllViews();
                        for (DataSnapshot s : dataSnapshot.getChildren()) {

                            String Id = s.getKey();
                            String fName = s.child("firstName").getValue(String.class);
                            String lName = s.child("lastName").getValue(String.class);
                            String email = s.child("userEmail").getValue(String.class);
                            String phone = s.child("phoneNumber").getValue(String.class);

                            if(((lastName.getText().toString()).equals(lName))){

                                TextView tv = new TextView(MainActivity.this); // Prepare textview object programmatically
                                tv.setText("\n"+fName + " " + lName + "\n"+email+"\n"+phone+"\n\n");
                                tv.setTextAppearance(getApplicationContext(),R.style.textStyle);
                                // tv.setId("i" + 5);
                                lLayout.addView(tv); // Add to your ViewGroup using this method
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
