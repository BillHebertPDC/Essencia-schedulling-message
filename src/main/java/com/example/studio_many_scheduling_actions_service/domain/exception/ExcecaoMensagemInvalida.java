package com.example.studio_many_scheduling_actions_service.domain.exception;

/**
 * Mensagem de comando invalida (payload incompleto, placeholder sem valor).
 * Originada no adaptador de entrada ou no caso de uso.
 */
public class ExcecaoMensagemInvalida extends RuntimeException {

    public ExcecaoMensagemInvalida(String motivo) {
        super(motivo);
    }
}
