package org.example;

import java.sql.*;

public class SeedData {


    public static void seedStudents() {
        String sql = """
        INSERT OR IGNORE INTO students (id, first_name, last_name, sex, password) VALUES
        ('1459/16','BEREKET','FIKRE ABIYO','Male','1234'),
        ('3113/16','NAOL','GIRMA TEFERA','Male','1234'),
        ('1420/16','BEHAILU','HAGOS ADANE','Male','1234'),
        ('3170/16','NATNAEL','REBUMA BEKELE','Male','1234'),
        ('2398/16','HENOK','LEMA KIFLE','Male','1234'),
        ('1598/16','BINIAM','MESFIN WELDETSADIK','Male','1234'),
        ('1996/16','ESMAEL','YASIN MUHAMMED','Male','1234'),
                ('3537/16','SOFONIAS','MINILIK TILAHUN','Male','1234'),
                       ('2077/16','EYUEL','MESFIN WOLDEGIORGIS','Male','1234'),
                       ('1090/16','ABENEZER','HAILE TADESE','Male','1234'),
                       ('1757/16','DAGNAW','MEKASHA WELDEAMANUEL','Male','1234'),
                       ('1439/16','BEMNET','ASEGED YEGEZU','Male','1234'),
                       ('2818/16','MEHARI','BEREKET TESHOME','Male','1234'),
                       ('2765/16','MAHLET','HAILE WOLDE','Female','1234'),
                       ('2488/16','ISRAEL','GEZAHEGN SIMA','Male','1234'),
                       ('1458/16','BEREKET','ENDALE DEBELA','Male','1234'),
                       ('1498/16','BESUFIKAD','DEJENE BERGID','Male','1234'),
                       ('3135/16','NATAN','SAHLU YIRGA','Male','1234'),
                       ('2989/16','MILLION','TESHOME GUDETO','Male','1234'),
                       ('1908/16','ELBETEL','ALEM NITSEBE','Female','1234'),
                       ('2662/16','LATERA','GELETA DIDA','Male','1234'),
                       ('2213/16','GENENE','FITSUM KIROS','Male','1234'),
                       ('2392/16','HENOK','BEFEKADU KEBEDE','Male','1234'),
                       ('2670/16','LEMI','GADISA TEKLE','Male','1234'),
                       ('3585/16','TAMIRAT','WENDIMAGEGNE TEREFE','Male','1234'),
                       ('3302/16','REDIET','WERET TEZERA','Female','1234'),
                       ('2137/16','FIKIRYILKAL','TAGES GEBREMARIYAM','Male','1234'),
                       ('3922/16','YOHANIS','ALEMU GEBRASENBET','Male','1234'),
                       ('3939/16','YOHANNES','TADELEW CHEKOLE','Male','1234'),
                       ('3921/16','YOHANIS','ALEMAYEHU MEKURIA','Male','1234'),
                       ('2736/16','LULIYA','FITSUM GEBREMEDIHN','Female','1234'),
                       ('1551/16','BETSEGAW','ABRAHAM ANJULO','Male','1234'),
                       ('3010/16','MINTESNOT','TESFAYE CHUFARE','Male','1234'),
                       ('2071/16','EYUEL','ADANE BIRATU','Male','1234'),
                       ('1320/16','AREGAWI','BARAMBARAS WENDM','Male','1234'),
                       ('1007/16','MUSSIE','NEGASI KIDANE','Male','1234'),
                       ('3628/16','TESFAHUN','YOSEF GETACHEW','Male','1234'),
                       ('1525/16','BETHELEHEM','BELETE ZEWDU','Female','1234'),
                       ('1134/16','ABNET','MEKONEN GOSAYE','Male','1234'),
                       ('2326/16','HANNA','MASRESHA MELESE','Female','1234'),
                       ('3502/16','SHURA','TADESE TIKI','Male','1234'),
                       ('3339/16','ROBERA','HAJI BIRMEJI','Male','1234'),
                       ('2680/16','LEUL','BIRHANU ADDISU','Male','1234'),
                       ('3597/16','TEBAREK','SOLOMON TAREKEGN','Male','1234'),
                       ('2477/16','IFTU','DEJENE GUDETA','Female','1234'),
                       ('3112/16','NAOD','ADDISU BANTIGEGN','Male','1234'),
                       ('3151/16','NATNAEL','ABAY TABABEL','Male','1234'),
                       ('1975/16','EPHREM','YOSEPH KEBEDE','Male','1234'),
                       ('2156/16','FIRDOS','ABDURAZAKE MOHAMMED','Female','1234'),
                       ('3630/16','TESFAMICHAEL','ASSEFA ABEBE','Male','1234'),
                       ('1892/16','EGATA','KEBEDE HIRPA','Male','1234'),
                       ('2597/16','KIBREAB','SISAY TAKELE','Male','1234'),
                       ('1647/16','BIRUK','MEKONNEN ABREHAM','Male','1234'),
                       ('0338/15','ELENI','YISFALEM ENDALE','Female','1234'),
                       ('2407/16','HERANI','WUDMA BEKA','Female','1234'),
                       ('1200/16','AFOMIYA','TEKETEL FIREW','Female','1234'),
                       ('1397/16','BARKOT','ADDISSE MITORO','Female','1234'),
                       ('1895/16','EHITNESH','WALE WAGAYE','Female','1234'),
                       ('2461/16','HONOLIYAT','ESKINDIR TILAHUN','Female','1234'),
                       ('3382/16','SAMRAWIT','EYASU BATTISO','Female','1234'),
                       ('2933/16','METI','MIDEKSA FUFA','Female','1234'),
                       ('2030/16','EYERUS','GETACHEW KEBEDE','Female','1234'),
                       ('3483/16','SENA','MENGISTU SORI','Female','1234'),
                       ('1325/16','ARSEMA','KALAYU BELAY','Female','1234'),
                       ('4034/16','ZEYNEB','YUSUF EDRIS','Female','1234'),
                       ('2534/16','KALKIDAN','FIKRE MELESE','Female','1234'),
                       ('3739/16','VITTORIA','TAMIRE ENDRIAS','Female','1234'),
                       ('2011/16','ETSUB','MICHAEL YITBAREK','Female','1234'),
                       ('3916/16','YOHANA','DESTA DELELEW','Female','1234'),
                       ('1299/16','ANDINET','DEMEKE KAYENEW','Female','1234');
        -- continue with all remaining students, each separated by a comma
    """;

        try (Connection conn = DB.getConnection(); Statement stmt = conn.createStatement()) {
            int rowsAffected = stmt.executeUpdate(sql);
            System.out.println("Students seeded successfully! Rows affected: " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Error seeding students: " + e.getMessage());
            e.printStackTrace();
        }
    }

        public static void seedOffices() {
        String sql = """
            INSERT OR IGNORE INTO offices (office_name, role, password) VALUES
            ('Dormitory','Dorm Manager','1'),
            ('Sport','Sport Officer','2'),
            ('Cafeteria','Cafeteria Manager','3'),
            ('Library','Librarian','4'),
            ('Police Office','Police Officer','5'),
            ('Faculty','Faculty Admin','6'),
            ('Student Service','Student Service Officer','7');
        """;

        try (Connection conn = DB.getConnection(); Statement stmt = conn.createStatement()) {
            int rowsAffected = stmt.executeUpdate(sql);
            System.out.println("Offices seeded successfully! Rows affected: " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Error seeding offices: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ADD THESE RETRIEVAL METHODS:
    public static void displayAllStudents() {
        String sql = "SELECT * FROM students";

        try (Connection conn = DB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n=== STUDENTS IN DATABASE ===");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("ID: " + rs.getString("id") +
                        ", Name: " + rs.getString("first_name") + " " + rs.getString("last_name") +
                        ", Sex: " + rs.getString("sex"));
            }

            if (!found) {
                System.out.println("No students found in the database!");
            }

        } catch (SQLException e) {
            System.err.println("Error displaying students: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void displayAllOffices() {
        String sql = "SELECT * FROM offices";

        try (Connection conn = DB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n=== OFFICES IN DATABASE ===");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Office: " + rs.getString("office_name") +
                        ", Role: " + rs.getString("role") +
                        ", Password: " + rs.getString("password"));
            }

            if (!found) {
                System.out.println("No offices found in the database!");
            }

        } catch (SQLException e) {
            System.err.println("Error displaying offices: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Add this method to check if tables exist and have data
    public static void debugDatabase() {
        System.out.println("\n=== DATABASE DEBUG INFO ===");

        // Check students table
        try (Connection conn = DB.getConnection();
             Statement stmt = conn.createStatement()) {

            // Check if students table exists and has data
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM students");
            if (rs.next()) {
                System.out.println("Students table count: " + rs.getInt("count"));
            }

            // Check if offices table exists and has data
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM offices");
            if (rs.next()) {
                System.out.println("Offices table count: " + rs.getInt("count"));
            }

        } catch (SQLException e) {
            System.err.println("Error during debug: " + e.getMessage());
        }
    }
}