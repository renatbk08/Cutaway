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
    import android.os.Build;
    import android.os.Bundle;
    import android.util.Base64;
    import android.util.Log;
    import android.view.Display;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.annotation.RequiresApi;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.content.ContextCompat;

    import com.google.android.material.floatingactionbutton.FloatingActionButton;
    import com.google.gson.Gson;
    import com.google.gson.GsonBuilder;
    import com.google.gson.JsonIOException;
    import com.google.gson.reflect.TypeToken;
    import com.theartofdev.edmodo.cropper.CropImage;

    import java.io.BufferedReader;
    import java.io.ByteArrayOutputStream;
    import java.io.FileInputStream;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.io.InputStreamReader;
    import java.lang.reflect.Type;
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
    private static int width, height;
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_business_card);
        mContext = getApplicationContext();
        businessCards = getBusinessCards();
        firstNameEditText = findViewById(R.id.edit_text_fName);
        lastNameEditText = findViewById(R.id.edit_text_lName);
        companyEditText = findViewById(R.id.edit_text_company);
        phoneEditText = findViewById(R.id.edit_text_phone);
        emailEditText = findViewById(R.id.edit_text_email);
        pictureImageView = findViewById(R.id.edit_imageView);
        saveButton = findViewById(R.id.button_save);
        addButton = findViewById(R.id.add_button);
        display = getWindowManager().getDefaultDisplay();
        width = display.getWidth(); // deprecated
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
            Intent resultIntent = new Intent();
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
            try {
                FileOutputStream outputStream = mContext.openFileOutput("business_cards.json", Context.MODE_PRIVATE | Context.MODE_APPEND);
                String json = convertListToJson(businessCards);
                outputStream.write(json.getBytes());
                outputStream.close();
                Intent intent = new Intent(AddBusinessCardActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    private String convertListToJson(ArrayList<BusinessCard> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
    public ArrayList<BusinessCard> getBusinessCards() {
        businessCards = new ArrayList<>();
        try {
            FileInputStream inputStream = mContext.openFileInput("business_cards.json");
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
            String json = builder.toString();
            if (!json.isEmpty()) {
                Gson gson = new GsonBuilder().setLenient().create();
                Type type = new TypeToken<ArrayList<BusinessCard>>() {}.getType();
                try {
                    businessCards = gson.fromJson(json, type);
                } catch (JsonIOException e) {
                    Log.e("TAG", "Error reading JSON format", e);
                }
            }
            inputStream.close();
        } catch (IOException e) {
            Log.e("TAG", "Ошибка чтения файла при запуске:", e);
        }
        return businessCards;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
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
                .start(this);
    }
}

