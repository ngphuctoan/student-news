package io.github.ngphuctoan.studentnews;

import org.jetbrains.annotations.Nullable;

import java.util.Date;

public record Notification(int id, String title, String summary, Date dateCreated, @Nullable String details) {
    public Notification(int id, String title, String summary, Date dateCreated) {
        this(id, title, summary, dateCreated, null);
    }

    public Notification withDetails(String details) {
        return new Notification(id, title, summary, dateCreated, details);
    }
}
