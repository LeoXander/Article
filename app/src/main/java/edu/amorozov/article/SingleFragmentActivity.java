package edu.amorozov.article;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * Base activity that contains one fragment
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

    /**
     * Creates a fragment for the activity
     * @return fragment
     */
    protected abstract Fragment createFragment();

    /**
     * Loads the fragment
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.container);
        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction().add(R.id.container, fragment).commit();
        }
    }
}
