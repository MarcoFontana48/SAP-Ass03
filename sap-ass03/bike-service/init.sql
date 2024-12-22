CREATE TABLE ebike
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    state       VARCHAR(50),
    x_location  FLOAT,
    y_location  FLOAT,
    x_direction FLOAT,
    y_direction FLOAT,
    speed       FLOAT,
    battery     FLOAT
);
