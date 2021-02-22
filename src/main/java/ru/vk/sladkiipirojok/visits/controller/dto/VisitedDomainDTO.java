package ru.vk.sladkiipirojok.visits.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class VisitedDomainDTO {
    private Set<String> domains;
    private String status;
}
