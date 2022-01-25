package edu.amorozov.article.create;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

import edu.amorozov.article.R;
import edu.amorozov.article.adapter.CreatePagerAdapter;
import edu.amorozov.article.feed.FeedFragment;
import edu.amorozov.article.model.Article;

/**
 * Fragment to create a new Article
 */
public class CreateFragment extends Fragment {

    private static final String PARCELABLE_USER = "parcelable_user";
    private CreatePagerAdapter mAdapter; //Adapter for the pager
    private ViewPager mViewPager; //pager that contains content and style pages
    private FirebaseUser mUser; //user who creates the article

    /**
     * @param user who creates the article
     * @return new instance of the class
     */
    public static CreateFragment getInstance(FirebaseUser user) {
        CreateFragment createFragment = new CreateFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(PARCELABLE_USER, user);
        createFragment.setArguments(bundle);
        return createFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = getArguments().getParcelable(PARCELABLE_USER);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        CreateActivity createActivity = (CreateActivity) getActivity();
        mAdapter = new CreatePagerAdapter(getChildFragmentManager());
        mAdapter.setupTabsSync(this, createActivity.mViewModel);
        mViewPager = view.findViewById(R.id.pager_vp);
        mViewPager.setAdapter(mAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.create_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                done();
                return true;
            default:
                return false;
        }
    }

    /**
     * Returns created article with new title
     */
    private void done() {
        final CreateActivity activity = (CreateActivity) getActivity();
        final CreateViewModel viewModel = activity.mViewModel;

        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);

        alert.setTitle("Title");
        alert.setMessage("Enter the title of the article");

        // Set an EditText view to get user input
        final EditText input = new EditText(activity);
        input.setMaxLines(1);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String title = input.getText().toString();

                if (title.isEmpty()) return;

                String userName;
                if (mUser == null)
                    userName = "no name";
                else
                    userName = mUser.getDisplayName();

                Article article = new Article(viewModel.getHtmlString().getValue(),
                        title, userName, String.valueOf(Calendar.getInstance().getTimeInMillis()), 0);
                Intent intent = new Intent();
                intent.putExtra(FeedFragment.RESULT_ARTICLE, article);
                activity.setResult(Activity.RESULT_OK, intent);
                activity.finish();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    /**
     * @return the current page
     */
    public int getCurrentPage() {
        return mViewPager.getCurrentItem();
    }
}
