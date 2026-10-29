# Testes

Rodar: `cd backend && mvn test`. Os testes sobem a API com banco H2 em memória e desfazem tudo ao final de cada teste.

| Área | Caso | Esperado | Teste |
|---|---|---|---|
| Cadastro | válido | 201, e-mail em minúsculas, sem senha na resposta | `UsuarioApiTest.cadastroValido` |
| Cadastro | e-mail inválido | 400 | `cadastroComEmailInvalido` |
| Cadastro | campos vazios | 400 | `cadastroComCamposVazios` |
| Cadastro | senha curta | 400 | `cadastroComSenhaCurta` |
| Cadastro | e-mail duplicado | 409 | `cadastroComEmailDuplicado` |
| Login | usuário correto | 200 + token | `loginCorreto` |
| Login | senha incorreta | 401 | `loginComSenhaIncorreta` |
| Login | usuário inexistente | 401 | `loginComUsuarioInexistente` |
| Segurança | rota sem token | 401 | `rotaProtegidaSemTokenDevolve401` |
| Perfil | habilidades repetidas/vazias | só 2 salvas | `atualizarPerfilComHabilidades` |
| Perfil | alterar perfil de outro | 403 | `naoPodeAlterarPerfilDeOutroAluno` |
| Perfil | e-mail de outro aluno | não aparece | `perfilDeOutroAlunoNaoMostraEmail` |
| Serviço | válido | 201 | `ServicoApiTest.servicoValido` |
| Serviço | título vazio | 400 | `servicoComTituloVazio` |
| Serviço | preço negativo / não numérico | 400 | `servicoComPrecoNegativo`, `servicoComPrecoQueNaoENumero` |
| Serviço | prazo zero | 400 | `servicoComPrazoZero` |
| Serviço | busca por tipo e texto | 1 resultado | `buscaFiltraPorTipoETexto` |
| Serviço | encerrado sai da busca | lista vazia | `servicoEncerradoSaiDaBusca` |
| Serviço | outro aluno edita | 403 | `outroAlunoNaoEditaServico` |
| Serviço | inexistente | 404 | `visualizarServicoInexistente` |
| Contratação | serviço existente | 201 SOLICITADA | `ContratacaoApiTest.solicitarServicoExistente` |
| Contratação | serviço inexistente | 404 | `solicitarServicoInexistente` |
| Contratação | duplicada | 409 | `solicitarContratacaoDuplicada` |
| Contratação | próprio serviço | 400 | `naoPodeSolicitarOProprioServico` |
| Contratação | fluxo completo | CONCLUIDA, ação AVALIAR | `fluxoCompletoAteConcluir` |
| Contratação | status inválido (pular etapa / voltar / inexistente) | 409 / 409 / 400 | `statusInvalidoPularEtapa`, `statusInvalidoVoltarParaSolicitada`, `statusQueNaoExiste` |
| Contratação | iniciar sem forma de pagamento | 400 | `naoIniciaSemFormaDePagamento` |
| Contratação | terceiro acessa | 403 | `terceiroNaoVeContratacao` |
| Chat | mensagem válida e vazia | 201 / 400 | `conversarNaContratacao` |
| Avaliação | nota válida | 201, média 5.0 | `AvaliacaoApiTest.notaValida` |
| Avaliação | nota abaixo (0) | 400 | `notaAbaixoDoPermitido` |
| Avaliação | nota acima (6) | 400 | `notaAcimaDoPermitido` |
| Avaliação | duplicada | 409 | `avaliacaoDuplicada` |
| Avaliação | prestador se avalia | 403 | `prestadorNaoAvaliaASiMesmo` |
| Portfólio | serviço concluído entra | 1 item | `servicoConcluidoEntraNoPortfolio` |
| Média | cálculo | 0.0 / 4.5 / 4.3 | `calculoDaMedia` |
| Senha | hash e conferência | sem texto puro | `SenhaServiceTest` |
