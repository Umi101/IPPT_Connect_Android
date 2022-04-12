package com.example.fyp_ippt_connect_android.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fyp_ippt_connect_android.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class NewProfileActivity extends AppCompatActivity {

    //Log
    private final static String TAG = NewProfileActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private String gender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_newprofile);

        TextInputLayout usernameInput = findViewById(R.id.username_input);
        TextInputLayout phoneInput = findViewById(R.id.phone_input);
        TextInputLayout ageInput = findViewById(R.id.age_input);
        TextInputLayout heightInput = findViewById(R.id.height_input);
        TextInputLayout weightInput = findViewById(R.id.weight_input);
        RadioGroup genderGroup = findViewById(R.id.genderGroup);
        Button createButton = findViewById(R.id.buttonCreate);


        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int position = genderGroup.indexOfChild(findViewById(i));
                if (position == 0){
                    gender = "Male";
                }else{
                    gender = "Female";
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        DatabaseReference userRef = mDatabase.child("Users").child(userId);


        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (usernameInput.getEditText().getText().toString().isEmpty()){
                    usernameInput.setError("Name is required!");
                    return;
                }
                if (heightInput.getEditText().getText().toString().isEmpty()){
                    heightInput.setError("Height is required!");
                    return;
                }
                userRef.child("name").setValue(usernameInput.getEditText().getText().toString());
                userRef.child("phone").setValue(phoneInput.getEditText().getText().toString());
                userRef.child("gender").setValue(gender);
                if (!ageInput.getEditText().getText().toString().isEmpty()){
                    userRef.child("age").setValue(Integer.parseInt(ageInput.getEditText().getText().toString()));
                }
                userRef.child("height").setValue(Double.parseDouble(heightInput.getEditText().getText().toString()));

                if(!weightInput.getEditText().getText().toString().isEmpty()){
                    userRef.child("weight").setValue(Double.parseDouble(weightInput.getEditText().getText().toString()));
                }

                Intent i = new Intent(NewProfileActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}
