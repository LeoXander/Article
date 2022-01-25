package edu.amorozov.article.view;

import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;

import edu.amorozov.article.R;
import edu.amorozov.article.adapter.CommentsAdapter;
import edu.amorozov.article.model.Article;
import edu.amorozov.article.model.Comment;

/**
 * Fragment to view an article
 */
public class ViewFragment extends Fragment implements
        View.OnClickListener,
        EventListener<DocumentSnapshot>,
        CommentDialogFragment.CommentListener {

    private static final String ARG_ARTICLE_ID = "article_id";
    private static final String TAG = "ViewFragment";
    private FirebaseFirestore mFirestore; //firestore instance
    private DocumentReference mArticleRef; //firestore reference to the article
    private ListenerRegistration mArticleRegistration;
    private RecyclerView mCommentsRv; //comments recycler view
    private CommentsAdapter mCommentsAdapter; //adapter for comments recycler view
    private CommentDialogFragment mCommentDialog; //dialog to add comment

    private WebView mArticleWv; //webview that displays the article

    /**
     * @param articleId
     * @return new instance of the class
     */
    public static ViewFragment getInstance(String articleId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_ARTICLE_ID, articleId);

        ViewFragment viewFragment = new ViewFragment();
        viewFragment.setArguments(bundle);
        return viewFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        String articleId = getArguments().getString(ARG_ARTICLE_ID);
        if (articleId == null) {
            throw new IllegalArgumentException("articleId is null");
        }

        mFirestore = FirebaseFirestore.getInstance();
        mArticleRef = mFirestore.collection("articles").document(articleId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_view, container, false);

        final FloatingActionButton fab = root.findViewById(R.id.fab_show_comment_dialog);
        fab.setOnClickListener(this);
        displayFab(fab);

        mArticleWv = root.findViewById(R.id.article_wv);
        mArticleWv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CardView cardView = getView().findViewById(R.id.article_cv);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 4);
                cardView.setLayoutParams(params);
                if (fab.getVisibility() != View.VISIBLE) {
                    displayFab(fab);
                }
                return false;
            }
        });
        final CardView cardView = root.findViewById(R.id.article_cv);
        mCommentsRv = root.findViewById(R.id.comments);

        // Get ratings
        Query commentsQuery = mArticleRef
                .collection("comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50);

        mCommentsAdapter = new CommentsAdapter(commentsQuery);
        mCommentsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mCommentsRv.setAdapter(mCommentsAdapter);

        mCommentsRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (fab.getVisibility() == View.VISIBLE)
                        fab.hide();
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
                    cardView.setLayoutParams(params);
                } else if (dy < 0) {
                    if (fab.getVisibility() != View.VISIBLE) {
                        displayFab(fab);
                    }
                }
            }
        });

        mCommentDialog = new CommentDialogFragment(this);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        mCommentsAdapter.startListening();
        mArticleRegistration = mArticleRef.addSnapshotListener(this);
    }

    /**
     * Shows floating action button
     * @param fab
     */
    private void displayFab(FloatingActionButton fab) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.isAnonymous() || user.getDisplayName() == null)
            fab.hide();
        else
            fab.show();
    }

    @Override
    public void onStop() {
        super.onStop();

        mCommentsAdapter.stopListening();

        if (mArticleRegistration != null) {
            mArticleRegistration.remove();
            mArticleRegistration = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_show_comment_dialog:
                onAddCommentClicked(v);
                break;
        }
    }

    /**
     * Shows comment dialog which fab is clicked
     * @param view
     */
    public void onAddCommentClicked(View view) {
        mCommentDialog.show(getActivity().getSupportFragmentManager(), CommentDialogFragment.TAG);
    }

    @Override
    public void onComment(Comment comment) {
        // In a transaction, add the new comment and update the aggregate totals
        addComment(mArticleRef, comment)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Rating added");

                        // Hide keyboard and scroll to top
                        hideKeyboard();
                        mCommentsRv.smoothScrollToPosition(0);
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Add comment failed", e);

                        // Show failure message and hide keyboard
                        hideKeyboard();
                        Snackbar.make(getView().findViewById(android.R.id.content), "Failed to add comment",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Adds comment to the firebase
     * @param articleRef firebase reference to the article
     * @param comment to add
     * @return task
     */
    private Task<Void> addComment(final DocumentReference articleRef,
                                 final Comment comment) {
        // Create reference for new comment, for use inside the transaction
        final DocumentReference commentsRef = articleRef.collection("comments")
                .document();

        // In a transaction, add the new comment and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction)
                    throws FirebaseFirestoreException {

                Article article = transaction.get(articleRef)
                        .toObject(Article.class);

                // Compute new number of ratings
                int newNumComments = article.getNumComments() + 1;

                // Set new article info
                article.setNumComments(newNumComments);

                // Commit to Firestore
                transaction.set(articleRef, article);
                transaction.set(commentsRef, comment);

                return null;
            }
        });
    }

    /**
     * Hides keyboard
     */
    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "restaurant:onEvent", e);
            return;
        }

        onArticleLoaded(snapshot.toObject(Article.class));
    }

    /**
     * Loads article html to webview
     * @param article to load
     */
    private void onArticleLoaded(Article article) {
        mArticleWv.loadData(Base64.encodeToString(article.getHtmlString().getBytes(), Base64.NO_PADDING),
                "text/html", "base64");
    }
}
