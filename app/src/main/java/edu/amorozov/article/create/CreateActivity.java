package edu.amorozov.article.create;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseUser;

import edu.amorozov.article.SingleFragmentActivity;

/**
 * Activity to create a new Article
 */
public class CreateActivity extends SingleFragmentActivity {

    protected CreateViewModel mViewModel; //ViewModel

    private static final String EXTRA_USER = "extra_user";

    /**
     * @param packageContext context
     * @param user who creates the article
     * @return new intent
     */
    public static Intent newIntent(Context packageContext, FirebaseUser user) {
        Intent intent = new Intent(packageContext, CreateActivity.class);
        intent.putExtra(EXTRA_USER, user);

        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return CreateFragment.getInstance((FirebaseUser) getIntent().getParcelableExtra(EXTRA_USER));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(CreateViewModel.class);
    }
}
