package ru.vk.sladkiipirojok.visits.service;

import ru.vk.sladkiipirojok.visits.service.dto.Domain;
import ru.vk.sladkiipirojok.visits.service.model.Link;

import java.util.List;
import java.util.Set;

public interface VisitsService {
    public void addLinks(List<Link> linkList);

    public Set<Domain> getDomainsFromInterval(Long from, Long to);
}
