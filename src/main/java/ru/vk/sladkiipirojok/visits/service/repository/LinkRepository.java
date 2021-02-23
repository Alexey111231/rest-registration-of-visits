package ru.vk.sladkiipirojok.visits.service.repository;

import org.springframework.data.repository.CrudRepository;
import ru.vk.sladkiipirojok.visits.service.model.Link;

public interface LinkRepository extends CrudRepository<Link, Long> {
}
