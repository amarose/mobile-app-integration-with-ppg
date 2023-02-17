package com.example.mobileappwithlogin.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileappwithlogin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pushpushgo.sdk.PushPushGo;

public class ProfileActivity extends AppCompatActivity {

    // Instantiate PPG
    private final PushPushGo ppg = PushPushGo.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Firebase signOut functionality on Logout button
        Button logout = findViewById(R.id.logoutButton);
        logout.setOnClickListener(view -> {
//            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        });

        // New activity on Choose Pokemon button click
        Button choosePokemon = findViewById(R.id.choosePokemonButton);
        choosePokemon.setOnClickListener(view -> startActivity(new Intent(ProfileActivity.this, ChoosePokemon.class)));

        // Instantiate user object from Firebase database
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        assert user != null;
        String userID = user.getUid();

        // Selectors
        final TextView welcomeTextView = findViewById(R.id.welcomeText);
        final TextView userFullNameTextView = findViewById(R.id.fullNameDisplay);
        final TextView emailTextView = findViewById(R.id.emailAddressDisplay);
        final TextView ageTextView = findViewById(R.id.ageDisplay);

        // Display user info from Firebase in App
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if(userProfile != null){
                    String fullName = userProfile.fullName;
                    String email = userProfile.email;
                    String age = userProfile.age;

                    welcomeTextView.setText("Welcome " + fullName + "!");
                    userFullNameTextView.setText(fullName);
                    emailTextView.setText(email);
                    ageTextView.setText(age);

                    // Send PPG beacon with user info
                    ppg.createBeacon()
                            .appendTag(fullName, "userName")
                            .appendTag(age, "age")
                            .setCustomId("MyFirstUser")
                            .send();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }
}