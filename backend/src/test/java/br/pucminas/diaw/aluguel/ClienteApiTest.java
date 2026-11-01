package br.pucminas.diaw.aluguel;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ClienteApiTest extends ApiTestBase {

    private String atualizacao(String nome, String rendimentos) {
        return """
                {"rg":"MG1","cpf":"%s","nome":"%s","endereco":"Rua B, 20","profissao":"Gerente","rendimentos":%s}
                """.formatted(CPF_1, nome, rendimentos);
    }

    @Test
    void cadastroNormalizaCpfENaoExpoeSenha() throws Exception {
        mvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON)
                        .content(clienteJson("ana", "MG1", "529.982.247-25")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cpf").value(CPF_1))
                .andExpect(jsonPath("$.rendaTotal").value(5000.00))
                .andExpect(jsonPath("$.senha").doesNotExist())
                .andExpect(jsonPath("$.senhaHash").doesNotExist());
    }

    @Test
    void cpfInvalidoERejeitado() throws Exception {
        mvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON)
                        .content(clienteJson("ana", "MG1", "12345678900")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.cpf").value("CPF inválido"));
    }

    @Test
    void cpfRgELoginDuplicadosSaoRejeitados() throws Exception {
        cadastrarCliente("ana", "MG1", CPF_1);
        for (String json : new String[] {
                clienteJson("bia", "MG2", CPF_1),
                clienteJson("bia", "MG1", CPF_2),
                clienteJson("ana", "MG2", CPF_2)}) {
            mvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isConflict());
        }
    }

    @Test
    void maisDeTresRendimentosSaoRejeitados() throws Exception {
        long id = cadastrarCliente("ana", "MG1", CPF_1);
        String item = "{\"entidadeEmpregadora\":\"E\",\"valor\":100}";
        String quatro = "[" + String.join(",", item, item, item, item) + "]";
        mvc.perform(comToken(put("/api/clientes/" + id), login("ana")).content(atualizacao("Ana", quatro)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.rendimentos").value("Informe no máximo 3 rendimentos"));
    }

    @Test
    void clienteAtualizaOsPropriosDadosESubstituiRendimentos() throws Exception {
        long id = cadastrarCliente("ana", "MG1", CPF_1);
        String token = login("ana");
        String dois = "[{\"entidadeEmpregadora\":\"A\",\"valor\":1000},{\"entidadeEmpregadora\":\"B\",\"valor\":500.50}]";
        mvc.perform(comToken(put("/api/clientes/" + id), token).content(atualizacao("Ana Maria", dois)))
                .andExpect(status().isOk());
        mvc.perform(comToken(get("/api/clientes/" + id), token))
                .andExpect(jsonPath("$.nome").value("Ana Maria"))
                .andExpect(jsonPath("$.rendimentos.length()").value(2))
                .andExpect(jsonPath("$.rendaTotal").value(1500.50));
    }

    @Test
    void clienteNaoAcessaDadosDeOutroCliente() throws Exception {
        long outro = cadastrarCliente("bia", "MG2", CPF_2);
        cadastrarCliente("ana", "MG1", CPF_1);
        String token = login("ana");
        mvc.perform(comToken(get("/api/clientes/" + outro), token)).andExpect(status().isForbidden());
        mvc.perform(comToken(put("/api/clientes/" + outro), token).content(atualizacao("X", "[]")))
                .andExpect(status().isForbidden());
        mvc.perform(comToken(delete("/api/clientes/" + outro), token)).andExpect(status().isForbidden());
        mvc.perform(comToken(get("/api/clientes"), token)).andExpect(status().isForbidden());
    }

    @Test
    void agenteConsultaClientesParaAnaliseMasNaoAltera() throws Exception {
        long id = cadastrarCliente("ana", "MG1", CPF_1);
        cadastrarAgente("loja", CNPJ_EMPRESA, "EMPRESA");
        String token = login("loja");
        mvc.perform(comToken(get("/api/clientes"), token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        mvc.perform(comToken(get("/api/clientes/" + id), token)).andExpect(status().isOk());
        mvc.perform(comToken(delete("/api/clientes/" + id), token)).andExpect(status().isForbidden());
    }

    @Test
    void novaSenhaPassaAValerNoLogin() throws Exception {
        long id = cadastrarCliente("ana", "MG1", CPF_1);
        String corpo = """
                {"novaSenha":"outra-senha-1","rg":"MG1","cpf":"%s","nome":"Ana","endereco":"Rua","profissao":"P","rendimentos":[]}
                """.formatted(CPF_1);
        mvc.perform(comToken(put("/api/clientes/" + id), login("ana")).content(corpo)).andExpect(status().isOk());
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"ana\",\"senha\":\"outra-senha-1\"}"))
                .andExpect(status().isOk());
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"ana\",\"senha\":\"" + SENHA + "\"}"))
                .andExpect(status().isUnauthorized());
    }
}
