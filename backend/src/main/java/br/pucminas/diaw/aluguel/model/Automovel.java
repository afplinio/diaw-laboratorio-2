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

@Entity
@Table(name = "automoveis")
public class Automovel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String matricula;

    @Column(nullable = false)
    private Integer ano;

    @Column(nullable = false, length = 60)
    private String marca;

    @Column(nullable = false, length = 60)
    private String modelo;

    @Column(nullable = false, unique = true, length = 7)
    private String placa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proprietario_id")
    private Usuario proprietario;

    protected Automovel() {
    }

    public Automovel(String matricula, Integer ano, String marca, String modelo, String placa, Usuario proprietario) {
        this.matricula = matricula;
        this.ano = ano;
        this.marca = marca;
        this.modelo = modelo;
        this.placa = placa;
        this.proprietario = proprietario;
    }

    public void atualizarDados(String matricula, Integer ano, String marca, String modelo, String placa) {
        this.matricula = matricula;
        this.ano = ano;
        this.marca = marca;
        this.modelo = modelo;
        this.placa = placa;
    }

    public void registrarProprietario(Usuario proprietario) {
        this.proprietario = proprietario;
    }

    /** Um automóvel transferido a um cliente deixa de estar disponível para novos aluguéis. */
    public boolean isDisponivelParaAluguel() {
        return proprietario.getTipoProprietario() != TipoProprietario.CLIENTE;
    }

    public Long getId() {
        return id;
    }

    public String getMatricula() {
        return matricula;
    }

    public Integer getAno() {
        return ano;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public Usuario getProprietario() {
        return proprietario;
    }
}
