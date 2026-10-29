import 'package:flutter/material.dart';
import '../services/api.dart';
import '../tema.dart';
import '../widgets/comuns.dart';

/// Tela 14 - Portfolio (RF12): servicos concluidos, projetos adicionados e avaliacoes recebidas.
class PortfolioScreen extends StatefulWidget {
  final int alunoId;
  const PortfolioScreen({super.key, required this.alunoId});

  @override
  State<PortfolioScreen> createState() => _PortfolioScreenState();
}

class _PortfolioScreenState extends State<PortfolioScreen> {
  int _versao = 0;

  bool get _ehMeu => widget.alunoId == Api.i.meuId;

  Future<List<dynamic>> _carregar() async {
    final itens = await Api.i.get('/usuarios/${widget.alunoId}/portfolio');
    final avaliacoes = await Api.i.get('/usuarios/${widget.alunoId}/avaliacoes');
    return [itens, avaliacoes];
  }

  Future<void> _adicionar() async {
    final titulo = TextEditingController();
    final descricao = TextEditingController();
    final link = TextEditingController();
    final confirmou = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Adicionar projeto'),
        content: SingleChildScrollView(
          child: Column(mainAxisSize: MainAxisSize.min, children: [
            TextField(controller: titulo, decoration: const InputDecoration(labelText: 'Titulo')),
            const SizedBox(height: 8),
            TextField(controller: descricao, maxLines: 3, decoration: const InputDecoration(labelText: 'Descricao')),
            const SizedBox(height: 8),
            TextField(controller: link, decoration: const InputDecoration(labelText: 'Link do projeto (opcional)')),
          ]),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx, false), child: const Text('Cancelar')),
          FilledButton(onPressed: () => Navigator.pop(ctx, true), child: const Text('Adicionar')),
        ],
      ),
    );
    if (confirmou != true) return;
    if (titulo.text.trim().isEmpty) {
      if (mounted) avisar(context, 'Informe o titulo');
      return;
    }
    try {
      await Api.i.post('/portfolio', {
        'titulo': titulo.text.trim(),
        'descricao': descricao.text.trim(),
        'linkProjeto': link.text.trim(),
      });
      setState(() => _versao++);
    } on ErroApi catch (e) {
      if (mounted) avisar(context, e.mensagem);
    }
  }

  Future<void> _remover(int id) async {
    try {
      await Api.i.delete('/portfolio/$id');
      setState(() => _versao++);
    } on ErroApi catch (e) {
      if (mounted) avisar(context, e.mensagem);
    }
  }

  String _estrelas(int nota) {
    String texto = '';
    for (int i = 1; i <= 5; i++) {
      texto += i <= nota ? '★' : '☆';
    }
    return texto;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Portfolio'), backgroundColor: Cores.verde),
      floatingActionButton: _ehMeu
          ? FloatingActionButton(
              backgroundColor: Cores.verde, foregroundColor: Colors.white,
              onPressed: _adicionar, child: const Icon(Icons.add))
          : null,
      body: Carregador<List<dynamic>>(
        key: ValueKey(_versao),
        carregar: _carregar,
        construir: (context, dados, _) {
          final itens = dados[0] as List<dynamic>;
          final resumo = dados[1] as Json;
          final avaliacoes = resumo['avaliacoes'] as List<dynamic>;
          return ListView(padding: const EdgeInsets.fromLTRB(16, 16, 16, 90), children: [
            Text('Trabalhos', style: Theme.of(context).textTheme.titleMedium),
            if (itens.isEmpty)
              const Padding(padding: EdgeInsets.all(8),
                  child: Text('Nenhum trabalho ainda. Servicos concluidos aparecem aqui.')),
            for (final item in itens)
              Card(
                child: ListTile(
                  leading: Icon((item as Json)['contratacaoId'] != null ? Icons.verified : Icons.folder,
                      color: Cores.verde),
                  title: Text(item['titulo'] as String),
                  subtitle: Text([
                    if (item['descricao'] != null && (item['descricao'] as String).isNotEmpty) item['descricao'],
                    if (item['linkProjeto'] != null && (item['linkProjeto'] as String).isNotEmpty) item['linkProjeto'],
                  ].join('\n')),
                  trailing: _ehMeu
                      ? IconButton(icon: const Icon(Icons.delete_outline),
                          onPressed: () => _remover((item['id'] as num).toInt()))
                      : null,
                ),
              ),
            const SizedBox(height: 16),
            Text('Avaliacoes  •  media ${resumo['media']} (${resumo['total']})',
                style: Theme.of(context).textTheme.titleMedium),
            if (avaliacoes.isEmpty) const Padding(padding: EdgeInsets.all(8), child: Text('Sem avaliacoes ainda.')),
            for (final a in avaliacoes)
              Card(
                child: ListTile(
                  title: Text((a as Json)['servicoTitulo'] as String),
                  subtitle: Text('${_estrelas((a['nota'] as num).toInt())}\n'
                      '${a['comentario'] ?? ''}\n— ${a['avaliadorNome']}'),
                  isThreeLine: true,
                ),
              ),
          ]);
        },
      ),
    );
  }
}
