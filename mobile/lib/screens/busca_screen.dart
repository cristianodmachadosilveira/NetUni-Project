import 'package:flutter/material.dart';
import '../services/api.dart';
import '../tema.dart';
import '../widgets/comuns.dart';
import 'servico_detalhe_screen.dart';

/// Tela 8 - Busca (RF06): texto + filtro de tipo.
class BuscaScreen extends StatefulWidget {
  final String textoInicial;
  final String tipoInicial;
  const BuscaScreen({super.key, this.textoInicial = '', this.tipoInicial = 'PEDIDO'});

  @override
  State<BuscaScreen> createState() => _BuscaScreenState();
}

class _BuscaScreenState extends State<BuscaScreen> {
  late final _texto = TextEditingController(text: widget.textoInicial);
  late String? _tipo = widget.tipoInicial; // null = todos
  int _versao = 0;

  Future<List<dynamic>> _buscar() async {
    final filtros = <String, String>{};
    if (_tipo != null) filtros['tipo'] = _tipo!;
    if (_texto.text.trim().isNotEmpty) filtros['busca'] = _texto.text.trim();
    return await Api.i.get('/servicos', filtros) as List<dynamic>;
  }

  @override
  Widget build(BuildContext context) {
    final cor = _tipo == null ? Cores.azul : Cores.doTipo(_tipo!);
    return Theme(
      data: temaNetUni(cor),
      child: Scaffold(
        appBar: AppBar(
          title: TextField(
            controller: _texto,
            autofocus: widget.textoInicial.isEmpty,
            textInputAction: TextInputAction.search,
            decoration: const InputDecoration(hintText: 'Pesquisar...', isDense: true),
            onSubmitted: (_) => setState(() => _versao++),
          ),
        ),
        body: Column(children: [
          Padding(
            padding: const EdgeInsets.all(8),
            child: Wrap(spacing: 8, children: [
              ChoiceChip(label: const Text('Todos'), selected: _tipo == null,
                  onSelected: (_) => setState(() { _tipo = null; _versao++; })),
              ChoiceChip(label: const Text('Pedidos'), selected: _tipo == 'PEDIDO',
                  onSelected: (_) => setState(() { _tipo = 'PEDIDO'; _versao++; })),
              ChoiceChip(label: const Text('Ofertas'), selected: _tipo == 'OFERTA',
                  onSelected: (_) => setState(() { _tipo = 'OFERTA'; _versao++; })),
            ]),
          ),
          Expanded(
            child: Carregador<List<dynamic>>(
              key: ValueKey(_versao),
              carregar: _buscar,
              construir: (context, lista, _) {
                if (lista.isEmpty) {
                  return const Center(child: Text('Nenhum servico encontrado.'));
                }
                return ListView.builder(
                  itemCount: lista.length,
                  itemBuilder: (_, i) {
                    final servico = lista[i] as Json;
                    return CardServico(
                      servico: servico,
                      aoTocar: () => Navigator.push(context, MaterialPageRoute(
                          builder: (_) => ServicoDetalheScreen(id: (servico['id'] as num).toInt()))),
                    );
                  },
                );
              },
            ),
          ),
        ]),
      ),
    );
  }
}
