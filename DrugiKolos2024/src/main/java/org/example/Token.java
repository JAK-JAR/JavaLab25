package pl.umcs.oop.auth;

import java.time.Instant;

// Represents a user token with creation timestamp
public record Token(String token, Instant creationTime) {}