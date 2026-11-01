package br.pucminas.diaw.aluguel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "agentes")
public class Agente extends Usuario {

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 10)
    private TipoAgente tipo;

    protected Agente() {
    }

    public Agente(String login, String senhaHash, String nome, String cnpj, TipoAgente tipo) {
        super(login, senhaHash);
        this.nome = nome;
        this.cnpj = cnpj;
        this.tipo = tipo;
    }

    public boolean isBanco() {
        return tipo == TipoAgente.BANCO;
    }

    @Override
    public Papel getPapel() {
        return Papel.AGENTE;
    }

    @Override
    public TipoProprietario getTipoProprietario() {
        return isBanco() ? TipoProprietario.BANCO : TipoProprietario.EMPRESA;
    }

    @Override
    public String getNome() {
        return nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public TipoAgente getTipo() {
        return tipo;
    }
}
