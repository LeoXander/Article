package edu.amorozov.article.feed;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;


import java.util.Collections;

import edu.amorozov.article.R;
import edu.amorozov.article.adapter.ArticleAdapter;
import edu.amorozov.article.create.CreateActivity;
import edu.amorozov.article.model.Article;
import edu.amorozov.article.view.ViewActivity;

import static android.app.Activity.RESULT_OK;

/*
    Login: test@test.test
    Password: qwerty123
 */

/**
 * Fragment for feed
 */
public class FeedFragment extends Fragment
        implements
        View.OnClickListener,
        ArticleAdapter.OnArticleSelectedListener{

    private static final int RC_SIGN_IN = 1;
    private static final int LIMIT = 20;
    private static final String TAG = "FeedFragment";
    public static final int REQUEST_CODE_CREATE = 5;
    public static final String RESULT_ARTICLE = "result_article";

    private Menu optionsMenu; //options menu
    private FirebaseFirestore mFirestore; //firestore instance
    private FirebaseAuth mAuth; //authentication object
    private Query mQuery; //query for firebase
    private RecyclerView mArticlesRv; //list of articles
    private ArticleAdapter mAdapter; //adapter for list of articles
    private FirebaseUser mUser; //current user

    private FeedViewModel mViewModel; //view model

    /**
     * @return new instance of the class
     */
    public static FeedFragment getInstance() {
        return new FeedFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        initFirestore();

        mViewModel = new ViewModelProvider(getActivity(), new ViewModelProvider.NewInstanceFactory()).get(FeedViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_feed, container, false);

        mArticlesRv = root.findViewById(R.id.articles_rv);
        final SearchView searchBarSv = root.findViewById(R.id.search_sv);
        searchBarSv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBarSv.setIconified(false);
            }
        });
        searchBarSv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Query query;
                if (newText.isEmpty())
                    query = mFirestore.collection("articles").orderBy("date", Query.Direction.DESCENDING)
                        .limit(LIMIT);
                else
                    query = mFirestore.collection("articles")
                        .whereGreaterThanOrEqualTo("title", newText).limit(LIMIT);

                mAdapter.setQuery(query);
                return true;
            }
        });

        initRecyclerView();

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.main_menu, menu);
        optionsMenu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_in:
                signIn();
                return true;

            case R.id.action_sing_out:
                signOut();
                return true;

            case R.id.action_add:
                addTestItem();
                return true;

            case R.id.action_create:
                create();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mUser = mAuth.getCurrentUser();

        // Start sign in if necessary
        if (shouldStartSignIn()) {
//            signIn();
            return;
        }

        // Apply filters
//        onFilter(mViewModel.getFilters());

        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    /**
     * Checks whether to ask user to sign in
     * @return if user should sing in
     */
    private boolean shouldStartSignIn() {
        return (!mViewModel.isSigningIn() && FirebaseAuth.getInstance().getCurrentUser() == null);
    }

    /**
     * Calls sing-in activity
     */
    private void signIn() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Collections.singletonList(new AuthUI.IdpConfig.EmailBuilder().build()))
                        .build(),
                RC_SIGN_IN);

    }

    /**
     * Signs user out
     */
    private void signOut() {
        AuthUI.getInstance()
                .signOut(getContext())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        toggleOptionsMenu();
                    }
                });

        mAuth.signInAnonymously().addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mUser = mAuth.getCurrentUser();
                    mAdapter.setQuery(mQuery);
                } else {
                    Log.w(TAG, "signInAnonymously:failure", task.getException());
                    Toast.makeText(getContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Switches menu elements' visibility
     */
    private void toggleOptionsMenu() {
        MenuItem signIn = optionsMenu.findItem(R.id.action_sign_in);
        MenuItem signOut = optionsMenu.findItem(R.id.action_sing_out);
        MenuItem create = optionsMenu.findItem(R.id.action_create);

        signIn.setVisible(!signIn.isVisible());
        signOut.setVisible(!signOut.isVisible());
        create.setVisible(!create.isVisible());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            mViewModel.setSigningIn(false);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                mUser = FirebaseAuth.getInstance().getCurrentUser();
                Log.i(TAG, "User " + mUser.getDisplayName() + " logged in");
                toggleOptionsMenu();
            }
        } else if (requestCode == REQUEST_CODE_CREATE) {
            if (resultCode == RESULT_OK) {
                Article article = data.getExtras().getParcelable(RESULT_ARTICLE);
                addItem(article);
            }
        }
    }

    /**
     * Initializes recycler view
     */
    public void initRecyclerView() {
        mAdapter = new ArticleAdapter(mQuery, this) {

            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mArticlesRv.setVisibility(View.GONE);
                } else {
                    mArticlesRv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(getView().findViewById(android.R.id.content),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };
        mArticlesRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mArticlesRv.setAdapter(mAdapter);
    }

    /**
     * Initializes Firestore
     */
    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mAuth.signInAnonymously().addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mUser = mAuth.getCurrentUser();
                    mAdapter.setQuery(mQuery);
                } else {
                    Log.w(TAG, "signInAnonymously:failure", task.getException());
                    Toast.makeText(getContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Get the 50 highest rated restaurants
        mQuery = mFirestore.collection("articles")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(LIMIT);

    }

    /**
     * For test purposes only
     * Adds test item to the Firebase
     */
    private void addTestItem() {
        CollectionReference articles = mFirestore.collection("articles");
        articles.add(new Article("test1"));
    }

    /**
     * Adds article to the Firebase
     * @param article to add
     */
    private void addItem(Article article) {
        CollectionReference articles = mFirestore.collection("articles");
        articles.add(article);
    }

    /**
     * Launches CreateActivity
     */
    private void create() {
        startActivityForResult(CreateActivity.newIntent(getContext(), mUser), REQUEST_CODE_CREATE);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onArticleSelected(DocumentSnapshot article) {
        startActivity(ViewActivity.newIntent(getContext(), article.getId()));
    }
}
