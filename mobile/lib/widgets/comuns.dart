import 'package:flutter/material.dart';
import '../services/api.dart';
import '../tema.dart';

void avisar(BuildContext context, String mensagem) {
  ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(mensagem)));
}

/// Circulo colorido com o icone de pessoa, como no prototipo.
class Avatar extends StatelessWidget {
  final int alunoId;
  final double tamanho;
  const Avatar({super.key, required this.alunoId, this.tamanho = 52});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: tamanho,
      height: tamanho,
      decoration: BoxDecoration(
        color: Cores.avatar(alunoId),
        shape: BoxShape.circle,
        boxShadow: const [BoxShadow(color: Colors.black26, offset: Offset(3, 3))],
      ),
      child: Icon(Icons.person, color: Colors.white, size: tamanho * 0.7),
    );
  }
}

/// Carrega dados da API e mostra carregando / erro com botao de tentar de novo.
class Carregador<T> extends StatefulWidget {
  final Future<T> Function() carregar;
  final Widget Function(BuildContext context, T dados, Future<void> Function() recarregar) construir;
  const Carregador({super.key, required this.carregar, required this.construir});

  @override
  State<Carregador<T>> createState() => _CarregadorState<T>();
}

class _CarregadorState<T> extends State<Carregador<T>> {
  late Future<T> _futuro = widget.carregar();

  Future<void> _recarregar() async {
    setState(() => _futuro = widget.carregar());
    try {
      await _futuro;
    } catch (_) {}
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<T>(
      future: _futuro,
      builder: (context, snap) {
        if (snap.connectionState != ConnectionState.done) {
          return const Center(child: CircularProgressIndicator());
        }
        if (snap.hasError) {
          return Center(
            child: Padding(
              padding: const EdgeInsets.all(24),
              child: Column(mainAxisSize: MainAxisSize.min, children: [
                Text(snap.error.toString(), textAlign: TextAlign.center),
                const SizedBox(height: 12),
                OutlinedButton(onPressed: _recarregar, child: const Text('Tentar de novo')),
              ]),
            ),
          );
        }
        return widget.construir(context, snap.data as T, _recarregar);
      },
    );
  }
}

/// Card de servico da lista (Home e Busca).
class CardServico extends StatelessWidget {
  final Json servico;
  final VoidCallback aoTocar;
  const CardServico({super.key, required this.servico, required this.aoTocar});

  @override
  Widget build(BuildContext context) {
    final autor = servico['autor'] as Json;
    final prazo = servico['prazoDias'];
    return InkWell(
      onTap: aoTocar,
      child: Container(
        decoration: const BoxDecoration(
          color: Cores.fundo,
          border: Border(bottom: BorderSide(color: Cores.divisoria, width: 12)),
        ),
        padding: const EdgeInsets.fromLTRB(16, 20, 16, 20),
        child: Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Avatar(alunoId: (autor['id'] as num).toInt()),
          const SizedBox(width: 16),
          Expanded(
            child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              Text(servico['titulo'] as String,
                  style: const TextStyle(fontSize: 17, fontWeight: FontWeight.bold)),
              const SizedBox(height: 4),
              Text('${autor['nome']} • ${autor['curso'] ?? 'UNA'}',
                  style: const TextStyle(color: Colors.black54)),
              const SizedBox(height: 8),
              Text('${reais(servico['preco'])}${prazo != null ? '  •  $prazo dias' : ''}',
                  style: TextStyle(color: Cores.doTipo(servico['tipo'] as String), fontWeight: FontWeight.w600)),
            ]),
          ),
        ]),
      ),
    );
  }
}
