import java.io.File;

public class Utils {
    static public void deleteFiles(String directory) {
        File dir = new File(directory);
        if(dir.listFiles() != null) {
            for (File file : dir.listFiles())
                if (file != null && !file.isDirectory())
                    file.delete();
        }
    }
}
