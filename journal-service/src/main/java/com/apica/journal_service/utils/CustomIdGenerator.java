package com.apica.journal_service.utils;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomIdGenerator {
    private static final AtomicInteger roleCodeCounter = new AtomicInteger(10000);

    public static String generateId(String entityName) {
        String prefix = entityName.substring(0, 3).toLowerCase();
        String uuid = UUID.randomUUID().toString();
        return prefix + "-" + uuid;
    }
}