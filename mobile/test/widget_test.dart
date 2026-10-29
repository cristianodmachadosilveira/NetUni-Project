import 'package:flutter_test/flutter_test.dart';
import 'package:netuni/services/api.dart';

void main() {
  test('formata valores em reais', () {
    expect(reais(40), 'R\$ 40,00');
    expect(reais(12.5), 'R\$ 12,50');
    expect(reais(null), 'A combinar');
  });

  test('traduz status da contratacao', () {
    expect(nomesStatus['EM_ANDAMENTO'], 'Em andamento');
  });
}
