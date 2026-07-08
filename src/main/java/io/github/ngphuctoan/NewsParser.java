package io.github.ngphuctoan;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class NewsParser {
    public static List<News> parseNews(Document doc) {
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

            String title = titleEl.attr("title");
            String summary = summaryEl.text();

            // Skip content for now since it's encoded in a script tag
            newsList.add(new News(Integer.parseInt(idStr), title, summary, null));
        }

        return newsList;
    }
}
