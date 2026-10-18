package com.example.studio_many_scheduling_actions_service.domain.model;

/**
 * Mascara numeros de telefone para uso em logs, preservando apenas o inicio e
 * o fim. Evita expor dados sensiveis nos logs.
 */
public final class MascaraTelefone {

    private MascaraTelefone() {
    }

    public static String mascarar(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return "****";
        }

        String limpo = telefone.replaceAll("\\D", "");
        if (limpo.length() < 6) {
            return "****";
        }

        return limpo.substring(0, 2)
                + "*".repeat(limpo.length() - 6)
                + limpo.substring(limpo.length() - 4);
    }
}
