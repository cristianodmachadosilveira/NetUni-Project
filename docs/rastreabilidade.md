# Rastreabilidade completa — RF09 Solicitação de serviço

Resposta para a banca: "Mostre a rastreabilidade de uma funcionalidade, do requisito até o código".

| Etapa | Onde está |
|---|---|
| **Requisito** | RF09 — o aluno solicita um serviço aberto publicado por outro aluno |
| **Caso de uso** | UC09 Solicitar serviço (ator: Aluno da UNA). Pré: serviço ABERTO e de outro aluno. Pós: contratação SOLICITADA |
| **Classe / modelo** | [`model/Contratacao.java`](../backend/src/main/java/br/una/netuni/model/Contratacao.java) + enum [`StatusContratacao`](../backend/src/main/java/br/una/netuni/model/StatusContratacao.java) |
| **Banco de dados** | Tabela `contratacao` com FKs `servico_id`, `contratante_id`, `prestador_id`, `solicitante_id` |
| **Backlog** | T11 — Solicitação de serviço (cria contratação) |
| **Endpoint** | `POST /contratacoes` em [`ContratacaoController.solicitar`](../backend/src/main/java/br/una/netuni/controller/ContratacaoController.java) |
| **Tela** | Detalhes do serviço → botão "Solicitar" → [`solicitacao_screen.dart`](../mobile/lib/screens/solicitacao_screen.dart) → abre a Contratação |
| **Código (regra)** | [`ContratacaoService.solicitar`](../backend/src/main/java/br/una/netuni/service/ContratacaoService.java) (linha 60) |
| **Teste** | [`ContratacaoApiTest`](../backend/src/test/java/br/una/netuni/ContratacaoApiTest.java): `solicitarServicoExistente`, `solicitarServicoInexistente`, `solicitarContratacaoDuplicada`, `naoPodeSolicitarOProprioServico`, `solicitarPedidoInverteOsPapeis` |

## O que o método `solicitar` faz, passo a passo

1. **Entrada:** id do aluno logado (vem do token) e `servicoId` (vem do JSON).
2. Busca o serviço; **se** não existir → 404.
3. **Se** o serviço estiver ENCERRADO → 409.
4. **Se** o aluno for o autor → 400.
5. **Para cada** contratação já existente desse serviço (`for`), **se** for do mesmo aluno e estiver ativa → 409 (duplicada).
6. **Se** o serviço é OFERTA, quem solicita é o contratante; **senão** (PEDIDO), quem solicita é o prestador.
7. Grava com status SOLICITADA e registra um aviso no chat.
8. **Saída:** JSON da contratação com `acoesPermitidas` → o app mostra os botões certos.
