package com.example.studio_many_scheduling_actions_service.application.dto;

import java.util.Map;

/**
 * Contrato generico da mensagem consumida da fila. Serializado como JSON pelo
 * JacksonJsonMessageConverter.
 *
 * Payload:
 * <pre>
 * {
 *   "numero": "5511999999999",
 *   "mensagem": "Ola {{nome}}, seu codigo e {{codigo}}.",
 *   "parametros": { "nome": "Ana", "codigo": "123456" }
 * }
 * </pre>
 * Placeholders no formato {{chave}} sao substituidos pelos valores de
 * "parametros"; a mensagem final enviada ao provedor nao possui dados parciais.
 */
public record NotificacaoWhatsApp(String numero, String mensagem, Map<String, String> parametros) {
}
