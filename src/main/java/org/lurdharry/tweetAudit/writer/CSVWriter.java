package org.lurdharry.tweetAudit.writer;

import org.lurdharry.tweetAudit.model.AnalysisResult;
import org.lurdharry.tweetAudit.model.Decision;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CSVWriter implements AutoCloseable{

    private final BufferedWriter writer;
    private boolean headerWritten;

    public CSVWriter(Path path, boolean append) throws IOException {
        Path dir = path.getParent();
        if (dir != null) {
            Files.createDirectories(dir);
        }
        this.headerWritten = append && Files.exists(path);
        this.writer = new BufferedWriter(new FileWriter(path.toFile(),append));
    }

    public static CSVWriter create(String path, boolean append) throws IOException {
        if (path == null || path.isBlank()) {
            throw  new IllegalArgumentException("Path cannot be null or blank");

        }
        return new CSVWriter(Paths.get(path).normalize(),append);
    }

    public void writeResult(AnalysisResult result) throws IOException {
        if (!headerWritten){
            writer.write("tweet_url,deleted");
            writer.newLine();
            headerWritten = true;
        }
        if (result.decision() == Decision.DELETE){
            writer.write(escapeCsv(result.tweetUrl() + ", false"));
            writer.newLine();
            writer.flush();
        }
    }


    public void  writeResults(List<AnalysisResult> results) throws IOException {
        for (AnalysisResult result:results){
            writeResult(result);
        }

    }
    private String escapeCsv(String s) {
        if (s.contains(",")||s.contains("\"")||s.contains("\n")){
            return "\"" + s.replace("\"","\"\"") + "\"";
        }
        return s;
    }

    @Override
    public void close() throws Exception {
        if (writer != null) {
            writer.close();
        }

    }
}
