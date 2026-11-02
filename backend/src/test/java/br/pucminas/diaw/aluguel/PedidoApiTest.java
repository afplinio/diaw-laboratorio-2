package br.pucminas.diaw.aluguel;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class PedidoApiTest extends FluxoTestBase {

    @Test
    void clienteCriaPedidoEVisualizaStatus() throws Exception {
        long id = criarPedido(tokenCliente);
        mvc.perform(comToken(get("/api/pedidos/" + id), tokenCliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_ANALISE"))
                .andExpect(jsonPath("$.automovel.placa").value("ABC1D23"));
        mvc.perform(comToken(get("/api/pedidos"), tokenCliente))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void agenteNaoCriaPedido() throws Exception {
        mvc.perform(comToken(post("/api/pedidos"), tokenEmpresa).content(pedidoJson(INICIO, FIM, "100")))
                .andExpect(status().isForbidden());
    }

    @Test
    void periodoInvalidoERejeitado() throws Exception {
        mvc.perform(comToken(post("/api/pedidos"), tokenCliente).content(pedidoJson(FIM, INICIO, "100")))
                .andExpect(status().isConflict());
        mvc.perform(comToken(post("/api/pedidos"), tokenCliente)
                        .content(pedidoJson(LocalDate.now().minusDays(1), FIM, "100")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.dataInicio").exists());
    }

    @Test
    void clienteNaoVeNemAlteraPedidoDeOutroCliente() throws Exception {
        long id = criarPedido(tokenCliente);
        cadastrarCliente("bia", "MG2", CPF_2);
        String outro = login("bia");
        mvc.perform(comToken(get("/api/pedidos/" + id), outro)).andExpect(status().isForbidden());
        mvc.perform(comToken(put("/api/pedidos/" + id), outro).content(pedidoJson(INICIO, FIM, "1")))
                .andExpect(status().isForbidden());
        mvc.perform(comToken(post("/api/pedidos/" + id + "/cancelamento"), outro)).andExpect(status().isForbidden());
        mvc.perform(comToken(get("/api/pedidos"), outro)).andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void clienteModificaSomenteEmAnalise() throws Exception {
        long id = criarPedido(tokenCliente);
        mvc.perform(comToken(put("/api/pedidos/" + id), tokenCliente).content(pedidoJson(INICIO, FIM, "2000.00")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorProposto").value(2000.00));
        aprovar(id);
        mvc.perform(comToken(put("/api/pedidos/" + id), tokenCliente).content(pedidoJson(INICIO, FIM, "1.00")))
                .andExpect(status().isConflict());
    }

    @Test
    void agenteModificaPedidoAprovado() throws Exception {
        long id = criarPedido(tokenCliente);
        aprovar(id);
        mvc.perform(comToken(put("/api/pedidos/" + id), tokenEmpresa).content(pedidoJson(INICIO, FIM, "1800.00")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADO"))
                .andExpect(jsonPath("$.valorProposto").value(1800.00));
    }

    @Test
    void clienteCancelaEPedidoCanceladoNaoMudaMais() throws Exception {
        long id = criarPedido(tokenCliente);
        mvc.perform(comToken(post("/api/pedidos/" + id + "/cancelamento"), tokenCliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADO"));
        mvc.perform(comToken(post("/api/pedidos/" + id + "/cancelamento"), tokenCliente))
                .andExpect(status().isConflict());
        mvc.perform(comToken(post("/api/pedidos/" + id + "/avaliacao"), tokenEmpresa)
                        .content("{\"aprovado\":true,\"parecer\":\"Ok\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void agenteAvaliaComParecerERegistraAvaliador() throws Exception {
        long id = criarPedido(tokenCliente);
        mvc.perform(comToken(post("/api/pedidos/" + id + "/avaliacao"), tokenEmpresa)
                        .content("{\"aprovado\":false,\"parecer\":\"Renda insuficiente\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REPROVADO"))
                .andExpect(jsonPath("$.parecerFinanceiro").value("Renda insuficiente"))
                .andExpect(jsonPath("$.avaliadorNome").value("Agente loja"));
    }

    @Test
    void avaliacaoExigeParecerEPapelDeAgente() throws Exception {
        long id = criarPedido(tokenCliente);
        mvc.perform(comToken(post("/api/pedidos/" + id + "/avaliacao"), tokenEmpresa)
                        .content("{\"aprovado\":true,\"parecer\":\" \"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(comToken(post("/api/pedidos/" + id + "/avaliacao"), tokenCliente)
                        .content("{\"aprovado\":true,\"parecer\":\"Eu mesmo aprovo\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void agenteFiltraPedidosPorStatus() throws Exception {
        long id = criarPedido(tokenCliente);
        criarPedido(tokenCliente);
        aprovar(id);
        mvc.perform(comToken(get("/api/pedidos").param("status", "APROVADO"), tokenEmpresa))
                .andExpect(jsonPath("$.length()").value(1));
        mvc.perform(comToken(get("/api/pedidos"), tokenBanco))
                .andExpect(jsonPath("$.length()").value(2));
    }
}
