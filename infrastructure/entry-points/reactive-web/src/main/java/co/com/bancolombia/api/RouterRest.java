package co.com.bancolombia.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(GET("/api/user/"), handler::listenGetAll)
                .andRoute(POST("/api/user/save"), handler::listenSave) // Ruta para guardar usuarios
                .and(route(GET("/api/user/{id}"), handler::listenGetById))
                .andRoute(POST("/api/user/update{id}"), handler::listenUpdate);
    }
}
