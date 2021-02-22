package ru.vk.sladkiipirojok.visits.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@AllArgsConstructor
@RedisHash("Link")
public final class Link {
    @Id
    private long id;
    private String link;
    private long date;

    public Link(String link, long date) {
        this.link = link;
        this.date = date;
    }
}