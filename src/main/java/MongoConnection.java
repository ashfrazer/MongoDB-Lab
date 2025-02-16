import com.mongodb.client.*;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoConnection {
    private static final String DATABASE_NAME = "gameDB";
    private static final String COLLECTION_NAME = "leaderboard";
    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public MongoConnection() {
        client = MongoClients.create("mongodb://localhost:27017");
        database = client.getDatabase(DATABASE_NAME);
        collection = database.getCollection(COLLECTION_NAME);

        // Insert default score
        if (collection.countDocuments() == 0) {
            Document scoreDoc = new Document("playerName", "Ash")
                    .append("score", 139);
            collection.insertOne(scoreDoc);
        }
    }

    // Store name and score into DB
    public void saveScoreToDatabase(String playerName, int score) {
        Document scoreDoc = new Document("playerName", playerName)
                .append("score", score);

        collection.insertOne(scoreDoc);
    }

    // Get top 5 scores
    public List<Document> getTopScores(int limit) {
        List<Document> topScores = new ArrayList<>();
        FindIterable<Document> results = collection.find()
                .sort(new Document("score", -1))
                .limit(limit);

        for (Document doc : results) {
            topScores.add(doc);
        }

        return topScores;
    }
}