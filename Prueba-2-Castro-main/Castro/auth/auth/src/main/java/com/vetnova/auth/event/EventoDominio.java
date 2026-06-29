package com.vetnova.auth.event;

import java.time.LocalDateTime;

public class EventoDominio<T> {
    private String eventType;
    private String source;
    private T payload;
    private LocalDateTime timestamp;

    public EventoDominio(String eventType, String source, T payload) {
        this.eventType = eventType;
        this.source = source;
        this.payload = payload;
        this.timestamp = LocalDateTime.now();
    }

    public String getEventType() { return eventType; }
    public String getSource() { return source; }
    public T getPayload() { return payload; }
    public LocalDateTime getTimestamp() { return timestamp; }
}