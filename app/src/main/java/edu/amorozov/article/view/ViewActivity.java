package edu.amorozov.article.view;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import edu.amorozov.article.SingleFragmentActivity;

/**
 * Activity to view an article
 */
public class ViewActivity extends SingleFragmentActivity {

    private static final String KEY_ARTICLE_ID = "edu.amorozov.article.view.article_id";

    /**
     * @param packageContext
     * @param articleId which is to be loaded
     * @return new instance of the class
     */
    public static Intent newIntent(Context packageContext, String articleId) {
        Intent intent = new Intent(packageContext, ViewActivity.class);
        intent.putExtra(ViewActivity.KEY_ARTICLE_ID, articleId);

        return intent;
    }

    @Override
    protected Fragment createFragment() {
        String articleId = getIntent().getExtras().getString(KEY_ARTICLE_ID);
        return ViewFragment.getInstance(articleId);
    }
}
