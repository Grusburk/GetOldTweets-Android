package com.d4t.getoldtweetslibrary;

import com.d4t.getoldtweetslibrary.manager.TweetManager;
import com.d4t.getoldtweetslibrary.model.Tweet;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class TweetParserTest {

    private String tweet = "<body><table class=\"tweet  \" href=\"/agarajev/status/727447785788813312?p=v\"> \n" +
            "       <tbody>\n" +
            "        <tr class=\"tweet-header \"> \n" +
            "         <td class=\"avatar\" rowspan=\"3\"> <a href=\"/agarajev?p=i\"> <img alt=\"azad\" src=\"https://pbs.twimg.com/profile_images/699715979417411585/NCjviplP_normal.jpg\"> </a> </td> \n" +
            "         <td class=\"user-info\"> <a href=\"/agarajev?p=s\"> <strong class=\"fullname\">azad</strong> \n" +
            "           <div class=\"username\"> \n" +
            "            <span>@</span>agarajev \n" +
            "           </div> </a></td> \n" +
            "         <td class=\"timestamp\"> <a name=\"tweet_727447785788813312\" href=\"/agarajev/status/727447785788813312?p=p\">May 3</a> </td>\n" +
            "        </tr>\n" +
            "        <tr class=\"tweet-container\"> \n" +
            "         <td colspan=\"2\" class=\"tweet-content\"> \n" +
            "          <div class=\"tweet-text\" data-id=\"727447785788813312\"> \n" +
            "           <div class=\"dir-ltr\" dir=\"ltr\">\n" +
            "             I don't know why I visited \n" +
            "            <a href=\"/hashtag/satansdemokrati?src=hash\" data-query-source=\"hashtag_click\" class=\"twitter-hashtag dir-ltr\" dir=\"ltr\">#<span class=\"twitter-hit-highlight\">satansdemokrati</span></a>.\n" +
            "           </div> \n" +
            "          </div> </td>\n" +
            "        </tr> \n" +
            "        <tr> \n" +
            "         <td colspan=\"2\" class=\"meta-and-actions\"> <span class=\"metadata\"> <a href=\"/agarajev/status/727447785788813312?p=v\">View details</a> <span class=\"middot\">·</span> </span> <span class=\"tweet-actions\"> <a href=\"/agarajev/reply/727447785788813312\" class=\"first\"> <span class=\"imgsprite_tweet_reply_gif\" title=\"Reply\"></span> </a> <a href=\"/statuses/727447785788813312/retweet\"> <span class=\"imgsprite_tweet_rt_gif\" title=\"Retweet\"></span> </a> <a href=\"/statuses/727447785788813312/favorite?authenticity_token=8a06bd74d8a0ebbf73626f79c14cd5cc\" class=\"favorite\"> <span class=\"imgsprite_tweet_heart_gif\" title=\"Like\"></span> </a> <a href=\"/agarajev/status/727447785788813312/actions\" class=\"last\"></a> </span> </td> \n" +
            "        </tr>\n" +
            "       </tbody>\n" +
            "      </table></body> ";

    private TweetManager manager;
    private Document doc;
    private Elements elements;

    @Before
    public void setUp() {
        manager = new TweetManager();
        doc = manager.getDocumentFromHtmlString(tweet, true);
        elements = manager.getTweetsFromDocument(doc);

    }

    @Test
    public void testHtmlIsValidAndParcelable() throws Exception {
        Assert.assertTrue("Body was null when it shouldn't", doc.body() != null);
        Assert.assertTrue("Could not get elements from html", elements != null && elements.size() > 0);
        Assert.assertTrue("Html should only contain one tweet", elements.size() == 1);
        List<Tweet> tweets = manager.parse(elements);
        Assert.assertTrue("Could not get tweets from html", tweets != null && tweets.size() > 0);
        Assert.assertTrue("Should only be one tweet", tweets.size() == 1);
    }

    @Test
    public void testParsingTheTextOfATweet() {
        Element tweet = elements.first();
        String txt = tweet.select("div.tweet-text").text().replaceAll("[^\\u0000-\\uFFFF]", "");
        Assert.assertTrue(txt.length() > 0);
        Assert.assertEquals("I don't know why I visited #satansdemokrati.", txt);
    }

    @Test
    public void testParsingTheUsernameOfATweet() {
        Element tweet = elements.first();
        String usernameTweet = tweet.select("div.username").text();
        Assert.assertTrue(usernameTweet.length() > 0);
        Assert.assertEquals("@agarajev", usernameTweet);
    }

    @Test
    public void testParsingAvatarUrlOfATweet() {
        Element tweet = elements.first();
        String avatarUrl = tweet.select("td.avatar a img").attr("src");
        Assert.assertTrue(avatarUrl.length() > 0);
        Assert.assertEquals("https://pbs.twimg.com/profile_images/699715979417411585/NCjviplP_normal.jpg", avatarUrl);
    }
}