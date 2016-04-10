package RPC;

import java.io.*;

/**
 * Created by yhf on 4/9/16.
 */
public class Utils {
    public static byte[] stringToByteArray(String s) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(bos);
        out.writeObject(s);
        return bos.toByteArray();
    }

    public static String byteArrayToString(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = new ObjectInputStream(bis);
        return (String) in.readObject();
    }
}
