package org.lurdharry.tweetAudit.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
*  Progress tracking of analyzed tweets
*
*/
public class Checkpoint {

    private final Path path;

    public Checkpoint(String path){
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Path cannot be null or blank");
        }
        this.path = Paths.get(path).normalize();
    }

    public int load() throws IOException {
        if (!Files.exists(path)){
            return 0;
        }
        try {
            String detail = Files.readString(path).trim();
            if (detail.isBlank()){
                return 0;
            }
            return Integer.parseInt(detail);
        } catch (NumberFormatException e) {
            throw new IOException("Invalid file details: " + e.getMessage());
        }
    }
    
    public void save(int index) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(path,String.valueOf(index));
    }

    public void reset() throws IOException {
        Files.deleteIfExists(path);
    }
}
