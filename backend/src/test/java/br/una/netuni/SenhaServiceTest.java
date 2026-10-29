package br.una.netuni;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.una.netuni.service.SenhaService;
import org.junit.jupiter.api.Test;

/** RNF03 - a senha nunca fica em texto puro. Teste unitario, sem subir o Spring. */
class SenhaServiceTest {

    private final SenhaService senhaService = new SenhaService();

    @Test
    void hashNaoContemASenhaEConfere() {
        String hash = senhaService.gerarHash("minhaSenha");
        assertFalse(hash.contains("minhaSenha"));
        assertTrue(senhaService.confere("minhaSenha", hash));
        assertFalse(senhaService.confere("outraSenha", hash));
    }

    @Test
    void mesmaSenhaGeraHashesDiferentes() {
        assertNotEquals(senhaService.gerarHash("123456"), senhaService.gerarHash("123456"));
    }
}
