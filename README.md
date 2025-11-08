# Instructions

1. Download and extract the ZIP file from this Github repository.
2. Create a new empty PostgreSQL database.
3. Populate the database using the following:

```
CREATE TABLE IF NOT EXISTS students (
    student_id SERIAL PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    enrollment_date DATE
);

INSERT INTO students (first_name, last_name, email, enrollment_date) VALUES
('John', 'Doe', 'john.doe@example.com', '2023-09-01'),
('Jane', 'Smith', 'jane.smith@example.com', '2023-09-01'),
('Jim', 'Beam', 'jim.beam@example.com', '2023-09-02');
```

4. Run `build.sh`.
5. Execute `PostgreSQLJDBCConnection.jar` using the following command with the database credentials:

```
java -jar PostgreSQLJDBCConnection.jar <HOST> <PORT> <DATABASE_NAME> <USERNAME> <PASSWORD>
```
