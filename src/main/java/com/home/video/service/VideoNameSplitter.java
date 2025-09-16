package com.home.video.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VideoNameSplitter {

    public static final String NAME_KEY = "NAME_KEY";
    public static final String ACTORS_KEY = "ACTORS_KEY";

    private final String actorAndNameDelimiter;
    private final String actorsDelimiter;

    public VideoNameSplitter(
            final @Value("${video.splitter.actor-and-name-delimiter}") String actorAndNameDelimiter,
            final @Value("${video.splitter.actors-delimiter}") String actorsDelimiter) {
        this.actorAndNameDelimiter = actorAndNameDelimiter;
        this.actorsDelimiter = actorsDelimiter;
    }

    public Map<String, Object> split(final String name) {
        String[] splitByActorsAndName = name.split(actorAndNameDelimiter);
        checkInvalidCharacters(splitByActorsAndName[0]);
        checkInvalidCharacters(splitByActorsAndName[1]);
        return Map.of(
                NAME_KEY, splitByActorsAndName[1].trim(),
                ACTORS_KEY, Arrays.stream(splitByActorsAndName[0].split(actorsDelimiter))
                        .map(String::trim)
                        .toList());

    }

    private void checkInvalidCharacters(final String name) {
        Set<Character> specialChars = new HashSet<>();

        for (char ch : getClearInputCharacters(name)) {
            if (!Character.isLetterOrDigit(ch)) {
                specialChars.add(ch);
            }
        }

        if (specialChars.size() > 0) {
            throw new RuntimeException();
        }
    }

    private char[] getClearInputCharacters(final String input) {
        return input
                .replace(" ", "")
                .replace("&", "")
                .replace(".mp4", "")
                .replace(".mkv", "")
                .toCharArray();
    }
}
