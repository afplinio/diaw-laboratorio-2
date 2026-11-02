package br.pucminas.diaw.aluguel;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

class ContratoApiTest extends FluxoTestBase {

    private String credito(long banco, String valor) {
        return """
                {"bancoId":%d,"valor":%s,"taxaJurosMensal":1.50,"parcelas":12}
                """.formatted(banco, valor);
    }

    private long pedidoAprovado() throws Exception {
        long id = criarPedido(tokenCliente);
        aprovar(id);
        return id;
    }

    @Test
    void pedidoNaoAprovadoNaoViraContrato() throws Exception {
        long id = criarPedido(tokenCliente);
        mvc.perform(comToken(post("/api/pedidos/" + id + "/contrato"), tokenEmpresa)
                        .content("{\"tipoProprietario\":\"EMPRESA\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void clienteNaoExecutaContrato() throws Exception {
        long id = pedidoAprovado();
        mvc.perform(comToken(post("/api/pedidos/" + id + "/contrato"), tokenCliente)
                        .content("{\"tipoProprietario\":\"CLIENTE\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void contratoDaEmpresaMantemAutomovelComAEmpresaEMudaStatus() throws Exception {
        long id = pedidoAprovado();
        mvc.perform(comToken(post("/api/pedidos/" + id + "/contrato"), tokenEmpresa)
                        .content("{\"tipoProprietario\":\"EMPRESA\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.proprietarioNome").value("Agente loja"))
                .andExpect(jsonPath("$.credito").doesNotExist());
        mvc.perform(comToken(get("/api/pedidos/" + id), tokenCliente))
                .andExpect(jsonPath("$.status").value("CONTRATADO"));
    }

    @Test
    void contratoDoClienteTransfereAutomovelETiraDaListaDeDisponiveis() throws Exception {
        long id = pedidoAprovado();
        mvc.perform(comToken(post("/api/pedidos/" + id + "/contrato"), tokenEmpresa)
                        .content("{\"tipoProprietario\":\"CLIENTE\"}"))
                .andExpect(status().isCreated());
        mvc.perform(comToken(get("/api/automoveis/" + automovelId), tokenCliente))
                .andExpect(jsonPath("$.tipoProprietario").value("CLIENTE"));
        mvc.perform(comToken(get("/api/automoveis").param("disponiveis", "true"), tokenCliente))
                .andExpect(jsonPath("$.length()").value(0));
        mvc.perform(comToken(post("/api/pedidos"), tokenCliente)
                        .content(pedidoJson(FIM.plusDays(5), FIM.plusDays(6), "100")))
                .andExpect(status().isConflict());
    }

    @Test
    void contratoDoBancoExigeCredito() throws Exception {
        long id = pedidoAprovado();
        mvc.perform(comToken(post("/api/pedidos/" + id + "/contrato"), tokenEmpresa)
                        .content("{\"tipoProprietario\":\"BANCO\"}"))
                .andExpect(status().isConflict());
        String corpo = "{\"tipoProprietario\":\"BANCO\",\"credito\":" + credito(bancoId, "1000.00") + "}";
        mvc.perform(comToken(post("/api/pedidos/" + id + "/contrato"), tokenEmpresa).content(corpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.proprietarioNome").value("Agente banco"))
                .andExpect(jsonPath("$.credito.bancoNome").value("Agente banco"))
                .andExpect(jsonPath("$.credito.parcelas").value(12));
    }

    @Test
    void creditoSoPodeVirDeBancoENaoPodeSuperarOPedido() throws Exception {
        long id = pedidoAprovado();
        String deEmpresa = "{\"tipoProprietario\":\"EMPRESA\",\"credito\":" + credito(empresaId, "1000.00") + "}";
        mvc.perform(comToken(post("/api/pedidos/" + id + "/contrato"), tokenEmpresa).content(deEmpresa))
                .andExpect(status().isConflict());
        String excessivo = "{\"tipoProprietario\":\"EMPRESA\",\"credito\":" + credito(bancoId, "9999.00") + "}";
        mvc.perform(comToken(post("/api/pedidos/" + id + "/contrato"), tokenEmpresa).content(excessivo))
                .andExpect(status().isConflict());
    }

    @Test
    void bancoNaoRegistraComoPropriedadeDeEmpresa() throws Exception {
        long id = pedidoAprovado();
        mvc.perform(comToken(post("/api/pedidos/" + id + "/contrato"), tokenBanco)
                        .content("{\"tipoProprietario\":\"EMPRESA\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void automovelNaoPodeSerContratadoDuasVezesNoMesmoPeriodo() throws Exception {
        long primeiro = pedidoAprovado();
        long segundo = pedidoAprovado();
        mvc.perform(comToken(post("/api/pedidos/" + primeiro + "/contrato"), tokenEmpresa)
                        .content("{\"tipoProprietario\":\"EMPRESA\"}"))
                .andExpect(status().isCreated());
        mvc.perform(comToken(post("/api/pedidos/" + segundo + "/contrato"), tokenEmpresa)
                        .content("{\"tipoProprietario\":\"EMPRESA\"}"))
                .andExpect(status().isConflict());
        mvc.perform(comToken(post("/api/pedidos"), tokenCliente).content(pedidoJson(INICIO, FIM, "100")))
                .andExpect(status().isConflict());
    }

    @Test
    void listaMostraContratosApenasAosEnvolvidos() throws Exception {
        long id = pedidoAprovado();
        String corpo = "{\"tipoProprietario\":\"EMPRESA\",\"credito\":" + credito(bancoId, "1000.00") + "}";
        mvc.perform(comToken(post("/api/pedidos/" + id + "/contrato"), tokenEmpresa).content(corpo))
                .andExpect(status().isCreated());
        for (String token : new String[] {tokenCliente, tokenEmpresa, tokenBanco}) {
            mvc.perform(comToken(get("/api/contratos"), token)).andExpect(jsonPath("$.length()").value(1));
        }
        cadastrarCliente("bia", "MG2", CPF_2);
        mvc.perform(comToken(get("/api/contratos"), login("bia"))).andExpect(jsonPath("$.length()").value(0));
    }
}
