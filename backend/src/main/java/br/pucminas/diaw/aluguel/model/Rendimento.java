package br.pucminas.diaw.aluguel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "rendimentos")
public class Rendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "entidade_empregadora", nullable = false, length = 120)
    private String entidadeEmpregadora;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    protected Rendimento() {
    }

    public Rendimento(String entidadeEmpregadora, BigDecimal valor) {
        this.entidadeEmpregadora = entidadeEmpregadora;
        this.valor = valor;
    }

    void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Long getId() {
        return id;
    }

    public String getEntidadeEmpregadora() {
        return entidadeEmpregadora;
    }

    public BigDecimal getValor() {
        return valor;
    }
}
