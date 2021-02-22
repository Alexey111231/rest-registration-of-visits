package ru.vk.sladkiipirojok.visits.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class Link {
    private String link;
    private Long date;
}