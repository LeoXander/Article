package edu.amorozov.article.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Article object
 */
public class Article implements Parcelable {

    private String htmlString; //article html
    private String title; //article title
    private String author; //of article
    private String date; //of the creation
    private int numComments; //number of comments

    /**
     * Default constructor
     */
    public Article() {}

    /**
     * Constructor
     * @param htmlString of the article
     * @param title of the article
     * @param author of the article
     * @param date of the article
     * @param numComments of the article
     */
    public Article(String htmlString, String title, String author, String date, int numComments) {
        this.htmlString = htmlString;
        this.title = title;
        this.author = author;
        this.date = date;
        this.numComments = numComments;
    }

    /**
     * Fot test purposes only
     * @param title on the article
     */
    public Article(String title) {
        this.title = title;
        this.author = "test";
        this.htmlString = "\n" +
                "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "  <head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>My CCM Experience</title>\n" +
                "    <link rel=\"stylesheet\" href=\"MyCCM.css\">\n" +
                "    <link rel=\"icon\" href=\"gradcap2.ico\" type=\"image/x-icon\">\n" +
                "   <style>\n" +
                "       .green { color: #00FF00; }\n" +
                "header {\n" +
                "  background-color: #FFFFFF;\n" +
                "  color: #000000;\n" +
                "  padding-left: 10px;\n" +
                "  padding-right: 10px;\n" +
                "}\n" +
                "\n" +
                "a {\n" +
                "  text-decoration: none;\n" +
                "  color: #000000;\n" +
                "}\n" +
                "\n" +
                "h1 { border: 1px solid #FFF;\n" +
                "  background-color: #0088FF;\n" +
                "  border-radius: 15px;\n" +
                "  padding-top: 10px;\n" +
                "  padding-bottom: 10px;\n" +
                "  padding-left: 10px;\n" +
                "  text-shadow: 3px 3px 4px #ff0000;\n" +
                "  color: #FFF;\n" +
                "  text-align: center;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "body {\n" +
                "   background-image: url(gradientbackground.jpg);\n" +
                "   font-family: Verdana, Arial, sans-serif;\n" +
                "}\n" +
                "\n" +
                "main { background-color: #FFAA88;\n" +
                "  padding: 10px;}\n" +
                "\n" +
                "main ul {\n" +
                "  list-style-image: url('trillium.gif');\n" +
                "}\n" +
                "\n" +
                "nav ul { list-style-type: none; }\n" +
                "\n" +
                ".contact { background-color: #FFCCAA;\n" +
                "  color: #555555;\n" +
                "  font-family: serif;\n" +
                "}\n" +
                "\n" +
                "footer {\n" +
                "  clear: both;\n" +
                "  text-align: center;\n" +
                "  background-color: #FDFDFD;\n" +
                "}\n" +
                "\n" +
                "#map { width: 100%; height: 100%}\n" +
                "   </style>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "      <header>\n" +
                "        <h1>Aleksandr Morozov</h1>\n" +
                "      </header>\n" +
                "      <main>\n" +
                "        <img src=\"generic_profile_2.jpg\" alt=\"Profile image\">\n" +
                "        <h2>Computer Science</h2>\n" +
                "\n" +
                "        <p>I have been a <span class = \"green\">student</span> at CCM for 1.5 years as a Computer Science major.\n" +
                "          I expect to graduate this semester. In the college, I became a tutor, IOS and almost Android developer, and president of the Computer Science Club.</p>\n" +
                "\n" +
                "        <ul>\n" +
                "          <li>CMP 128 – Computer Science I</li>\n" +
                "          <li>CMP 129 – Computer Science II</li>\n" +
                "          <li>CMP 230 – Comp Architectur Assembly Lang</li>\n" +
                "          <li>CMP 233 – Data Structures & Algorithms</li>\n" +
                "          <li>CMP 280 – Software Engineering</li>\n" +
                "          <li>MAT 225 – Discrete Mathematics</li>\n" +
                "          <li>CMP 239 – The Internet & Web Page Design</li>\n" +
                "          <li>CMP 271 – Mobile App Programming</li>\n" +
                "          <li>ENG 111 – Composition I</li>\n" +
                "          <li>ENG 112 – Composition II</li>\n" +
                "          <li>MAT 131 – Analytic Geometry & Calculus I</li>\n" +
                "          <li>MAT 132 – Analytic Geometry & Calculus II</li>\n" +
                "          <li>MAT 130 – Probability & Statistics</li>\n" +
                "          <li>SOC 120 – Principles of Sociology</li>\n" +
                "          <li>SOC 215 – Physical Anthropology\t</li>\n" +
                "          <li>HIS 166 – Emer of America US History I</li>\n" +
                "          <li>PHY-125 –\tGen Physics I - Lecture</li>\n" +
                "          <li>PHY-126 –\tGen Physics I - Lab</li>\n" +
                "          <li>PHY-127 –\tGen Physics II - Lecture</li>\n" +
                "          <li>PHY-125 –\tGen Physics II - Lab</li>\n" +
                "        </ul>\n" +
                "\n" +
                "        <div class=\"contact\">\n" +
                "          Contact me:<br/>\n" +
                "          <a href=\"mailto:morozov.aleksandr@student.ccm.edu\">morozov.aleksandr@student.ccm.edu</a><br>\n" +
                "          973-555-5555\n" +
                "        </div>\n" +
                "      </main>\n" +
                "      <footer>Copyright &copy;2020 Aleksandr Morozov</footer>\n" +
                "  </body>\n" +
                "</html>\n";
    }

    /**
     * Constructor for parsing
     * @param in
     */
    protected Article(Parcel in) {
        htmlString = in.readString();
        title = in.readString();
        author = in.readString();
        date = in.readString();
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public String getHtmlString() {
        return htmlString;
    }

    public void setHtmlString(String htmlString) {
        this.htmlString = htmlString;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNumComments() {
        return numComments;
    }

    public void setNumComments(int numComments) {
        this.numComments = numComments;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(htmlString);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(date);
    }
}
