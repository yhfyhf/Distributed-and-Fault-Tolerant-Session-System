package group;

import RPC.Conf;

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
    public static List<String> readServerData() {
        //https://www.caveofprogramming.com/java/java-file-reading-and-writing-files-in-java.html
        String fileName = Conf.SERVERS_INFO_FILEPATH;
        String line;
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

    /**
     * Get the reboot number from the rebootnum.txt
     */
    public static int readRebootNum() {
        String fileName = Conf.REBOOTNUM_FILEPATH;
        String line;
        int rebootNum = 0;

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                rebootNum = Integer.valueOf(line.trim());
            }
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }
        return rebootNum;
    }

    /**
     * Get F from the f.txt
     * */
    public static List<Integer> readFAndN() {
        String fileName = Conf.FANDN_FILEPATH;
        String line;
        List ret = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                ret.add(Integer.valueOf(line.trim()));
            }
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }

        return ret;
    }
}
