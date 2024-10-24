package co.com.bancolombia.dynamodb;

import co.com.bancolombia.dynamodb.helper.TemplateAdapterOperations;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;
import java.util.Optional;

@Repository
public class DynamoDBTemplateAdapter
        extends TemplateAdapterOperations<User /* domain model */, String, UserEntity /* adapter model */>
        implements UserRepository /* implements Gateway from domain */ {

    private final DynamoDbEnhancedAsyncClient getDynamoDbEnhancedAsyncClient;

    public DynamoDBTemplateAdapter(DynamoDbEnhancedAsyncClient connectionFactory, ObjectMapper mapper,
            DynamoDbEnhancedAsyncClient getDynamoDbEnhancedAsyncClient) {
        /*
         * Could be use mapper.mapBuilder if your domain model implement builder pattern
         * super(repository, mapper, d ->
         * mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         * Or using mapper.map with the class of the object model
         */
        super(connectionFactory, mapper, d -> mapper.map(d, User.class /* domain model */), "users", "idx_status" /*
                                                                                                                   * index
                                                                                                                   * is
                                                                                                                   * optional
                                                                                                                   */);
        this.getDynamoDbEnhancedAsyncClient = getDynamoDbEnhancedAsyncClient;
    }

    public Mono<List<User /* domain model */>> getEntityBySomeKeys(String partitionKey, String sortKey) {
        QueryEnhancedRequest queryExpression = generateQueryExpression(partitionKey, sortKey);
        return query(queryExpression);
    }

    public Mono<List<User /* domain model */>> getEntityBySomeKeysByIndex(String partitionKey, String sortKey) {
        QueryEnhancedRequest queryExpression = generateQueryExpression(partitionKey, sortKey);
        return queryByIndex(queryExpression, "idx_status" /* index is optional if you define in constructor */);
    }

    private QueryEnhancedRequest generateQueryExpression(String partitionKey, String sortKey) {
        return QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue(partitionKey).build()))
                .queryConditional(QueryConditional.sortGreaterThanOrEqualTo(Key.builder().sortValue(sortKey).build()))
                .build();
    }

    @Override
    public Mono<User> saveUser(User user) {
        return this.save(user);
    }

    @Override
    public Flux<User> findAll() {
        return this.scan().flatMapMany(Flux::fromIterable);
    }

    @Override
    public void updateStatus(String email, String newStatus) {

    }

    @Override
    public Mono<User> findByEmail(String email) {
        return this.getById(email);
    }
}
