-- Create the user if it doesn't exist
CREATE USER IF NOT EXISTS 'cfo_user'@'%' IDENTIFIED BY 'theksqu4r3gr0upMx';

-- Grant privileges to the user on the specific database
GRANT ALL PRIVILEGES ON cfo_reporting.* TO 'cfo_user'@'%';

-- Apply the changes
FLUSH PRIVILEGES;