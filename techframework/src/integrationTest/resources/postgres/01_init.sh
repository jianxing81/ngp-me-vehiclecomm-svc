#!/bin/bash

# Set database connection details
DB_NAME="ngp_me_vehiclecomm"
SCHEMA_NAME="ngp_me_vehiclecomm"
DB_USER="postgres"
DB_PASSWORD="postgres"

# Connect to the database
PGPASSWORD="$DB_PASSWORD" psql -U "$DB_USER" -d "$DB_NAME" <<EOF

-- Create the schema if it doesn't exist
CREATE SCHEMA IF NOT EXISTS $SCHEMA_NAME;


EOF

echo "Schema created in database '$DB_NAME'."
