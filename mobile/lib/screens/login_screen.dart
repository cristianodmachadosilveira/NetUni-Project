import 'package:flutter/material.dart';
import '../services/api.dart';
import '../tema.dart';
import '../widgets/comuns.dart';
import 'cadastro_screen.dart';
import 'home_screen.dart';
import 'splash_screen.dart' show LogoNetUni;

/// Tela 2 - Login (RF02).
class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _formulario = GlobalKey<FormState>();
  final _email = TextEditingController();
  final _senha = TextEditingController();
  bool _enviando = false;

  Future<void> _entrar() async {
    if (!_formulario.currentState!.validate()) return;
    setState(() => _enviando = true);
    try {
      await Api.i.login(_email.text.trim(), _senha.text);
      if (!mounted) return;
      Navigator.pushAndRemoveUntil(
          context, MaterialPageRoute(builder: (_) => const HomeScreen()), (_) => false);
    } on ErroApi catch (e) {
      if (mounted) avisar(context, e.mensagem);
    } finally {
      if (mounted) setState(() => _enviando = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(32),
            child: Form(
              key: _formulario,
              child: Column(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
                const Center(child: LogoNetUni(tamanho: 70)),
                const SizedBox(height: 8),
                const Text('NetUni',
                    textAlign: TextAlign.center,
                    style: TextStyle(fontSize: 36, fontWeight: FontWeight.bold, color: Cores.azul)),
                const SizedBox(height: 32),
                TextFormField(
                  controller: _email,
                  keyboardType: TextInputType.emailAddress,
                  decoration: const InputDecoration(labelText: 'E-mail institucional'),
                  validator: (v) => (v == null || !v.contains('@')) ? 'Informe um e-mail valido' : null,
                ),
                const SizedBox(height: 16),
                TextFormField(
                  controller: _senha,
                  obscureText: true,
                  decoration: const InputDecoration(labelText: 'Senha'),
                  validator: (v) => (v == null || v.isEmpty) ? 'Informe a senha' : null,
                ),
                const SizedBox(height: 24),
                FilledButton(
                  style: FilledButton.styleFrom(backgroundColor: Cores.verde, minimumSize: const Size.fromHeight(48)),
                  onPressed: _enviando ? null : _entrar,
                  child: Text(_enviando ? 'Entrando...' : 'Entrar'),
                ),
                const SizedBox(height: 12),
                FilledButton(
                  style: FilledButton.styleFrom(backgroundColor: Cores.azul, minimumSize: const Size.fromHeight(48)),
                  onPressed: () => Navigator.push(
                      context, MaterialPageRoute(builder: (_) => const CadastroScreen())),
                  child: const Text('Criar conta'),
                ),
              ]),
            ),
          ),
        ),
      ),
    );
  }
}
