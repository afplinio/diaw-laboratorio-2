package br.pucminas.diaw.aluguel.security;

import br.pucminas.diaw.aluguel.model.Papel;

/** Identidade do usuário da requisição atual, disponível nos controllers. */
public record UsuarioAutenticado(Long id, String login, Papel papel) {

    public boolean isAgente() {
        return papel == Papel.AGENTE;
    }
}
