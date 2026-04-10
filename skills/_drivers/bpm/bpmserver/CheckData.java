import java.sql.*;

public class CheckData {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:sqlite:C:/Users/Administrator/.ooder/bpm/bpm.db";
        try (Connection conn = DriverManager.getConnection(url)) {
            System.out.println("=== BPM_PROCESSDEF_PROPERTY ===");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM BPM_PROCESSDEF_PROPERTY")) {
                ResultSetMetaData meta = rs.getMetaData();
                while (rs.next()) {
                    System.out.println("Property: " + rs.getString("PROPNAME") + 
                                       " = " + rs.getString("PROPVALUE"));
                }
            }
            
            System.out.println("\n=== BPM_ACTIVITYDEF_PROPERTY ===");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM BPM_ACTIVITYDEF_PROPERTY")) {
                while (rs.next()) {
                    System.out.println("Activity: " + rs.getString("ACTIVITYDEF_ID") + 
                                       ", Property: " + rs.getString("PROPNAME") + 
                                       " = " + rs.getString("PROPVALUE"));
                }
            }
            
            System.out.println("\n=== BPM_ACTIVITYDEF ===");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT ACTIVITYDEF_ID, DEFNAME, POSITION FROM BPM_ACTIVITYDEF")) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getString("ACTIVITYDEF_ID") + 
                                       ", Name: " + rs.getString("DEFNAME") + 
                                       ", Position: " + rs.getString("POSITION"));
                }
            }
        }
    }
}
