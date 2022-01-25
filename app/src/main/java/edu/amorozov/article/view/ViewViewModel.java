package edu.amorozov.article.view;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.UUID;

/**
 * View Model for ViewActivity
 */
public class ViewViewModel extends AndroidViewModel {

    /**
     * Constructor
     * @param application
     */
    public ViewViewModel(@NonNull Application application) {
        super(application);
    }
}
