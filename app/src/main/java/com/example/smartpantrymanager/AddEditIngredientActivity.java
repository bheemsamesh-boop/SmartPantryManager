package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuItem;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AlertDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText etIngredientName;
    private EditText etQuantity;
    private EditText etUnit;
    private EditText etExpiryDate;

    private Button btnSaveIngredient;
    private Button btnDeleteIngredient;

    private TextView tvIngredientTitle;

    private DatabaseHelper databaseHelper;

    private int ingredientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        Toolbar toolbar = findViewById(R.id.addEditToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etIngredientName = findViewById(R.id.etIngredientName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etExpiryDate = findViewById(R.id.etExpiryDate);

        btnSaveIngredient = findViewById(R.id.btnSaveIngredient);
        btnDeleteIngredient = findViewById(R.id.btnDeleteIngredient);

        tvIngredientTitle = findViewById(R.id.tvIngredientTitle);

        databaseHelper = new DatabaseHelper(this);

        // Checks if the existing ingredient was selected
        ingredientId = getIntent().getIntExtra("ingredient_id", -1);

        if (ingredientId != -1) {

            // Edit mode
            tvIngredientTitle.setText("Edit Ingredient");
            btnDeleteIngredient.setVisibility(View.VISIBLE);

            loadIngredientData();

        } else {

            // Add mode
            tvIngredientTitle.setText("Add Ingredient");
            btnDeleteIngredient.setVisibility(View.GONE);
        }

        btnSaveIngredient.setOnClickListener(v -> saveIngredient());

        btnDeleteIngredient.setOnClickListener(v -> deleteIngredient());
    }

    private void loadIngredientData() {

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        // Get the ingredient that was selected on the pantry screen
        Cursor cursor = db.rawQuery(
                "SELECT * FROM pantry WHERE id = ?",
                new String[]{String.valueOf(ingredientId)}
        );

        if (cursor.moveToFirst()) {

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

            etIngredientName.setText(name);
            etQuantity.setText(String.valueOf(quantity));
            etUnit.setText(unit);
            etExpiryDate.setText(expiryDate);

            btnSaveIngredient.setText("Update Ingredient");
        }

        cursor.close();
    }

    private void saveIngredient() {

        String name =
                etIngredientName.getText().toString().trim();

        String quantityText =
                etQuantity.getText().toString().trim();

        String unit =
                etUnit.getText().toString().trim();

        String expiryDate =
                etExpiryDate.getText().toString().trim();

        // Name, quantity and unit are a must
        if (name.isEmpty()
                || quantityText.isEmpty()
                || unit.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter name, quantity and unit",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Expiry date is not needed
        if (!expiryDate.isEmpty()) {

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    );

            dateFormat.setLenient(false);

            try {

                dateFormat.parse(expiryDate);

            } catch (ParseException e) {

                Toast.makeText(
                        this,
                        "Expiry date must be YYYY-MM-DD",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }
        }

        double quantity;

        // Makes sure the quantity is actually a number
        try {

            quantity = Double.parseDouble(quantityText);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Please enter a valid quantity",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (quantity <= 0) {

            Toast.makeText(
                    this,
                    "Quantity must be greater than 0",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);
        values.put("expiry_date", expiryDate);

        boolean success;

        if (ingredientId == -1) {

            // Adds a new ingredient
            long result = db.insert(
                    "pantry",
                    null,
                    values
            );

            success = result != -1;

        } else {

            int result = db.update(
                    "pantry",
                    values,
                    "id = ?",
                    new String[]{String.valueOf(ingredientId)}
            );

            success = result > 0;
        }

        if (success) {

            if (ingredientId == -1) {

                Toast.makeText(
                        this,
                        "Ingredient saved successfully",
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                Toast.makeText(
                        this,
                        "Ingredient updated successfully",
                        Toast.LENGTH_SHORT
                ).show();
            }

            // Close the current screen and returns to the pantry
            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed to save ingredient",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void deleteIngredient() {

        SharedPreferences preferences =
                getSharedPreferences(
                        "SmartPantrySettings",
                        MODE_PRIVATE
                );

        boolean confirmDelete =
                preferences.getBoolean(
                        "confirm_before_delete",
                        true
                );

        if (confirmDelete) {

            new AlertDialog.Builder(this)
                    .setTitle("Delete Ingredient")
                    .setMessage(
                            "Are you sure you want to delete this ingredient?"
                    )
                    .setPositiveButton(
                            "Delete",
                            (dialog, which) -> performDeleteIngredient()
                    )
                    .setNegativeButton(
                            "Cancel",
                            null
                    )
                    .show();

        } else {

            performDeleteIngredient();
        }
    }

    private void performDeleteIngredient() {

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        int result = db.delete(
                "pantry",
                "id = ?",
                new String[]{String.valueOf(ingredientId)}
        );

        if (result > 0) {

            Toast.makeText(
                    this,
                    "Ingredient deleted successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed to delete ingredient",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}