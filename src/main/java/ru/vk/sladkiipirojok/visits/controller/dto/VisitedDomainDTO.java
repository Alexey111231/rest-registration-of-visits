package ru.vk.sladkiipirojok.visits.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitedDomainDTO {
    private Set<String> domains;
    private String status;
}
