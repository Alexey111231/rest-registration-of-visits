package ru.vk.sladkiipirojok.visits.service;

import ru.vk.sladkiipirojok.visits.service.dto.Domain;
import ru.vk.sladkiipirojok.visits.service.model.Link;

import java.util.List;
import java.util.Set;

public interface VisitsService {
    void addLinks(List<Link> linkList);

    Set<Domain> getDomainsFromInterval(Long from, Long to);
}
