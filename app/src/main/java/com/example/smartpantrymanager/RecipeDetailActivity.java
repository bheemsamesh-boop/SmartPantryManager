package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView tvRecipeDetailName;
    private TextView tvRecipeIngredients;
    private TextView tvRecipeInstructions;

    private Button btnCookRecipe;

    private DatabaseHelper databaseHelper;

    private int recipeId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Toolbar toolbar = findViewById(R.id.recipeDetailToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvRecipeDetailName = findViewById(R.id.tvRecipeDetailName);
        tvRecipeIngredients = findViewById(R.id.tvRecipeIngredients);
        tvRecipeInstructions = findViewById(R.id.tvRecipeInstructions);

        btnCookRecipe = findViewById(R.id.btnCookRecipe);

        databaseHelper = new DatabaseHelper(this);

        // Get the recipe ID sent from Suggested Recipes
        recipeId = getIntent().getIntExtra("recipe_id", -1);

        if (recipeId != -1) {
            loadRecipeDetails(recipeId);
        }

        btnCookRecipe.setOnClickListener(v -> showCookConfirmation());
    }

    private void loadRecipeDetails(int recipeId) {

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        // Get the recipe name and instructions
        Cursor recipeCursor = db.rawQuery(
                "SELECT name, instructions FROM recipes WHERE id = ?",
                new String[]{String.valueOf(recipeId)}
        );

        if (recipeCursor.moveToFirst()) {

            String name = recipeCursor.getString(
                    recipeCursor.getColumnIndexOrThrow("name")
            );

            String instructions = recipeCursor.getString(
                    recipeCursor.getColumnIndexOrThrow("instructions")
            );

            tvRecipeDetailName.setText(name);
            tvRecipeInstructions.setText(instructions);
        }

        recipeCursor.close();

        // Get all the ingredients needed for this recipe
        Cursor ingredientCursor = db.rawQuery(
                "SELECT ingredient_name, required_quantity, unit " +
                        "FROM recipe_ingredients WHERE recipe_id = ?",
                new String[]{String.valueOf(recipeId)}
        );

        StringBuilder ingredientsText = new StringBuilder();

        while (ingredientCursor.moveToNext()) {

            String ingredientName = ingredientCursor.getString(
                    ingredientCursor.getColumnIndexOrThrow("ingredient_name")
            );

            double quantity = ingredientCursor.getDouble(
                    ingredientCursor.getColumnIndexOrThrow("required_quantity")
            );

            String unit = ingredientCursor.getString(
                    ingredientCursor.getColumnIndexOrThrow("unit")
            );

            ingredientsText.append("• ")
                    .append(ingredientName)
                    .append(" - ")
                    .append(formatQuantity(quantity))
                    .append(" ")
                    .append(unit)
                    .append("\n");
        }

        ingredientCursor.close();

        tvRecipeIngredients.setText(ingredientsText.toString());
    }

    private void showCookConfirmation() {

        new AlertDialog.Builder(this)
                .setTitle("Cook Recipe")
                .setMessage(
                        "This will use the required ingredients from your pantry. Continue?"
                )
                .setPositiveButton("Cook", (dialog, which) -> cookRecipe())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void cookRecipe() {

        if (recipeId == -1) {
            return;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        /*
         * Check everything before changing the pantry.
         * This makes sure the recipe can still be made.
         */
        Cursor checkCursor = db.rawQuery(
                "SELECT ingredient_name, required_quantity, unit " +
                        "FROM recipe_ingredients WHERE recipe_id = ?",
                new String[]{String.valueOf(recipeId)}
        );

        while (checkCursor.moveToNext()) {

            String ingredientName = checkCursor.getString(
                    checkCursor.getColumnIndexOrThrow("ingredient_name")
            );

            double requiredQuantity = checkCursor.getDouble(
                    checkCursor.getColumnIndexOrThrow("required_quantity")
            );

            String requiredUnit = checkCursor.getString(
                    checkCursor.getColumnIndexOrThrow("unit")
            );

            double availableQuantity =
                    getAvailableQuantity(
                            db,
                            ingredientName,
                            requiredUnit
                    );

            double requiredBaseQuantity =
                    convertToBaseUnit(
                            requiredQuantity,
                            requiredUnit
                    );

            if (availableQuantity < requiredBaseQuantity) {

                checkCursor.close();

                Toast.makeText(
                        this,
                        "You no longer have enough " + ingredientName,
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }
        }

        checkCursor.close();

        db.beginTransaction();

        try {

            Cursor ingredientCursor = db.rawQuery(
                    "SELECT ingredient_name, required_quantity, unit " +
                            "FROM recipe_ingredients WHERE recipe_id = ?",
                    new String[]{String.valueOf(recipeId)}
            );

            while (ingredientCursor.moveToNext()) {

                String ingredientName = ingredientCursor.getString(
                        ingredientCursor.getColumnIndexOrThrow(
                                "ingredient_name"
                        )
                );

                double requiredQuantity = ingredientCursor.getDouble(
                        ingredientCursor.getColumnIndexOrThrow(
                                "required_quantity"
                        )
                );

                String requiredUnit = ingredientCursor.getString(
                        ingredientCursor.getColumnIndexOrThrow("unit")
                );

                deductIngredient(
                        db,
                        ingredientName,
                        requiredQuantity,
                        requiredUnit
                );
            }

            ingredientCursor.close();

            db.setTransactionSuccessful();

            Toast.makeText(
                    this,
                    "Recipe cooked! Pantry updated.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } finally {

            db.endTransaction();
        }
    }

    private double getAvailableQuantity(
            SQLiteDatabase db,
            String ingredientName,
            String requiredUnit) {

        double totalAvailable = 0;

        Cursor cursor = db.rawQuery(
                "SELECT name, quantity, unit FROM pantry",
                null
        );

        while (cursor.moveToNext()) {

            String pantryName = cursor.getString(
                    cursor.getColumnIndexOrThrow("name")
            );

            double pantryQuantity = cursor.getDouble(
                    cursor.getColumnIndexOrThrow("quantity")
            );

            String pantryUnit = cursor.getString(
                    cursor.getColumnIndexOrThrow("unit")
            );

            if (normalizeIngredientName(pantryName)
                    .equals(normalizeIngredientName(ingredientName))
                    && getUnitType(pantryUnit)
                    .equals(getUnitType(requiredUnit))) {

                totalAvailable +=
                        convertToBaseUnit(
                                pantryQuantity,
                                pantryUnit
                        );
            }
        }

        cursor.close();

        return totalAvailable;
    }

    private void deductIngredient(
            SQLiteDatabase db,
            String ingredientName,
            double requiredQuantity,
            String requiredUnit) {

        double amountNeeded =
                convertToBaseUnit(
                        requiredQuantity,
                        requiredUnit
                );

        Cursor cursor = db.rawQuery(
                "SELECT id, name, quantity, unit FROM pantry",
                null
        );

        while (cursor.moveToNext() && amountNeeded > 0) {

            int pantryId = cursor.getInt(
                    cursor.getColumnIndexOrThrow("id")
            );

            String pantryName = cursor.getString(
                    cursor.getColumnIndexOrThrow("name")
            );

            double pantryQuantity = cursor.getDouble(
                    cursor.getColumnIndexOrThrow("quantity")
            );

            String pantryUnit = cursor.getString(
                    cursor.getColumnIndexOrThrow("unit")
            );

            if (normalizeIngredientName(pantryName)
                    .equals(normalizeIngredientName(ingredientName))
                    && getUnitType(pantryUnit)
                    .equals(getUnitType(requiredUnit))) {

                double availableAmount =
                        convertToBaseUnit(
                                pantryQuantity,
                                pantryUnit
                        );

                if (availableAmount <= amountNeeded) {

                    // All of this pantry item was used
                    db.delete(
                            "pantry",
                            "id = ?",
                            new String[]{String.valueOf(pantryId)}
                    );

                    amountNeeded -= availableAmount;

                } else {

                    // Only part of this pantry item was used
                    double remainingBaseAmount =
                            availableAmount - amountNeeded;

                    double newQuantity =
                            convertFromBaseUnit(
                                    remainingBaseAmount,
                                    pantryUnit
                            );

                    ContentValues values = new ContentValues();

                    values.put(
                            "quantity",
                            newQuantity
                    );

                    db.update(
                            "pantry",
                            values,
                            "id = ?",
                            new String[]{String.valueOf(pantryId)}
                    );

                    amountNeeded = 0;
                }
            }
        }

        cursor.close();
    }

    private String getUnitType(String unit) {

        unit = unit.trim().toLowerCase();

        switch (unit) {

            case "ml":
            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return "volume";

            case "g":
            case "gram":
            case "grams":
            case "kg":
            case "kilogram":
            case "kilograms":
                return "weight";

            case "pc":
            case "pcs":
            case "piece":
            case "pieces":
                return "pieces";

            case "slice":
            case "slices":
                return "slices";

            default:
                return unit;
        }
    }

    private double convertToBaseUnit(
            double quantity,
            String unit) {

        unit = unit.trim().toLowerCase();

        switch (unit) {

            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return quantity * 1000;

            case "kg":
            case "kilogram":
            case "kilograms":
                return quantity * 1000;

            default:
                return quantity;
        }
    }

    private double convertFromBaseUnit(
            double quantity,
            String originalUnit) {

        originalUnit = originalUnit.trim().toLowerCase();

        switch (originalUnit) {

            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return quantity / 1000;

            case "kg":
            case "kilogram":
            case "kilograms":
                return quantity / 1000;

            default:
                return quantity;
        }
    }

    private String normalizeIngredientName(String name) {

        name = name.trim().toLowerCase();

        if (name.equals("tomatoes")) {
            return "tomato";
        }

        if (name.equals("potatoes")) {
            return "potato";
        }

        if (name.endsWith("s") && name.length() > 1) {
            name = name.substring(0, name.length() - 1);
        }

        return name;
    }

    private String formatQuantity(double quantity) {

        if (quantity == Math.floor(quantity)) {
            return String.valueOf((int) quantity);
        }

        return String.valueOf(quantity);
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