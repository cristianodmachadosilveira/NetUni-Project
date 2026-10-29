import 'package:flutter/material.dart';
import 'screens/splash_screen.dart';
import 'tema.dart';

void main() {
  runApp(const NetUniApp());
}

class NetUniApp extends StatelessWidget {
  const NetUniApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'NetUni',
      debugShowCheckedModeBanner: false,
      theme: temaNetUni(Cores.verde),
      home: const SplashScreen(),
    );
  }
}
