import 'package:flutter/material.dart';
import '../services/api.dart';
import '../tema.dart';
import 'home_screen.dart';
import 'login_screen.dart';

/// Tela 1 - Splash: mostra a marca e decide se vai para Login ou Home.
class SplashScreen extends StatefulWidget {
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> {
  @override
  void initState() {
    super.initState();
    _iniciar();
  }

  Future<void> _iniciar() async {
    await Future.wait([
      Api.i.restaurarSessao(),
      Future.delayed(const Duration(milliseconds: 1200)),
    ]);
    if (!mounted) return;
    final destino = Api.i.logado && Api.i.usuario != null ? const HomeScreen() : const LoginScreen();
    Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => destino));
  }

  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      backgroundColor: Cores.azul,
      body: Center(
        child: Column(mainAxisSize: MainAxisSize.min, children: [
          LogoNetUni(tamanho: 110),
          SizedBox(height: 16),
          Text('NetUni', style: TextStyle(color: Colors.white, fontSize: 40, fontWeight: FontWeight.bold)),
          SizedBox(height: 8),
          Text('Conectando habilidades, oportunidades e estudantes.',
              style: TextStyle(color: Colors.white70), textAlign: TextAlign.center),
        ]),
      ),
    );
  }
}

/// Planeta inspirado em Netuno: circulo azul com anel verde.
class LogoNetUni extends StatelessWidget {
  final double tamanho;
  const LogoNetUni({super.key, this.tamanho = 80});

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: tamanho * 1.6,
      height: tamanho,
      child: Stack(alignment: Alignment.center, children: [
        Container(
          width: tamanho,
          height: tamanho,
          decoration: const BoxDecoration(
            shape: BoxShape.circle,
            gradient: LinearGradient(colors: [Color(0xFF6FB6FF), Color(0xFF1B4F9C)]),
          ),
        ),
        Transform.rotate(
          angle: -0.35,
          child: Container(
            width: tamanho * 1.55,
            height: tamanho * 0.38,
            decoration: BoxDecoration(
              borderRadius: BorderRadius.all(Radius.elliptical(tamanho * 1.55, tamanho * 0.38)),
              border: Border.all(color: Cores.verde, width: tamanho * 0.07),
            ),
          ),
        ),
      ]),
    );
  }
}
