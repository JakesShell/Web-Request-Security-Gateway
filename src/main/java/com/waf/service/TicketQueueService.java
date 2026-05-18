package com.waf.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waf.model.GatewayEvent;
import com.waf.model.Ticket;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class TicketQueueService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CopyOnWriteArrayList<Ticket> tickets = new CopyOnWriteArrayList<>();
    private final File storageFile = new File("data/gatewatch-tickets.json");

    public TicketQueueService() {
        load();
    }

    public List<Ticket> tickets() {
        return new ArrayList<>(tickets);
    }

    public Ticket openTicketForEvent(GatewayEvent event) {
        if (event == null || !"blocked".equals(event.getDecision())) {
            return null;
        }

        boolean exists = tickets.stream().anyMatch(ticket -> event.getId().equals(ticket.getLinkedEventId()));
        if (exists) {
            return null;
        }

        Ticket ticket = new Ticket(
                "GW-TCK-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase(),
                Instant.now().toString(),
                "open",
                event.getSeverity(),
                "Review " + event.getRuleId() + " on " + event.getPath(),
                event.getId(),
                ownerForSeverity(event.getSeverity()),
                event.getRecommendation()
        );

        tickets.add(0, ticket);
        save();
        return ticket;
    }

    public Ticket updateStatus(String ticketId, String status) {
        for (Ticket ticket : tickets) {
            if (ticket.getId().equals(ticketId)) {
                ticket.setStatus(status == null || status.isBlank() ? "in-review" : status);
                save();
                return ticket;
            }
        }

        return null;
    }

    public void clear() {
        tickets.clear();
        save();
    }

    private String ownerForSeverity(String severity) {
        if ("high".equals(severity)) {
            return "Security Operations";
        }
        if ("medium".equals(severity)) {
            return "Platform Support";
        }
        return "Application Support";
    }

    private void load() {
        try {
            if (!storageFile.exists()) {
                return;
            }

            List<Ticket> loaded = objectMapper.readValue(storageFile, new TypeReference<List<Ticket>>() {});
            tickets.clear();
            tickets.addAll(loaded);
        } catch (Exception ignored) {
            tickets.clear();
        }
    }

    private void save() {
        try {
            storageFile.getParentFile().mkdirs();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(storageFile, tickets);
        } catch (Exception ignored) {
        }
    }
}
