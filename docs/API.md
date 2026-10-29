# API REST do NetUni

Base: `http://localhost:8080`. Todas as rotas, menos cadastro e login, exigem o cabeçalho
`Authorization: Bearer <token>` (o token vem do `POST /login`). Erros sempre voltam como
`{"status": 400, "erro": "mensagem"}`.

| Método | Rota | O que faz | RF |
|---|---|---|---|
| POST | `/usuarios` | Cadastro (público) | RF01 |
| POST | `/login` | Login, devolve token (público) | RF02 |
| GET | `/usuarios/me` | Meu perfil | RF03 |
| GET | `/usuarios/{id}` | Perfil de um aluno (e-mail só aparece para o próprio) | RF03 |
| PUT | `/usuarios/{id}` | Atualiza perfil e habilidades (só o próprio) | RF03 |
| GET | `/usuarios/{id}/servicos` | Serviços publicados pelo aluno | RF05 |
| GET | `/usuarios/{id}/avaliacoes` | Avaliações recebidas + média | RF11 |
| GET | `/usuarios/{id}/portfolio` | Itens do portfólio | RF12 |
| POST | `/servicos` | Publica pedido ou oferta | RF04 |
| GET | `/servicos?tipo=OFERTA&busca=calculo&comunidadeId=2` | Busca (filtros opcionais; sem `comunidadeId` é a lista geral) | RF06 / RF13 |
| GET | `/servicos/{id}` | Detalhes | RF07 |
| PUT | `/servicos/{id}` | Edita (só o autor) | RF05 |
| DELETE | `/servicos/{id}` | Encerra (sai da busca) | RF05 |
| POST | `/contratacoes` | Solicita um serviço | RF09 |
| GET | `/contratacoes` | Minhas contratações | RF10 |
| GET | `/contratacoes/{id}` | Detalhe + `acoesPermitidas` | RF10 |
| PUT | `/contratacoes/{id}` | Registra valor e forma de pagamento combinados | RF10 |
| PUT | `/contratacoes/{id}/status` | Muda status (`ACEITA`, `EM_ANDAMENTO`, `CONCLUIDA`, `CANCELADA`) | RF10 |
| GET | `/contratacoes/{id}/mensagens` | Lista mensagens do chat | RF08 |
| POST | `/contratacoes/{id}/mensagens` | Envia mensagem | RF08 |
| POST | `/avaliacoes` | Avalia contratação concluída (nota 1 a 5) | RF11 |
| POST | `/portfolio` | Adiciona projeto ao portfólio | RF12 |
| DELETE | `/portfolio/{id}` | Remove item do portfólio | RF12 |
| GET | `/comunidades` | Lista comunidades (com quantos anúncios abertos cada uma tem) | RF13 |
| POST | `/comunidades` | Cria comunidade (nome, descrição, ícone, cor) | RF13 |
| GET | `/comunidades/{id}` | Detalhes da comunidade | RF13 |

## Estados da contratação

```
SOLICITADA --(autor do serviço aceita)--> ACEITA --(prestador inicia*)--> EM_ANDAMENTO --(contratante confirma)--> CONCLUIDA
     \______________________ qualquer participante pode CANCELAR ______________________/
* só depois de registrar valor e forma de pagamento
```

Ao chegar em CONCLUIDA, o serviço entra automaticamente no portfólio do prestador.

**Pagamento:** a API só registra `valorNegociado` e `formaPagamento`. Não existe carteira,
checkout, transferência nem comissão.

## Comunidades

Uma comunidade funciona como etiqueta. No `POST /servicos` (ou `PUT /servicos/{id}`), o campo opcional
`comunidadeId` liga o anúncio a ela. `GET /servicos?comunidadeId=2` mostra só os anúncios daquela comunidade,
e `GET /servicos` sem o filtro continua mostrando todos, com ou sem etiqueta.
