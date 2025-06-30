package pl.umcs.oop.auth;

import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {
    private final List<Token> tokens = new CopyOnWriteArrayList<>();
    private final Set<String> bannedTokens = ConcurrentHashMap.newKeySet();

    // Generates a new unique token (UUID)
    public Token generateToken() {
        String token = UUID.randomUUID().toString();
        Token newToken = new Token(token, Instant.now());
        tokens.add(newToken);
        return newToken;
    }

    // Checks if token is valid (not banned and not expired)
    public boolean isTokenActive(String token) {
        if (bannedTokens.contains(token)) return false;

        return tokens.stream()
                .filter(t -> t.token().equals(token))
                .findAny()
                .map(t -> !isTokenExpired(t))
                .orElse(false);
    }

    // Checks token expiration (5 minutes)
    private boolean isTokenExpired(Token token) {
        return Instant.now().isAfter(token.creationTime().plus(5, TimeUnit.MINUTES.toChronoUnit()));
    }

    // Bans a token (immediate expiration)
    public void banToken(String token) {
        bannedTokens.add(token);
    }

    // Returns all tokens with active status
    public List<Map<String, Object>> getAllTokens() {
        return tokens.stream()
                .map(token -> Map.<String, Object>of(
                        "token", token.token(),
                        "createdAt", token.creationTime().toString(),
                        "active", isTokenActive(token.token())
                ))
                .toList();
    }
}