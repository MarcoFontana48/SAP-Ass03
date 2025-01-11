// Connect to the database
db = db.getSiblingDB('ebike');

// Create the collection with schema validation
db.createCollection("users", {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["id", "credit"],
            properties: {
                id: {
                    bsonType: "string",
                    description: "must be a string and is required"
                },
                credit: {
                    bsonType: "int",
                    description: "must be an int and is required"
                },
                x_location: {
                    bsonType: "double",
                    description: "must be a double"
                },
                y_location: {
                    bsonType: "double",
                    description: "must be a double"
                }
            }
        }
    }
});
