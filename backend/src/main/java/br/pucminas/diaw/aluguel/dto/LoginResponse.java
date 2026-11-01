package br.pucminas.diaw.aluguel.dto;

public record LoginResponse(String token, UsuarioResponse usuario) {
}
