package edu.amorozov.article.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Calendar;

import edu.amorozov.article.R;
import edu.amorozov.article.model.Article;

/**
 * adapter for the feed recycler view
 */
public class ArticleAdapter extends FirestoreAdapter<ArticleAdapter.ViewHolder> {

    public interface OnArticleSelectedListener {

        void onArticleSelected(DocumentSnapshot article);

    }

    private OnArticleSelectedListener mListener;

    /**
     * Constructor
     * @param query for data from Firestore
     * @param listener
     */
    public ArticleAdapter(Query query, OnArticleSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ArticleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_article, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleAdapter.ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    /**
     * View holder for an article
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        TextView authorView;
        TextView dateView;

        /**
         * Constructor
         * @param itemView
         */
        public ViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.title_tv);
            authorView = itemView.findViewById(R.id.author_tv);
            dateView = itemView.findViewById(R.id.date_tv);
        }

        /**
         * Binds data to the view holder
         * @param snapshot
         * @param listener
         */
        public void bind(final DocumentSnapshot snapshot, final OnArticleSelectedListener listener) {
            Article article = snapshot.toObject(Article.class);
            titleView.setText(article.getTitle());
            authorView.setText(article.getAuthor());
            dateView.setText(getDateString(article.getDate()));

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onArticleSelected(snapshot);
                    }
                }
            });
        }

        /**
         * @param date in millis (String)
         * @return date in dd/mm/yyyy hh:MM format
         */
        private String getDateString(String date) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(date));
            int hour = calendar.get(Calendar.HOUR);
            int min = calendar.get(Calendar.MINUTE);
            int amPm = calendar.get(Calendar.AM_PM);

            return String.format("%d/%d/%d %d:%s %s",
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DATE),
                    calendar.get(Calendar.YEAR),
                    hour == 0 ? 12 : hour,
                    min <= 9 ? "0" + min : min,
                    amPm == Calendar.AM ? "am" : "pm");
        }
    }
}
