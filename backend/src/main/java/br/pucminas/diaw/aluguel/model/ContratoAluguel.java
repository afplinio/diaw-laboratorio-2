package br.pucminas.diaw.aluguel.model;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "contratos_aluguel")
public class ContratoAluguel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id")
    private PedidoAluguel pedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agente_id")
    private Agente agente;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "tipo_proprietario", nullable = false, length = 10)
    private TipoProprietario tipoProprietario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proprietario_id")
    private Usuario proprietario;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "contrato_credito_id")
    private ContratoCredito contratoCredito;

    @Column(name = "assinado_em", nullable = false)
    private Instant assinadoEm;

    protected ContratoAluguel() {
    }

    public ContratoAluguel(PedidoAluguel pedido, Agente agente, TipoProprietario tipoProprietario,
                           Usuario proprietario, ContratoCredito contratoCredito) {
        this.pedido = pedido;
        this.agente = agente;
        this.tipoProprietario = tipoProprietario;
        this.proprietario = proprietario;
        this.contratoCredito = contratoCredito;
        this.assinadoEm = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public PedidoAluguel getPedido() {
        return pedido;
    }

    public Agente getAgente() {
        return agente;
    }

    public TipoProprietario getTipoProprietario() {
        return tipoProprietario;
    }

    public Usuario getProprietario() {
        return proprietario;
    }

    public ContratoCredito getContratoCredito() {
        return contratoCredito;
    }

    public Instant getAssinadoEm() {
        return assinadoEm;
    }
}
