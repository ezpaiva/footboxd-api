package com.footboxd.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.footboxd.model.TipoAvaliacao;

public class AvaliacaoResponseDTO {
    public Long id;
    public Long fixtureId;
    public TipoAvaliacao tipo;
    public Long referenciaId;
    public BigDecimal nota;
    public Instant criadoEm;

    public AvaliacaoResponseDTO(Long id, Long fixtureId, TipoAvaliacao tipo,
                                Long referenciaId, BigDecimal nota, Instant criadoEm) {
        this.id = id;
        this.fixtureId = fixtureId;
        this.tipo = tipo;
        this.referenciaId = referenciaId;
        this.nota = nota;
        this.criadoEm = criadoEm;
    }
}
