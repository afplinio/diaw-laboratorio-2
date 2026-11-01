package br.pucminas.diaw.aluguel;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class AuthApiTest extends ApiTestBase {

    @Test
    void acessoSemLoginENegado() throws Exception {
        mvc.perform(get("/api/pedidos")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/automoveis")).andExpect(status().isUnauthorized());
    }

    @Test
    void tokenInvalidoENegado() throws Exception {
        mvc.perform(comToken(get("/api/auth/me"), "token-falso")).andExpect(status().isUnauthorized());
    }

    @Test
    void loginDevolveTokenEIdentificaOUsuario() throws Exception {
        cadastrarCliente("ana", "MG1", CPF_1);
        mvc.perform(comToken(get("/api/auth/me"), login("ana")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("ana"))
                .andExpect(jsonPath("$.papel").value("CLIENTE"));
    }

    @Test
    void agenteTemTipoIdentificado() throws Exception {
        cadastrarAgente("banco", CNPJ_BANCO, "BANCO");
        mvc.perform(comToken(get("/api/auth/me"), login("banco")))
                .andExpect(jsonPath("$.papel").value("AGENTE"))
                .andExpect(jsonPath("$.tipoAgente").value("BANCO"));
    }

    @Test
    void senhaErradaEUsuarioInexistenteTemAMesmaResposta() throws Exception {
        cadastrarCliente("ana", "MG1", CPF_1);
        for (String login : new String[] {"ana", "ninguem"}) {
            mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                            .content("{\"login\":\"" + login + "\",\"senha\":\"errada-123\"}"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.mensagem").value("Usuário ou senha inválidos"));
        }
    }

    @Test
    void tokenDeContaExcluidaDeixaDeValer() throws Exception {
        long id = cadastrarCliente("ana", "MG1", CPF_1);
        String token = login("ana");
        mvc.perform(comToken(delete("/api/clientes/" + id), token))
                .andExpect(status().isNoContent());
        mvc.perform(comToken(get("/api/auth/me"), token)).andExpect(status().isUnauthorized());
    }
}
