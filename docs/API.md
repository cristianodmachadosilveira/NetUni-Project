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
| GET | `/servicos?tipo=OFERTA&busca=calculo` | Busca (filtros opcionais) | RF06 |
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
| GET | `/comunidades` | Lista comunidades (com `souMembro`) | RF13 |
| POST | `/comunidades` | Cria comunidade (quem cria já entra) | RF13 |
| GET | `/comunidades/{id}` | Detalhes da comunidade | RF13 |
| POST | `/comunidades/{id}/membros` | Entrar na comunidade | RF13 |
| DELETE | `/comunidades/{id}/membros` | Sair da comunidade (o criador não sai) | RF13 |
| POST | `/midias` | Upload de foto ou vídeo (multipart, campo `arquivo`, até 30 MB) | RF14 |
| GET | `/midias/{arquivo}` | Baixa a mídia enviada (público, sem token) | RF14 |
| POST | `/posts` | Publica post `TEXTO`, `IMAGEM` ou `VIDEO` (só membro posta na comunidade) | RF14 |
| DELETE | `/posts/{id}` | Apaga post (só o autor) | RF14 |
| GET | `/feed` | Feed "Para você", ranqueado pelo algoritmo | RF15 |
| GET | `/feed?comunidadeId=2` | Feed de uma comunidade, do mais novo ao mais antigo | RF15 |
| POST | `/posts/{id}/salvo` | Salvar / tirar dos salvos (alterna). Não existem curtidas | RF15 |
| GET | `/usuarios/me/salvos` | Posts que eu salvei (aba Salvos do perfil) | RF15 |
| GET | `/posts/{id}/comentarios` | Lista comentários | RF15 |
| POST | `/posts/{id}/comentarios` | Comenta | RF15 |
| GET | `/usuarios/{id}/posts` | Posts de um aluno (grade do perfil) | RF15 |

## Estados da contratação

```
SOLICITADA --(autor do serviço aceita)--> ACEITA --(prestador inicia*)--> EM_ANDAMENTO --(contratante confirma)--> CONCLUIDA
     \______________________ qualquer participante pode CANCELAR ______________________/
* só depois de registrar valor e forma de pagamento
```

Ao chegar em CONCLUIDA, o serviço entra automaticamente no portfólio do prestador.

**Pagamento:** a API só registra `valorNegociado` e `formaPagamento`. Não existe carteira,
checkout, transferência nem comissão.

## Algoritmo do feed "Para você"

Para cada um dos 100 posts mais recentes:

```
pontos = 1 + salvos x 2 + comentarios x 3 (+ 10 se o post e de uma comunidade minha)
pontuacao = pontos / (1 + horas desde o post / 12)
```

Depois ordena da maior para a menor pontuação. Um post com 12 horas vale metade de um igual recém-publicado.
As mídias ficam na pasta `./data/midias`. No plano gratuito do Render ela é apagada quando o servidor reinicia,
igual ao banco H2. As mídias de demonstração ficam em `src/main/resources/static/demo` e nunca somem.
