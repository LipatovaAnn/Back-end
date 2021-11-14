package RestAssured;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class FileUtils
{
    public static byte[] getFileContent(String filePath) {
        byte[] byteArray = new byte[0];
        try {
            byteArray = org.apache.commons.io.FileUtils.readFileToByteArray(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    public static String getContentAsBase64String(String filePath){
        byte[] byteArray = getFileContent(filePath);
        return Base64.getEncoder().encodeToString(byteArray);
    }
}
