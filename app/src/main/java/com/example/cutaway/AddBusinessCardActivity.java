    package com.example.cutaway;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class AddBusinessCardActivity extends AppCompatActivity {
    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText companyEditText;
    EditText phoneEditText;
    EditText emailEditText;
    TextView photoPlaceTextView;
    ImageView pictureImageView;
    Button saveButton;
    FloatingActionButton addButton;
    ArrayList<BusinessCard> businessCards;
    private static final int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_business_card);
        businessCards = getIntent().getParcelableArrayListExtra("businessCards");
        firstNameEditText = findViewById(R.id.edit_text_fName);
        lastNameEditText = findViewById(R.id.edit_text_lName);
        companyEditText = findViewById(R.id.edit_text_company);
        phoneEditText = findViewById(R.id.edit_text_phone);
        emailEditText = findViewById(R.id.edit_text_email);
        photoPlaceTextView = findViewById(R.id.first_name_text_view);
        pictureImageView = findViewById(R.id.edit_imageView);
        saveButton = findViewById(R.id.button_save);
        addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CropperActivity.class);
                startActivity(intent);
            }
        });
        if(saveButton != null) {
            saveButton.setOnClickListener(v -> {
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String company = companyEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String email = emailEditText.getText().toString();
                Bitmap picture = null;
                if(pictureImageView.getDrawable() != null) {
                    picture = ((BitmapDrawable) pictureImageView.getDrawable()).getBitmap();
                }
                if (firstName.isEmpty() || lastName.isEmpty() || company.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                    Toast.makeText(AddBusinessCardActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    if(picture != null) {
                        businessCards.add(new BusinessCard(picture, firstName, lastName, company, phone, email));
                    } else businessCards.add(new BusinessCard(firstName, lastName, company, phone, email));
                    Intent resultIntent = new Intent();
                    if(picture != null) {
                        resultIntent.putExtra("newBusinessCard", new BusinessCard(picture, firstName, lastName, company, phone, email));
                    } else resultIntent.putExtra("newBusinessCard", new BusinessCard(firstName, lastName, company, phone, email));
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            });
        }
    }

}

