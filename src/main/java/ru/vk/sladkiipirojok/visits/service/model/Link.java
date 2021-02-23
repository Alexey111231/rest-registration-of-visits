package ru.vk.sladkiipirojok.visits.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("Link")
public final class Link {
    @Id
    private Integer id;
    private String link;
    private Long date;

    public Link(String link, Long date) {
        this.link = link;
        this.date = date;
    }
}