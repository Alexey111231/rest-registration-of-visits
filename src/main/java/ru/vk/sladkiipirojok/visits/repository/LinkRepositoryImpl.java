package ru.vk.sladkiipirojok.visits.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;
import ru.vk.sladkiipirojok.visits.service.model.Link;
import ru.vk.sladkiipirojok.visits.service.repository.LinkRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class LinkRepositoryImpl implements LinkRepository {
    private static final String Z_SET_NAME = "links";
    private static final String SEPARATOR = ":";

    private final ZSetOperations<String, String> zSetOps;

    @Autowired
    public LinkRepositoryImpl(RedisTemplate<String, String> redisTemplate) {
        zSetOps = redisTemplate.opsForZSet();
    }

    //За разумный срок для тестового задания я не смог найти,
    //Как задавать Range не из базовых типов, поэтому создан костыль
    //который влияет только на эту реализацию репозитория и изолирован от остальной части системы
    //В случае реальной практики я бы уточнил у старших коллег, либо продолижл поиски
    @Override
    public Set<Link> findByDateBetween(long from, long to) {
        Set<String> dateLinks = zSetOps.rangeByLex(Z_SET_NAME,
                RedisZSetCommands.Range.range()
                        .lte((from == 0 ? "" : from) + SEPARATOR) // 0 больше всех чисел, поэтому начинаем с :
                        .gte(to + SEPARATOR));

        return dateLinks == null ? new HashSet<>() : dateLinks.stream()
                .map(LinkRepositoryImpl::StringToLink)
                .collect(Collectors.toSet());
    }

    //Используются лексиграфически упорядоченные множества
    @Override
    public void saveAll(List<Link> links) {
        zSetOps.add(Z_SET_NAME, links.stream()
                .map(link -> new DefaultTypedTuple<>(LinkToString(link), 0d))
                .collect(Collectors.toSet()));
    }

    private static String LinkToString(Link link) {
        return link.getDate() + SEPARATOR + link.getLink();
    }

    private static Link StringToLink(String string) {
        int separatorPos = string.indexOf(SEPARATOR);
        return new Link(string.substring(separatorPos + 1),
                Long.parseLong(string.substring(0, separatorPos)));
    }
}
