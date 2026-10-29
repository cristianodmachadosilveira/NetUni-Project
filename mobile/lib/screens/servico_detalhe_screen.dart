import 'package:flutter/material.dart';
import '../services/api.dart';
import '../tema.dart';
import '../widgets/comuns.dart';
import 'criar_servico_screen.dart';
import 'perfil_screen.dart';
import 'solicitacao_screen.dart';

/// Tela 9 - Detalhes do servico (RF07).
class ServicoDetalheScreen extends StatefulWidget {
  final int id;
  const ServicoDetalheScreen({super.key, required this.id});

  @override
  State<ServicoDetalheScreen> createState() => _ServicoDetalheScreenState();
}

class _ServicoDetalheScreenState extends State<ServicoDetalheScreen> {
  int _versao = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Servico'), backgroundColor: Cores.azul),
      body: Carregador<Json>(
      key: ValueKey(_versao),
      carregar: () async => await Api.i.get('/servicos/${widget.id}') as Json,
      construir: (context, s, _) {
        final autor = s['autor'] as Json;
        final autorId = (autor['id'] as num).toInt();
        final ehMeu = autorId == Api.i.meuId;
        final pedido = s['tipo'] == 'PEDIDO';
        final aberto = s['status'] == 'ABERTO';
        return Theme(
          data: temaNetUni(Cores.doTipo(s['tipo'] as String)),
          child: Builder(
            builder: (context) => ListView(padding: const EdgeInsets.all(20), children: [
              Text(s['titulo'] as String, style: Theme.of(context).textTheme.headlineSmall),
              const SizedBox(height: 12),
              Text((s['descricao'] as String?) ?? 'Sem descricao.'),
              const Divider(height: 32),
              ListTile(
                contentPadding: EdgeInsets.zero,
                leading: const Icon(Icons.payments_outlined),
                title: Text(reais(s['preco'])),
                subtitle: Text('Prazo: ${s['prazoDias']} dias  •  valor pode ser negociado'),
              ),
              ListTile(
                contentPadding: EdgeInsets.zero,
                leading: Avatar(alunoId: autorId, tamanho: 44),
                title: Text(autor['nome'] as String),
                subtitle: Text((autor['curso'] as String?) ?? 'UNA'),
                trailing: const Icon(Icons.chevron_right),
                onTap: () => Navigator.push(
                    context, MaterialPageRoute(builder: (_) => PerfilScreen(alunoId: autorId))),
              ),
              const SizedBox(height: 16),
              if (!aberto)
                const Text('Este servico foi encerrado.', textAlign: TextAlign.center)
              else if (ehMeu) ...[
                OutlinedButton.icon(
                  icon: const Icon(Icons.edit),
                  label: const Text('Editar'),
                  onPressed: () async {
                    await Navigator.push(context, MaterialPageRoute(
                        builder: (_) => CriarServicoScreen(tipoInicial: s['tipo'] as String, existente: s)));
                    if (!mounted) return;
                    setState(() => _versao++);
                  },
                ),
                const SizedBox(height: 8),
                OutlinedButton.icon(
                  icon: const Icon(Icons.close),
                  label: const Text('Encerrar servico'),
                  onPressed: () async {
                    try {
                      await Api.i.delete('/servicos/${s['id']}');
                      if (context.mounted) Navigator.pop(context);
                    } on ErroApi catch (e) {
                      if (context.mounted) avisar(context, e.mensagem);
                    }
                  },
                ),
              ] else
                FilledButton.icon(
                  style: FilledButton.styleFrom(minimumSize: const Size.fromHeight(48)),
                  icon: const Icon(Icons.send),
                  label: Text(pedido ? 'Quero fazer este servico' : 'Solicitar servico'),
                  onPressed: () => Navigator.push(
                      context, MaterialPageRoute(builder: (_) => SolicitacaoScreen(servico: s))),
                ),
            ]),
          ),
        );
      },
      ),
    );
  }
}
