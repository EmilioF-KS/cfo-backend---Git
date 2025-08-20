-- Create the user if it doesn't exist
CREATE USER IF NOT EXISTS 'cfo_user'@'%' IDENTIFIED BY 'your_password';

-- Grant privileges to the user on the specific database
GRANT ALL PRIVILEGES ON your_db_name.* TO 'cfo_user'@'%';

-- Apply the changes
FLUSH PRIVILEGES;