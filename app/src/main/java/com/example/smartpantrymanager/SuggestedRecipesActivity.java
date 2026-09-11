package com.example.smartpantrymanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRecipes;
    private TextView tvSuggestedRecipesMessage;

    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        recyclerViewRecipes = findViewById(R.id.recyclerViewRecipes);
        tvSuggestedRecipesMessage = findViewById(R.id.tvSuggestedRecipesMessage);

        databaseHelper = new DatabaseHelper(this);
        recipeList = new ArrayList<>();

        recyclerViewRecipes.setLayoutManager(new LinearLayoutManager(this));

        recipeAdapter = new RecipeAdapter(
                SuggestedRecipesActivity.this,
                recipeList
        );

        recyclerViewRecipes.setAdapter(recipeAdapter);

        loadRecipes();
    }

    private void loadRecipes() {

        recipeList.clear();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM recipes ORDER BY name ASC",
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

                String instructions = cursor.getString(
                        cursor.getColumnIndexOrThrow("instructions")
                );

                if (canMakeRecipe(id)) {

                    Recipe recipe = new Recipe(
                            id,
                            name,
                            instructions
                    );

                    recipeList.add(recipe);
                }

            } while (cursor.moveToNext());
        }

        cursor.close();

        recipeAdapter.notifyDataSetChanged();

        if (recipeList.isEmpty()) {
            tvSuggestedRecipesMessage.setVisibility(View.VISIBLE);
        } else {
            tvSuggestedRecipesMessage.setVisibility(View.GONE);
        }
    }

    private boolean canMakeRecipe(int recipeId) {

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor ingredientCursor = db.rawQuery(
                "SELECT ingredient_name, required_quantity, unit " +
                        "FROM recipe_ingredients WHERE recipe_id = ?",
                new String[]{String.valueOf(recipeId)}
        );

        while (ingredientCursor.moveToNext()) {

            String ingredientName = ingredientCursor.getString(
                    ingredientCursor.getColumnIndexOrThrow("ingredient_name")
            );

            double requiredQuantity = ingredientCursor.getDouble(
                    ingredientCursor.getColumnIndexOrThrow("required_quantity")
            );

            String requiredUnit = ingredientCursor.getString(
                    ingredientCursor.getColumnIndexOrThrow("unit")
            );

            Cursor pantryCursor = db.rawQuery(
                    "SELECT quantity, unit FROM pantry " +
                            "WHERE LOWER(name) = LOWER(?)",
                    new String[]{ingredientName}
            );

            double totalAvailable = 0;
            boolean compatibleUnitFound = false;

            while (pantryCursor.moveToNext()) {

                double pantryQuantity = pantryCursor.getDouble(
                        pantryCursor.getColumnIndexOrThrow("quantity")
                );

                String pantryUnit = pantryCursor.getString(
                        pantryCursor.getColumnIndexOrThrow("unit")
                );

                if (getUnitType(pantryUnit).equals(getUnitType(requiredUnit))) {

                    totalAvailable += convertToBaseUnit(
                            pantryQuantity,
                            pantryUnit
                    );

                    compatibleUnitFound = true;
                }
            }

            pantryCursor.close();

            if (!compatibleUnitFound) {
                ingredientCursor.close();
                return false;
            }

            double requiredBaseQuantity =
                    convertToBaseUnit(requiredQuantity, requiredUnit);

            if (totalAvailable < requiredBaseQuantity) {
                ingredientCursor.close();
                return false;
            }
        }

        ingredientCursor.close();

        return true;
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

    private double convertToBaseUnit(double quantity, String unit) {

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
}