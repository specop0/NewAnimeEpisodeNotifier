package models;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class File {

    public static String ReadAllText(String filename) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String ls = System.lineSeparator();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append(ls);
                }
            }

            String content = stringBuilder.toString();
            return content;
        } catch (IOException ex) {
            Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public static void WriteAllText(String filename, String content) {
        try {
            try (PrintWriter out = new PrintWriter(filename)) {
                out.write(content);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
