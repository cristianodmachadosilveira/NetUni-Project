import 'package:flutter/material.dart';
import '../services/api.dart';
import '../tema.dart';
import '../widgets/comuns.dart';
import 'contratacao_screen.dart';

/// Tela 10 - Solicitacao (RF09): confirma e envia a solicitacao do servico.
class SolicitacaoScreen extends StatefulWidget {
  final Json servico;
  const SolicitacaoScreen({super.key, required this.servico});

  @override
  State<SolicitacaoScreen> createState() => _SolicitacaoScreenState();
}

class _SolicitacaoScreenState extends State<SolicitacaoScreen> {
  final _observacoes = TextEditingController();
  bool _enviando = false;

  Future<void> _enviar() async {
    setState(() => _enviando = true);
    try {
      final c = await Api.i.post('/contratacoes', {
        'servicoId': widget.servico['id'],
        'observacoes': _observacoes.text.trim(),
      }) as Json;
      if (!mounted) return;
      Navigator.pushReplacement(context, MaterialPageRoute(
          builder: (_) => ContratacaoScreen(id: (c['id'] as num).toInt())));
    } on ErroApi catch (e) {
      if (mounted) avisar(context, e.mensagem);
    } finally {
      if (mounted) setState(() => _enviando = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final s = widget.servico;
    final pedido = s['tipo'] == 'PEDIDO';
    return Theme(
      data: temaNetUni(Cores.doTipo(s['tipo'] as String)),
      child: Scaffold(
        appBar: AppBar(title: const Text('Solicitar')),
        body: ListView(padding: const EdgeInsets.all(20), children: [
          Text(s['titulo'] as String, style: Theme.of(context).textTheme.titleLarge),
          const SizedBox(height: 8),
          Text('Com ${(s['autor'] as Json)['nome']}  •  ${reais(s['preco'])}'),
          const SizedBox(height: 16),
          Text(pedido
              ? 'Voce vai se oferecer para fazer este servico. O autor do pedido precisa aceitar.'
              : 'Voce vai pedir este servico. O autor da oferta precisa aceitar.'),
          const SizedBox(height: 16),
          TextField(
            controller: _observacoes,
            maxLines: 3,
            decoration: const InputDecoration(labelText: 'Mensagem inicial (opcional)'),
          ),
          const SizedBox(height: 12),
          const Text('Depois voces combinam valor e forma de pagamento. O pagamento e feito fora do NetUni.',
              style: TextStyle(fontSize: 12, color: Colors.black54)),
          const SizedBox(height: 20),
          FilledButton(
            style: FilledButton.styleFrom(minimumSize: const Size.fromHeight(48)),
            onPressed: _enviando ? null : _enviar,
            child: Text(_enviando ? 'Enviando...' : 'Confirmar solicitacao'),
          ),
        ]),
      ),
    );
  }
}
