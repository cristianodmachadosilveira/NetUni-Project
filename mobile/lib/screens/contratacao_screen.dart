import 'package:flutter/material.dart';
import '../services/api.dart';
import '../tema.dart';
import '../widgets/comuns.dart';
import 'avaliacao_screen.dart';
import 'conversa_screen.dart';

/// Tela 11 - Contratacao (RF10): status, negociacao de valor/forma de pagamento e acoes.
class ContratacaoScreen extends StatefulWidget {
  final int id;
  const ContratacaoScreen({super.key, required this.id});

  @override
  State<ContratacaoScreen> createState() => _ContratacaoScreenState();
}

class _ContratacaoScreenState extends State<ContratacaoScreen> {
  int _versao = 0;

  void _recarregar() {
    if (mounted) setState(() => _versao++);
  }

  Future<void> _executar(Future<dynamic> Function() acao) async {
    try {
      await acao();
      _recarregar();
    } on ErroApi catch (e) {
      if (mounted) avisar(context, e.mensagem);
    }
  }

  Future<void> _mudarStatus(String novoStatus) =>
      _executar(() => Api.i.put('/contratacoes/${widget.id}/status', {'status': novoStatus}));

  Future<void> _negociar(Json c) async {
    final valor = TextEditingController(text: c['valorNegociado']?.toString() ?? '');
    final forma = TextEditingController(text: (c['formaPagamento'] as String?) ?? '');
    final confirmou = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Combinar valor e pagamento'),
        content: Column(mainAxisSize: MainAxisSize.min, children: [
          TextField(
            controller: valor,
            keyboardType: const TextInputType.numberWithOptions(decimal: true),
            decoration: const InputDecoration(labelText: 'Valor combinado (R\$)'),
          ),
          const SizedBox(height: 12),
          TextField(
            controller: forma,
            decoration: const InputDecoration(labelText: 'Forma de pagamento', hintText: 'Pix, dinheiro, link...'),
          ),
          const SizedBox(height: 8),
          const Text('O NetUni so registra o combinado. O pagamento e feito fora do app.',
              style: TextStyle(fontSize: 12)),
        ]),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx, false), child: const Text('Cancelar')),
          FilledButton(onPressed: () => Navigator.pop(ctx, true), child: const Text('Registrar')),
        ],
      ),
    );
    if (confirmou != true) return;
    final numero = double.tryParse(valor.text.replaceAll(',', '.'));
    if (numero == null || numero < 0 || forma.text.trim().isEmpty) {
      if (mounted) avisar(context, 'Informe um valor valido e a forma de pagamento');
      return;
    }
    await _executar(() => Api.i.put('/contratacoes/${widget.id}',
        {'valorNegociado': numero, 'formaPagamento': forma.text.trim()}));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Contratacao'), backgroundColor: Cores.azul),
      body: Carregador<Json>(
        key: ValueKey(_versao),
        carregar: () async => await Api.i.get('/contratacoes/${widget.id}') as Json,
        construir: (context, c, _) {
          final servico = c['servico'] as Json;
          final acoes = (c['acoesPermitidas'] as List<dynamic>).cast<String>();
          return ListView(padding: const EdgeInsets.all(16), children: [
            Text(servico['titulo'] as String, style: Theme.of(context).textTheme.titleLarge),
            const SizedBox(height: 8),
            Chip(label: Text(nomesStatus[c['status']] ?? c['status'].toString())),
            const SizedBox(height: 8),
            _linha('Contratante', (c['contratante'] as Json)['nome'] as String),
            _linha('Prestador', (c['prestador'] as Json)['nome'] as String),
            _linha('Valor combinado', reais(c['valorNegociado'])),
            _linha('Forma de pagamento', (c['formaPagamento'] as String?) ?? 'a combinar'),
            if (c['observacoes'] != null && (c['observacoes'] as String).isNotEmpty)
              _linha('Observacoes', c['observacoes'] as String),
            const SizedBox(height: 8),
            const Text('Pagamento feito fora do NetUni.', style: TextStyle(fontSize: 12, color: Colors.black54)),
            const Divider(height: 32),
            OutlinedButton.icon(
              icon: const Icon(Icons.chat_bubble_outline),
              label: const Text('Abrir conversa'),
              onPressed: () async {
                await Navigator.push(context, MaterialPageRoute(builder: (_) => ConversaScreen(contratacaoId: widget.id)));
                _recarregar();
              },
            ),
            const SizedBox(height: 8),
            if (acoes.contains('NEGOCIAR'))
              OutlinedButton.icon(
                  icon: const Icon(Icons.handshake_outlined),
                  label: const Text('Combinar valor e pagamento'),
                  onPressed: () => _negociar(c)),
            if (acoes.contains('ACEITAR'))
              FilledButton(onPressed: () => _mudarStatus('ACEITA'), child: const Text('Aceitar solicitacao')),
            if (acoes.contains('INICIAR'))
              FilledButton(onPressed: () => _mudarStatus('EM_ANDAMENTO'), child: const Text('Iniciar servico')),
            if (acoes.contains('CONCLUIR'))
              FilledButton(onPressed: () => _mudarStatus('CONCLUIDA'), child: const Text('Confirmar conclusao')),
            if (acoes.contains('AVALIAR'))
              FilledButton.icon(
                style: FilledButton.styleFrom(backgroundColor: Cores.verde),
                icon: const Icon(Icons.star),
                label: const Text('Avaliar servico'),
                onPressed: () async {
                  await Navigator.push(context, MaterialPageRoute(builder: (_) => AvaliacaoScreen(contratacao: c)));
                  _recarregar();
                },
              ),
            if (acoes.contains('CANCELAR'))
              TextButton(
                style: TextButton.styleFrom(foregroundColor: Colors.red),
                onPressed: () => _mudarStatus('CANCELADA'),
                child: const Text('Cancelar contratacao'),
              ),
            if (c['avaliada'] == true)
              const Padding(padding: EdgeInsets.only(top: 8), child: Text('Servico avaliado.', textAlign: TextAlign.center)),
          ]);
        },
      ),
    );
  }

  Widget _linha(String rotulo, String valor) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(children: [
        SizedBox(width: 150, child: Text(rotulo, style: const TextStyle(color: Colors.black54))),
        Expanded(child: Text(valor, style: const TextStyle(fontWeight: FontWeight.w600))),
      ]),
    );
  }
}
