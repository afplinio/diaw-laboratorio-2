package br.pucminas.diaw.aluguel;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;

/** Cenário comum: cliente, empresa com um automóvel e banco já cadastrados. */
abstract class FluxoTestBase extends ApiTestBase {

    protected static final LocalDate INICIO = LocalDate.now().plusDays(10);
    protected static final LocalDate FIM = LocalDate.now().plusDays(20);

    protected long clienteId;
    protected long empresaId;
    protected long bancoId;
    protected long automovelId;
    protected String tokenCliente;
    protected String tokenEmpresa;
    protected String tokenBanco;

    @BeforeEach
    void prepararCenario() throws Exception {
        clienteId = cadastrarCliente("ana", "MG1", CPF_1);
        empresaId = cadastrarAgente("loja", CNPJ_EMPRESA, "EMPRESA");
        bancoId = cadastrarAgente("banco", CNPJ_BANCO, "BANCO");
        tokenCliente = login("ana");
        tokenEmpresa = login("loja");
        tokenBanco = login("banco");
        automovelId = idDe(mvc.perform(comToken(post("/api/automoveis"), tokenEmpresa).content("""
                        {"matricula":"M1","ano":2024,"marca":"Fiat","modelo":"Argo","placa":"ABC1D23"}
                        """))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
    }

    protected String pedidoJson(LocalDate inicio, LocalDate fim, String valor) {
        return """
                {"automovelId":%d,"dataInicio":"%s","dataFim":"%s","valorProposto":%s,"observacoes":"Viagem"}
                """.formatted(automovelId, inicio, fim, valor);
    }

    protected long criarPedido(String token) throws Exception {
        return idDe(mvc.perform(comToken(post("/api/pedidos"), token).content(pedidoJson(INICIO, FIM, "1500.00")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
    }

    protected void aprovar(long pedidoId) throws Exception {
        mvc.perform(comToken(post("/api/pedidos/" + pedidoId + "/avaliacao"), tokenEmpresa)
                        .content("{\"aprovado\":true,\"parecer\":\"Renda compatível\"}"))
                .andExpect(status().isOk());
    }
}
