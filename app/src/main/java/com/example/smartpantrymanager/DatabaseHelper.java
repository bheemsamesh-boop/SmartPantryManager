package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 3;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        String createPantryTable =
                "CREATE TABLE pantry (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL, " +
                        "expiry_date TEXT" +
                        ")";

        db.execSQL(createPantryTable);

        String createRecipesTable =
                "CREATE TABLE recipes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "instructions TEXT NOT NULL" +
                        ")";

        db.execSQL(createRecipesTable);

        String createRecipeIngredientsTable =
                "CREATE TABLE recipe_ingredients (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "recipe_id INTEGER NOT NULL, " +
                        "ingredient_name TEXT NOT NULL, " +
                        "required_quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL" +
                        ")";

        db.execSQL(createRecipeIngredientsTable);

        preloadRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {

            String createRecipesTable =
                    "CREATE TABLE IF NOT EXISTS recipes (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "name TEXT NOT NULL, " +
                            "instructions TEXT NOT NULL" +
                            ")";

            db.execSQL(createRecipesTable);

            String createRecipeIngredientsTable =
                    "CREATE TABLE IF NOT EXISTS recipe_ingredients (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "recipe_id INTEGER NOT NULL, " +
                            "ingredient_name TEXT NOT NULL, " +
                            "required_quantity REAL NOT NULL, " +
                            "unit TEXT NOT NULL" +
                            ")";

            db.execSQL(createRecipeIngredientsTable);
        }
        if (oldVersion < 3) {
            preloadRecipes(db);
        }
    }

    private long insertRecipe(
            SQLiteDatabase db,
            String name,
            String instructions) {

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("instructions", instructions);

        return db.insert("recipes", null, values);
    }

    private void insertRecipeIngredient(
            SQLiteDatabase db,
            long recipeId,
            String ingredientName,
            double requiredQuantity,
            String unit) {

        ContentValues values = new ContentValues();

        values.put("recipe_id", recipeId);
        values.put("ingredient_name", ingredientName);
        values.put("required_quantity", requiredQuantity);
        values.put("unit", unit);

        db.insert("recipe_ingredients", null, values);
    }

    private void preloadRecipes(SQLiteDatabase db) {

        // Recipe 1: Scrambled Eggs
        long scrambledEggsId = insertRecipe(
                db,
                "Scrambled Eggs",
                "Beat the eggs with milk. Melt the butter in a pan, add the egg mixture and stir until cooked."
        );
        insertRecipeIngredient(db, scrambledEggsId, "Egg", 2, "pcs");
        insertRecipeIngredient(db, scrambledEggsId, "Milk", 50, "ml");
        insertRecipeIngredient(db, scrambledEggsId, "Butter", 10, "g");

        // Recipe 2: Cheese Sandwich
        long cheeseSandwichId = insertRecipe(
                db,
                "Cheese Sandwich",
                "Place the cheese between two slices of bread and serve."
        );
        insertRecipeIngredient(db, cheeseSandwichId, "Bread", 2, "slices");
        insertRecipeIngredient(db, cheeseSandwichId, "Cheese", 2, "slices");

        // Recipe 3: Peanut Butter Toast
        long peanutButterToastId = insertRecipe(
                db,
                "Peanut Butter Toast",
                "Toast the bread and spread peanut butter evenly over each slice."
        );
        insertRecipeIngredient(db, peanutButterToastId, "Bread", 2, "slices");
        insertRecipeIngredient(db, peanutButterToastId, "Peanut Butter", 30, "g");

        // Recipe 4: Banana Smoothie
        long bananaSmoothieId = insertRecipe(
                db,
                "Banana Smoothie",
                "Add the banana and milk to a blender and blend until smooth."
        );
        insertRecipeIngredient(db, bananaSmoothieId, "Banana", 1, "pcs");
        insertRecipeIngredient(db, bananaSmoothieId, "Milk", 250, "ml");

        // Recipe 5: Fried Egg on Toast
        long friedEggToastId = insertRecipe(
                db,
                "Fried Egg on Toast",
                "Toast the bread. Fry the egg in butter and place it on top of the toast."
        );
        insertRecipeIngredient(db, friedEggToastId, "Bread", 1, "slices");
        insertRecipeIngredient(db, friedEggToastId, "Egg", 1, "pcs");
        insertRecipeIngredient(db, friedEggToastId, "Butter", 5, "g");

        // Recipe 6: Cheese Omelette
        long cheeseOmeletteId = insertRecipe(
                db,
                "Cheese Omelette",
                "Beat the eggs with milk. Cook in a buttered pan, add cheese and fold the omelette."
        );
        insertRecipeIngredient(db, cheeseOmeletteId, "Egg", 2, "pcs");
        insertRecipeIngredient(db, cheeseOmeletteId, "Milk", 30, "ml");
        insertRecipeIngredient(db, cheeseOmeletteId, "Cheese", 30, "g");
        insertRecipeIngredient(db, cheeseOmeletteId, "Butter", 5, "g");

        // Recipe 7: Banana Peanut Butter Toast
        long bananaPbToastId = insertRecipe(
                db,
                "Banana Peanut Butter Toast",
                "Toast the bread, spread with peanut butter and top with sliced banana."
        );
        insertRecipeIngredient(db, bananaPbToastId, "Bread", 2, "slices");
        insertRecipeIngredient(db, bananaPbToastId, "Peanut Butter", 30, "g");
        insertRecipeIngredient(db, bananaPbToastId, "Banana", 1, "pcs");

        // Recipe 8: Cheese Toast
        long cheeseToastId = insertRecipe(
                db,
                "Cheese Toast",
                "Place cheese on the bread and toast until the cheese has melted."
        );
        insertRecipeIngredient(db, cheeseToastId, "Bread", 2, "slices");
        insertRecipeIngredient(db, cheeseToastId, "Cheese", 40, "g");

        // Recipe 9: Tomato Sandwich
        long tomatoSandwichId = insertRecipe(
                db,
                "Tomato Sandwich",
                "Slice the tomato, place it between the bread slices and season with salt."
        );
        insertRecipeIngredient(db, tomatoSandwichId, "Bread", 2, "slices");
        insertRecipeIngredient(db, tomatoSandwichId, "Tomato", 1, "pcs");
        insertRecipeIngredient(db, tomatoSandwichId, "Salt", 1, "g");

        // Recipe 10: Boiled Eggs
        long boiledEggsId = insertRecipe(
                db,
                "Boiled Eggs",
                "Place the eggs in boiling water and cook until they reach your preferred firmness."
        );
        insertRecipeIngredient(db, boiledEggsId, "Egg", 2, "pcs");

        // Recipe 11: Egg Sandwich
        long eggSandwichId = insertRecipe(
                db,
                "Egg Sandwich",
                "Boil the eggs, slice them and place them between the bread slices."
        );
        insertRecipeIngredient(db, eggSandwichId, "Egg", 2, "pcs");
        insertRecipeIngredient(db, eggSandwichId, "Bread", 2, "slices");


// Recipe 12: Tomato and Cheese Sandwich
        long tomatoCheeseSandwichId = insertRecipe(
                db,
                "Tomato and Cheese Sandwich",
                "Slice the tomato and place it on the bread with the cheese."
        );
        insertRecipeIngredient(db, tomatoCheeseSandwichId, "Bread", 2, "slices");
        insertRecipeIngredient(db, tomatoCheeseSandwichId, "Tomato", 1, "pcs");
        insertRecipeIngredient(db, tomatoCheeseSandwichId, "Cheese", 2, "slices");


// Recipe 13: Banana Milkshake
        long bananaMilkshakeId = insertRecipe(
                db,
                "Banana Milkshake",
                "Blend the banana, milk and sugar together until smooth."
        );
        insertRecipeIngredient(db, bananaMilkshakeId, "Banana", 1, "pcs");
        insertRecipeIngredient(db, bananaMilkshakeId, "Milk", 250, "ml");
        insertRecipeIngredient(db, bananaMilkshakeId, "Sugar", 10, "g");


// Recipe 14: French Toast
        long frenchToastId = insertRecipe(
                db,
                "French Toast",
                "Beat the egg with milk. Dip the bread into the mixture and fry in butter until golden."
        );
        insertRecipeIngredient(db, frenchToastId, "Bread", 2, "slices");
        insertRecipeIngredient(db, frenchToastId, "Egg", 1, "pcs");
        insertRecipeIngredient(db, frenchToastId, "Milk", 50, "ml");
        insertRecipeIngredient(db, frenchToastId, "Butter", 10, "g");


// Recipe 15: Tomato Omelette
        long tomatoOmeletteId = insertRecipe(
                db,
                "Tomato Omelette",
                "Beat the eggs, add chopped tomato and cook the mixture in a buttered pan."
        );
        insertRecipeIngredient(db, tomatoOmeletteId, "Egg", 2, "pcs");
        insertRecipeIngredient(db, tomatoOmeletteId, "Tomato", 1, "pcs");
        insertRecipeIngredient(db, tomatoOmeletteId, "Butter", 5, "g");


// Recipe 16: Cheese and Egg Toast
        long cheeseEggToastId = insertRecipe(
                db,
                "Cheese and Egg Toast",
                "Toast the bread, cook the egg and place it on the toast with cheese."
        );
        insertRecipeIngredient(db, cheeseEggToastId, "Bread", 1, "slices");
        insertRecipeIngredient(db, cheeseEggToastId, "Egg", 1, "pcs");
        insertRecipeIngredient(db, cheeseEggToastId, "Cheese", 1, "slices");


// Recipe 17: Peanut Butter Banana Smoothie
        long pbBananaSmoothieId = insertRecipe(
                db,
                "Peanut Butter Banana Smoothie",
                "Blend the banana, milk and peanut butter together until smooth."
        );
        insertRecipeIngredient(db, pbBananaSmoothieId, "Banana", 1, "pcs");
        insertRecipeIngredient(db, pbBananaSmoothieId, "Milk", 250, "ml");
        insertRecipeIngredient(db, pbBananaSmoothieId, "Peanut Butter", 20, "g");


// Recipe 18: Buttered Toast
        long butteredToastId = insertRecipe(
                db,
                "Buttered Toast",
                "Toast the bread until golden and spread butter over each slice."
        );
        insertRecipeIngredient(db, butteredToastId, "Bread", 2, "slices");
        insertRecipeIngredient(db, butteredToastId, "Butter", 10, "g");


// Recipe 19: Tomato Cheese Toast
        long tomatoCheeseToastId = insertRecipe(
                db,
                "Tomato Cheese Toast",
                "Top the bread with sliced tomato and cheese, then toast until the cheese melts."
        );
        insertRecipeIngredient(db, tomatoCheeseToastId, "Bread", 2, "slices");
        insertRecipeIngredient(db, tomatoCheeseToastId, "Tomato", 1, "pcs");
        insertRecipeIngredient(db, tomatoCheeseToastId, "Cheese", 40, "g");


// Recipe 20: Sweet Banana Toast
        long sweetBananaToastId = insertRecipe(
                db,
                "Sweet Banana Toast",
                "Toast the bread, add sliced banana and sprinkle sugar over the top."
        );
        insertRecipeIngredient(db, sweetBananaToastId, "Bread", 2, "slices");
        insertRecipeIngredient(db, sweetBananaToastId, "Banana", 1, "pcs");
        insertRecipeIngredient(db, sweetBananaToastId, "Sugar", 5, "g");

    }
}