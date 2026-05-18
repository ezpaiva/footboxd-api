package com.footboxd.dto;

import java.math.BigDecimal;

import com.footboxd.model.TipoAvaliacao;

public class AvaliacaoRequestDTO {
    public Long fixtureId;
    public TipoAvaliacao tipo;
    public Long referenciaId;
    public BigDecimal nota;
}