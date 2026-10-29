import 'package:flutter/material.dart';
import '../services/api.dart';
import '../tema.dart';
import '../widgets/comuns.dart';
import 'busca_screen.dart';
import 'contratacoes_screen.dart';
import 'criar_servico_screen.dart';
import 'login_screen.dart';
import 'perfil_screen.dart';
import 'portfolio_screen.dart';
import 'servico_detalhe_screen.dart';

/// Tela 4 - Home e Tela 7 - Lista de servicos (RF06/RF07).
/// Duas variacoes, como no prototipo: PEDIDOS (verde) e OFERTAS (azul).
class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  String _tipo = 'PEDIDO';
  int _versao = 0; // muda para forcar recarregar a lista
  final _busca = TextEditingController();

  Color get _cor => Cores.doTipo(_tipo);

  void _recarregar() => setState(() => _versao++);

  Future<void> _abrir(Widget tela) async {
    await Navigator.push(context, MaterialPageRoute(builder: (_) => tela));
    _recarregar();
  }

  Future<void> _sair() async {
    final confirmar = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Sair do NetUni?'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx, false), child: const Text('Cancelar')),
          FilledButton(onPressed: () => Navigator.pop(ctx, true), child: const Text('Sair')),
        ],
      ),
    );
    if (confirmar != true) return;
    await Api.i.sair();
    if (!mounted) return;
    Navigator.pushAndRemoveUntil(context, MaterialPageRoute(builder: (_) => const LoginScreen()), (_) => false);
  }

  @override
  Widget build(BuildContext context) {
    return Theme(
      data: temaNetUni(_cor),
      child: Scaffold(
        body: Column(children: [
          _barraDoTopo(),
          _abas(),
          Expanded(
            child: Carregador<List<dynamic>>(
              key: ValueKey('$_tipo-$_versao'),
              carregar: () async => await Api.i.get('/servicos', {'tipo': _tipo}) as List<dynamic>,
              construir: (context, lista, recarregar) => RefreshIndicator(
                onRefresh: recarregar,
                child: lista.isEmpty
                    ? ListView(children: const [
                        Padding(padding: EdgeInsets.all(40), child: Center(child: Text('Nada publicado ainda.')))
                      ])
                    : ListView.builder(
                        padding: const EdgeInsets.only(bottom: 90),
                        itemCount: lista.length,
                        itemBuilder: (_, i) {
                          final servico = lista[i] as Json;
                          return CardServico(
                            servico: servico,
                            aoTocar: () => _abrir(ServicoDetalheScreen(id: (servico['id'] as num).toInt())),
                          );
                        },
                      ),
              ),
            ),
          ),
        ]),
        floatingActionButton: FloatingActionButton.large(
          backgroundColor: Cores.fab,
          shape: const CircleBorder(),
          elevation: 0,
          tooltip: _tipo == 'PEDIDO' ? 'Novo pedido' : 'Nova oferta',
          onPressed: () => _abrir(CriarServicoScreen(tipoInicial: _tipo)),
          child: Icon(Icons.add, size: 64, color: _cor),
        ),
        bottomNavigationBar: _barraDeBaixo(),
      ),
    );
  }

  Widget _barraDoTopo() {
    return Container(
      color: _cor,
      padding: EdgeInsets.fromLTRB(4, MediaQuery.of(context).padding.top + 8, 12, 12),
      child: Row(children: [
        IconButton(
          icon: const Icon(Icons.arrow_back_ios_new, color: Colors.white, size: 30),
          tooltip: 'Sair',
          onPressed: _sair,
        ),
        Expanded(
          child: TextField(
            controller: _busca,
            textInputAction: TextInputAction.search,
            decoration: const InputDecoration(
              hintText: 'Pesquisar...',
              hintStyle: TextStyle(fontStyle: FontStyle.italic),
              suffixIcon: Icon(Icons.search),
              isDense: true,
              border: OutlineInputBorder(borderRadius: BorderRadius.zero, borderSide: BorderSide.none),
            ),
            onSubmitted: (texto) {
              _busca.clear();
              _abrir(BuscaScreen(textoInicial: texto, tipoInicial: _tipo));
            },
          ),
        ),
        const SizedBox(width: 12),
        GestureDetector(
          onTap: () => _abrir(PerfilScreen(alunoId: Api.i.meuId)),
          child: Avatar(alunoId: 3, tamanho: 50),
        ),
      ]),
    );
  }

  Widget _abas() {
    Widget aba(String tipo, String rotulo, Color cor) {
      final ativa = _tipo == tipo;
      return Expanded(
        child: InkWell(
          onTap: () => setState(() => _tipo = tipo),
          child: Container(
            height: 56,
            color: cor,
            alignment: Alignment.center,
            child: Text(
              rotulo,
              style: TextStyle(
                fontSize: 22,
                color: ativa ? Colors.white : Colors.white.withOpacity(0.35),
                fontWeight: ativa ? FontWeight.bold : FontWeight.normal,
              ),
            ),
          ),
        ),
      );
    }

    return Container(
      color: Cores.divisoria,
      padding: const EdgeInsets.only(top: 4, bottom: 6),
      child: Row(children: [
        aba('PEDIDO', 'PEDIDOS', Cores.verde),
        const SizedBox(width: 5),
        aba('OFERTA', 'OFERTAS', Cores.azul),
      ]),
    );
  }

  Widget _barraDeBaixo() {
    return Container(
      color: _cor,
      padding: EdgeInsets.only(bottom: MediaQuery.of(context).padding.bottom),
      height: 64 + MediaQuery.of(context).padding.bottom,
      child: Row(mainAxisAlignment: MainAxisAlignment.spaceAround, children: [
        IconButton(
          icon: const Icon(Icons.groups_outlined, color: Colors.white, size: 32),
          tooltip: 'Meu portfolio',
          onPressed: () => _abrir(PortfolioScreen(alunoId: Api.i.meuId)),
        ),
        IconButton(
          icon: const Icon(Icons.home, color: Colors.white, size: 32),
          tooltip: 'Inicio',
          onPressed: _recarregar,
        ),
        IconButton(
          icon: const Icon(Icons.mail, color: Colors.white, size: 32),
          tooltip: 'Contratacoes e conversas',
          onPressed: () => _abrir(const ContratacoesScreen()),
        ),
        IconButton(
          icon: const Icon(Icons.notifications, color: Colors.white54, size: 32),
          tooltip: 'Notificacoes (proxima etapa)',
          onPressed: () => avisar(context, 'Notificacoes estao no backlog (proxima etapa).'),
        ),
      ]),
    );
  }
}
