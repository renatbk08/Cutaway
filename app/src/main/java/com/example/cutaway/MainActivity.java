package com.example.cutaway;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BusinessCardAdapter.BusinessCardAdapterCallback {
    private RecyclerView recyclerView;
    private ArrayList<BusinessCard> businessCards = new ArrayList<>();
    private TextView noCardsTextView;
    private BusinessCardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        noCardsTextView = findViewById(R.id.no_cards_text_view);
        FloatingActionButton addButton = findViewById(R.id.add_button);
        businessCards = readBusinessCardsFromJson();
        adapter = new BusinessCardAdapter(this);
        adapter.setCallback(this);

        if (businessCards.isEmpty()) {
            noCardsTextView.setVisibility(View.VISIBLE);
        } else {
            noCardsTextView.setVisibility(View.GONE);
            setupRecyclerView();
        }

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddBusinessCardActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }

    private String convertListToJson(ArrayList<BusinessCard> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    /*private void saveBusinessCards(ArrayList<BusinessCard> businessCards) {
        try {
            FileOutputStream outputStream = openFileOutput("business_cards.json", Context.MODE_PRIVATE);
            String json = convertListToJson(businessCards);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (IOException e) {
            Log.e("TAG", "Error writing file", e);
            Toast.makeText(this, "Error writing file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }*/
    public static void writeBusinessCardsToJson(ArrayList<BusinessCard> businessCards) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("business_cards.json", false)) {
            gson.toJson(businessCards, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<BusinessCard> readBusinessCardsFromJson() {
        Gson gson = new GsonBuilder().create();
        ArrayList<BusinessCard> businessCards = new ArrayList<>();
        try (FileReader reader = new FileReader("business_cards.json")) {
            Type listType = new TypeToken<ArrayList<BusinessCard>>() {}.getType();
            businessCards = gson.fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return businessCards;
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*saveBusinessCards*/writeBusinessCardsToJson(businessCards);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        /*saveBusinessCards*/writeBusinessCardsToJson(businessCards);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*saveBusinessCards*/writeBusinessCardsToJson(businessCards);
    }

    @Override
    public void onCardRemoved(int position) {
        if (position >= 0) {
            businessCards.remove(position);
        } else {
            businessCards.clear();
        }
        /*saveBusinessCards*/writeBusinessCardsToJson(businessCards);

        if (businessCards.isEmpty()) {
            noCardsTextView.setVisibility(View.VISIBLE);
        } else {
            noCardsTextView.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }
}



