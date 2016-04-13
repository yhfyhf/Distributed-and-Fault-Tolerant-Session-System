package group;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Utils {
    /**
     * Get the server data from the data.txt, return the server data with the format "id,ip,domain"
     */
    public static List<String> getServerData() {
        //https://www.caveofprogramming.com/java/java-file-reading-and-writing-files-in-java.html
        String fileName = "/Users/Christina/DropBox/Courses/CS5300/project/pro1b/src/data.txt";
        String line = null;
        List<String> serverData = new ArrayList<>();

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                serverData.add(line.trim());
            }
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }
        return serverData;
    }

    public static void main(String[] args) {
        System.out.println(Utils.getServerData());
    }

}
