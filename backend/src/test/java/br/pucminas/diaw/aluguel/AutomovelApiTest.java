package br.pucminas.diaw.aluguel;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

class AutomovelApiTest extends ApiTestBase {

    private String automovel(String matricula, String placa) {
        return """
                {"matricula":"%s","ano":2024,"marca":"Fiat","modelo":"Argo","placa":"%s"}
                """.formatted(matricula, placa);
    }

    @Test
    void agenteCadastraEViraProprietarioENormalizaPlaca() throws Exception {
        cadastrarAgente("loja", CNPJ_EMPRESA, "EMPRESA");
        mvc.perform(comToken(post("/api/automoveis"), login("loja")).content(automovel("M1", "abc-1d23")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placa").value("ABC1D23"))
                .andExpect(jsonPath("$.tipoProprietario").value("EMPRESA"))
                .andExpect(jsonPath("$.proprietarioNome").value("Agente loja"));
    }

    @Test
    void clienteConsultaMasNaoCadastra() throws Exception {
        cadastrarCliente("ana", "MG1", CPF_1);
        String token = login("ana");
        mvc.perform(comToken(post("/api/automoveis"), token).content(automovel("M1", "ABC1D23")))
                .andExpect(status().isForbidden());
        mvc.perform(comToken(get("/api/automoveis"), token)).andExpect(status().isOk());
    }

    @Test
    void placaEMatriculaDuplicadasSaoRejeitadas() throws Exception {
        cadastrarAgente("loja", CNPJ_EMPRESA, "EMPRESA");
        String token = login("loja");
        mvc.perform(comToken(post("/api/automoveis"), token).content(automovel("M1", "ABC1D23")))
                .andExpect(status().isCreated());
        mvc.perform(comToken(post("/api/automoveis"), token).content(automovel("M2", "ABC1D23")))
                .andExpect(status().isConflict());
        mvc.perform(comToken(post("/api/automoveis"), token).content(automovel("M1", "XYZ1234")))
                .andExpect(status().isConflict());
    }

    @Test
    void placaInvalidaERejeitada() throws Exception {
        cadastrarAgente("loja", CNPJ_EMPRESA, "EMPRESA");
        mvc.perform(comToken(post("/api/automoveis"), login("loja")).content(automovel("M1", "12345")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.placa").value("Placa inválida"));
    }

    @Test
    void somenteOProprietarioAlteraOuExclui() throws Exception {
        cadastrarAgente("loja", CNPJ_EMPRESA, "EMPRESA");
        cadastrarAgente("banco", CNPJ_BANCO, "BANCO");
        String dono = login("loja");
        String outro = login("banco");
        long id = idDe(mvc.perform(comToken(post("/api/automoveis"), dono).content(automovel("M1", "ABC1D23")))
                .andReturn().getResponse().getContentAsString());
        mvc.perform(comToken(put("/api/automoveis/" + id), outro).content(automovel("M1", "ABC1D23")))
                .andExpect(status().isForbidden());
        mvc.perform(comToken(delete("/api/automoveis/" + id), outro)).andExpect(status().isForbidden());
        mvc.perform(comToken(put("/api/automoveis/" + id), dono).content(automovel("M9", "ABC1D23")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.matricula").value("M9"));
        mvc.perform(comToken(delete("/api/automoveis/" + id), dono)).andExpect(status().isNoContent());
        mvc.perform(comToken(get("/api/automoveis/" + id), dono)).andExpect(status().isNotFound());
    }
}
