package com.example.twitter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import java.util.List;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    private TwitterLoginButton loginButton;
    private TextView tweetsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig("tNH6vrBPq3CPVFyr2mk8AFDfY", "cGLr14WkAyCLt3J4T19REGtNVqkM4IntDk2zBOlkgqWF9wQxfu"))
                .debug(true)
                .build();
        Twitter.initialize(config);

        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.login_button);
        tweetsTextView = findViewById(R.id.tweets_text_view);

        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
                StatusesService statusesService = twitterApiClient.getStatusesService();
                Call<List<Tweet>> calls = statusesService.userTimeline(session.getUserId(), null, 2, null, null, null, null, null, null);
                calls.enqueue(new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> result) {
                        String text = new String();
                        for (Tweet tweet : result.data) {
                            text += tweet.text + "\n";
                        }
                        tweetsTextView.setText(text);
                    }

                    @Override
                    public void failure(TwitterException exception) {

                    }
                });
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }
}
