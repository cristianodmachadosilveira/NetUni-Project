import 'package:flutter/material.dart';
import '../services/api.dart';
import '../tema.dart';
import '../widgets/comuns.dart';

/// Tela 6 - Criar servico (RF04). Tambem edita um servico existente (RF05).
class CriarServicoScreen extends StatefulWidget {
  final String tipoInicial;
  final Json? existente;
  const CriarServicoScreen({super.key, required this.tipoInicial, this.existente});

  @override
  State<CriarServicoScreen> createState() => _CriarServicoScreenState();
}

class _CriarServicoScreenState extends State<CriarServicoScreen> {
  final _formulario = GlobalKey<FormState>();
  late String _tipo = (widget.existente?['tipo'] as String?) ?? widget.tipoInicial;
  late final _titulo = TextEditingController(text: widget.existente?['titulo'] as String?);
  late final _descricao = TextEditingController(text: widget.existente?['descricao'] as String?);
  late final _preco = TextEditingController(text: widget.existente?['preco']?.toString() ?? '');
  late final _prazo = TextEditingController(text: widget.existente?['prazoDias']?.toString() ?? '');
  bool _salvando = false;

  Future<void> _salvar() async {
    if (!_formulario.currentState!.validate()) return;
    setState(() => _salvando = true);
    final dados = {
      'tipo': _tipo,
      'titulo': _titulo.text.trim(),
      'descricao': _descricao.text.trim(),
      'preco': double.parse(_preco.text.replaceAll(',', '.')),
      'prazoDias': int.parse(_prazo.text),
    };
    try {
      if (widget.existente == null) {
        await Api.i.post('/servicos', dados);
      } else {
        await Api.i.put('/servicos/${widget.existente!['id']}', dados);
      }
      if (mounted) Navigator.pop(context, true);
    } on ErroApi catch (e) {
      if (mounted) avisar(context, e.mensagem);
    } finally {
      if (mounted) setState(() => _salvando = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final cor = Cores.doTipo(_tipo);
    return Theme(
      data: temaNetUni(cor),
      child: Scaffold(
        appBar: AppBar(title: Text(widget.existente == null ? 'Publicar' : 'Editar servico')),
        body: Form(
          key: _formulario,
          child: ListView(padding: const EdgeInsets.all(16), children: [
            SegmentedButton<String>(
              segments: const [
                ButtonSegment(value: 'PEDIDO', label: Text('Pedido'), icon: Icon(Icons.search)),
                ButtonSegment(value: 'OFERTA', label: Text('Oferta'), icon: Icon(Icons.storefront)),
              ],
              selected: {_tipo},
              onSelectionChanged: (s) => setState(() => _tipo = s.first),
            ),
            const SizedBox(height: 8),
            Text(_tipo == 'PEDIDO' ? 'Voce procura alguem para fazer este servico.' : 'Voce oferece este servico.',
                style: const TextStyle(color: Colors.black54)),
            const SizedBox(height: 16),
            TextFormField(
              controller: _titulo,
              decoration: const InputDecoration(labelText: 'Titulo'),
              validator: (v) => (v == null || v.trim().isEmpty) ? 'Informe o titulo' : null,
            ),
            const SizedBox(height: 12),
            TextFormField(controller: _descricao, maxLines: 4, decoration: const InputDecoration(labelText: 'Descricao')),
            const SizedBox(height: 12),
            TextFormField(
              controller: _preco,
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
              decoration: const InputDecoration(labelText: 'Valor sugerido (R\$)', helperText: 'Pode ser negociado depois'),
              validator: (v) {
                final n = double.tryParse((v ?? '').replaceAll(',', '.'));
                if (n == null) return 'Informe um numero';
                if (n < 0) return 'Nao pode ser negativo';
                return null;
              },
            ),
            const SizedBox(height: 12),
            TextFormField(
              controller: _prazo,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(labelText: 'Prazo (dias)'),
              validator: (v) {
                final n = int.tryParse(v ?? '');
                if (n == null || n <= 0) return 'Informe um prazo maior que zero';
                return null;
              },
            ),
            const SizedBox(height: 12),
            const Text('O pagamento e combinado entre os alunos e feito fora do NetUni (Pix, dinheiro, link).',
                style: TextStyle(fontSize: 12, color: Colors.black54)),
            const SizedBox(height: 20),
            FilledButton(
              style: FilledButton.styleFrom(minimumSize: const Size.fromHeight(48)),
              onPressed: _salvando ? null : _salvar,
              child: Text(_salvando ? 'Salvando...' : 'Publicar'),
            ),
          ]),
        ),
      ),
    );
  }
}
