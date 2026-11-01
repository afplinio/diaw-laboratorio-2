package br.pucminas.diaw.aluguel.dto;

import br.pucminas.diaw.aluguel.model.Agente;
import br.pucminas.diaw.aluguel.model.Papel;
import br.pucminas.diaw.aluguel.model.TipoAgente;
import br.pucminas.diaw.aluguel.model.Usuario;

public record UsuarioResponse(Long id, String login, String nome, Papel papel, TipoAgente tipoAgente) {

    public static UsuarioResponse de(Usuario usuario) {
        TipoAgente tipo = usuario instanceof Agente agente ? agente.getTipo() : null;
        return new UsuarioResponse(usuario.getId(), usuario.getLogin(), usuario.getNome(), usuario.getPapel(), tipo);
    }
}
