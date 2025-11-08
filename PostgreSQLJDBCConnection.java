import java.sql.*;

public class PostgreSQLJDBCConnection {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_BLUE = "\u001B[34m";

    public static final String OUT_PROGRAM = "[" + ANSI_YELLOW + "PROGRAM" + ANSI_RESET + "]: ";
    public static final String OUT_ERROR = "[" + ANSI_RED + "ERROR" + ANSI_RESET + "]: ";
    public static final String OUT_DATABASE = "[" + ANSI_PURPLE + "DATABASE" + ANSI_RESET + "]: ";

    private Connection conn;

    public PostgreSQLJDBCConnection(Connection conn) {
        this.conn = conn;
    }

    // Close the database connection.
    public void close() throws SQLException { conn.close(); }

    // Check if the database connection is connected.
    public boolean connectionOpen() { return !(conn == null); }

    // Retrieve all records from the students table.
    public void getAllStudents() throws SQLException {
        String query = "SELECT * FROM students";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        // Print out each student record.
        System.out.println(OUT_DATABASE + "Displaying all students in table " + out_table("students") + ".");
        System.out.println();
        System.out.println(
            String.format(
                "\t%-10s | %-10s | %-10s | %-24s | %-15s",
                "student_id",
                "first_name",
                "last_name",
                "email",
                "enrollment_date"
            )
        );
        System.out.println("\t" + new String(new char[81]).replace("\0", "-"));
        while (rs.next())
            System.out.println(
                String.format(
                    "\t%-10s | %-10s | %-10s | %-24s | %-15s",
                    rs.getInt("student_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("enrollment_date")
                )
            );
        System.out.println();
        stmt.close();
        rs.close();
    }

    // Insert a new student record into the students table.
    public void addStudent(
        String first_name, String last_name, String email, Date enrollment_date) throws SQLException {
            String query = "INSERT INTO students (first_name, last_name, email, enrollment_date) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, first_name);
            pstmt.setString(2, last_name);
            pstmt.setString(3, email);
            pstmt.setDate(4, enrollment_date);
            pstmt.executeUpdate();
            System.out.println(
                OUT_DATABASE + 
                "Inserted student {" + 
                out_input(first_name) +
                ", " +
                out_input(last_name) +
                ", " +
                out_input(email) +
                ", " +
                out_input(enrollment_date.toString()) + 
                "} into " +
                out_table("students") +
                "."
            );
            getAllStudents();
            pstmt.close();
    }

    // Update a student's email address.
    public void updateStudentEmail(Integer student_id, String new_email) throws SQLException {
        String query = "UPDATE students SET email=? WHERE student_id=?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, new_email);
        pstmt.setInt(2, student_id);
        pstmt.executeUpdate();
        System.out.println(
            OUT_DATABASE + 
            "Updated email of student with ID " + 
            out_table(student_id.toString()) +
            " to " +
            out_input(new_email) +
            "."
        );
        getAllStudents();
        pstmt.close();
    }

    // Delete a student's record.
    public void deleteStudent(Integer student_id) throws SQLException {
        String query = "DELETE FROM students WHERE student_id=?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, student_id);
        pstmt.executeUpdate();
        System.out.println(
            OUT_DATABASE + 
            "Removed student with ID " + 
            out_table(student_id.toString()) +
            "."
        );
        getAllStudents();
        pstmt.close();
    }

    private static String out_input(String input) {
        return ANSI_CYAN + input + ANSI_RESET;
    }

    private static String out_table(String input) {
        return ANSI_BLUE + input + ANSI_RESET;
    }

    public static void main(String[] args) {

        // Get the database credentials.
        if (args.length < 5) {
            System.out.println(OUT_PROGRAM + "Missing arguments. Exiting program...");
            System.exit(0);
        }
        String url = "jdbc:postgresql://" + args[0] + ":" + args[1] + "/" + args[2];
        String user = args[3];
        String password = args[4];

        System.out.println(OUT_PROGRAM + "Received database URL " + out_input(url) + ".");
        System.out.println(OUT_PROGRAM + "Database username set to " + out_input(user) + ".");
        System.out.println(OUT_PROGRAM + "Database password set to " + out_input(password) + ".");

        System.out.println(OUT_PROGRAM + "Attempting to log in to database...");

        // Load PostgreSQL JDBC driver.
        try {
            Class.forName("org.postgresql.Driver");
            // Connect to the database.
            PostgreSQLJDBCConnection conn = new PostgreSQLJDBCConnection(
                DriverManager.getConnection(url, user, password));
            if (conn.connectionOpen()) {
                System.out.println(OUT_PROGRAM + "Connected to PostgreSQL successfully.");

                // Execute program commands.
                conn.getAllStudents();
                conn.addStudent(
                    "Tarō",
                    "Yamada",
                    "ytaro@example.com",
                    java.sql.Date.valueOf(java.time.LocalDate.now())
                );
                conn.updateStudentEmail(10, "taro.yamada@example.com");
                conn.deleteStudent(10);
            }
            else System.out.println(OUT_PROGRAM + "Failed to establish connection to database.");
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(OUT_ERROR + "Exception thrown by the PostgreSQL JDBC driver.");
            System.out.println(OUT_ERROR + "Printing stack trace...");
            System.out.println();
            e.printStackTrace();
            System.out.println();
        }
    }
}

