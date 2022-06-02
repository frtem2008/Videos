package TCPOnlineGame.Utils;

import java.io.*;

//функции для работы с файлами
public class FileUtils {
    public static void appendStrToFile(File file, String str) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
            out.write(str + "\n");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearFile(File file) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file, false));
            out.write("");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String readFile(File file) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file.getPath()))) {
            String line = reader.readLine();
            while (line != null) {
                sb.append(line).append(System.lineSeparator());
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void deleteFile(File toDelete) {
        if (toDelete.delete()) {
            System.out.println("File " + toDelete.getName() + " has been deleted");
        } else {
            System.out.println("Error deleting file: " + toDelete.getName());
        }
    }
}
