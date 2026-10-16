# studio-many-scheduling-actions-service

Microservico worker (sem HTTP) que consome comandos de envio de WhatsApp do
RabbitMQ: le o **numero** e os **parametros** da mensagem, renderiza o texto e
entrega via provedor configurado (WAHA, Twilio ou NoOp).

## Como funciona

1. O servico publica no `exchange_notificacao` (routing key `notificacao.whatsapp`).
2. Este servico consome `fila_notificacao_whatsapp` e envia via provedor.
3. Falhas (payload invalido ou erro de envio apos 3 tentativas) vao para a
   DLQ `fila_notificacao_whatsapp_dlq` — nenhum consumo reenfileira para sempre.

### Contrato da mensagem

```json
{
  "numero": "5511999999999",
  "mensagem": "Ola {{nome}}, seu codigo e {{codigo}}.",
  "parametros": { "nome": "Ana", "codigo": "123456" }
}
```

- `numero`: obrigatorio (somente digitos de destino).
- `mensagem`: obrigatoria; placeholders `{{chave}}` sao obrigatoriamente
  resolvidos com `parametros` (parametro ausente = mensagem rejeitada).
- `parametros`: opcionais (nulos = sem placeholders); chaves nao utilizadas
  sao aceitas.

Comando local de teste (rabbitmqadmin):

```
rabbitmqadmin -u <usuario> -p <senha> publish exchange=exchange_notificacao \
  routing_key=notificacao.whatsapp \
  payload='{"numero":"5511999999999","mensagem":"Ola {{nome}}!","parametros":{"nome":"Ana"}}'
```

## Configuracao

Copie `docker/.env.example` para `docker/.env` e preencha. Variaveis usadas
pelo servico:

| Variavel                        | Uso                                                  |
|---------------------------------|------------------------------------------------------|
| `RABBITMQ_HOST` / `RABBITMQ_PORT` | Endereco do broker (padrao `localhost:5672`)      |
| `RABBITMQ_USERNAME` / `RABBITMQ_PASSWORD` | Credenciais obrigatorias (sem padrao inseguro) |
| `RABBITMQ_SSL`                  | `true` para AMQPS/TLS em producao                   |
| `WHATSAPP_PROVIDER`             | `waha` (padrao), `twilio` ou `noop`                 |
| `WAHA_API_URL` / `WAHA_API_KEY` / `WAHA_SESSION` | Credenciais e sessao do WAHA        |
| `TWILIO_*`                      | Credenciais do Twilio (quando `twilio`)            |

## Seguranca

- As credenciais do RabbitMQ sao obrigatorias: a aplicacao nao sobe com
  `admin/admin` nem qualquer fallback.
- Use um **usuario exclusivo por servico**. Comandos uteis (broker em execucao):

```
rabbitmqctl add_user produtor_notificacao '<senha>'
rabbitmqctl set_permissions -p / produtor_notificacao '^exchange_notificacao$' '^notificacao\.whatsapp$' '^$'
```

- Em producao, exponha o broker apenas em rede privada e mantenha
  `RABBITMQ_SSL=true`; no docker-compose de desenvolvimento as portas ficam
  vinculadas a `127.0.0.1`.
- Segredos ficam apenas em `docker/.env` (ignorado pelo git, ver
  `docker/.env.example`).

## Build e teste

```
./mvnw test
```
