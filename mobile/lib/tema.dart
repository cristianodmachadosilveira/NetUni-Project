import 'package:flutter/material.dart';

/// Identidade visual do NetUni (mesmas cores do prototipo).
class Cores {
  static const verde = Color(0xFF5BB46E); // aba PEDIDOS
  static const azul = Color(0xFF2167B4); // aba OFERTAS
  static const fundo = Color(0xFFE4E8EB);
  static const divisoria = Color(0xFFD3DADD);
  static const fab = Color(0xFFC9D2DB);

  /// Cor de destaque de acordo com o tipo do servico.
  static Color doTipo(String tipo) => tipo == 'PEDIDO' ? verde : azul;

  /// Cor fixa do avatar de cada aluno (mesmo aluno, mesma cor).
  static const _avatares = [
    Color(0xFFFFD983),
    Color(0xFFE584FF),
    Color(0xFF7F88FF),
    Color(0xFFFF85AE),
    Color(0xFF7FD6C2),
    Color(0xFFFFA77F),
  ];
  static Color avatar(int id) => _avatares[id % _avatares.length];
}

ThemeData temaNetUni(Color principal) {
  return ThemeData(
    useMaterial3: true,
    colorScheme: ColorScheme.fromSeed(seedColor: principal, primary: principal),
    scaffoldBackgroundColor: Cores.fundo,
    appBarTheme: AppBarTheme(backgroundColor: principal, foregroundColor: Colors.white),
    inputDecorationTheme: const InputDecorationTheme(
      border: OutlineInputBorder(),
      filled: true,
      fillColor: Colors.white,
    ),
  );
}
