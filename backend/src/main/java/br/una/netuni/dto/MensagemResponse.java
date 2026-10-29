package br.una.netuni.dto;

import br.una.netuni.model.Mensagem;
import java.time.LocalDateTime;

public class MensagemResponse {

    public Long id;
    /** null quando e um aviso automatico do sistema. */
    public Long remetenteId;
    public String remetenteNome;
    public String conteudo;
    public LocalDateTime data;

    public static MensagemResponse de(Mensagem m) {
        MensagemResponse r = new MensagemResponse();
        r.id = m.getId();
        if (m.getRemetente() != null) {
            r.remetenteId = m.getRemetente().getId();
            r.remetenteNome = m.getRemetente().getNome();
        } else {
            r.remetenteNome = "NetUni";
        }
        r.conteudo = m.getConteudo();
        r.data = m.getData();
        return r;
    }
}
