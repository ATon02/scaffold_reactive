package co.com.bancolombia.api;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
    private final UserUseCase userUseCase;
    // private final UseCase2 useCase2;

    public Mono<ServerResponse> listenGetAll(ServerRequest serverRequest) {
        return userUseCase.findAll()
                .collectList()
                .flatMap(users -> ServerResponse.ok().bodyValue(users))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> listenGetById(ServerRequest serverRequest) {
        return userUseCase.findById(serverRequest.pathVariable("id"))
                .flatMap(user -> ServerResponse.ok().bodyValue(user))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> listenSave(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(User.class)
                .flatMap(user -> userUseCase.saveUser(user))
                .flatMap(savedUser -> ServerResponse.ok().bodyValue(savedUser))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> listenUpdate(ServerRequest serverRequest) {
        return userUseCase.update(serverRequest.pathVariable("id"))
                .flatMap(user -> ServerResponse.ok().bodyValue("UPDATING"))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .bodyValue(e.getMessage()));
    }
}
