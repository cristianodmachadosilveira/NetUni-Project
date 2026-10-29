import 'package:flutter/material.dart';
import '../services/api.dart';
import '../tema.dart';
import '../widgets/comuns.dart';
import 'home_screen.dart';

/// Tela 3 - Cadastro (RF01).
class CadastroScreen extends StatefulWidget {
  const CadastroScreen({super.key});

  @override
  State<CadastroScreen> createState() => _CadastroScreenState();
}

class _CadastroScreenState extends State<CadastroScreen> {
  final _formulario = GlobalKey<FormState>();
  final _nome = TextEditingController();
  final _email = TextEditingController();
  final _senha = TextEditingController();
  final _curso = TextEditingController();
  final _semestre = TextEditingController();
  bool _enviando = false;

  Future<void> _cadastrar() async {
    if (!_formulario.currentState!.validate()) return;
    setState(() => _enviando = true);
    try {
      await Api.i.post('/usuarios', {
        'nome': _nome.text.trim(),
        'email': _email.text.trim(),
        'senha': _senha.text,
        'curso': _curso.text.trim(),
        'semestre': int.tryParse(_semestre.text),
      });
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
      appBar: AppBar(title: const Text('Criar conta'), backgroundColor: Cores.azul),
      body: Form(
        key: _formulario,
        child: ListView(padding: const EdgeInsets.all(20), children: [
          TextFormField(
            controller: _nome,
            decoration: const InputDecoration(labelText: 'Nome completo'),
            validator: (v) => (v == null || v.trim().isEmpty) ? 'Informe o nome' : null,
          ),
          const SizedBox(height: 12),
          TextFormField(
            controller: _email,
            keyboardType: TextInputType.emailAddress,
            decoration: const InputDecoration(labelText: 'E-mail institucional'),
            validator: (v) => (v == null || !v.contains('@')) ? 'E-mail invalido' : null,
          ),
          const SizedBox(height: 12),
          TextFormField(
            controller: _senha,
            obscureText: true,
            decoration: const InputDecoration(labelText: 'Senha (minimo 6)'),
            validator: (v) => (v == null || v.length < 6) ? 'Minimo 6 caracteres' : null,
          ),
          const SizedBox(height: 12),
          TextFormField(controller: _curso, decoration: const InputDecoration(labelText: 'Curso')),
          const SizedBox(height: 12),
          TextFormField(
            controller: _semestre,
            keyboardType: TextInputType.number,
            decoration: const InputDecoration(labelText: 'Semestre'),
            validator: (v) {
              if (v == null || v.isEmpty) return null;
              final n = int.tryParse(v);
              if (n == null || n < 1 || n > 12) return 'Semestre de 1 a 12';
              return null;
            },
          ),
          const SizedBox(height: 24),
          FilledButton(
            style: FilledButton.styleFrom(backgroundColor: Cores.verde, minimumSize: const Size.fromHeight(48)),
            onPressed: _enviando ? null : _cadastrar,
            child: Text(_enviando ? 'Enviando...' : 'Cadastrar'),
          ),
        ]),
      ),
    );
  }
}
