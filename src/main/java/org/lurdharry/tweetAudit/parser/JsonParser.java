package org.lurdharry.tweetAudit.parser;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.lurdharry.tweetAudit.model.Tweet;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JsonParser {

    private final Gson gson = new Gson();

    public List<Tweet> parse(Path filePath) throws IOException {

        try(Reader reader = new FileReader(filePath.toFile())){

            TweetWrapper[] wrappers = gson.fromJson(reader,TweetWrapper[].class);
            if (wrappers == null) {
                throw  new IOException("Invalid JSON: empty or malformed content");
            }

            return getTweets(wrappers);
        }
    }

    private static List<Tweet> getTweets(TweetWrapper[] wrappers) throws IOException {
        List<Tweet> tweets = new ArrayList<>();

        for(TweetWrapper wrapper : wrappers){
            if (wrapper == null||wrapper.tweet==null) {
                throw new IOException("Missing wrapper or tweet data");
            }
            if (wrapper.tweet.id == null||wrapper.tweet.text==null) {
                throw new IOException("Missing wrapper or tweet data");
            }

            tweets.add(new Tweet(wrapper.tweet.id,wrapper.tweet.text));
        }
        return tweets;
    }

    private static final class TweetWrapper{
        TweetData tweet;

        private static  final class TweetData {

            @SerializedName("id_str")
            private String id;

            @SerializedName("full_text")
            private String text;

        }
    }
}
