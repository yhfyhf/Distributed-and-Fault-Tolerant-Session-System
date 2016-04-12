package group;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


public class Utils {
    public static List<String> getServerData() {
        //https://www.caveofprogramming.com/java/java-file-reading-and-writing-files-in-java.html
        String fileName = "/Users/Christina/DropBox/Courses/CS5300/project/pro1b/src/data.txt";
        String line = null;
        List<String> serverData = new LinkedList<>();

        try {
            FileReader fileReader = new FileReader(fileName);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                serverData.add(line);
                System.out.println(line);
            }

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }
        return serverData;
    }

    public static void main(String[] args) {
        System.out.println(Utils.getServerData());
    }

}