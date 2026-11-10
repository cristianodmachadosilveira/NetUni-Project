package br.una.netuni.model;

/**
 * Como o anuncio sera pago (menu suspenso ao publicar).
 * VALOR: o autor informa um preco em R$.
 * A_COMBINAR: sem preco no anuncio; o valor e combinado na conversa.
 * TROCA: troca de servicos; o autor diz o que aceita em troca.
 */
public enum ModalidadePreco {
    VALOR, A_COMBINAR, TROCA
}
