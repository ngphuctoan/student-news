package io.github.ngphuctoan.studentnews;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class NotificationClient {
    static URI LOG_IN_URL = URI.create("https://stdportal.tdtu.edu.vn/Login/SignIn");
    static URI STUDENT_NEWS_URLS = URI.create("https://studentnews.tdtu.edu.vn");

    Connection session = Jsoup.newSession().ignoreContentType(true);
    ObjectMapper mapper = JsonMapper.builder().enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS).build();
    Logger logger = LoggerFactory.getLogger(getClass());
    DurationManager duration = new DurationManager();

    void logIn(String studentId, String password) throws IOException {
        String body = session.newRequest(LOG_IN_URL.toURL()).method(Connection.Method.POST).data("user", studentId).data("pass", password).execute().body();

        AuthResponse data = mapper.readValue(body, AuthResponse.class);

        if (data.status() == AuthStatus.FAIL) {
            throw new IllegalArgumentException("Incorrect student ID or password");
        }

        session.newRequest(data.returnUrl()).execute();
    }

    void goToStudentNewsPage() throws IOException {
        session.newRequest(STUDENT_NEWS_URLS.toURL()).execute();
    }

    Document getNotificationsPage() throws IOException {
        return session.newRequest(STUDENT_NEWS_URLS.resolve("/ThongBao").toURL()).get();
    }

    Document getNotificationDetailsPage(int id) throws IOException {
        return session.newRequest(STUDENT_NEWS_URLS.resolve("/ThongBao/Detail/" + id).toURL()).get();
    }

    public List<Notification> getLatestNotifications(String studentId, String password) throws IOException {
        duration.resetTimer();

        logIn(studentId, password);
        logger.atDebug().addKeyValue("student_id", studentId).log("Logged in");

        goToStudentNewsPage();
        Document document = getNotificationsPage();

        List<Notification> notifications = NotificationParser.parseNotifications(document);
        logger.atInfo().addKeyValue("amount", notifications.size()).addKeyValue("duration_ms", duration.getDurationInMilliSeconds()).log("Fetch complete");

        List<Notification> notificationsWithDetails = new ArrayList<>();
        int withDetailsAmount = 0;

        for (Notification notification : notifications) {
            int id = notification.id();

            try {
                Document newDocument = getNotificationDetailsPage(id);
                String details = NotificationParser.parseNotificationDetails(newDocument, id);
                withDetailsAmount++;
                notificationsWithDetails.add(notification.withDetails(details));
            } catch (IOException exception) {
                logger.atWarn().setCause(exception).addKeyValue("id", id).log("Cannot get notification details");
                notificationsWithDetails.add(notification);
            }
        }

        logger.atInfo().addKeyValue("with_details", withDetailsAmount).addKeyValue("duration_ms", duration.getDurationInMilliSeconds()).log("Fetch details complete");

        return notificationsWithDetails;
    }
}
