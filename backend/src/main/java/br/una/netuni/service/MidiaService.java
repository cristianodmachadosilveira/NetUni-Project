package br.una.netuni.service;

import br.una.netuni.dto.MidiaResponse;
import br.una.netuni.exception.RegraDeNegocioException;
import br.una.netuni.model.TipoMidia;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Guarda as fotos e videos enviados em uma pasta do servidor (padrao ./data/midias).
 * Os arquivos ficam acessiveis em GET /midias/{nome} (ver WebConfig).
 * Obs.: no plano gratuito do Render essa pasta e apagada quando o servidor reinicia.
 */
@Service
public class MidiaService {

    /** Tipos aceitos -> extensao do arquivo salvo. Qualquer outro tipo e recusado. */
    private static final Map<String, String> EXTENSOES = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp",
            "image/gif", "gif",
            "video/mp4", "mp4",
            "video/webm", "webm",
            "video/quicktime", "mov");

    private final Path pasta;

    public MidiaService(@Value("${netuni.pasta-midias:./data/midias}") String pasta) {
        this.pasta = Paths.get(pasta).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.pasta);
        } catch (IOException e) {
            throw new UncheckedIOException("Nao foi possivel criar a pasta de midias " + this.pasta, e);
        }
    }

    public Path getPasta() {
        return pasta;
    }

    public MidiaResponse salvar(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw RegraDeNegocioException.dadosInvalidos("Escolha uma imagem ou um video");
        }
        String tipo = arquivo.getContentType() == null ? "" : arquivo.getContentType().toLowerCase();
        String extensao = EXTENSOES.get(tipo);
        if (extensao == null) {
            throw RegraDeNegocioException.dadosInvalidos("Formato nao aceito. Use JPG, PNG, WEBP, GIF, MP4, WEBM ou MOV");
        }

        // Nome aleatorio: evita conflito e impede que o usuario escolha o caminho do arquivo.
        String nome = UUID.randomUUID() + "." + extensao;
        try {
            arquivo.transferTo(pasta.resolve(nome));
        } catch (IOException e) {
            throw new UncheckedIOException("Nao foi possivel salvar o arquivo", e);
        }
        TipoMidia tipoMidia = tipo.startsWith("video/") ? TipoMidia.VIDEO : TipoMidia.IMAGEM;
        return new MidiaResponse("/midias/" + nome, tipoMidia);
    }
}
