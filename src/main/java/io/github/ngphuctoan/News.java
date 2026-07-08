package io.github.ngphuctoan;

import org.jetbrains.annotations.Nullable;

public record News(int id, String title, String summary, @Nullable String content) {
}
