// Connect to the database
db = db.getSiblingDB('ebike');

// Create the rides collection with schema validation
db.createCollection("rides", {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["ride_id", "start_date", "end_date", "ongoing", "user_id", "ebike_id"],
            properties: {
                ride_id: {
                    bsonType: "string",
                    description: "must be a string and is required"
                },
                start_date: {
                    bsonType: "string",
                    description: "must be a string and is required"
                },
                end_date: {
                    bsonType: "string",
                    description: "must be a string and is required"
                },
                ongoing: {
                    bsonType: "bool",
                    description: "must be a boolean and is required"
                },
                user_id: {
                    bsonType: "string",
                    description: "must be a string and is required"
                },
                ebike_id: {
                    bsonType: "string",
                    description: "must be a string and is required"
                }
            }
        }
    }
});
