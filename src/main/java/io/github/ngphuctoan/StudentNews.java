package io.github.ngphuctoan;

import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.io.IOException;
import java.util.List;

public class StudentNews {
    public static final String DB_URL = "jdbc:h2:file:./data/cache";

    static void main() throws IOException {
        // Configure Flyway migration
        Flyway flyway = Flyway.configure().dataSource(DB_URL, "meep", null).load();
        // Start the migration
        flyway.migrate();

        // Configure JDBI + install plugins
        Jdbi jdbi = Jdbi.create(DB_URL, "meep", "");
        jdbi.installPlugin(new SqlObjectPlugin());

        // Asks for credentials in console for now
        String studentId = IO.readln("Student ID: ");
        String password = IO.readln("Password: ");

        NewsClient client = new NewsClient();
        // Get the first 20 news
        List<News> newsList = client.getNews(studentId, password);

        // Debug length of news list
        IO.println(newsList.size());

        // Get news list from cache and filter out news that aren't in cache
        List<News> cachedNewsList = jdbi.withExtension(NewsDao.class, NewsDao::listNews);
        List<News> newNewsList = newsList.stream().filter(news -> cachedNewsList.stream().noneMatch(cachedNews -> cachedNews.id() == news.id())).toList();

        // Debug news lists
        IO.println(newNewsList);
        IO.println("---");
        IO.println(cachedNewsList);

        // Add new news to cache
        jdbi.withExtension(NewsDao.class, dao -> {
            for (News news : newNewsList) dao.insertBean(news);
            return null;
        });
    }
}
