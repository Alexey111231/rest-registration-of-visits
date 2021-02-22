package ru.vk.sladkiipirojok.visits.service.repository;

import org.springframework.data.repository.CrudRepository;
import ru.vk.sladkiipirojok.visits.service.model.Link;

import java.util.List;

public interface LinkRepository extends CrudRepository<Link, Long> {
    List<Link> findByDateBetween(long from, long to);
}
