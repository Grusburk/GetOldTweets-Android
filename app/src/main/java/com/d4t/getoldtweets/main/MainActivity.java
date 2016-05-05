package com.d4t.getoldtweets.main;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.d4t.getoldtweets.R;
import com.d4t.getoldtweetslibrary.manager.TweetManager;
import com.d4t.getoldtweetslibrary.model.Tweet;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        TwitterCriteria criteria;

        /**
         *  Example 1 - Get tweets by username
         **/

        TweetManager manager = new TweetManager();
        manager.executeTwitterQuery("Satansdemokrati", 100, new TweetManager.TwitterCallback() {
            @Override
            public void onResponse(List<Tweet> tweets) {
                Log.i("Tweets", "Size is: " + tweets.toString());
                toolbar.setBackgroundColor(Color.RED);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

}
