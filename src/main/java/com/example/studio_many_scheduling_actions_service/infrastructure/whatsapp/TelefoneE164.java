package com.example.studio_many_scheduling_actions_service.infrastructure.whatsapp;

/**
 * Normaliza telefones para o formato E.164 exigido pelo Twilio (ex.:
 * +5511944407845).
 * Considera numeros brasileiros armazenados sem '+' e, quando sem codigo do
 * pais
 * (ate 11 digitos), assume o codigo do Brasil (55).
 */
public final class TelefoneE164 {

    private static final String CODIGO_BRASIL = "55";

    private TelefoneE164() {
    }

    public static String normalizar(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            throw new IllegalArgumentException("Telefone e obrigatorio");
        }

        String limpo = telefone.replaceAll("\\D", "");
        if (limpo.startsWith("00")) {
            limpo = limpo.substring(2);
        }
        if (!limpo.startsWith(CODIGO_BRASIL) && limpo.length() <= 11) {
            limpo = CODIGO_BRASIL + limpo;
        }
        return "+" + limpo;
    }
}