package net.cechacek.demo.operator.conditions;

import java.util.List;
import java.util.stream.Collectors;

public class InvalidHtmlException extends RuntimeException{

    public InvalidHtmlException(List<String> pages) {
        super("The following pages do not represent a valid HTML content: %s".formatted(pageNames(pages)));
    }

    private static String pageNames(List<String> pages) {
        return pages.stream()
                .map(p -> "'" + p + "'")
                .collect(Collectors.joining(","));
    }
}
