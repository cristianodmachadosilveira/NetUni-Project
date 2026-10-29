package br.una.netuni.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.springframework.stereotype.Service;

/**
 * RNF03 - Seguranca: a senha nunca e salva em texto puro.
 * Usa PBKDF2 (ja vem no Java, sem biblioteca extra) com um "sal" aleatorio por aluno.
 * Formato salvo no banco: "sal:hash", ambos em Base64.
 */
@Service
public class SenhaService {

    private static final int ITERACOES = 65536;
    private static final int TAMANHO_HASH_BITS = 256;
    private static final int TAMANHO_SAL_BYTES = 16;

    private final SecureRandom aleatorio = new SecureRandom();

    public String gerarHash(String senha) {
        byte[] sal = new byte[TAMANHO_SAL_BYTES];
        aleatorio.nextBytes(sal);
        byte[] hash = calcular(senha, sal);
        Base64.Encoder base64 = Base64.getEncoder();
        return base64.encodeToString(sal) + ":" + base64.encodeToString(hash);
    }

    public boolean confere(String senhaDigitada, String salvo) {
        if (senhaDigitada == null || salvo == null) {
            return false;
        }
        String[] partes = salvo.split(":");
        if (partes.length != 2) {
            return false;
        }
        byte[] sal = Base64.getDecoder().decode(partes[0]);
        byte[] hashSalvo = Base64.getDecoder().decode(partes[1]);
        byte[] hashDigitado = calcular(senhaDigitada, sal);
        return compararSemVazarTempo(hashSalvo, hashDigitado);
    }

    private byte[] calcular(String senha, byte[] sal) {
        try {
            PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), sal, ITERACOES, TAMANHO_HASH_BITS);
            SecretKeyFactory fabrica = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return fabrica.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Falha ao calcular hash da senha", e);
        }
    }

    /** Compara byte a byte sem parar no primeiro erro (evita ataque por tempo de resposta). */
    private boolean compararSemVazarTempo(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }
        int diferenca = 0;
        for (int i = 0; i < a.length; i++) {
            diferenca = diferenca | (a[i] ^ b[i]);
        }
        return diferenca == 0;
    }
}
