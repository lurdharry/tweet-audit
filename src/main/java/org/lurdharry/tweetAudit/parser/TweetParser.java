package org.lurdharry.tweetAudit.parser;

import org.lurdharry.tweetAudit.model.Tweet;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TweetParser {
    private final FileType fileType;
    private  final Path path;


    public TweetParser(String path , FileType parserType){
        this.fileType = parserType;
        this.path = Paths.get(path).normalize();

    }

    public List<Tweet> parse() throws IOException {
       return switch (fileType){
            case CSV ->  new CsvParser().parse(path);
            case JSON ->  new JsonParser().parse(path);
        };
    }
}
