# Algoritmos e Programação — onde está cada conceito

Resposta para: "Mostre no código onde estão as estruturas de decisão e repetição, vetores e funções,
e como a lógica processa as entradas do usuário para gerar os resultados."

Caminho base: `backend/src/main/java/br/una/netuni/`

| Conceito | Onde | O que faz com a entrada do usuário |
|---|---|---|
| **Entrada** | DTOs em `dto/` (ex.: `CadastroRequest`, `ServicoRequest`, `AvaliacaoRequest`) e formulários do app | Recebem o JSON enviado pelas telas |
| **Validação** | Anotações `@NotBlank`, `@Email`, `@Size`, `@PositiveOrZero` nos DTOs + `if` nos services | Rejeita dados inválidos com 400 |
| **Tipos de dados** | `String`, `Integer`, `BigDecimal` (dinheiro), `LocalDateTime`, `boolean`, `enum` em `model/` | Cada campo com o tipo certo |
| **if/else** | `service/ContratacaoService.java` → `validarTransicao` (linha 161) | Decide se a mudança de status é permitida e por quem |
| **if/else** | `service/UsuarioService.java` → `login` (linha 69) | Valida e-mail e senha |
| **if** | `service/AvaliacaoService.java` linha 35 | Confere se a nota está entre 1 e 5 |
| **for** | `service/ServicoService.java` → `buscar`, laço na linha 78 | Percorre os serviços e filtra por tipo e texto digitado |
| **for** | `service/ContratacaoService.java` linha 73 | Procura solicitação duplicada |
| **for** | `service/UsuarioService.java` → `montarHabilidades`, laço na linha 124 | Percorre as habilidades digitadas, ignora vazias/repetidas |
| **for (acumulador)** | `service/AvaliacaoService.java` linha 70 + `calcularMedia` (linha 80) | Soma as notas e calcula a média |
| **for com índice** | `service/SenhaService.java` linha 63 | Compara o hash da senha byte a byte |
| **for + if (ranking)** | `service/PostService.java` → `feed`, laço na linha 123, e `calcularPontuacao` (linha 148) | Percorre os posts, dá uma nota para cada um (salvos, comentários, comunidade, idade) |
| **Ordenação** | `service/PostService.java` linha 134 (`itens.sort`) | Coloca o feed "Para você" da maior para a menor pontuação |
| **for (busca em lista)** | `service/ComunidadeService.java` → `ehMembro` (linha 102) | Percorre os membros para saber se o aluno participa |
| **while** | `service/ComunidadeService.java` linha 89 | Percorre os membros com um `Iterator` e remove quem saiu |
| **if/else (alternar)** | `service/PostService.java` → `alternarSalvo` | Se já salvou, tira dos salvos; senão, salva |
| **Vetores** | `byte[] sal`, `String[] partes` em `SenhaService`; `String[] habilidades` em `config/DadosDemonstracao` | |
| **Listas** | `List<ServicoResponse>`, `List<Mensagem>`, `List<Avaliacao>`, `Set<Habilidade>` | Resultados de busca, chat, avaliações |
| **Métodos** | `cadastrar`, `login`, `criar`, `buscar`, `solicitar`, `alterarStatus`, `avaliar`, `calcularMedia` | Cada regra em um método com nome claro |
| **Tratamento de erros** | `exception/RegraDeNegocioException` + `exception/TratadorDeErros` | Erro vira JSON com código HTTP (400, 401, 403, 404, 409) |
| **Saída** | DTOs `*Response` (JSON) e telas do app | O que o aluno vê |

No app (Dart): `for` em `perfil_screen.dart` (separa habilidades digitadas com vírgula),
`portfolio_screen.dart` (`_estrelas`, monta ★★★☆☆) e `avaliacao_screen.dart` (desenha as 5 estrelas).
