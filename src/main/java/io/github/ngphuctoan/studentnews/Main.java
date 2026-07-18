import io.github.ngphuctoan.studentnews.*;
import jakarta.mail.MessagingException;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

static String DB_URL = "jdbc:h2:file:./data/cache";
static String DB_USER = "meep";
static @Nullable String DB_PASS = null;

void migrateDatabase(String dbUrl, String user, @Nullable String pass) {
    Flyway flyway = Flyway.configure().dataSource(dbUrl, user, pass).load();
    MigrateResult result = flyway.migrate();
    logger.atDebug().addKeyValue("performed", result.migrationsExecuted).log("Migration complete");
}

Jdbi initialiseJdbi(String dbUrl, String user, @Nullable String pass) {
    Jdbi jdbi = Jdbi.create(dbUrl, user, pass == null ? "" : pass);
    jdbi.installPlugin(new SqlObjectPlugin());
    return jdbi;
}

Logger logger = LoggerFactory.getLogger(getClass());

void main() {
    DurationManager duration = new DurationManager();

    logger.info("Application started");

    try {
        String studentId = System.getenv("STUDENT_ID");
        String password = System.getenv("PASSWORD");

        migrateDatabase(DB_URL, DB_USER, DB_PASS);
        Jdbi jdbi = initialiseJdbi(DB_URL, DB_USER, DB_PASS);

        NotificationClient client = new NotificationClient();
        NotificationCache cache = jdbi.onDemand(NotificationCache.class);
        NotificationMailer mailer = new NotificationMailer();

        List<Notification> notifications = client.getLatestNotifications(studentId, password);

        List<Integer> cachedNotificationIds = cache.listAllIds();
        logger.atInfo().addKeyValue("amount", cachedNotificationIds.size()).log("Cache queried");

        List<Notification> filteredNotifications = new ArrayList<>();
        int emailSentAmount = 0;

        for (Notification notification : notifications) {
            int id = notification.id();

            if (cachedNotificationIds.contains(id)) {
                logger.atDebug().addKeyValue("id", id).log("Skipping sending email");
                continue;
            }

            try {
                mailer.sendMail(notification);
                emailSentAmount++;
                filteredNotifications.add(notification);
            } catch (MessagingException exception) {
                logger.atWarn().setCause(exception).addKeyValue("id", notification.id()).log("Send email failed");
            }
        }

        int[] updateCountPerBatch = cache.insertMany(filteredNotifications);
        int cachedAmount = Arrays.stream(updateCountPerBatch).sum();

        logger.atInfo().addKeyValue("new", filteredNotifications.size()).addKeyValue("email_sent", emailSentAmount).addKeyValue("cached", cachedAmount).addKeyValue("duration_ms", duration.getDurationInMilliSeconds()).log("Application finished");
    } catch (Exception exception) {
        logger.atError().setCause(exception).addKeyValue("duration_ms", duration.getDurationInMilliSeconds()).log("Application failed");
    }
}
