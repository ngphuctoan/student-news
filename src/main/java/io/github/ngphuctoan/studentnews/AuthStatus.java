package io.github.ngphuctoan.studentnews;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum AuthStatus {
    SUCCESS, FAIL,
}
