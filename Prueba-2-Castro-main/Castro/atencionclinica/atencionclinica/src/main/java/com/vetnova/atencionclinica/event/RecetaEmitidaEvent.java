package com.vetnova.atencionclinica.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class RecetaEmitidaEvent {

    private final String eventId;
    private final String eventType;
    private final String source;
    private final LocalDateTime occurredAt;
    private final Payload payload;

    public RecetaEmitidaEvent(Long idDiagnostico, Long idVeterinario, Long idMascota, String recetaMedica) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = "RecetaEmitida";
        this.source = "ms-atencion-clinica";
        this.occurredAt = LocalDateTime.now();
        this.payload = new Payload(idDiagnostico, idVeterinario, idMascota, recetaMedica);
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getSource() {
        return source;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public Payload getPayload() {
        return payload;
    }

    public Long getIdDiagnostico() {
        return payload != null ? payload.getIdDiagnostico() : null;
    }

    public Long getIdVeterinario() {
        return payload != null ? payload.getIdVeterinario() : null;
    }

    public Long getIdMascota() {
        return payload != null ? payload.getIdMascota() : null;
    }

    public String getRecetaMedica() {
        return payload != null ? payload.getRecetaMedica() : null;
    }

    public static class Payload {
        private final Long idDiagnostico;
        private final Long idVeterinario;
        private final Long idMascota;
        private final String recetaMedica;

        public Payload(Long idDiagnostico, Long idVeterinario, Long idMascota, String recetaMedica) {
            this.idDiagnostico = idDiagnostico;
            this.idVeterinario = idVeterinario;
            this.idMascota = idMascota;
            this.recetaMedica = recetaMedica;
        }

        public Long getIdDiagnostico() {
            return idDiagnostico;
        }

        public Long getIdVeterinario() {
            return idVeterinario;
        }

        public Long getIdMascota() {
            return idMascota;
        }

        public String getRecetaMedica() {
            return recetaMedica;
        }
    }
}