package server.repositories;

import server.Recipe;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

public class RecipeRepository {

    private static final String CONNECTION_URI = 
            "mongodb+srv://cse110-lab6:iLoveCSE110@cluster0.e0wpva4.mongodb.net/?retryWrites=true&w=majority";
    private final MongoClient mongoClient = MongoClients.create(CONNECTION_URI);
    private final MongoDatabase pantryPalDB = mongoClient.getDatabase("pantryPal");
    private MongoCollection<Document> recipeCollection = pantryPalDB.getCollection("recipe");


    public RecipeRepository(String collection) {
        this.recipeCollection = pantryPalDB.getCollection(collection);
    }

    // public ArrayList<String> getRecipeList() {
    //     ArrayList<String> recipeList = new ArrayList<String>();

    //     for (Recipe recipe : data.values())
    //         recipeList.add(recipe.uuid + "," + recipe.name);

    //     Collections.sort(recipeList,
    //             (a, b) -> (Long.compare(this.getRecipe(a).createdAt, this.getRecipe(b).createdAt) * -1));

    //     return recipeList;
    // }

    public Recipe createRecipe(JSONObject createRecipeJSON) {
        Recipe recipe = new Recipe(createRecipeJSON);
        System.out.println(recipe.toJSON().toString());
        Document recipeDoc = new Document("_id", recipe.id);
        recipeDoc.append("name", recipe.name)
                .append("mealType", recipe.mealType)
                .append("details", recipe.details)
                .append("createdAt", recipe.createdAt);

        recipeCollection.insertOne(recipeDoc);
        return recipe;
    }

    public Recipe getRecipe(String id) {
        Document recipeDocument = recipeCollection.find(new Document("_id", new ObjectId(id))).first();
        Recipe recipe = new Recipe(recipeDocument);
        return recipe;
    }


    public Recipe editRecipe(JSONObject editRecipeRequest) {
        ObjectId id = new ObjectId(editRecipeRequest.getString("id"));
        Bson filter = eq("_id", id);
        Bson updateName = set("name", editRecipeRequest.getString("name"));
        Bson updatedDetails = set("details", editRecipeRequest.getString("details"));
        recipeCollection.updateOne(filter, updateName);
        recipeCollection.updateOne(filter, updatedDetails);

        return new Recipe(recipeCollection.find(filter).first());
    }

    public Recipe deleteRecipe(String id) {
        Bson filter = eq("_id", new ObjectId(id));
        Recipe recipe = new Recipe(recipeCollection.find(filter).first());
        recipeCollection.deleteOne(filter);
        return recipe;
    }
}