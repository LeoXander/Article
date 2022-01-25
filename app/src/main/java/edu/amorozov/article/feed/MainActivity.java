package edu.amorozov.article.feed;

import androidx.fragment.app.Fragment;

import android.os.Bundle;

import edu.amorozov.article.SingleFragmentActivity;

/**
 * MainActivity – FeedActivity
 */
public class MainActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return FeedFragment.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
