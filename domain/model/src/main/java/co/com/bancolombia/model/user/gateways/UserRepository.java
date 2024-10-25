package co.com.bancolombia.model.user.gateways;

import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> saveUser(User user);

    Flux<User> findAll();

    Mono<User> findByEmail(String email);

    Mono<Void> updateStatus(User user);
}
