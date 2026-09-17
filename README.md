# Smart Pantry Manager

Smart pantry manager is a Java android application that helps users to keep track of ingredients they already have and suggests recipes they can make using those ingredients.

The app is designed around strict recipe matching. A recipe is only suggested if every required ingredient is available in the pantry in a sufficient quantity.

## Features

- Add pantry ingredients
- Edit existing ingredients
- Delete ingredients
- Store quantity, unit and optional expiry date
- View all pantry items in a RecyclerView
- View suggested recipes based on current pantry ingredients
- Strict recipe matching
- Handles simple singular and plural differences such as egg and eggs
- Supports basic unit conversion such as litres to millilitres and kilograms to grams
- View recipe ingredients and preparation instructions
- Settings screen
- Toolbar menu navigation
- SQLite data persistence

## Database

The application uses SQLite with SQLiteOpenHelper.

SQLite was chosen because the application only needs local on device storage and does not require an internet connection or cloud syncing.

The database contains three main tables:

- pantry
- recipes
- recipe_ingredients

The pantry table stores the ingredients added by the user.

The recipes table stores the recipe names and preparation instructions.

The recipe_ingredients table stores the ingredients and quantities required for each recipe.

The application includes 20 preloaded recipes.

## Requirements

- Android Studio
- Java
- Android device or emulator
- Minimum Android version supported by the project

## How to Run the Application

1. Download or clone the GitHub repository.
2. Open the project folder in Android Studio.
3. Wait for gradle to finish syncing.
4. Connect an android phone with USB debugging enabled or start an android emulator.
5. Select the device in Android Studio.
6. Click Run.
7. The Smart pantry manager application will install and open on the selected device.

## Using the App

1. Open the app.
2. Tap Add Ingredient to add pantry items.
3. Enter the ingredient name, quantity, unit and optional expiry date.
4. Tap an existing pantry item to edit or delete it.
5. Use the toolbar menu to open Suggested Recipes.
6. Only recipes that can be fully made using the current pantry will be shown.
7. Tap a recipe to view its ingredients and preparation instructions.
8. Use the toolbar menu to open the Settings screen.

## Technologies Used

- Java
- Android Studio
- SQLite
- SQLiteOpenHelper
- RecyclerView
- Intents
- XML layouts

## Author

Samesh Bheem