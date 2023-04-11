    package com.example.cutaway;

    import android.Manifest;
    import android.content.Context;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.content.pm.PackageManager;
    import android.graphics.Bitmap;
    import android.graphics.drawable.BitmapDrawable;
    import android.net.Uri;
    import android.os.Build;
    import android.os.Bundle;
    import android.util.Base64;
    import android.view.Display;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.annotation.RequiresApi;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.content.ContextCompat;

    import com.google.android.material.floatingactionbutton.FloatingActionButton;
    import com.theartofdev.edmodo.cropper.CropImage;

    import java.io.ByteArrayOutputStream;
    import java.util.ArrayList;

public class AddBusinessCardActivity extends AppCompatActivity {
    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText companyEditText;
    EditText phoneEditText;
    EditText emailEditText;
    TextView photoPlaceTextView;
    ImageView pictureImageView, mImageView;
    Button saveButton;
    FloatingActionButton addButton;
    ArrayList<BusinessCard> businessCards;
    String cameraPermission[];
    String storagePermission[];
    Display display;
    private static int width, height;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int GalleryPick = 1;
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private static final int IMAGE_PICKCAMERA_REQUEST = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_business_card);
        mImageView = findViewById(R.id.mImageView);
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
        display = getWindowManager().getDefaultDisplay();
        width = display.getWidth(); // deprecated
        height = display.getHeight();  // deprecated
        // allowing permissions of gallery and camera
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    pickFromGallery();
                }
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
                    Intent resultIntent = new Intent();
                    if(picture != null) {
                        String fileName = "myImage"; //no .png or .jpg needed
                        try {
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            picture.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                            String encodedString = Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT);
                            SharedPreferences preferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("image_data", encodedString);
                            editor.apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                            fileName = null;
                        }
                        businessCards.add(new BusinessCard(true, picture, firstName, lastName, company, phone, email));
                        resultIntent.putExtra("newBusinessCard", new BusinessCard(true, firstName, lastName, company, phone, email));
                    } else {
                        resultIntent.putExtra("newBusinessCard", new BusinessCard(false, firstName, lastName, company, phone, email));
                        businessCards.add(new BusinessCard(false, firstName, lastName, company, phone, email));
                    }
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            photoPlaceTextView.setVisibility(View.GONE);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
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
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }

    // checking camera permissions
    private Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    // Requesting camera permission
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);
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
                .setMaxCropResultSize(width, width)
                .start(this);
    }
}

