# NetUni — App mobile (Flutter)

App do NetUni que consome a API REST do `backend/`. As telas seguem o protótipo
(PEDIDOS em verde, OFERTAS em azul).

## Telas (14)

| # | Tela | Arquivo | RF |
|---|------|---------|----|
| 1 | Splash | `lib/screens/splash_screen.dart` | — |
| 2 | Login | `lib/screens/login_screen.dart` | RF02 |
| 3 | Cadastro | `lib/screens/cadastro_screen.dart` | RF01 |
| 4 | Home (abas Pedidos/Ofertas) | `lib/screens/home_screen.dart` | RF06, RF07 |
| 5 | Perfil | `lib/screens/perfil_screen.dart` | RF03 |
| 6 | Criar/editar serviço | `lib/screens/criar_servico_screen.dart` | RF04, RF05 |
| 7 | Lista de serviços | dentro da Home | RF06 |
| 8 | Busca | `lib/screens/busca_screen.dart` | RF06 |
| 9 | Detalhes do serviço | `lib/screens/servico_detalhe_screen.dart` | RF07 |
| 10 | Solicitação | `lib/screens/solicitacao_screen.dart` | RF09 |
| 11 | Contratação (+ lista em `contratacoes_screen.dart`) | `lib/screens/contratacao_screen.dart` | RF10 |
| 12 | Conversa | `lib/screens/conversa_screen.dart` | RF08 |
| 13 | Avaliação | `lib/screens/avaliacao_screen.dart` | RF11 |
| 14 | Portfólio | `lib/screens/portfolio_screen.dart` | RF12 |

Barra de baixo: grupo de pessoas = meu portfólio, casa = início, envelope = contratações
e conversas, sino = notificações (no backlog, ainda sem função).

## Como rodar

Pré-requisitos: Flutter 3.22 ou mais novo e um emulador Android (ou celular).

```bash
cd mobile
flutter create --project-name netuni --platforms android,ios .   # gera as pastas android/ e ios/ (só na 1ª vez)
flutter pub get
flutter run
```

**Importante (Android):** a API roda em `http://` sem HTTPS. Depois do `flutter create`, abra
`android/app/src/main/AndroidManifest.xml` e adicione `android:usesCleartextTraffic="true"`
na tag `<application ...>`. Para gerar APK de release, adicione também
`<uses-permission android:name="android.permission.INTERNET"/>` no mesmo arquivo (antes de `<application>`).

Por padrão o app usa a API publicada em `https://netuni-api.onrender.com` (no plano gratuito ela
dorme e a primeira chamada demora ~1 minuto). Para usar a API rodando no seu computador, passe o endereço:
`http://10.0.2.2:8080` no emulador Android, ou o IP do computador na mesma rede Wi-Fi em um celular físico:

```bash
flutter run --dart-define=API_URL=http://192.168.0.10:8080
```

Logins de demonstração (senha `123456`): `ana@una.br`, `bruno@una.br`, `carla@una.br`.
