package ru.vk.sladkiipirojok.visits.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vk.sladkiipirojok.visits.validator.URIOptionalSchema;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitedLinksDTO {
    @NotEmpty
    private List<@URIOptionalSchema String> links;
}
