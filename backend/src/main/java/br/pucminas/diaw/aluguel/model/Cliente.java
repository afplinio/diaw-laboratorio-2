package br.pucminas.diaw.aluguel.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "clientes")
public class Cliente extends Usuario {

    public static final int MAX_RENDIMENTOS = 3;

    @Column(nullable = false, unique = true, length = 20)
    private String rg;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false)
    private String endereco;

    @Column(nullable = false, length = 80)
    private String profissao;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<Rendimento> rendimentos = new ArrayList<>();

    protected Cliente() {
    }

    public Cliente(String login, String senhaHash, String rg, String cpf, String nome,
                   String endereco, String profissao) {
        super(login, senhaHash);
        this.rg = rg;
        this.cpf = cpf;
        this.nome = nome;
        this.endereco = endereco;
        this.profissao = profissao;
    }

    public void atualizarDados(String rg, String cpf, String nome, String endereco, String profissao) {
        this.rg = rg;
        this.cpf = cpf;
        this.nome = nome;
        this.endereco = endereco;
        this.profissao = profissao;
    }

    public void definirRendimentos(List<Rendimento> novos) {
        if (novos.size() > MAX_RENDIMENTOS) {
            throw new IllegalArgumentException("O cliente pode ter no máximo " + MAX_RENDIMENTOS + " rendimentos");
        }
        rendimentos.clear();
        novos.forEach(rendimento -> {
            rendimento.setCliente(this);
            rendimentos.add(rendimento);
        });
    }

    public BigDecimal getRendaTotal() {
        return rendimentos.stream().map(Rendimento::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Papel getPapel() {
        return Papel.CLIENTE;
    }

    @Override
    public TipoProprietario getTipoProprietario() {
        return TipoProprietario.CLIENTE;
    }

    @Override
    public String getNome() {
        return nome;
    }

    public String getRg() {
        return rg;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getProfissao() {
        return profissao;
    }

    public List<Rendimento> getRendimentos() {
        return Collections.unmodifiableList(rendimentos);
    }
}
