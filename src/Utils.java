import java.io.File;

public class Utils {
    static public void deleteFiles(String directory){
        File dir = new File(directory);
        for(File file: dir.listFiles())
            if (!file.isDirectory())
                file.delete();
    }
}
