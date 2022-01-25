package edu.amorozov.article.feed;

import androidx.lifecycle.ViewModel;

/**
 * ViewModel for MainActivity – FeedActivity
 */
public class FeedViewModel extends ViewModel {

    private boolean isSigningIn;

    /**
     * Constructor
     */
    public FeedViewModel() {
        isSigningIn = false;
    }

    public boolean isSigningIn() {
        return isSigningIn;
    }

    public void setSigningIn(boolean signingIn) {
        isSigningIn = signingIn;
    }
}
