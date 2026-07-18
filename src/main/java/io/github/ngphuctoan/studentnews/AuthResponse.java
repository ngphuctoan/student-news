package io.github.ngphuctoan.studentnews;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URL;

public record AuthResponse(@JsonProperty("result") AuthStatus status, @JsonProperty("url") URL returnUrl) {
}
