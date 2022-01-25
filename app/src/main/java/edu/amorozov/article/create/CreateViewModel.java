package edu.amorozov.article.create;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import edu.amorozov.article.R;

/**
 * ViewModel for CreateActivity
 */
public class CreateViewModel extends AndroidViewModel {

    public final static int CONTENT_PAGE = 0;
    public final static int STYLE_PAGE = 1;

    private MutableLiveData<String> htmlString; //full html that contains content and style

    private int mCurrentPage; //current page

    private String[] mFontFamilies; //array of font families
    private String[] mFontSizes; //array of font sizes
    private String[] mTextAlignments; //array of text alignments

    /**
     * Constructor
     * @param context
     */
    public CreateViewModel(Application context) {
        super(context);
        htmlString = new MutableLiveData<>(getInitialHtml());
        mFontFamilies = context.getResources().getStringArray(R.array.font_families_array);
        mFontSizes = context.getResources().getStringArray(R.array.font_sizes_array);
        mTextAlignments = context.getResources().getStringArray(R.array.text_alignment_array);
    }

    /**
     * For test purposes only
     * @return test html template
     */
    private String getTestHtml() {
        return "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "<meta charset=\"utf-8\">" +
                "<style>" +
                "body{word-wrap:break-word;}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "Bo<br/>dy<span id=\"test\">Text</span>!" +
                "<img scr=\"" + Uri.parse("android.resource://edu.amorozov.article/" + R.drawable.ic_launcher_foreground) + "\" alt=\"Icon\" width=\"100%\">" +
                "</body>" +
                "</html>";
    }

    /**
     * Used to initialize html
     * @return initial html template
     */
    public static String getInitialHtml() {
        return "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "<meta charset=\"utf-8\">" +
                "<style>" +
                "body{word-wrap:break-word;}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "</body>" +
                "</html>";
    }

    /**
     * Used to apply styles to content
     * @return base html template
     */
    public static String getBaseHtml() {
        return "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "<meta charset=\"utf-8\">" +
                "<style>" +
                "</style>" +
                "</head>" +
                "<body>" +
                "</body>" +
                "</html>";
    }

    public MutableLiveData<String> getHtmlString() {
        return htmlString;
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public void setCurrentPage(int currentPage) {
        mCurrentPage = currentPage;
    }

    public String[] getFontFamilies() {
        return mFontFamilies;
    }

    public String[] getFontSizes() {
        return mFontSizes;
    }

    public String[] getTextAlignments() {
        return mTextAlignments;
    }
}
