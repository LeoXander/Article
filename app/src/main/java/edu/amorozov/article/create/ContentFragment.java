package edu.amorozov.article.create;

import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import edu.amorozov.article.R;

/**
 * Fragment with content of a new article
 */
public class ContentFragment extends Fragment {

    private static final String TAG = "ContentFragment";

    private EditText mContentEd; //EditText with the content
    private CreateViewModel mViewModel; //ViewModel
    private String mStyle; //Saved style html from StyleFragment
    private List<String> mTags; //Tags associated with the styles

    /**
     * @return new instance of the class
     */
    public static ContentFragment getInstance() {
        return new ContentFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CreateActivity createActivity = (CreateActivity)getActivity();
        mViewModel = createActivity.mViewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_content, container, false);
        mContentEd = root.findViewById(R.id.content_ed);
        loadHtml(mViewModel.getHtmlString().getValue());
        mContentEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                CreateFragment parent = (CreateFragment) getParentFragment();
                if (parent.getCurrentPage() == CreateViewModel.CONTENT_PAGE)
                    saveHtml();
            }
        });

        return root;
    }

    /**
     * Removes converts html to content string
     * @param s full html with content and styles
     * @return content without styling
     */
    private Spanned cleaned(String s) {
        if (s == null) return null;

//        Log.i(TAG, s);

        mStyle = s.substring(s.indexOf("<style>") + "<style>".length(), s.indexOf("</style>"));

        String body = s.substring(s.indexOf("<body>") + "<body>".length(), s.indexOf("</body>"));
        mTags = new ArrayList<>();
        String tagCap = "<font color=#FF0000>"+ getContext().getResources().getString(R.string.empty_char) + "</font>";
        int start = body.indexOf('<');
        int end = body.indexOf('>');

        while (start != -1) {
            String tag = body.substring(start, end + 1);
            if (!tag.contains("font") && !tag.contains("br") && !tag.contains("image")) {
                mTags.add(tag);

                body = body.replaceFirst(tag, tagCap);
            }
            start = body.indexOf('<', start + 1);
            end = body.indexOf('>', start + 1);
        }

        Log.i(TAG, body);

        return Html.fromHtml(body);
    }

    /**
     * Applies saved styles to content string
     * @param body content string
     * @return full html with content and styles
     */
    private String fixed(String body) {
        if (body == null) return null;

        body = body.replace("\n", "<br>").replace(" ", "&nbsp;");

        for (int i = 0; i < mTags.size(); i++) {
            body = body.replaceFirst(getContext().getResources().getString(R.string.empty_char), mTags.get(i));
        }

        String html = CreateViewModel.getBaseHtml();
        int indexStyle = html.indexOf("<style>") + "<style>".length();
        html = html.substring(0, indexStyle) + mStyle + html.substring(indexStyle);
        int indexBody = html.indexOf("<body>") + "<body>".length();

        html = html.substring(0, indexBody) + body + html.substring(indexBody);
        return html;
    }

    /**
     * Sets text of the content EditView
     * @param s html string with content and styles
     */
    public void loadHtml(String s) {
        if (mContentEd != null && s != null)
        mContentEd.setText(cleaned(s));
    }

    /**
     * Converts content string to full html string and saves it to the mViewModel
     */
    public void saveHtml() {
        mViewModel.getHtmlString().setValue(fixed(mContentEd.getText().toString()));
    }
}
