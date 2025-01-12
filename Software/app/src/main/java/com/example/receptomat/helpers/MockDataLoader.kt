package com.example.receptomat.helpers

import com.example.receptomat.entities.Meal
import com.example.receptomat.entities.Recipe
import java.util.Date

object MockDataLoader {
    private val recipes = mutableListOf<Recipe>()

    init {
        recipes.addAll(
            listOf(
                Recipe(
                    recipe_id = 1,
                    name = "Špageti bolonjez",
                    meal = Meal.LUNCH,
                    ingredients = listOf(
                        "500g mljevenog mesa",
                        "1 luk",
                        "2 češnja češnjaka",
                        "400g rajčica",
                        "200g špageta",
                        "Maslinovo ulje",
                        "Sol i papar"
                    ),
                    instructions = "U velikoj tavi na maslinovom ulju propržite luk i češnjak. Dodajte mljeveno meso i pržite dok ne porumeni. Dodajte rajčice, začine i kuhajte još 30 minuta. Poslužite s kuhanim špagetama.",
                    preparationTime = 45,
                    image_path = "spaghetti_bolognese",
                    dateOfAddition = Date()
                ),
                Recipe(
                    recipe_id = 2,
                    name = "Palačinke",
                    meal = Meal.DESSERT,
                    ingredients = listOf(
                        "200g brašna",
                        "2 jaja",
                        "1 šalica mlijeka",
                        "1 žlica šećera",
                        "Prstohvat soli",
                        "Maslac za prženje"
                    ),
                    instructions = "Pomiješajte sve sastojke u glatku smjesu. Na vrućoj tavi s malo maslaca pržite palačinke dok ne postanu zlatne. Poslužite s omiljenim preljevom.",
                    preparationTime = 20,
                    image_path = "nedostupno",
                    dateOfAddition = Date()
                ),
                Recipe(
                    recipe_id = 3,
                    name = "Salata s piletinom",
                    meal = Meal.DINNER,
                    ingredients = listOf(
                        "1 pileća prsa",
                        "100g miješanih salata",
                        "Cherry rajčice",
                        "Krastavac",
                        "Maslinovo ulje",
                        "Ocat",
                        "Sol i papar"
                    ),
                    instructions = "Ispecite pileća prsa na maslinovom ulju. U međuvremenu, pomiješajte salatu, rajčicu i krastavac. Narezano pile poslužite na salati, prelijte s maslinovim uljem i octom.",
                    preparationTime = 30,
                    image_path = "chicken_salad",
                    dateOfAddition = Date()
                )
            )
        )
    }

    fun getDemoData(): List<Recipe> = recipes
}