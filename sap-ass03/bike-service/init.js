// Connect to the database
db = db.getSiblingDB('ebike');

// Create the collection with schema validation
db.createCollection("ebikes", {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["id", "state", "x_location", "y_location", "x_direction", "y_direction", "speed", "battery"],
            properties: {
                id: {
                    bsonType: "string",
                    description: "must be a string and is required"
                },
                state: {
                    bsonType: "string",
                    description: "must be a string and is required"
                },
                x_location: {
                    bsonType: "double",
                    description: "must be a double and is required"
                },
                y_location: {
                    bsonType: "double",
                    description: "must be a double and is required"
                },
                x_direction: {
                    bsonType: "double",
                    description: "must be a double and is required"
                },
                y_direction: {
                    bsonType: "double",
                    description: "must be a double and is required"
                },
                speed: {
                    bsonType: "double",
                    description: "must be a double and is required"
                },
                battery: {
                    bsonType: "int",
                    description: "must be an int and is required"
                }
            }
        }
    }
});