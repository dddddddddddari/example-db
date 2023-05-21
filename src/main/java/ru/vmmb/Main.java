package ru.vmmb;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Map;

public class Main {

    private static Connection con;
    private static Statement stmt;
    private static Statement stm;
    private static ResultSet rs;
    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/new connection", "root", "root");
            stmt = con.createStatement();
            stm = con.prepareStatement("insert into file values (?,?,?)");
            countWords("C://Users//user//IdeaProjects//example-db//lab4.txt");
            rs = stmt.executeQuery("SELECT * FROM FILE");
            while (rs.next()) {
                String a = rs.getObject(1).toString();
                String b = rs.getObject(2).toString();
                String c = rs.getObject(3).toString();
                System.out.println(a + " " + b + " " + c);
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { con.close();} catch (SQLException se) {}
            try { stmt.close();} catch (SQLException se) {}
            try { rs.close();} catch (SQLException se) {}
        }
    }
    public static void countWords(String path) throws IOException, SQLException {
        File dir = new File(path);
        if (!dir.isDirectory()) {
            System.err.println("There is no such directory" + path);
            return;
        }
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                countWords(file.getPath());
            } else if (file.getName().endsWith(".txt")) {
                Map<String, Integer> wordCount = new HashMap<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] words = line.split("\\s+");
                        for (String word : words) {
                            if (!word.isEmpty()) {
                                int count = wordCount.getOrDefault(word.toLowerCase(), 0) + 1;
                                wordCount.put(word.toLowerCase(), count);
                            }
                        }
                    }
                }
                for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
                    PreparedStatement stm = con.prepareStatement("insert into FILE values (?,?,?)");
                    stm.setString(1, file.getPath());
                    stm.setString(2, entry.getKey());
                    stm.setInt(3, entry.getValue());
                    stm.executeUpdate();
                }
            }
        }
    }
}