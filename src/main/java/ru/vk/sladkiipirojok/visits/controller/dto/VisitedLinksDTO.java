package ru.vk.sladkiipirojok.visits.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
public class VisitedLinksDTO {
    @NotEmpty
    private List<@URL String> links;
}
