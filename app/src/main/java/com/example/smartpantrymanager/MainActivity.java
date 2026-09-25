package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnAddIngredient;

    private RecyclerView recyclerViewPantry;
    private TextView tvPantryMessage;

    private PantryAdapter pantryAdapter;
    private List<PantryItem> pantryItems;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sets up the toolbar at the top of the screen
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        // Show a back arrow to return to Home
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        recyclerViewPantry = findViewById(R.id.recyclerViewPantry);
        tvPantryMessage = findViewById(R.id.tvPantryMessage);

        databaseHelper = new DatabaseHelper(this);

        pantryItems = new ArrayList<>();

        recyclerViewPantry.setLayoutManager(
                new LinearLayoutManager(this)
        );

        pantryAdapter = new PantryAdapter(
                this,
                pantryItems
        );

        recyclerViewPantry.setAdapter(pantryAdapter);

        btnAddIngredient.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    AddEditIngredientActivity.class
            );

            startActivity(intent);
        });

        loadPantryItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Loads the three dot menu
        getMenuInflater().inflate(
                R.menu.main_menu,
                menu
        );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        // Open suggested recipes from the toolbar menu
        if (id == R.id.menuSuggestedRecipes) {

            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );

            startActivity(intent);

            return true;
        }

        // Opens settings from the toolbar menu
        if (id == R.id.menuSettings) {

            Intent intent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );

            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadPantryItems() {

        pantryItems.clear();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        // Gets all pantry items and sorts them by name
        Cursor cursor = db.rawQuery(
                "SELECT * FROM pantry ORDER BY name ASC",
                null
        );

        if (cursor.moveToFirst()) {

            do {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name")
                );

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow("quantity")
                );

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow("unit")
                );

                String expiryDate = cursor.getString(
                        cursor.getColumnIndexOrThrow("expiry_date")
                );

                PantryItem item = new PantryItem(
                        id,
                        name,
                        quantity,
                        unit,
                        expiryDate
                );

                pantryItems.add(item);

            } while (cursor.moveToNext());
        }

        cursor.close();

        pantryAdapter.notifyDataSetChanged();

        // Show a message if the pantry has no ingredients
        if (pantryItems.isEmpty()) {
            tvPantryMessage.setVisibility(View.VISIBLE);
        } else {
            tvPantryMessage.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reloads the pantry when coming back from another screen
        if (pantryAdapter != null) {
            loadPantryItems();
        }
    }
}