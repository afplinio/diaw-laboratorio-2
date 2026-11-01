package br.pucminas.diaw.aluguel;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
abstract class ApiTestBase {

    protected static final String CPF_1 = "52998224725";
    protected static final String CPF_2 = "11144477735";
    protected static final String CNPJ_EMPRESA = "11222333000181";
    protected static final String CNPJ_BANCO = "45997418000153";
    protected static final String SENHA = "senha-segura";

    @Autowired
    protected MockMvc mvc;

    protected String clienteJson(String login, String rg, String cpf) {
        return """
                {"login":"%s","senha":"%s","rg":"%s","cpf":"%s","nome":"Cliente %s",
                 "endereco":"Rua A, 10","profissao":"Analista",
                 "rendimentos":[{"entidadeEmpregadora":"Empresa X","valor":5000.00}]}
                """.formatted(login, SENHA, rg, cpf, login);
    }

    protected long cadastrarCliente(String login, String rg, String cpf) throws Exception {
        return idDe(mvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON)
                        .content(clienteJson(login, rg, cpf)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
    }

    protected long cadastrarAgente(String login, String cnpj, String tipo) throws Exception {
        String json = """
                {"login":"%s","senha":"%s","nome":"Agente %s","cnpj":"%s","tipo":"%s"}
                """.formatted(login, SENHA, login, cnpj, tipo);
        return idDe(mvc.perform(post("/api/agentes").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
    }

    protected String login(String login) throws Exception {
        String json = """
                {"login":"%s","senha":"%s"}
                """.formatted(login, SENHA);
        String corpo = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return JsonPath.read(corpo, "$.token");
    }

    protected MockHttpServletRequestBuilder comToken(MockHttpServletRequestBuilder requisicao, String token) {
        return requisicao.header(HttpHeaders.AUTHORIZATION, "Bearer " + token).contentType(MediaType.APPLICATION_JSON);
    }

    protected long idDe(String corpo) {
        return ((Number) JsonPath.read(corpo, "$.id")).longValue();
    }
}
