package edu.amorozov.article.model;

import android.text.TextUtils;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Comment object
 */
public class Comment {

    private String userId; //id of user
    private String userName; //name of user
    private double comment;
    private String text; //of the comment
    private @ServerTimestamp
    Date timestamp;

    /**
     * Constructor
     */
    public Comment() {}

    /**
     * Constructor
     * @param user
     * @param text
     */
    public Comment(FirebaseUser user, String text) {
        this.userId = user.getUid();
        this.userName = user.getDisplayName();
        if (TextUtils.isEmpty(this.userName)) {
            this.userName = user.getEmail();
        }

        this.comment = comment;
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getComment() {
        return comment;
    }

    public void setComment(double comment) {
        this.comment = comment;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
