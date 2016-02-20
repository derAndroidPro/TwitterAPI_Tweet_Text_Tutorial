package derandroidpro.de.twitter.api.tutorial;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    // Diese beiden Codes solltet ihr geheim halten, wenn ihr euren Code veröffentlicht:
    private static final String TWITTER_KEY = "vfgjnMm7HyFFHj2LjLTa7ncwJ";
    private static final String TWITTER_SECRET = "8UuWgSXnC0j20vGhf65JnLe8bincUkMdwPZTuc69XmMQWqWt0U";

    TwitterSession twitterSession;

    EditText et1;
    TextView tvCharacters;
    Button btnPost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        twitterSession = Twitter.getSessionManager().getActiveSession();
        if(twitterSession == null){
            startActivity(new Intent(MainActivity.this, TwitterLoginActivity.class));
            finish();
        } else {
            // Etwas mit der Twitter API tun
        }

        setUpUi();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.menu_logout  && twitterSession != null){

            Twitter.logOut();
            Toast.makeText(MainActivity.this, "Von Twitter abgemeldet.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, TwitterLoginActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpUi(){
        et1 = (EditText) findViewById(R.id.editText);
        tvCharacters = (TextView) findViewById(R.id.textView);
        btnPost = (Button) findViewById(R.id.button);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postTweet(et1.getText().toString());
                et1.setText(null);
            }
        });

        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(charactersCountOk(s.toString())){
                    btnPost.setEnabled(true);
                } else {
                    btnPost.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean charactersCountOk (String text){
        int numerUrls = 0;
        int lengthAllUrls = 0;

        String regex = "\\(?\\b(http://|https://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        Pattern urlPattern = Pattern.compile(regex);
        Matcher urlMatcher = urlPattern.matcher(text);
        while (urlMatcher.find()){
            lengthAllUrls += urlMatcher.group().length();
            numerUrls++;
        }

        int tweetLength = text.length()-lengthAllUrls+numerUrls*23;
        tvCharacters.setText(Integer.toString(140-tweetLength));

        if(tweetLength >0 && tweetLength <= 140){
            return true;
        } else {
            return false;
        }
    }

    private void postTweet(String text){
        StatusesService statusesService = Twitter.getApiClient().getStatusesService();
        statusesService.update(text, null, false, null, null, null, false, false, null, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                Toast.makeText(MainActivity.this, "Tweet gepostet.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(TwitterException e) {
                Toast.makeText(MainActivity.this, "Tweet konnte nicht gepostet werden.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }
}
