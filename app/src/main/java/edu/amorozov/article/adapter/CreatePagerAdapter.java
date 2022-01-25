package edu.amorozov.article.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;

import edu.amorozov.article.create.ContentFragment;
import edu.amorozov.article.create.CreateFragment;
import edu.amorozov.article.create.CreateViewModel;
import edu.amorozov.article.create.StyleFragment;

/**
 * Adapter for tabs in Create Fragment
 */
public class CreatePagerAdapter extends FragmentPagerAdapter {

    private ContentFragment mContentFragment; //Fragment with content
    private StyleFragment mStyleFragment; // Fragment with style

    /**
     * Constructor
     * @param fm fragment manager
     */
    public CreatePagerAdapter(FragmentManager fm) {
        super(fm);
        mContentFragment = ContentFragment.getInstance();
        mStyleFragment = StyleFragment.getInstance();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                //load ContentFragment
                return mContentFragment;
            case 1:
                //load StyleFragment
                return mStyleFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Content";
            case 1:
                return "Style";
            default:
                return "no title";
        }
    }

    /**
     * Syncs Content and Style tabs
     * @param parent CreateFragment
     * @param viewModel CreateViewModel associated with the activity
     */
    public void setupTabsSync(final CreateFragment parent, CreateViewModel viewModel) {
        viewModel.getHtmlString().observe(parent.getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (parent.getCurrentPage() == CreateViewModel.CONTENT_PAGE)
                    mStyleFragment.loadHtml(s);
                else {
                    mStyleFragment.reloadHtml(s);
                    mContentFragment.loadHtml(s);
                }
            }
        });
    }
}
