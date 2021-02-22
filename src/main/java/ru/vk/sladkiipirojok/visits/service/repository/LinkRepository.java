package ru.vk.sladkiipirojok.visits.service.repository;

import ru.vk.sladkiipirojok.visits.service.model.Link;

import java.util.List;
import java.util.Set;

public interface LinkRepository {
    Set<Link> findByDateBetween(long from, long to);

    void saveAll(List<Link> links);
}
