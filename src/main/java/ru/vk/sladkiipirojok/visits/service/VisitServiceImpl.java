package ru.vk.sladkiipirojok.visits.service;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vk.sladkiipirojok.visits.service.dto.Domain;
import ru.vk.sladkiipirojok.visits.service.model.Link;
import ru.vk.sladkiipirojok.visits.service.repository.LinkRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class VisitServiceImpl implements VisitsService {
    private final LinkRepository linkRepository;
    private final UrlValidator urlValidator;

    @Autowired
    public VisitServiceImpl(LinkRepository linkRepository, UrlValidator urlValidator) {
        this.linkRepository = linkRepository;
        this.urlValidator = urlValidator;
    }

    @Override
    public void addLinks(List<Link> linkList) {
        linkRepository.saveAll(linkList);
    }

    //Решение до более глубокого знакомства с redis
    @Override
    public Set<Domain> getDomainsFromInterval(Long from, Long to) {
        Iterable<Link> links = linkRepository.findAll();

        Set<Domain> visitedLinks = new HashSet<>();
        links.forEach(link -> {
            if (link.getDate() >= from && link.getDate() <= to) {
                visitedLinks.add(new Domain(getDomain(link)));
            }
        });

        return visitedLinks;
    }

    private String getDomain(Link link) {
        String linkStr = link.getLink();
        URI uri;

        try {
            uri = urlValidator.isValid(linkStr) ? new URI(linkStr)
                    : new URI("http://" + linkStr);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Is invalid link:" + linkStr);
        }

        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }
}
