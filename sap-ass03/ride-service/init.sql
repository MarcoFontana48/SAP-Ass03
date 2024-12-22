CREATE TABLE rides
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    user_id    VARCHAR(255),
    ebike_id   VARCHAR(255),
    start_date DATE,
    end_date   DATE,
    ongoing    BOOLEAN
);
