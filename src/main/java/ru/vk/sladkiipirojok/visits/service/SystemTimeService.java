package ru.vk.sladkiipirojok.visits.service;

import org.springframework.stereotype.Service;

@Service
public class SystemTimeService implements TimeService {
    @Override
    public long getTime() {
        return System.currentTimeMillis();
    }
}
