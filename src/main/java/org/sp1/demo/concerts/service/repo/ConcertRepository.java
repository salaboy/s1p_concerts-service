package org.sp1.demo.concerts.service.repo;


import org.sp1.demo.concerts.service.model.Concert;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ConcertRepository extends ReactiveMongoRepository<Concert, String> {
    Flux<Concert> findByName(String name);


    Mono<Concert> findByBand(String band);

}
