    package com.example.cutaway;

    import android.Manifest;
    import android.content.Context;
    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.graphics.Bitmap;
    import android.graphics.drawable.BitmapDrawable;
    import android.graphics.drawable.Drawable;
    import android.net.Uri;
    import android.os.Bundle;
    import android.util.Base64;
    import android.view.Display;
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
public class AddBusinessCardActivity extends AppCompatActivity {
    Bitmap picture;
    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText companyEditText;
    EditText phoneEditText;
    EditText emailEditText;
    ImageView pictureImageView;
    Button saveButton;
    FloatingActionButton addButton;
    ArrayList<BusinessCard> businessCards;
    String[] cameraPermission;
    String[] storagePermission;
    Display display;
    Context mContext;
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_business_card);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = getApplicationContext();
        businessCards = readBusinessCardsFromJson();
        firstNameEditText = findViewById(R.id.edit_text_fName);
        lastNameEditText = findViewById(R.id.edit_text_lName);
        companyEditText = findViewById(R.id.edit_text_company);
        phoneEditText = findViewById(R.id.edit_text_phone);
        emailEditText = findViewById(R.id.edit_text_email);
        pictureImageView = findViewById(R.id.edit_imageView);
        saveButton = findViewById(R.id.button_save);
        addButton = findViewById(R.id.add_button);

        display = getWindowManager().getDefaultDisplay();
        // allowing permissions of gallery and camera
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
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
            }
            if(picture != null) {
                // Convert Bitmap to byte array
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                picture.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String encodedString = Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT);
                // Encode byte array to Base64 string
                businessCards.add(new BusinessCard(encodedString, firstName, lastName, company, phone, email));
            } else {
                businessCards.add(new BusinessCard(firstName, lastName, company, phone, email));
            }
            // Save businessCards to JSON file
            writeBusinessCardsToJson(businessCards);
            Intent intent = new Intent(AddBusinessCardActivity.this, MainActivity.class);
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
    // checking storage permissions
    private Boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    // Requesting gallery permission
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
    public void onBackPressed(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    // Requesting camera and gallery
    // permission if not given
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0) {
                    boolean camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (camera_accepted && writeStorageaccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please Enable Camera and Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean writeStorageaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageaccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
    }

    // Here we will pick image from gallery or camera
    private void pickFromGallery() {
        // Launch the crop activity with the given options
        CropImage.activity()
                .start(this);
    }
}

