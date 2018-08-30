package org.spring.cloud.k8s.concertsservice.repo;


import org.spring.cloud.k8s.concertsservice.model.Concert;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ConcertRepository extends ReactiveMongoRepository<Concert, String> {
    Flux<Concert> findByAuthor(String author);

    Mono<Concert> findByBandandDeleteIsFalse(String title);

}
