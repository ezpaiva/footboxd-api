package com.footboxd.model;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.*;

@Entity
@Table(
    name = "avaliacoes",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_avaliacao_usuario_fixture_tipo_ref",
        columnNames = {"usuario_id", "fixture_id", "tipo", "referencia_id"}
    )
)
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fixture_id", nullable = false)
    private Long fixtureId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAvaliacao tipo;

    @Column(name = "referencia_id", nullable = false)
    private Long referenciaId;

    @Column(nullable = false, precision = 3, scale = 1)
    private BigDecimal nota;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm = Instant.now();

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Long getId() { return id; }
    public Long getFixtureId() { return fixtureId; }
    public TipoAvaliacao getTipo() { return tipo; }
    public Long getReferenciaId() { return referenciaId; }
    public BigDecimal getNota() { return nota; }
    public Instant getCriadoEm() { return criadoEm; }
    public Usuario getUsuario() { return usuario; }

    public void setId(Long id) { this.id = id; }
    public void setFixtureId(Long fixtureId) { this.fixtureId = fixtureId; }
    public void setTipo(TipoAvaliacao tipo) { this.tipo = tipo; }
    public void setReferenciaId(Long referenciaId) { this.referenciaId = referenciaId; }
    public void setNota(BigDecimal nota) { this.nota = nota; }
    public void setCriadoEm(Instant criadoEm) { this.criadoEm = criadoEm; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}