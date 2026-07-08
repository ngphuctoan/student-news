package io.github.ngphuctoan;

import jakarta.mail.MessagingException;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StudentNews {
    public static final String DB_URL = "jdbc:h2:file:./data/cache";

    static void main() throws IOException, MessagingException {
        // Configure Flyway migration
        Flyway flyway = Flyway.configure().dataSource(DB_URL, "meep", null).load();
        // Start the migration
        flyway.migrate();

        // Configure JDBI + install plugins
        Jdbi jdbi = Jdbi.create(DB_URL, "meep", "");
        jdbi.installPlugin(new SqlObjectPlugin());

        String studentId = System.getenv("STUDENT_ID");
        String password = System.getenv("PASSWORD");

        assert studentId != null;
        assert password != null;

        NewsClient client = new NewsClient();
        // Get the first 20 news
        List<News> newsList = client.getNews(studentId, password);

        NewsCacheDao dao = jdbi.onDemand(NewsCacheDao.class);

        List<News> cachedNewsList = dao.listAll();
        List<Integer> cachedNewsIds = cachedNewsList.stream().map(News::id).toList();

        List<News> filteredNewsList = new ArrayList<>();
        MailManager mailer = new MailManager();
        for (News news : newsList) {
            if (cachedNewsIds.contains(news.id())) continue;
            mailer.sendNewsMail(news);
            filteredNewsList.add(news);
        }

        dao.insertMany(filteredNewsList);
    }
}
