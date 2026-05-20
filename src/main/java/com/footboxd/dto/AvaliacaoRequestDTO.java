package com.footboxd.dto;

import java.math.BigDecimal;

import com.footboxd.model.TipoAvaliacao;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class AvaliacaoRequestDTO {
    
    @NotNull(message = "fixtureId é obrigatório")
    public Long fixtureId;
    
    @NotNull(message = "tipo é obrigatório")
    public TipoAvaliacao tipo;
    
    public Long referenciaId;
    
    @NotNull(message = "nota é obrigatória")
    @DecimalMin(value = "0.0", message = "nota deve ser no mínimo 0.0")
    @DecimalMax(value = "10.0", message = "nota deve ser no máximo 10.0")
    public BigDecimal nota;
}