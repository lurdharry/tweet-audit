package org.lurdharry.tweetAudit.parser;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.lurdharry.tweetAudit.model.Tweet;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class CsvParser {

    public List<Tweet> parse(Path filepath) throws IOException {

        try(Reader reader = new FileReader(filepath.toFile())) {
            CSVReader csvReader = new CSVReader(reader);

            String[] headers = csvReader.readNext();

            if (headers == null) {
                throw new IOException("csv file is empty");
            }
            if (!Arrays.equals(headers, new String[]{"id_str", "full_text"})) {
                throw new IOException("Invalid CSV format: expected 'id_str,full_text' headers");
            }
            List<Tweet> tweets = new ArrayList<>();
            for(String[] row:csvReader){
                if (row.length != 2) {
                    throw new IOException("Invalid CSV record: expected 2 fields, got " + row.length);
                }
                tweets.add(new Tweet(row[0],row[1]));
            }

            return tweets;

        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

}
