CREATE TABLE `user` (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(320) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_user_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE crops (
    id BIGINT NOT NULL AUTO_INCREMENT,
    crop_name VARCHAR(255) NOT NULL,
    crop_type VARCHAR(255) NOT NULL,
    crop_variety VARCHAR(255) NOT NULL,
    field_location VARCHAR(255) NOT NULL,
    cultivated_area DOUBLE NOT NULL,
    sowing_date DATE NOT NULL,
    expected_harvest_date DATE NOT NULL,
    farmer_name VARCHAR(255) NOT NULL,
    notes VARCHAR(1000),
    owner_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_crops_owner FOREIGN KEY (owner_id) REFERENCES `user` (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE harvesting_records (
    id BIGINT NOT NULL AUTO_INCREMENT,
    crop_id BIGINT NOT NULL,
    farmer_name VARCHAR(255) NOT NULL,
    field_location VARCHAR(255) NOT NULL,
    planned_harvest_date DATE NOT NULL,
    actual_harvest_date DATE,
    harvested_quantity DOUBLE NOT NULL,
    status VARCHAR(255) NOT NULL,
    notes VARCHAR(1000),
    PRIMARY KEY (id),
    CONSTRAINT fk_harvesting_records_crop FOREIGN KEY (crop_id) REFERENCES crops (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
