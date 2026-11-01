package br.pucminas.diaw.aluguel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "pedidos_aluguel")
public class PedidoAluguel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "automovel_id")
    private Automovel automovel;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Column(name = "valor_proposto", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorProposto;

    @Column(length = 500)
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private StatusPedido status;

    @Column(name = "parecer_financeiro", length = 500)
    private String parecerFinanceiro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avaliador_id")
    private Agente avaliador;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "avaliado_em")
    private Instant avaliadoEm;

    @Version
    private Long versao;

    protected PedidoAluguel() {
    }

    public PedidoAluguel(Cliente cliente, Automovel automovel, LocalDate dataInicio, LocalDate dataFim,
                         BigDecimal valorProposto, String observacoes) {
        this.cliente = cliente;
        this.automovel = automovel;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.valorProposto = valorProposto;
        this.observacoes = observacoes;
        this.status = StatusPedido.EM_ANALISE;
        this.criadoEm = Instant.now();
    }

    public void modificar(Automovel automovel, LocalDate dataInicio, LocalDate dataFim,
                          BigDecimal valorProposto, String observacoes) {
        this.automovel = automovel;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.valorProposto = valorProposto;
        this.observacoes = observacoes;
    }

    public void avaliar(Agente avaliador, boolean aprovado, String parecerFinanceiro) {
        this.avaliador = avaliador;
        this.parecerFinanceiro = parecerFinanceiro;
        this.avaliadoEm = Instant.now();
        this.status = aprovado ? StatusPedido.APROVADO : StatusPedido.REPROVADO;
    }

    public void cancelar() {
        this.status = StatusPedido.CANCELADO;
    }

    public void marcarContratado() {
        this.status = StatusPedido.CONTRATADO;
    }

    public boolean pertenceAoCliente(Long clienteId) {
        return cliente.getId().equals(clienteId);
    }

    public Long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Automovel getAutomovel() {
        return automovel;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public BigDecimal getValorProposto() {
        return valorProposto;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public String getParecerFinanceiro() {
        return parecerFinanceiro;
    }

    public Agente getAvaliador() {
        return avaliador;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAvaliadoEm() {
        return avaliadoEm;
    }
}
