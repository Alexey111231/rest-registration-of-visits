package ru.vk.sladkiipirojok.visits.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.vk.sladkiipirojok.visits.controller.dto.StatusDTO;
import ru.vk.sladkiipirojok.visits.controller.dto.VisitedDomainDTO;
import ru.vk.sladkiipirojok.visits.controller.dto.VisitedLinksDTO;
import ru.vk.sladkiipirojok.visits.service.VisitsService;
import ru.vk.sladkiipirojok.visits.service.dto.Domain;
import ru.vk.sladkiipirojok.visits.service.model.Link;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Validated
public class VisitsController {
    private final VisitsService visitsService;

    @Autowired
    public VisitsController(VisitsService visitsService) {
        this.visitsService = visitsService;
    }

    @GetMapping("/visited_domains")
    public ResponseEntity<VisitedDomainDTO> getVisited(@RequestParam @PositiveOrZero @NotNull Long from,
                                                       @RequestParam @PositiveOrZero @NotNull Long to) {
        assertTimeInterval(from, to);

        Set<Domain> domainsFromInterval = visitsService.getDomainsFromInterval(from, to);
        Set<String> domainsDTO = domainsFromInterval.stream()
                .map(Domain::getDomain)
                .collect(Collectors.toSet());

        return new ResponseEntity<>(new VisitedDomainDTO(domainsDTO, "ok"), HttpStatus.OK);
    }

    @PostMapping("/visited_links")
    public ResponseEntity<StatusDTO> addVisited(@Valid @RequestBody VisitedLinksDTO visitedLinksDTO) {
        List<Link> links = visitedLinksDTO
                .getLinks()
                .stream()
                .map(link -> new Link(link, System.currentTimeMillis()))
                .collect(Collectors.toList());

        visitsService.addLinks(links);

        return new ResponseEntity<>(new StatusDTO("ok"), HttpStatus.CREATED);
    }

    //EXCEPTIONS

    private static void assertTimeInterval(long from, long to) {
        if (from >= to) {
            throw new IllegalArgumentException("Start time is greater than end time");
        }
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    private ResponseEntity<StatusDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return new ResponseEntity<>(new StatusDTO("Error json"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ValidationException.class})
    private ResponseEntity<StatusDTO> handleValidationExceptions(ValidationException ex) {
        return new ResponseEntity<>(new StatusDTO("Error parameter value"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    private ResponseEntity<StatusDTO> handleValidationExceptions(MethodArgumentTypeMismatchException ex) {
        return new ResponseEntity<>(new StatusDTO("Error type parameter"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ServletRequestBindingException.class})
    protected ResponseEntity<StatusDTO> handleValidationExceptions(ServletRequestBindingException ex) {
        return new ResponseEntity<>(new StatusDTO("Wrong number of parameters"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    protected ResponseEntity<StatusDTO> handleValidationExceptions(IllegalArgumentException ex) {
        return new ResponseEntity<>(new StatusDTO(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
