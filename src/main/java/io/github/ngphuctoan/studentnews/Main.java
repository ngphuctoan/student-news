import io.github.ngphuctoan.studentnews.MailManager;
import io.github.ngphuctoan.studentnews.News;
import io.github.ngphuctoan.studentnews.NewsCacheDao;
import io.github.ngphuctoan.studentnews.NewsClient;
import jakarta.mail.MessagingException;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

static final String DB_URL = "jdbc:h2:file:./data/cache";
static final String DB_USER = "meep";
static final @Nullable String DB_PASS = null;

record Credentials(String studentId, String password) {
}

void migrateDb(String dbUrl, String user, @Nullable String pass) {
    Flyway flyway = Flyway.configure().dataSource(dbUrl, user, pass).load();
    flyway.migrate();
}

Jdbi configureJdbi(String dbUrl, String user, @Nullable String pass) {
    Jdbi jdbi = Jdbi.create(dbUrl, user, pass == null ? "" : pass);
    jdbi.installPlugin(new SqlObjectPlugin());
    return jdbi;
}

Credentials getCredentialsFromEnv() {
    String studentId = System.getenv("STUDENT_ID");
    String password = System.getenv("PASSWORD");

    assert studentId != null;
    assert password != null;

    return new Credentials(studentId, password);
}

List<News> filterNewsList(List<News> newsList, List<Integer> cachedNewsIds, Consumer<News> sideEffect) {
    List<News> filteredNewsList = new ArrayList<>();

    for (News news : newsList) {
        if (cachedNewsIds.contains(news.id())) continue;
        sideEffect.accept(news);
        filteredNewsList.add(news);
    }

    return filteredNewsList;
}

void main() throws IOException {
    Credentials credentials = getCredentialsFromEnv();

    migrateDb(DB_URL, DB_USER, DB_PASS);
    Jdbi jdbi = configureJdbi(DB_URL, DB_USER, DB_PASS);

    NewsClient client = new NewsClient();
    NewsCacheDao dao = jdbi.onDemand(NewsCacheDao.class);
    MailManager mailer = new MailManager();

    List<News> newsList = client.getNews(credentials.studentId(), credentials.password());
    List<Integer> cachedNewsIds = dao.listAllIds();

    List<News> filteredNewsList = filterNewsList(newsList, cachedNewsIds, (news) -> {
        try {
            mailer.sendNewsMail(news);
        } catch (MessagingException _) {
            // Failing to sending email should not block the loop
        }
    });

    dao.insertMany(filteredNewsList);
}
