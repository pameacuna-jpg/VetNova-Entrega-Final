package com.vetnova.atencionclinica.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class CertificadoEmitidoEvent {

    private final String eventId;
    private final String eventType;
    private final String source;
    private final LocalDateTime occurredAt;
    private final Payload payload;

    public CertificadoEmitidoEvent(Long idDiagnostico, Long idVeterinario, Long idMascota, Long idCliente, String detalleCertificado) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = "CertificadoEmitido";
        this.source = "ms-atencion-clinica";
        this.occurredAt = LocalDateTime.now();
        this.payload = new Payload(idDiagnostico, idVeterinario, idMascota, idCliente, detalleCertificado);
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

    public Long getIdCliente() {
        return payload != null ? payload.getIdCliente() : null;
    }

    public String getDetalleCertificado() {
        return payload != null ? payload.getDetalleCertificado() : null;
    }

    public static class Payload {
        private final Long idDiagnostico;
        private final Long idVeterinario;
        private final Long idMascota;
        private final Long idCliente;
        private final String detalleCertificado;

        public Payload(Long idDiagnostico, Long idVeterinario, Long idMascota, Long idCliente, String detalleCertificado) {
            this.idDiagnostico = idDiagnostico;
            this.idVeterinario = idVeterinario;
            this.idMascota = idMascota;
            this.idCliente = idCliente;
            this.detalleCertificado = detalleCertificado;
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

        public Long getIdCliente() {
            return idCliente;
        }

        public String getDetalleCertificado() {
            return detalleCertificado;
        }
    }
}
