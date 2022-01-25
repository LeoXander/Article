package edu.amorozov.article.create;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.UUID;

import edu.amorozov.article.R;

/**
 * Fragment to style article
 */
public class StyleFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private final static String TAG = "StyleFragment";

    private WebView mHtmlWv; //WebView used to display article and apply styles
    private CreateViewModel mViewModel; //ViewModel

    private String mSelectionId; //Id of currently selected text

    /**
     * @return new instance of the class
     */
    public static StyleFragment getInstance() {
        return new StyleFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CreateActivity createActivity = (CreateActivity)getActivity();
        mViewModel = createActivity.mViewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_style, container, false);

        mHtmlWv = root.findViewById(R.id.html_wv);

        mHtmlWv.getSettings().setJavaScriptEnabled(true);
        mHtmlWv.addJavascriptInterface(new WebAppInterface() {}, "js");
        loadHtml(mViewModel.getHtmlString().getValue());

        Spinner fontsSpinner = root.findViewById(R.id.font_family_s);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.font_families_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontsSpinner.setAdapter(adapter);
        fontsSpinner.setOnItemSelectedListener(this);

        Spinner sizeSpinner = root.findViewById(R.id.font_size_s);
        adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.font_sizes_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeSpinner.setAdapter(adapter);
        sizeSpinner.setOnItemSelectedListener(this);

        Spinner alignmentSpinner = root.findViewById(R.id.text_alignment_s);
        adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.text_alignment_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alignmentSpinner.setAdapter(adapter);
        alignmentSpinner.setOnItemSelectedListener(this);

        root.findViewById(R.id.font_bold_b).setOnClickListener(this);
        root.findViewById(R.id.font_underlined_b).setOnClickListener(this);
        root.findViewById(R.id.font_italic_b).setOnClickListener(this);
        root.findViewById(R.id.font_strikethrough_b).setOnClickListener(this);
        root.findViewById(R.id.block_inline_b).setOnClickListener(this);

        Button applyColor = root.findViewById(R.id.apply_color_b);
        applyColor.setOnClickListener(this);

        return root;
    }

    /**
     * @param raw converts string to color value (0-255)
     * @return
     */
    public int getColorValue(String raw) {
        int color;

        if (raw != null && !raw.isEmpty()) {
            color = Integer.parseInt(raw);
            if (color < 0 || color > 255) color = 255;
        }
        else
            color = 255;

        return color;
    }

    /**
     * loads html to the webview
     * @param s html string
     */
    public void loadHtml(final String s) {
        if (s != null && mHtmlWv != null)
            mHtmlWv.post(new Runnable() {
                @Override
                public void run() {
                    mHtmlWv.loadData(Base64.encodeToString(s.getBytes(), Base64.NO_PADDING),
                            "text/html", "base64");
                }
            });
    }

    /**
     * Applies specific style to the html element
     * @param html which is being changed
     * @param optionId of option which should be applied
     * @param position in the arrays associated with drop-down menus
     */
    private void modifyHtml(String html, int optionId, int position) {
        String id;

        switch (optionId) {
            case R.id.font_bold_b:
                id = getId(html);
                if (id == null) {
                    id = "id" + UUID.randomUUID();
                    html = html.replaceFirst(getResources().getString(R.string.empty_char), "<span id=\"" + id + "\">");
                    html = html.replaceFirst(getResources().getString(R.string.empty_char), "</span>");
                } else
                    html = html.replace(getResources().getString(R.string.empty_char), "");

                mSelectionId = id;
                html = applyStyle(html, id, "font-weight", "bold");
                break;
            case R.id.font_italic_b:
                id = getId(html);
                if (id == null) {
                    id = "id" + UUID.randomUUID();
                    html = html.replaceFirst(getResources().getString(R.string.empty_char), "<span id=\"" + id + "\">");
                    html = html.replaceFirst(getResources().getString(R.string.empty_char), "</span>");
                } else
                    html = html.replace(getResources().getString(R.string.empty_char), "");

                mSelectionId = id;
                html = applyStyle(html, id, "font-style", "italic");
                break;
            case R.id.font_underlined_b:
                id = getId(html);
                if (id == null) {
                    id = "id" + UUID.randomUUID();
                    html = html.replaceFirst(getResources().getString(R.string.empty_char), "<span id=\"" + id + "\">");
                    html = html.replaceFirst(getResources().getString(R.string.empty_char), "</span>");
                } else
                    html = html.replace(getResources().getString(R.string.empty_char), "");

                mSelectionId = id;
                html = applyStyle(html, id, "text-decoration", "underline");
                break;
            case R.id.font_strikethrough_b:
                id = getId(html);
                if (id == null) {
                    id = "id" + UUID.randomUUID();
                    html = html.replaceFirst(getResources().getString(R.string.empty_char), "<span id=\"" + id + "\">");
                    html = html.replaceFirst(getResources().getString(R.string.empty_char), "</span>");
                } else
                    html = html.replace(getResources().getString(R.string.empty_char), "");

                mSelectionId = id;
                html = applyStyle(html, id, "text-decoration", "line-through");
                break;
            case R.id.font_family_s:
                id = getId(html);
                if (id == null) {
                    id = "id" + UUID.randomUUID();
                    html = html.replaceFirst(getResources().getString(R.string.empty_char), "<span id=\"" + id + "\">");
                    html = html.replaceFirst(getResources().getString(R.string.empty_char), "</span>");
                } else
                    html = html.replace(getResources().getString(R.string.empty_char), "");

                mSelectionId = id;
                html = applyStyle(html, id, "font-family", mViewModel.getFontFamilies()[position]);
                break;
            case R.id.font_size_s:
                id = getId(html);
                if (id == null) {
                    id = "id" + UUID.randomUUID();
                    html = html.replaceFirst(getResources().getString(R.string.empty_char), "<span id=\"" + id + "\">");
                    html = html.replaceFirst(getResources().getString(R.string.empty_char), "</span>");
                } else
                    html = html.replace(getResources().getString(R.string.empty_char), "");

                mSelectionId = id;
                html = applyStyle(html, id, "font-size", mViewModel.getFontSizes()[position] + "px");
                break;
            case R.id.text_alignment_s:
                id = getId(html);
                if (id == null) {
                    id = "id" + UUID.randomUUID();
                    html = html.replaceFirst(getResources().getString(R.string.empty_char), "<span id=\"" + id + "\">");
                    html = html.replaceFirst(getResources().getString(R.string.empty_char), "</span>");
                } else
                    html = html.replace(getResources().getString(R.string.empty_char), "");

                mSelectionId = id;
                html = applyStyle(html, id, "text-align", mViewModel.getTextAlignments()[position]);
                break;
            case R.id.apply_color_b:
                id = getId(html);
                if (id == null) {
                    id = "id" + UUID.randomUUID();
                    html = html.replaceFirst(getResources().getString(R.string.empty_char), "<span id=\"" + id + "\">");
                    html = html.replaceFirst(getResources().getString(R.string.empty_char), "</span>");
                } else
                    html = html.replace(getResources().getString(R.string.empty_char), "");

                mSelectionId = id;

                EditText redEt = getView().findViewById(R.id.color_red_ed);
                EditText greenEt = getView().findViewById(R.id.color_green_ed);
                EditText blueEt = getView().findViewById(R.id.color_blue_ed);

                html = applyStyle(html, id, "color", "#"
                        + String.format("%02X", getColorValue(redEt.getText().toString()))
                        + String.format("%02X", getColorValue(greenEt.getText().toString()))
                        + String.format("%02X", getColorValue(blueEt.getText().toString())));
                break;
            case R.id.block_inline_b:
                id = getId(html);
                if (id == null) {
                    id = "id" + UUID.randomUUID();
                    html = html.replaceFirst(getResources().getString(R.string.empty_char), "<span id=\"" + id + "\">");
                    html = html.replaceFirst(getResources().getString(R.string.empty_char), "</span>");
                } else
                    html = html.replace(getResources().getString(R.string.empty_char), "");

                mSelectionId = id;
                html = applyStyle(html, id, "display", "block");

                break;
            default:
                html = html.replace(getResources().getString(R.string.empty_char), "");
                mSelectionId = null;
        }
        Log.i(TAG, html);
        saveHtml(html);
    }

    /**
     * Defines whether this selected text has already had on id associated with it
     * @param html
     * @return id if found and null, otherwise
     */
    private String getId(String html) {
        int left = html.indexOf(getResources().getString(R.string.empty_char));
        int right = html.lastIndexOf(getResources().getString(R.string.empty_char));
        int open = html.substring(0, left).lastIndexOf("<span id=\"");
        int close = html.indexOf("</span>", right);

        // <span id="id87623487">text</span>
        if (html.substring(left - 2, left).equals("\">") && close == (right + 1))
            return html.substring(open + "<span id=\"".length(), left - 2);
        else
            return null;
    }

    /**
     * Applies style to the html
     * @param html to which apply style
     * @param id of the selected text
     * @param attribute which should be modified
     * @param newValue of the attribute
     * @return html with style applied
     */
    private String applyStyle(String html, String id, String attribute, String newValue) {
        int styleClose = html.indexOf("</style>");
        int idLoc = html.substring(0, styleClose).indexOf(id);

        // #id{attribute:value;}
        if (idLoc == -1)
            html = html.substring(0, styleClose) + "#" + id + "{" + attribute + ":" + newValue + ";}" + html.substring(styleClose);
        else {
            int close = html.indexOf('}', idLoc);
            int attrLoc = html.substring(0, close).lastIndexOf(attribute);

            if (attrLoc == -1 || attrLoc < idLoc)
                html = html.substring(0, close) + attribute + ":" + newValue + ";" + html.substring(close);
            else {
                int semicolonLoc = html.indexOf(';', attrLoc);
                String currentValue = html.substring(attrLoc + attribute.length() + 1, semicolonLoc);

                if (currentValue.equals(newValue))
                    html = html.substring(0, attrLoc) + html.substring(semicolonLoc + 1);
                else
                    html = html.substring(0, attrLoc + attribute.length() + 1) + newValue + html.substring(semicolonLoc);
            }
        }

        return html;
    }

    /**
     * Reloads webview using javascript. Restores selection
     * @param html to load
     */
    public void reloadHtml(String html) {
        if (mSelectionId != null)
            mHtmlWv.loadUrl("javascript:(function() {\n" +
                "    document.querySelector('style').innerHTML = '" + getStyle(html) + "';\n" +
                "    document.querySelector('body').innerHTML = '" + getBody(html) + "';\n" +
                "    node = document.getElementById('" + mSelectionId + "');\n" +
                "    const selection = window.getSelection();\n" +
                "    const range = document.createRange();\n" +
                "    range.selectNodeContents(node);\n" +
                "    selection.removeAllRanges();\n" +
                "    selection.addRange(range);\n" +
                "})();\n");
        else
            mHtmlWv.loadUrl("javascript:(function() {\n" +
                    "    document.querySelector('style').innerHTML = '" + getStyle(html) + "';\n" +
                    "    document.querySelector('body').innerHTML = '" + getBody(html) + "';\n" +
                    "})();\n");
    }

    /**
     * @param html
     * @return style block of html
     */
    private String getStyle(String html) {
        return  html.substring(html.indexOf("<style>") + "<style>".length(), html.indexOf("</style>"));
    }

    /**
     * @param html
     * @return body block of html
     */
    private String getBody(String html) {
        return  html.substring(html.indexOf("<body>") + "<body>".length(), html.indexOf("</body>"));
    }

    /**
     * Request full html from the webview
     * For test purposes
     */
    private void requestHtml() {
        mHtmlWv.loadUrl("javascript:js.onReceiveHtml('<!DOCTYPE html><html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
    }

    /**
     * Saves html to view model
     * @param html
     */
    public void saveHtml(String html) {
        mViewModel.getHtmlString().postValue(html);
    }

    @Override
    public void onClick(View v) {
        requestSelection(v.getId(), -1);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        requestSelection(parent.getId(), position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Requests selected text in webview
     * @param id of option that requests selection
     * @param position in the arrays of drop-down lists
     */
    private void requestSelection(int id, int position) {
        mHtmlWv.loadUrl("javascript:(function() {\n" +
                "    var sel, range, node;\n" +
                "    if (window.getSelection) {\n" +
                "        sel = window.getSelection();\n" +
                "        if (sel.getRangeAt && sel.rangeCount) {\n" +
                "            range = window.getSelection().getRangeAt(0).cloneRange();\n" +
                "            range.collapse(false);\n" +
                "            var el = document.createElement(\"div\");\n" +
                "            el.innerHTML = '"+ getContext().getResources().getString(R.string.empty_char) + "';\n" +
                "            var frag = document.createDocumentFragment(), node, lastNode;\n" +
                "            while ( (node = el.firstChild) ) {\n" +
                "                lastNode = frag.appendChild(node);\n" +
                "            }\n" +
                "            range.insertNode(frag);\n" +
                "\n" +
                "            range = window.getSelection().getRangeAt(0).cloneRange();\n" +
                "            range.collapse(true);\n" +
                "            el = document.createElement(\"div\");\n" +
                "            el.innerHTML = '"+ getContext().getResources().getString(R.string.empty_char) + "';\n" +
                "            frag = document.createDocumentFragment();\n" +
                "            while ( (node = el.firstChild) ) {\n" +
                "                lastNode = frag.appendChild(node);\n" +
                "            }\n" +
                "            range.insertNode(frag);\n" +
                "        }\n" +
                "    }\n" +
                "})();\n");

        //Request current html text
        mHtmlWv.loadUrl("javascript:js.onReceiveSelection('<!DOCTYPE html><html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>', " + id + ", " + position + ");");
    }

    /**
     * WebAppInterface to run javascript functions on webview
     */
    public class WebAppInterface
    {
        /**
         * For test purposes only.
         * Shows toast with selected text
         * @param value selected text
         */
        @JavascriptInterface
        public void callback(String value)
        {
            Toast.makeText(getContext(), value, Toast.LENGTH_SHORT).show();
        }

        /**
         * For test purposes only.
         * Logs html
         * @param html
         */
        @JavascriptInterface
        public void showHTML(String html) {
            Log.i(TAG, html.replace(getResources().getString(R.string.empty_char), "#"));
        }

        /**
         * Receives html, optoinId, position from webview and calls modifyHtml() method
         * @param html
         * @param optionId
         * @param position
         */
        @JavascriptInterface
        public void onReceiveSelection(String html, int optionId, int position) {
            modifyHtml(html, optionId, position);
        }

        /**
         * Receives html to save it
         * @param html
         */
        @JavascriptInterface
        public void onReceiveHtml(String html) {
            saveHtml(html);
        }
    }
}
