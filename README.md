# Smart Pantry Manager

Smart Pantry Manager is a Java Android application that helps users keep track of the ingredients they have at home and suggests recipes they can make using those ingredients.

The main goal of the app is to reduce food waste by helping users make better use of food that is already available in their pantry.

The application uses strict recipe matching, meaning a recipe is only suggested when every required ingredient is available in the correct quantity.

## Main Features

- Home screen with navigation to the main parts of the app
- Add pantry ingredients
- Edit existing ingredients
- Delete ingredients
- Store quantity, unit and optional expiry date
- View pantry items using a RecyclerView
- Food emojis for pantry items
- 20 preloaded recipes
- Suggested Recipes screen
- Strict recipe matching
- Handles simple singular and plural ingredient differences
- Supports basic unit conversion
- View full recipe ingredients and preparation instructions
- Cook Recipe feature
- Automatically deducts used ingredients from the pantry
- Removes pantry items when their quantity reaches zero
- Toolbar and back-arrow navigation
- Light and Dark Mode
- Option to show or hide expiry dates
- Option to confirm before deleting an ingredient
- SQLite data persistence

## Home Screen

The Home screen is the starting point of the application.

It provides access to:

- My Pantry
- Suggested Recipes
- Settings

This makes the application easier to navigate and keeps the main features separated.

## Pantry Management

The pantry allows the user to create, read, update and delete ingredients.

Each ingredient can contain:

- Ingredient name
- Quantity
- Unit
- Optional expiry date

The pantry is stored in SQLite, so the information remains available after the app is closed and reopened.

Pantry items are displayed using a RecyclerView and a custom PantryAdapter.

## Suggested Recipes

The app contains 20 recipes that are preloaded into the SQLite database.

A recipe is only shown if the user has every required ingredient in a sufficient quantity.

For example, if a recipe requires:

- 2 Eggs
- 50 ml Milk
- 10 g Butter

the recipe will only appear if all three ingredients are available in the pantry.

## Ingredient Matching

The app handles some simple differences in ingredient names and units.

Examples include:

- Egg and Eggs
- Tomato and Tomatoes
- 1 litre = 1000 ml
- 1 kilogram = 1000 g

Unrelated units such as pieces and slices are not automatically converted.

## Cook Recipe

The Recipe Detail screen includes a Cook Recipe button.

When a user cooks a recipe, the app automatically deducts the ingredients used from the pantry.

For example:

If the pantry contains:

4 Eggs

and the recipe uses:

2 Eggs

the pantry will be updated to:

2 Eggs

If the remaining quantity reaches zero, the ingredient is removed from the pantry.

The Suggested Recipes list is then updated based on the new pantry quantities.

## Settings

The Settings screen contains working user preferences.

### Dark Mode

The user can switch between Light Mode and Dark Mode.

The selected mode is stored using SharedPreferences and remains selected after the app is closed.

### Show Expiry Dates

The user can choose whether expiry dates are displayed underneath pantry items.

### Confirm Before Delete

The user can choose whether the app asks for confirmation before deleting an ingredient.

These preferences are stored using SharedPreferences.

## Database

The application uses SQLite with SQLiteOpenHelper.

SQLite was chosen because the application only requires local on-device storage and does not need an internet connection or cloud database.

The database contains three main tables:

### pantry

Stores the user's pantry ingredients.

Fields include:

- id
- name
- quantity
- unit
- expiry_date

### recipes

Stores recipe information.

Fields include:

- id
- name
- instructions

### recipe_ingredients

Stores the ingredients needed for each recipe.

Fields include:

- id
- recipe_id
- ingredient_name
- required_quantity
- unit

## Technologies Used

- Java
- Android Studio
- SQLite
- SQLiteOpenHelper
- RecyclerView
- Custom Adapters
- Intents
- SharedPreferences
- XML layouts
- AppCompat
- Git and GitHub

## Navigation

The application uses multiple Android Activities.

Main navigation flow:

Home

- My Pantry
    - Add Ingredient
    - Edit Ingredient

- Suggested Recipes
    - Recipe Detail
    - Cook Recipe

- Settings

Back arrows are provided on secondary screens to make navigation clear and consistent.

## How to Run the Application

1. Download or clone the GitHub repository.
2. Open the project in Android Studio.
3. Wait for Gradle to finish syncing.
4. Connect an Android device with USB debugging enabled or start an Android emulator.
5. Select the device in Android Studio.
6. Click Run.
7. The Smart Pantry Manager app will install and open on the selected device.

## Author

Samesh Bheem