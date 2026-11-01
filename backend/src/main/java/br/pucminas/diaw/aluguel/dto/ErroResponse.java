package br.pucminas.diaw.aluguel.dto;

import java.util.Map;

public record ErroResponse(String mensagem, Map<String, String> campos) {
}
