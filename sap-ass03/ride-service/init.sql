CREATE TABLE users (
                       user_id VARCHAR(255) PRIMARY KEY,
                       credit INT
                       x_location DOUBLE,
                       y_location DOUBLE
);

CREATE TABLE ebikes (
                        ebike_id VARCHAR(255) PRIMARY KEY,
                        state VARCHAR(255),
                        x_location DOUBLE,
                        y_location DOUBLE,
                        x_direction DOUBLE,
                        y_direction DOUBLE,
                        speed DOUBLE,
                        battery INT
);

CREATE TABLE rides (
                       id VARCHAR(255) PRIMARY KEY,
                       user_id VARCHAR(255),
                       ebike_id VARCHAR(255),
                       start_date DATE,
                       end_date DATE,
                       ongoing BOOLEAN,
                       FOREIGN KEY (user_id) REFERENCES users(user_id),
                       FOREIGN KEY (ebike_id) REFERENCES ebikes(ebike_id)
);
