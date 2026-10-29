import 'package:flutter/material.dart';
import '../services/api.dart';
import '../tema.dart';
import '../widgets/comuns.dart';
import 'portfolio_screen.dart';
import 'servico_detalhe_screen.dart';

/// Tela 5 - Perfil (RF03). Mostra o proprio perfil (editavel) ou o de outro aluno.
class PerfilScreen extends StatefulWidget {
  final int alunoId;
  const PerfilScreen({super.key, required this.alunoId});

  @override
  State<PerfilScreen> createState() => _PerfilScreenState();
}

class _PerfilScreenState extends State<PerfilScreen> {
  int _versao = 0;

  bool get _ehMeu => widget.alunoId == Api.i.meuId;

  Future<List<dynamic>> _carregar() async {
    final perfil = await Api.i.get('/usuarios/${widget.alunoId}');
    final servicos = await Api.i.get('/usuarios/${widget.alunoId}/servicos');
    final avaliacoes = await Api.i.get('/usuarios/${widget.alunoId}/avaliacoes');
    return [perfil, servicos, avaliacoes];
  }

  Future<void> _editar(Json perfil) async {
    final nome = TextEditingController(text: perfil['nome'] as String?);
    final curso = TextEditingController(text: perfil['curso'] as String?);
    final semestre = TextEditingController(text: perfil['semestre']?.toString() ?? '');
    final descricao = TextEditingController(text: perfil['descricao'] as String?);
    final habilidades = TextEditingController(
        text: ((perfil['habilidades'] as List<dynamic>?) ?? []).join(', '));

    final confirmou = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Editar perfil'),
        content: SingleChildScrollView(
          child: Column(mainAxisSize: MainAxisSize.min, children: [
            TextField(controller: nome, decoration: const InputDecoration(labelText: 'Nome')),
            const SizedBox(height: 8),
            TextField(controller: curso, decoration: const InputDecoration(labelText: 'Curso')),
            const SizedBox(height: 8),
            TextField(controller: semestre, keyboardType: TextInputType.number,
                decoration: const InputDecoration(labelText: 'Semestre')),
            const SizedBox(height: 8),
            TextField(controller: descricao, maxLines: 3, decoration: const InputDecoration(labelText: 'Sobre mim')),
            const SizedBox(height: 8),
            TextField(controller: habilidades,
                decoration: const InputDecoration(labelText: 'Habilidades', helperText: 'Separe por virgula')),
          ]),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx, false), child: const Text('Cancelar')),
          FilledButton(onPressed: () => Navigator.pop(ctx, true), child: const Text('Salvar')),
        ],
      ),
    );
    if (confirmou != true) return;

    // Transforma "Java, Design, Excel" em ["Java", "Design", "Excel"].
    final lista = <String>[];
    for (final parte in habilidades.text.split(',')) {
      if (parte.trim().isNotEmpty) lista.add(parte.trim());
    }
    try {
      Api.i.usuario = await Api.i.put('/usuarios/${widget.alunoId}', {
        'nome': nome.text.trim(),
        'curso': curso.text.trim(),
        'semestre': int.tryParse(semestre.text),
        'descricao': descricao.text.trim(),
        'habilidades': lista,
      }) as Json;
      setState(() => _versao++);
    } on ErroApi catch (e) {
      if (mounted) avisar(context, e.mensagem);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(_ehMeu ? 'Meu perfil' : 'Perfil'), backgroundColor: Cores.azul),
      body: Carregador<List<dynamic>>(
        key: ValueKey(_versao),
        carregar: _carregar,
        construir: (context, dados, _) {
          final perfil = dados[0] as Json;
          final servicos = dados[1] as List<dynamic>;
          final resumo = dados[2] as Json;
          final habilidades = (perfil['habilidades'] as List<dynamic>).cast<String>();
          final semestre = perfil['semestre'];
          return ListView(padding: const EdgeInsets.all(16), children: [
            Center(child: Avatar(alunoId: widget.alunoId, tamanho: 84)),
            const SizedBox(height: 12),
            Text(perfil['nome'] as String, textAlign: TextAlign.center,
                style: Theme.of(context).textTheme.headlineSmall),
            Text('${perfil['curso'] ?? 'Curso nao informado'}${semestre != null ? ' • $semestre o semestre' : ''}',
                textAlign: TextAlign.center),
            if (perfil['email'] != null)
              Text(perfil['email'] as String, textAlign: TextAlign.center, style: const TextStyle(color: Colors.black54)),
            const SizedBox(height: 8),
            Row(mainAxisAlignment: MainAxisAlignment.center, children: [
              const Icon(Icons.star, color: Colors.amber),
              Text(' ${resumo['media']}  (${resumo['total']} avaliacoes)'),
            ]),
            if (perfil['descricao'] != null)
              Padding(padding: const EdgeInsets.only(top: 12), child: Text(perfil['descricao'] as String)),
            const SizedBox(height: 12),
            const Text('Habilidades', style: TextStyle(fontWeight: FontWeight.bold)),
            const SizedBox(height: 4),
            Wrap(spacing: 6, runSpacing: 4, children: [
              if (habilidades.isEmpty) const Text('Nenhuma habilidade informada.'),
              for (final h in habilidades) Chip(label: Text(h)),
            ]),
            const SizedBox(height: 12),
            Row(children: [
              if (_ehMeu)
                Expanded(child: OutlinedButton.icon(
                    onPressed: () => _editar(perfil), icon: const Icon(Icons.edit), label: const Text('Editar'))),
              if (_ehMeu) const SizedBox(width: 8),
              Expanded(child: FilledButton.icon(
                style: FilledButton.styleFrom(backgroundColor: Cores.verde),
                onPressed: () => Navigator.push(context,
                    MaterialPageRoute(builder: (_) => PortfolioScreen(alunoId: widget.alunoId))),
                icon: const Icon(Icons.workspace_premium),
                label: const Text('Portfolio'),
              )),
            ]),
            const Divider(height: 32),
            Text(_ehMeu ? 'Meus pedidos e ofertas' : 'Pedidos e ofertas',
                style: const TextStyle(fontWeight: FontWeight.bold)),
            if (servicos.isEmpty) const Padding(padding: EdgeInsets.all(8), child: Text('Nada publicado.')),
            for (final s in servicos)
              ListTile(
                contentPadding: EdgeInsets.zero,
                leading: Icon(Icons.circle, size: 14, color: Cores.doTipo((s as Json)['tipo'] as String)),
                title: Text(s['titulo'] as String),
                subtitle: Text('${s['tipo']} • ${s['status']} • ${reais(s['preco'])}'),
                onTap: () async {
                  await Navigator.push(context,
                      MaterialPageRoute(builder: (_) => ServicoDetalheScreen(id: (s['id'] as num).toInt())));
                  setState(() => _versao++);
                },
              ),
          ]);
        },
      ),
    );
  }
}
