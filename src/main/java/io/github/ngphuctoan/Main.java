package io.github.ngphuctoan;

import java.io.IOException;

public class Main {
    static void main() throws IOException {
        String studentId = IO.readln("Student ID: ");
        String password = IO.readln("Password: ");

        NewsClient client = new NewsClient();
        IO.println(client.getNews(studentId, password).size());
    }
}
