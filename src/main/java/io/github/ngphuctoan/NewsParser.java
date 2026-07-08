package io.github.ngphuctoan;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsParser {
    public static List<News> parseNews(Document doc, Function<Integer, Document> getNewsContentDoc) {
        List<News> newsList = new ArrayList<>();

        Elements allNewsEl = doc.select("#div_lstThongBao > .list > .list-item");
        for (Element newsEl : allNewsEl) {
            Element titleEl = newsEl.selectFirst(".title");
            Element summaryEl = newsEl.selectFirst(".desc");
            Element linkEl = newsEl.selectFirst(".link-detail");

            assert titleEl != null;
            assert summaryEl != null;
            assert linkEl != null;

            // Get news ID from onClick handler
            String handleClick = linkEl.attr("onclick");
            String idStr = handleClick.replaceAll("openInNewTab\\('/ThongBao/Detail/(\\d+)'\\)", "$1");
            int id = Integer.parseInt(idStr);

            String title = titleEl.attr("title");
            String summary = summaryEl.text();

            // Get news content
            Document newsContentDoc = getNewsContentDoc.apply(id);
            String content = newsContentDoc != null ? parseNewsContent(newsContentDoc, id) : null;

            // Skip content for now since it's encoded in a script tag
            newsList.add(new News(id, title, summary, content));
        }

        return newsList;
    }

    // Override for optional getNewsContentDoc param
    public static List<News> parseNews(Document doc) {
        return parseNews(doc, _ -> null);
    }

    private static String parseNewsContent(Document doc, int id) {
        Element contentScriptEl = doc.selectFirst(".page-content > script:not([src])");
        assert contentScriptEl != null;
        String contentScript = contentScriptEl.html();

        Pattern encodedHtmlPattern = Pattern.compile("^ *var tmp = '(.+)';$", Pattern.MULTILINE);
        Matcher matcher = encodedHtmlPattern.matcher(contentScript);
        if (!matcher.find()) return null;

        String html = Parser.unescapeEntities(matcher.group(1), false);
        Document contentDoc = Jsoup.parse(html);

        String detailsLink = "https://studentnews.tdtu.edu.vn/Thongbao/Detail/" + id;
        Element detailsAnchor = contentDoc.createElement("a").attr("href", detailsLink).text(detailsLink);

        Element detailsPar = contentDoc.createElement("p").appendText("Chi tiết: ").appendChild(detailsAnchor);
        contentDoc.prependChild(detailsPar);

        String tidiedHtml = contentDoc.outerHtml();
        return Jsoup.clean(tidiedHtml, Safelist.relaxed().addTags("style").addAttributes(":all", "style"));
    }
}
