package sample.Tools;

import java.io.File;
import java.io.IOException;

public class GetFile {
    File file;
    File folder;
    String path;

    {
        try {
            path = new File(".").getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String kitobNomi;
    String lastFolder = "/Data";

    public GetFile(){
    }

    public GetFile(String kitobNomi){
        this.kitobNomi = kitobNomi;
    }

    public File getFile() {
        String filePath;
        folder = new File(path + lastFolder);
        if (!folder.exists()) {
            boolean folderCreated = folder.mkdirs();
            if (folderCreated) {
                System.out.println("Ochdi");
            }
        }
        filePath = folder + "/" + kitobNomi.trim();
        System.out.println(filePath);
        File file = new File(filePath);
        if (file.isFile()) {
            System.out.println("Topdim");
        } else {
            System.out.println("Topa olmadim. Yangi fayl ochaman");
        }
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getLastFolder() {
        return lastFolder;
    }

    public void setLastFolder(String lastFolder) {
        this.lastFolder = lastFolder;
    }

    public String getKitobNomi() {
        return kitobNomi;
    }

    public void setKitobNomi(String kitobNomi) {
        this.kitobNomi = kitobNomi;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
