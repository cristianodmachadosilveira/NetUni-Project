import 'package:flutter/material.dart';
import '../services/api.dart';
import '../tema.dart';
import '../widgets/comuns.dart';

/// Tela 13 - Avaliacao (RF11): nota de 1 a 5 estrelas + comentario.
class AvaliacaoScreen extends StatefulWidget {
  final Json contratacao;
  const AvaliacaoScreen({super.key, required this.contratacao});

  @override
  State<AvaliacaoScreen> createState() => _AvaliacaoScreenState();
}

class _AvaliacaoScreenState extends State<AvaliacaoScreen> {
  int _nota = 0;
  final _comentario = TextEditingController();
  bool _enviando = false;

  Future<void> _enviar() async {
    if (_nota < 1 || _nota > 5) {
      avisar(context, 'Escolha de 1 a 5 estrelas');
      return;
    }
    setState(() => _enviando = true);
    try {
      await Api.i.post('/avaliacoes', {
        'contratacaoId': widget.contratacao['id'],
        'nota': _nota,
        'comentario': _comentario.text.trim(),
      });
      if (!mounted) return;
      avisar(context, 'Avaliacao enviada. Obrigado!');
      Navigator.pop(context);
    } on ErroApi catch (e) {
      if (mounted) avisar(context, e.mensagem);
    } finally {
      if (mounted) setState(() => _enviando = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final prestador = widget.contratacao['prestador'] as Json;
    return Scaffold(
      appBar: AppBar(title: const Text('Avaliar servico'), backgroundColor: Cores.verde),
      body: ListView(padding: const EdgeInsets.all(24), children: [
        Center(child: Avatar(alunoId: (prestador['id'] as num).toInt(), tamanho: 72)),
        const SizedBox(height: 12),
        Text('Como foi o servico de ${prestador['nome']}?',
            textAlign: TextAlign.center, style: Theme.of(context).textTheme.titleMedium),
        Text((widget.contratacao['servico'] as Json)['titulo'] as String,
            textAlign: TextAlign.center, style: const TextStyle(color: Colors.black54)),
        const SizedBox(height: 16),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            for (int estrela = 1; estrela <= 5; estrela++)
              IconButton(
                iconSize: 44,
                icon: Icon(estrela <= _nota ? Icons.star : Icons.star_border, color: Colors.amber),
                onPressed: () => setState(() => _nota = estrela),
              ),
          ],
        ),
        const SizedBox(height: 16),
        TextField(
          controller: _comentario,
          maxLines: 4,
          decoration: const InputDecoration(labelText: 'Comentario (opcional)'),
        ),
        const SizedBox(height: 20),
        FilledButton(
          style: FilledButton.styleFrom(backgroundColor: Cores.verde, minimumSize: const Size.fromHeight(48)),
          onPressed: _enviando ? null : _enviar,
          child: const Text('Enviar avaliacao'),
        ),
      ]),
    );
  }
}
