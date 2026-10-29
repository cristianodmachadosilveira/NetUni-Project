import 'package:flutter/material.dart';
import '../services/api.dart';
import '../tema.dart';
import '../widgets/comuns.dart';

/// Tela 12 - Conversa (RF08). Chat simplificado: atualiza ao enviar ou ao puxar a tela.
class ConversaScreen extends StatefulWidget {
  final int contratacaoId;
  const ConversaScreen({super.key, required this.contratacaoId});

  @override
  State<ConversaScreen> createState() => _ConversaScreenState();
}

class _ConversaScreenState extends State<ConversaScreen> {
  final _texto = TextEditingController();
  final _rolagem = ScrollController();
  List<dynamic> _mensagens = [];
  bool _carregando = true;

  @override
  void initState() {
    super.initState();
    _carregar();
  }

  Future<void> _carregar() async {
    try {
      final lista = await Api.i.get('/contratacoes/${widget.contratacaoId}/mensagens') as List<dynamic>;
      if (!mounted) return;
      setState(() {
        _mensagens = lista;
        _carregando = false;
      });
      WidgetsBinding.instance.addPostFrameCallback((_) {
        if (_rolagem.hasClients) _rolagem.jumpTo(_rolagem.position.maxScrollExtent);
      });
    } on ErroApi catch (e) {
      if (!mounted) return;
      setState(() => _carregando = false);
      avisar(context, e.mensagem);
    }
  }

  Future<void> _enviar() async {
    final texto = _texto.text.trim();
    if (texto.isEmpty) return;
    _texto.clear();
    try {
      await Api.i.post('/contratacoes/${widget.contratacaoId}/mensagens', {'conteudo': texto});
      await _carregar();
    } on ErroApi catch (e) {
      if (mounted) avisar(context, e.mensagem);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Conversa'),
        backgroundColor: Cores.azul,
        actions: [IconButton(icon: const Icon(Icons.refresh), onPressed: _carregar)],
      ),
      body: Column(children: [
        Expanded(
          child: _carregando
              ? const Center(child: CircularProgressIndicator())
              : RefreshIndicator(
                  onRefresh: _carregar,
                  child: ListView.builder(
                    controller: _rolagem,
                    padding: const EdgeInsets.all(12),
                    itemCount: _mensagens.length,
                    itemBuilder: (_, i) => _balao(_mensagens[i] as Json),
                  ),
                ),
        ),
        SafeArea(
          top: false,
          child: Container(
            color: Colors.white,
            padding: const EdgeInsets.fromLTRB(12, 6, 4, 6),
            child: Row(children: [
              Expanded(
                child: TextField(
                  controller: _texto,
                  textInputAction: TextInputAction.send,
                  onSubmitted: (_) => _enviar(),
                  decoration: const InputDecoration(hintText: 'Escreva uma mensagem', isDense: true),
                ),
              ),
              IconButton(icon: const Icon(Icons.send, color: Cores.azul), onPressed: _enviar),
            ]),
          ),
        ),
      ]),
    );
  }

  Widget _balao(Json m) {
    // Aviso automatico do sistema (sem remetente).
    if (m['remetenteId'] == null) {
      return Padding(
        padding: const EdgeInsets.symmetric(vertical: 6),
        child: Text(m['conteudo'] as String,
            textAlign: TextAlign.center,
            style: const TextStyle(fontSize: 12, fontStyle: FontStyle.italic, color: Colors.black54)),
      );
    }
    final minha = (m['remetenteId'] as num).toInt() == Api.i.meuId;
    return Align(
      alignment: minha ? Alignment.centerRight : Alignment.centerLeft,
      child: Container(
        margin: const EdgeInsets.symmetric(vertical: 4),
        padding: const EdgeInsets.all(10),
        constraints: BoxConstraints(maxWidth: MediaQuery.of(context).size.width * 0.75),
        decoration: BoxDecoration(
          color: minha ? Cores.azul : Colors.white,
          borderRadius: BorderRadius.circular(12),
        ),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          if (!minha)
            Text(m['remetenteNome'] as String,
                style: const TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Cores.verde)),
          Text(m['conteudo'] as String, style: TextStyle(color: minha ? Colors.white : Colors.black87)),
        ]),
      ),
    );
  }
}
