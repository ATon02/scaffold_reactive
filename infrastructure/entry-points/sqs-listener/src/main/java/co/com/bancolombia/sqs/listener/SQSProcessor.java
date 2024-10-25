package co.com.bancolombia.sqs.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import co.com.bancolombia.usecase.user.UserUseCase;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {
    private final UserUseCase userUseCase;

    @Override
    public Mono<Void> apply(Message message) {
        String cleanList = message.body().replaceAll("[\\[\\]]", "");
        List<String> emailList = Arrays.stream(cleanList.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
        return Flux.fromIterable(emailList)
                .flatMap(email -> {
                    return userUseCase.update(email)
                            .doOnError(error -> System.out.println(email + " : " + error.getMessage()))
                            .onErrorResume(e -> Mono.empty());
                })
                .then();
    }
}
