package com.example.smartpantrymanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView tvRecipeDetailName;
    private TextView tvRecipeIngredients;
    private TextView tvRecipeInstructions;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        tvRecipeDetailName = findViewById(R.id.tvRecipeDetailName);
        tvRecipeIngredients = findViewById(R.id.tvRecipeIngredients);
        tvRecipeInstructions = findViewById(R.id.tvRecipeInstructions);

        databaseHelper = new DatabaseHelper(this);

        int recipeId = getIntent().getIntExtra("recipe_id", -1);

        if (recipeId != -1) {
            loadRecipeDetails(recipeId);
        }
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

        // Get all required ingredients
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

    private String formatQuantity(double quantity) {

        if (quantity == Math.floor(quantity)) {
            return String.valueOf((int) quantity);
        }

        return String.valueOf(quantity);
    }
}