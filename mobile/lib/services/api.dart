import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';

/// Endereco da API Spring Boot.
/// Emulador Android: 10.0.2.2 aponta para o computador.
/// Celular fisico: flutter run --dart-define=API_URL=http://IP_DO_PC:8080
const String urlApi = String.fromEnvironment('API_URL', defaultValue: 'http://10.0.2.2:8080');

typedef Json = Map<String, dynamic>;

class ErroApi implements Exception {
  final int status;
  final String mensagem;
  ErroApi(this.status, this.mensagem);

  @override
  String toString() => mensagem;
}

/// Cliente HTTP unico do app. Guarda o token do login e o aluno logado.
class Api {
  Api._();
  static final Api i = Api._();

  String? _token;
  Json? usuario;

  bool get logado => _token != null;
  int get meuId => (usuario?['id'] as num?)?.toInt() ?? -1;

  /// Recupera o login salvo no celular (usado pela Splash).
  Future<void> restaurarSessao() async {
    final prefs = await SharedPreferences.getInstance();
    _token = prefs.getString('token');
    if (_token == null) return;
    try {
      usuario = await get('/usuarios/me') as Json;
    } on ErroApi catch (e) {
      if (e.status == 401) await sair();
    }
  }

  Future<void> login(String email, String senha) async {
    final r = await post('/login', {'email': email, 'senha': senha}) as Json;
    _token = r['token'] as String;
    usuario = r['usuario'] as Json;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('token', _token!);
  }

  Future<void> sair() async {
    _token = null;
    usuario = null;
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('token');
  }

  Future<dynamic> get(String caminho, [Map<String, String>? query]) =>
      _enviar(() => http.get(_uri(caminho, query), headers: _cabecalhos));

  Future<dynamic> post(String caminho, Object corpo) =>
      _enviar(() => http.post(_uri(caminho), headers: _cabecalhos, body: jsonEncode(corpo)));

  Future<dynamic> put(String caminho, Object corpo) =>
      _enviar(() => http.put(_uri(caminho), headers: _cabecalhos, body: jsonEncode(corpo)));

  Future<dynamic> delete(String caminho) =>
      _enviar(() => http.delete(_uri(caminho), headers: _cabecalhos));

  Map<String, String> get _cabecalhos => {
        'Content-Type': 'application/json',
        if (_token != null) 'Authorization': 'Bearer $_token',
      };

  Uri _uri(String caminho, [Map<String, String>? query]) {
    final uri = Uri.parse('$urlApi$caminho');
    return query == null ? uri : uri.replace(queryParameters: query);
  }

  Future<dynamic> _enviar(Future<http.Response> Function() requisicao) async {
    http.Response resposta;
    try {
      resposta = await requisicao().timeout(const Duration(seconds: 15));
    } catch (_) {
      throw ErroApi(0, 'Sem conexao com o servidor ($urlApi).');
    }
    final texto = utf8.decode(resposta.bodyBytes);
    final dados = texto.isEmpty ? null : jsonDecode(texto);
    if (resposta.statusCode >= 200 && resposta.statusCode < 300) {
      return dados;
    }
    String mensagem = 'Erro ${resposta.statusCode}';
    if (dados is Map && dados['erro'] != null) {
      mensagem = dados['erro'].toString();
    }
    throw ErroApi(resposta.statusCode, mensagem);
  }
}

/// Formata um valor em reais: 40 -> "R$ 40,00".
String reais(dynamic valor) {
  if (valor == null) return 'A combinar';
  final numero = (valor as num).toDouble();
  return 'R\$ ${numero.toStringAsFixed(2).replaceAll('.', ',')}';
}

const nomesStatus = {
  'SOLICITADA': 'Solicitada',
  'ACEITA': 'Aceita',
  'EM_ANDAMENTO': 'Em andamento',
  'CONCLUIDA': 'Concluida',
  'CANCELADA': 'Cancelada',
};
