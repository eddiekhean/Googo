-- Role (vai trò người dùng)
create type user_role as enum ('ADMIN', 'CUSTOMER', 'RESTAURANT');

-- Status (trạng thái tài khoản)
create type user_status as enum ('ACTIVE', 'INACTIVE', 'BANNED');

-- Gender (giới tính)
create type gender_type as enum ('MALE', 'FEMALE', 'OTHER');

-- Trạng thái của nhà hàng
CREATE TYPE restaurant_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED', 'BANNED', 'CLOSED');

-- GENDER
CREATE CAST (character varying AS gender_type)
    WITH INOUT AS IMPLICIT;
CREATE CAST (text AS gender_type)
    WITH INOUT AS IMPLICIT;

-- ROLE
CREATE CAST (character varying AS user_role)
    WITH INOUT AS IMPLICIT;
CREATE CAST (text AS user_role)
    WITH INOUT AS IMPLICIT;

-- STATUS
CREATE CAST (character varying AS user_status)
    WITH INOUT AS IMPLICIT;
CREATE CAST (text AS user_status)
    WITH INOUT AS IMPLICIT;

-- Restaurant
CREATE CAST (character varying AS restaurant_status)
    WITH INOUT AS IMPLICIT;
CREATE CAST (text AS restaurant_status)
    WITH INOUT AS IMPLICIT;


CREATE EXTENSION IF NOT EXISTS pgcrypto;

create table users (
                       id uuid primary key default gen_random_uuid(),
                       username varchar(50) unique not null,
                       email varchar(100) unique not null,
                       password varchar(255) not null,
                       full_name varchar(100),
                       phone varchar(15),
                       gender gender_type DEFAULT 'OTHER',
                       date_of_birth date DEFAULT '1900-01-01',
                       avatar_url varchar(255),
                       role user_role not null default 'CUSTOMER',
                       status user_status not null default 'INACTIVE',
                       created_at timestamp default now(),
                       updated_at timestamp default now()
);

create table addresses (
                           id uuid primary key default gen_random_uuid(),
                           user_id uuid not null references users(id) on delete cascade,
                           street varchar(255) not null,
                           ward varchar(100),
                           district varchar(100),
                           city varchar(100),
                           latitude double precision,
                           longitude double precision,
                           is_default boolean default false,
                           created_at timestamp default now()
);

CREATE TABLE restaurant_profiles (
                                     id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                                     user_id uuid UNIQUE REFERENCES users(id) ON DELETE CASCADE,
                                     address_id uuid REFERENCES addresses(id) ON DELETE SET NULL,
                                     name VARCHAR(100) NOT NULL,
                                     description VARCHAR(255),
                                     opening_hours VARCHAR(100),
                                     image_url VARCHAR(255),
                                     is_open BOOLEAN DEFAULT TRUE,
                                     status restaurant_status DEFAULT 'PENDING',
                                     created_at TIMESTAMP DEFAULT now(),
                                     updated_at TIMESTAMP DEFAULT now()
);

-- Cấu hình các cột trong db theo kiểu index tăng tốc độ truy vấn , giảm tốc độ INSERT, UPDATE, DELETE do vì nó ít được sửa --
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_addresses_user_id ON addresses(user_id);

CREATE INDEX idx_restaurant_status ON restaurant_profiles(status);
CREATE INDEX idx_restaurant_is_open ON restaurant_profiles(is_open);
CREATE INDEX idx_restaurant_user_id ON restaurant_profiles(user_id);


-- Trigger tự động set thời gian created_at khi insert --
create or replace function update_created_at_column()
    returns trigger as $$
begin
    new.created_at = now();
    return new;
end;
$$ language plpgsql;

create trigger set_created_at
    before insert on users
    for each row
execute function update_created_at_column();

-- Trigger tự động update thời gian khi có update cột --
create function update_updated_at_column()
    returns trigger as $$
begin
  new.updated_at = now();
return new;
end;
$$ language 'plpgsql';
create trigger set_timestamp
    before update on users
    for each row
    execute function update_updated_at_column();
ALTER TABLE users
ALTER COLUMN status SET DEFAULT 'INACTIVE';


-- Trigger tự động cập nhật updated_at khi update
CREATE OR REPLACE FUNCTION update_restaurant_updated_at()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_restaurant_timestamp
    BEFORE UPDATE ON restaurant_profiles
    FOR EACH ROW
EXECUTE FUNCTION update_restaurant_updated_at();

-- (Tuỳ chọn) Trigger đảm bảo created_at không bị null
CREATE OR REPLACE FUNCTION set_restaurant_created_at()
    RETURNS TRIGGER AS $$
BEGIN
    IF NEW.created_at IS NULL THEN
        NEW.created_at = now();
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_set_restaurant_created_at
    BEFORE INSERT ON restaurant_profiles
    FOR EACH ROW
EXECUTE FUNCTION set_restaurant_created_at();