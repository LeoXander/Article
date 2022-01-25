package edu.amorozov.article.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;

import edu.amorozov.article.R;
import edu.amorozov.article.model.Comment;

/**
 * Dialog for adding comment
 */
public class CommentDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "CommentsDialog";

    private EditText mCommentText; //text
    private ViewFragment parent; //parent

    /**
     * Interface
     */
    interface CommentListener {

        void onComment(Comment comment);

    }

    /**
     * Comment listener
     */
    private CommentListener mCommentListener;

    /**
     * Constructor
     * @param parent
     */
    public CommentDialogFragment (ViewFragment parent) {
        this.parent = parent;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_comment, container, false);
        mCommentText = v.findViewById(R.id.article_form_text);

        v.findViewById(R.id.article_form_button).setOnClickListener(this);
        v.findViewById(R.id.article_form_cancel).setOnClickListener(this);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (parent instanceof CommentListener) {
            mCommentListener = parent;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.article_form_button:
                onSubmitClicked(v);
                break;
            case R.id.article_form_cancel:
                onCancelClicked(v);
                break;
        }
    }

    /**
     * Add comment to firebase
     * @param view
     */
    public void onSubmitClicked(View view) {
        Comment comment = new Comment(
                FirebaseAuth.getInstance().getCurrentUser(),
                mCommentText.getText().toString());

        if (mCommentListener != null) {
            mCommentListener.onComment(comment);
        }

        dismiss();
    }

    /**
     * Dismiss the dialog
     * @param view
     */
    public void onCancelClicked(View view) {
        dismiss();
    }
}


