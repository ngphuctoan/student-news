package io.github.ngphuctoan.studentnews;

import org.jetbrains.annotations.Nullable;

public record News(int id, String title, String summary, @Nullable String content) {
}
