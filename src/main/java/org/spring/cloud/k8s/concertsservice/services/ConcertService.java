package org.spring.cloud.k8s.concertsservice.services;

import org.spring.cloud.k8s.concertsservice.model.Concert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface ConcertService {

    Mono<Concert> createConcert(Concert concert);

    Mono<Concert> updateConcert(Concert concert, String id);

    Flux<Concert> findAll();

    Mono<Concert> findOne(String id);

    Flux<Concert> findByName(String name);

    Mono<Boolean> delete(String id);

}
