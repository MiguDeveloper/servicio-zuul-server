package pe.tuna.serviciozuulserver.oauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/*
 * Configuraciones para el servidor de recursos
 * aqui debemos de implementar dos metodos: uno para la proteccion de las rutas endpoints, y el otro para la
 * configuracion del token con la misma estructura que el servidor de autorizacion
 * - RefreshScope: para cuando modifiquemos los properties y no reiniciar el servicio
 */
@RefreshScope
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    /*
     * Podemos hacer la inyeccion con enviroment para obtener el properties
     * pero como es una sola propiedad lo podemos hacer mediante @Value
     */
    @Value("${config.security.oauth.jwt.key}")
    private String jwtkey;

    /*
     * En este método configuramos el tokenStore
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenStore(tokenStore());
    }

    /*
     * Ahora protegemos(nuestros endpoints) cada ruta del zuul server
     * recordar que en la rutas podemos aplicar patrones e.g.: "/api/security/oauth/**" todas las rutas despues de oauth
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/api/security/oauth/token").permitAll()
                .antMatchers(HttpMethod.GET, "/api/productos/listar", "/api/items/listar", "/api/usuarios/usuarios").permitAll()
                .antMatchers(HttpMethod.GET, "/api/productos/ver/{id}",
                        "/api/items/ver/{id}/cantidad/{cantidad}", "/api/usuarios/usuarios/{id}").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.POST, "/api/productos/crear",
                        "/api/items/crear", "/api/usuarios/usuarios").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/productos/editar/{id}",
                        "/api/items/editar/{id}", "/api/usuarios/usuarios/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/productos/eliminar/{id}", "/api/items/eliminar/{id}",
                        "/api/usuarios/usuarios/{id}").hasRole("ADMIN")
                .anyRequest().authenticated();

    }

    /*
     * TokenStore: se encarga de generar y almacenar el token con los datos de accessTokenConverter(este es el encargado
     * de convertir en JsonWebToken con todos los datos: username, roles , etc)
     */
    @Bean
    public JwtTokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    /*
     * En este metodo debemos de agregar el codigo secreto para firmar el token, el cual se usara en el servidor de recursos
     * para validar el token y dar permiso a nuestros recursos protegidos
     */
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
        tokenConverter.setSigningKey(jwtkey);
        return tokenConverter;
    }
}
