package br.una.netuni.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AutenticacaoInterceptor autenticacao;

    public WebConfig(AutenticacaoInterceptor autenticacao) {
        this.autenticacao = autenticacao;
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
}
