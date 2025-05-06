package ru.tatarinov.NauJava.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookDto {
    private Long id;
    private String title;
    private String authorName;
    private String authorCountry;
    private Integer publicationYear;
    private String isbn;
}
