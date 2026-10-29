import 'package:flutter/material.dart';
import '../services/api.dart';
import '../tema.dart';
import '../widgets/comuns.dart';
import 'contratacao_screen.dart';

/// Lista das contratacoes do aluno (como contratante ou prestador). Entrada para as Telas 11 e 12.
class ContratacoesScreen extends StatefulWidget {
  const ContratacoesScreen({super.key});

  @override
  State<ContratacoesScreen> createState() => _ContratacoesScreenState();
}

class _ContratacoesScreenState extends State<ContratacoesScreen> {
  int _versao = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Minhas contratacoes'), backgroundColor: Cores.azul),
      body: Carregador<List<dynamic>>(
        key: ValueKey(_versao),
        carregar: () async => await Api.i.get('/contratacoes') as List<dynamic>,
        construir: (context, lista, recarregar) {
          if (lista.isEmpty) {
            return const Center(child: Text('Voce ainda nao tem contratacoes.'));
          }
          return RefreshIndicator(
            onRefresh: recarregar,
            child: ListView.builder(
              itemCount: lista.length,
              itemBuilder: (_, i) {
                final c = lista[i] as Json;
                final souContratante = ((c['contratante'] as Json)['id'] as num).toInt() == Api.i.meuId;
                final outro = (souContratante ? c['prestador'] : c['contratante']) as Json;
                return Card(
                  margin: const EdgeInsets.fromLTRB(12, 8, 12, 0),
                  child: ListTile(
                    leading: Avatar(alunoId: (outro['id'] as num).toInt(), tamanho: 40),
                    title: Text((c['servico'] as Json)['titulo'] as String),
                    subtitle: Text('${souContratante ? 'Prestador' : 'Contratante'}: ${outro['nome']}\n'
                        '${nomesStatus[c['status']] ?? c['status']}'),
                    isThreeLine: true,
                    trailing: const Icon(Icons.chevron_right),
                    onTap: () async {
                      await Navigator.push(context, MaterialPageRoute(
                          builder: (_) => ContratacaoScreen(id: (c['id'] as num).toInt())));
                      if (!mounted) return;
                      setState(() => _versao++);
                    },
                  ),
                );
              },
            ),
          );
        },
      ),
    );
  }
}
