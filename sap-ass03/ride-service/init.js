// Connect to the database
db = db.getSiblingDB('ebike');

// Create the rides collection with schema validation
db.createCollection("rides", {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["ride_id", "start_date", "end_date", "ongoing", "user", "ebike"],
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
                user: {
                    bsonType: "object",
                    required: ["user_id", "credit"],
                    properties: {
                        user_id: {
                            bsonType: "string",
                            description: "must be a string and is required"
                        },
                        credit: {
                            bsonType: "int",
                            description: "must be an integer and is required"
                        }
                    }
                },
                ebike: {
                    bsonType: "object",
                    required: ["ebike_id", "state", "x_location", "y_location", "x_direction", "y_direction", "speed", "battery"],
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
        }
    }
});
