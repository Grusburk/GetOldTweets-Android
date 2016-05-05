package com.d4t.getoldtweetslibrary.manager;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.d4t.getoldtweetslibrary.model.Tweet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TweetManager {

    private static final int TWEETS_FETCHED = 12;
    private final List<Tweet> tweets = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == TWEETS_FETCHED)  {
                callback.onResponse(tweets);
            }
        }
    };
    private TwitterCallback callback;
    /**
     * @param querySearch (The word to search for)
     * @param tweetCount (Number of tweets you want)
     * @param callback (Callback that will receive the result and/or error)
	 */
	public void executeTwitterQuery(final String querySearch, final int tweetCount, final TwitterCallback callback) {
        this.callback = callback;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String url = String.format("https://mobile.twitter.com/search?q=%s", querySearch);
                try {
                    String html = new TwitterTask().execute(url).get();
                    Document doc = getDocumentFromHtmlString(html, false);
                    Elements elements = getTweetsFromDocument(doc);
                    tweets.addAll(parse(elements));
                    while (tweets.size() < tweetCount) {
                        String continueUrl = doc.select("div.w-button-more a").attr("href");
                        if ( continueUrl == null || continueUrl.length() == 0) {
                            break;
                        }
                        url = String.format("https://mobile.twitter.com%s", continueUrl);
                        html = new TwitterTask().execute(url).get();
                        doc = Jsoup.parse(html);
                        elements = doc.select("table.tweet");
                        tweets.addAll(parse(elements));
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();

                    callback.onFailure(e.getCause());
                } finally {
                    handler.sendEmptyMessage(TWEETS_FETCHED);
                }
            }
        });
        thread.start();
	}

    public Document getDocumentFromHtmlString(String html, boolean isPartialHtml) {
        if (isPartialHtml) {
            return Jsoup.parseBodyFragment(html);
        }
        return Jsoup.parse(html);
    }

    public Elements getTweetsFromDocument(Document doc) {
        return doc.select("table.tweet");
    }

    public List<Tweet> parse(Elements tweets) {
        List<Tweet> tweetList = new ArrayList<>();
        for (Element tweet : tweets) {
            String usernameTweet = tweet.select("div.username").text();
            String txt = tweet.select("div.tweet-text").text().replaceAll("[^\\u0000-\\uFFFF]", "");
            String fullname = tweet.select("strong.fullname").text();
            String dateMs = tweet.select("td.timestamp a").text(); //Long.valueOf(tweet.select("small.time span.js-short-timestamp").attr("data-time-ms"));
            String avatarUrl = tweet.select("td.avatar a img").attr("src");
            Tweet t = new Tweet();
            t.setUsername(usernameTweet);
            t.setFullName(fullname);
            t.setText(txt);
            t.setDate(dateMs);
            t.setAvatarUrl(avatarUrl);
            tweetList.add(t);
//            int retweets = Integer.valueOf(tweet.select("span.ProfileTweet-action--retweet span.ProfileTweet-actionCount").attr("data-tweet-stat-count").replaceAll(",", ""));
//            int favorites = Integer.valueOf(tweet.select("span.ProfileTweet-action--favorite span.ProfileTweet-actionCount").attr("data-tweet-stat-count").replaceAll(",", ""));
//            String id = tweet.attr("data-tweet-id");
//            String permalink = tweet.attr("data-permalink-path");

//            String geo = "";
//            Elements geoElement = tweet.select("span.Tweet-geo");
//            if (geoElement.size() > 0) {
//                geo = geoElement.attr("title");
//            }

            //t.setPermalink("https://twitter.com"+permalink);
//            t.setId(id);
//            t.setRetweets(retweets);
//            t.setFavorites(favorites);
//            t.setMentions(processTerms("(@\\w*)", txt));
//            t.setHashtags(processTerms("(#\\w*)", txt));
//            t.setGeo(geo);
        }
        return tweetList;
    }

    private static String processTerms(String patternS, String tweetText) {
		StringBuilder sb = new StringBuilder();
		Matcher matcher = Pattern.compile(patternS).matcher(tweetText);
		while (matcher.find()) {
			sb.append(matcher.group());
			sb.append(" ");
		}

		return sb.toString().trim();
	}

    public interface TwitterCallback {
        void onResponse(List<Tweet> tweets);
        void onFailure(Throwable t);
    }
}