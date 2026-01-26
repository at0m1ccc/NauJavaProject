package ru.tatarinov.NauJava.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KnigaDto {
    private Long id;
    private String title;
    private String authorName;
    private String authorCountry;
    private Integer publicationYear;
}
