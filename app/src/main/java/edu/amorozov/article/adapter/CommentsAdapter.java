package edu.amorozov.article.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.Query;

import edu.amorozov.article.R;
import edu.amorozov.article.model.Comment;

/**
 * Adapter for comments recycler view
 */
public class CommentsAdapter extends FirestoreAdapter<CommentsAdapter.ViewHolder> {

    /**
     * Constructor
     * @param query
     */
    public CommentsAdapter(Query query) {
        super(query);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position).toObject(Comment.class));
    }

    /**
     * View holder for a comment
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameView; // username
        TextView textView; // comment

        /**
         * Constructor
         * @param itemView
         */
        public ViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.comment_item_name);
            textView = itemView.findViewById(R.id.comment_item_text);
        }

        /**
         * Binds data to the view holder
         * @param comment
         */
        public void bind(Comment comment) {
            nameView.setText(comment.getUserName());
            textView.setText(comment.getText());
        }
    }
}
