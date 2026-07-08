package io.github.ngphuctoan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@JsonFormat(shape = JsonFormat.Shape.STRING)
enum LogInStatus {
    SUCCESS, FAIL,
}

record LogInResponse(@JsonProperty("result") LogInStatus status, @JsonProperty("url") URL returnUrl) {
}

public class NewsClient {
    private final Connection session;

    public NewsClient(Connection session) {
        this.session = session;
    }

    public NewsClient() {
        this(Jsoup.newSession().ignoreContentType(true));
    }

    private URL logIn(String studentId, String password, @Nullable URL returnUrl) throws IOException {
        // Construct the login URL (optionally append the return URL)
        StringBuilder url = new StringBuilder("https://stdportal.tdtu.edu.vn/Login/SignIn");
        if (returnUrl != null) url.append("?ReturnURL=").append(returnUrl);

        // Perform the request and read the body response directly as string
        Connection req = session.newRequest(url.toString()).method(Connection.Method.POST).data("user", studentId).data("pass", password);
        String body = req.execute().readBody();

        // JSON deserialisation
        ObjectMapper mapper = JsonMapper.builder().enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS).build();
        LogInResponse data = mapper.readValue(body, LogInResponse.class);

        return switch (data.status()) {
            case SUCCESS -> data.returnUrl();
            case FAIL -> throw new IllegalArgumentException("Incorrect student ID or password");
        };
    }

    // Override for return URL param as string
    @SuppressWarnings("unused")
    private URL logIn(String studentId, String password, @Nullable String returnUrl) throws IOException, URISyntaxException {
        return logIn(studentId, password, returnUrl != null ? new URI(returnUrl).toURL() : null);
    }

    // Override for optional return URL param
    private URL logIn(String studentId, String password) throws IOException {
        return logIn(studentId, password, (URL) null);
    }

    public List<News> getNews(String studentId, String password) throws IOException {
        // Log in first, call the return URL to initialise the student portal page
        URL url = logIn(studentId, password);
        session.newRequest(url).execute();

        // Navigate to the news home page to initialise it first
        session.newRequest("https://studentnews.tdtu.edu.vn").execute();

        // Get the first 20 result of the news, too lazy to handle pagination :b
        Document doc = session.newRequest("https://studentnews.tdtu.edu.vn/Thongbao").get();
        return NewsParser.parseNews(doc, id -> {
            try {
                return session.newRequest("https://studentnews.tdtu.edu.vn/Thongbao/Detail/" + id).get();
            } catch (Exception err) {
                //noinspection CallToPrintStackTrace
                err.printStackTrace();
                return null;
            }
        });
    }
}
