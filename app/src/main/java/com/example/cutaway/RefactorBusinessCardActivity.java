package com.example.cutaway;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class RefactorBusinessCardActivity extends AppCompatActivity {
    private Bitmap picture;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText companyEditText;
    private EditText phoneEditText;
    private EditText emailEditText;
    private ImageView pictureImageView;
    private Button saveButton;
    private FloatingActionButton addButton;
    private ArrayList<BusinessCard> businessCards;
    private String[] cameraPermission;
    private String[] storagePermission;
    private Context mContext;
    private int position;
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refactor_business_card);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = getApplicationContext();
        businessCards = readBusinessCardsFromJson();

        firstNameEditText = findViewById(R.id.edit_text_fName_ref);
        lastNameEditText = findViewById(R.id.edit_text_lName_ref);
        companyEditText = findViewById(R.id.edit_text_company_ref);
        phoneEditText = findViewById(R.id.edit_text_phone_ref);
        emailEditText = findViewById(R.id.edit_text_email_ref);
        pictureImageView = findViewById(R.id.edit_imageView_ref);
        saveButton = findViewById(R.id.button_save_ref);
        addButton = findViewById(R.id.add_button_ref);

        // Allowing permissions for gallery and camera
        position = getIntent().getIntExtra("position", -1);
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (position > -1) {
            BusinessCard card = businessCards.get(position);
            firstNameEditText.setText(card.getFirstName());
            lastNameEditText.setText(card.getLastName());
            companyEditText.setText(card.getCompany());
            phoneEditText.setText(card.getPhone());
            emailEditText.setText(card.getEmail());
            if (card.getEncoded() != null) {
                byte[] decodedBytes = Base64.decode(card.getEncoded(), Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                pictureImageView.setImageBitmap(decodedBitmap);
            } else {
                pictureImageView.setImageResource(R.drawable.ic_none);
            }
        }

        addButton.setOnClickListener(v -> {
            if (!checkStoragePermission()) {
                requestStoragePermission();
            } else {
                pickFromGallery();
            }
        });

        saveButton.setOnClickListener(v -> {
            String firstName = firstNameEditText.getText().toString();
            String lastName = lastNameEditText.getText().toString();
            String company = companyEditText.getText().toString();
            String phone = phoneEditText.getText().toString();
            String email = emailEditText.getText().toString();
            Drawable drawable = pictureImageView.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                picture = ((BitmapDrawable) drawable).getBitmap();
            } else {
                picture = BitmapFactory.decodeResource(getResources(), R.drawable.ic_none);
            }
            String encodedString;
            if (picture != null) {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                picture.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                encodedString = Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT);
                businessCards.set(position, new BusinessCard(encodedString, firstName, lastName, company, phone, email));
            } else {
                businessCards.set(position, new BusinessCard(firstName, lastName, company, phone, email));
            }
            // Save businessCards to JSON file
            writeBusinessCardsToJson(businessCards);
            Intent intent = new Intent(RefactorBusinessCardActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public void writeBusinessCardsToJson(ArrayList<BusinessCard> businessCards) {
        Gson gson = new Gson();
        String json = gson.toJson(businessCards);
        try {
            FileOutputStream fileOutput = openFileOutput("business_card.txt", MODE_PRIVATE);
            fileOutput.write(json.getBytes());
            fileOutput.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<BusinessCard> readBusinessCardsFromJson() {
        ArrayList<BusinessCard> businessCards = new ArrayList<>();
        try {
            FileInputStream fileInput = openFileInput("business_card.txt");
            int size = fileInput.available();
            byte[] buffer = new byte[size];
            fileInput.read(buffer);
            fileInput.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            Type businessCardListType = new TypeToken<ArrayList<BusinessCard>>() {}.getType();
            businessCards = gson.fromJson(json, businessCardListType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return businessCards;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = null;
                if (result != null) {
                    resultUri = result.getUri();
                }
                pictureImageView.setImageURI(resultUri);
            }
        }
    }

    private boolean checkStoragePermission() {
        boolean writeStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return writeStoragePermission;
    }

    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please enable camera and storage permissions", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please enable storage permissions", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }
        }
    }
    private void pickFromGallery() {
        CropImage.activity().start(this);
    }
}




