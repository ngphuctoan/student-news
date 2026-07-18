package io.github.ngphuctoan.studentnews;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationParser {
    public static List<Notification> parseNotifications(Document doc) {
        List<Notification> newsList = new ArrayList<>();

        Elements allNotificationEl = doc.select("#div_lstThongBao > .list > .list-item");
        for (Element newsEl : allNotificationEl) {
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

            // Skip details for now since it's encoded in a script tag
            newsList.add(new Notification(id, title, summary, new Date()));
        }

        return newsList;
    }

    public static String parseNotificationDetails(Document doc, int id) throws IOException {
        Element contentScriptEl = doc.selectFirst(".page-content > script:not([src])");
        if (contentScriptEl == null) throw new IOException("Cannot find the script in .page-content");
        String contentScript = contentScriptEl.html();

        Pattern encodedHtmlPattern = Pattern.compile("^ *var tmp = '(.+)';$", Pattern.MULTILINE);
        Matcher matcher = encodedHtmlPattern.matcher(contentScript);
        if (!matcher.find()) throw new IOException("Cannot find the raw HTML inside the script's temporary variable");

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
