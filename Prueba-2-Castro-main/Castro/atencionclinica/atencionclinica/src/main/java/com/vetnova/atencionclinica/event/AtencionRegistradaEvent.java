package com.vetnova.atencionclinica.event;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AtencionRegistradaEvent {
    private final Long idFicha;
    private final Long idMascota;
    private final String estado;
}
