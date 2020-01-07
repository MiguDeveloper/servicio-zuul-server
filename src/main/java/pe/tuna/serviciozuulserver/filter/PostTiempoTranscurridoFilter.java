package pe.tuna.serviciozuulserver.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class PostTiempoTranscurridoFilter extends ZuulFilter {

    private static final Logger logger = LoggerFactory.getLogger(PostTiempoTranscurridoFilter.class);

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    // Aqui configuramos cuando ejecutar el filtro por eso es un boolean
    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        logger.info("Entrando al post");

        Long tiempoInicio = (Long) request.getAttribute("tiempoInicio");
        Long tiempoFin = System.currentTimeMillis();

        Long tiempoTranscurrido = tiempoFin - tiempoInicio;

        logger.info(String.format("Tiempo transcurrido en seg. %s", tiempoTranscurrido.doubleValue()/1000.00));
        logger.info(String.format("Tiempo transcurrido en ms. %s", tiempoTranscurrido));

        return null;
    }
}
