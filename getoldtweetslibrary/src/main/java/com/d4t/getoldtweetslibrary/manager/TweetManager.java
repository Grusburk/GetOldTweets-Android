package com.d4t.getoldtweetslibrary.manager;

import android.support.annotation.NonNull;

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


/**
 * Class to getting tweets based on username and optional time constraints
 * 
 * @author Jefferson Henrique
 */
public class TweetManager {

    private final List<Tweet> tweets = new ArrayList<>();
    /**
	 * @param tweetCount (Parameter used by Twitter to do pagination of results)
	 * @return JSON response used by Twitter to build its results
	 * @throws Exception
	 */
	public void executeTwitterQuery(@NonNull final String querySearch, final int tweetCount, final TwitterCallback callback) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String url = String.format("https://mobile.twitter.com/search?q=%s", querySearch);

                try {
                    String html = new TwitterTask().execute(url).get();
                    Document doc = Jsoup.parse(html);
                    Elements elements = doc.select("table.tweet");
                    tweets.addAll(parse(elements));
                    while (tweets.size() < tweetCount) {
                        String continueUrl = doc.select("div.w-button-more a").attr("href");
                        if ( continueUrl == null || continueUrl.length() == 0) {
                            callback.onResponse(tweets);
                            break;
                        }
                        url = String.format("https://mobile.twitter.com%s", continueUrl);
                        html = new TwitterTask().execute(url).get();
                        doc = Jsoup.parse(html);
                        elements = doc.select("table.tweet");
                        tweets.addAll(parse(elements));
                    }
                    callback.onResponse(tweets);
                } catch (InterruptedException | ExecutionException e) {
                    callback.onFailure(e.getCause());
                }
            }
        });
        thread.start();
	}



//	/**
//	 * @param criteria An object of the class {@link TwitterCriteria} to indicate how tweets must be searched
//	 * @return A list of all tweets found
//	 */
//	public void getTweets(TwitterCriteria criteria) {
//		List<Tweet> results = new ArrayList<Tweet>();
////        getURLResponse();
////		try {
////			String refreshCursor = null;
////			outerLace: while (true) {
//        try {
////            getURLResponse(criteria.getQuerySearch(), null, new TwitterTask.HtmlResultHandler() {
////                @Override
////                public void didRecieveHtml(String html) {
////                    Document doc = Jsoup.parse(html);
////                    Elements elements = doc.select("table.tweet");
////                    List<Tweet> tweetList = parse(elements);
////                }
////            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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
            String usernameTweet = tweet.select("span.username.js-action-profile-name b").text();
            String txt = tweet.select("div.tweet-text").text().replaceAll("[^\\u0000-\\uFFFF]", "");
//            int retweets = Integer.valueOf(tweet.select("span.ProfileTweet-action--retweet span.ProfileTweet-actionCount").attr("data-tweet-stat-count").replaceAll(",", ""));
//            int favorites = Integer.valueOf(tweet.select("span.ProfileTweet-action--favorite span.ProfileTweet-actionCount").attr("data-tweet-stat-count").replaceAll(",", ""));
            String dateMs = tweet.select("td.timestamp a").text(); //Long.valueOf(tweet.select("small.time span.js-short-timestamp").attr("data-time-ms"));
            String avatarUrl = tweet.select("td.avatar a img").attr("src");
//            String id = tweet.attr("data-tweet-id");
//            String permalink = tweet.attr("data-permalink-path");

//            String geo = "";
//            Elements geoElement = tweet.select("span.Tweet-geo");
//            if (geoElement.size() > 0) {
//                geo = geoElement.attr("title");
//            }

//            Date date = new Date(dateMs);

            Tweet t = new Tweet();
//            t.setId(id);
            //t.setPermalink("https://twitter.com"+permalink);
            t.setUsername(usernameTweet);
            t.setText(txt);
            t.setDate(dateMs);
            t.setAvatarUrl(avatarUrl);
//            t.setRetweets(retweets);
//            t.setFavorites(favorites);
//            t.setMentions(processTerms("(@\\w*)", txt));
//            t.setHashtags(processTerms("(#\\w*)", txt));
//            t.setGeo(geo);
            tweetList.add(t);
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