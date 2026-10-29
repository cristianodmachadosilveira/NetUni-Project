package br.una.netuni.config;

import br.una.netuni.service.MidiaService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AutenticacaoInterceptor autenticacao;
    private final MidiaService midiaService;

    public WebConfig(AutenticacaoInterceptor autenticacao, MidiaService midiaService) {
        this.autenticacao = autenticacao;
        this.midiaService = midiaService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(autenticacao)
                .addPathPatterns("/**")
                .excludePathPatterns("/error", "/h2-console/**");
    }

    /** Permite que o app (ou um teste no navegador) chame a API durante o desenvolvimento. */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }

    /** GET /midias/{arquivo} devolve as fotos e videos enviados (com suporte a "Range" para video). */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String local = midiaService.getPasta().toUri().toString();
        if (!local.endsWith("/")) {
            local = local + "/";
        }
        registry.addResourceHandler("/midias/**").addResourceLocations(local);
    }
}
