package ru.vk.sladkiipirojok.visits.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vk.sladkiipirojok.visits.service.dto.Domain;
import ru.vk.sladkiipirojok.visits.service.model.Link;
import ru.vk.sladkiipirojok.visits.service.repository.LinkRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VisitServiceImpl implements VisitsService {
    private final LinkRepository linkRepository;

    @Autowired
    public VisitServiceImpl(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    @Override
    public void addLinks(List<Link> linkList) {
        linkRepository.saveAll(linkList);
    }

    @Override
    public Set<Domain> getDomainsFromInterval(Long from, Long to) {
        List<Link> visitedLinks = linkRepository.findByDateBetween(from, to);
        return visitedLinks.stream()
                .map(link -> new Domain(getDomain(link)))
                .collect(Collectors.toSet());
    }

    private static String getDomain(Link link) {
        try {
            URI uri = new URI(link.getLink());
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Illegal link in databases");
        }
    }
}
