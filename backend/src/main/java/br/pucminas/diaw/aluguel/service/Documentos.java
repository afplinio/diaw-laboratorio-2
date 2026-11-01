package br.pucminas.diaw.aluguel.service;

final class Documentos {

    private Documentos() {
    }

    static String somenteDigitos(String valor) {
        return valor.replaceAll("\\D", "");
    }
}
