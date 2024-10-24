package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {
    private final UserRepository userRepository;

    public Mono<User> saveUser(User user) {
        if (user.getEmail().isBlank()) {
            return Mono.error(new RuntimeException("MISSING EMAIL"));
        }
        if (user.getName().isBlank()) {
            return Mono.error(new RuntimeException("MISSING NAME"));
        }
        if (user.getAge() <= 0) {
            return Mono.error(new RuntimeException("AGE IS NOT VALID"));
        }
        user.setStatus("ACTIVE");
        return userRepository.findByEmail(user.getEmail())
                .flatMap(existingUser -> Mono.error(new RuntimeException("USER ALREADY EXISTS")))
                .switchIfEmpty(userRepository.saveUser(user))
                .map(savedUser -> (User) savedUser)
                .onErrorResume(e -> {
                    return Mono.error(new RuntimeException(e.getMessage()));
                });
    }

    public Mono<User> findById(String email) {
        return userRepository.findByEmail(email)
                .flatMap(user -> {
                    if (!user.getStatus().equals("ACTIVE")) {
                        return Mono.error(new RuntimeException("USER IS INACTIVE"));
                    }
                    return Mono.just(user);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("USER NOT FOUND")));
    }

    public Flux<User> findAll() {
        return userRepository.findAll()
                .onErrorResume(e -> {
                    return Flux.error(new RuntimeException("ERROR GET USERS"));
                });
    }

    public void update(String id) {

    }

    public String test() {
        return "TESTS";
    }

}
