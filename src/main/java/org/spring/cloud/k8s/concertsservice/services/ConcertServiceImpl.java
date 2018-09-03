package org.spring.cloud.k8s.concertsservice.services;

import org.spring.cloud.k8s.concertsservice.model.Concert;
import org.spring.cloud.k8s.concertsservice.repo.ConcertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ConcertServiceImpl implements ConcertService{

    @Autowired
    private ConcertRepository concertRepository;

    @Override
    public Mono<Concert> createConcert(Concert concert) {
        return concertRepository.insert(concert);
    }

    @Override
    public Flux<Concert> findAll() {
        return concertRepository.findAll();
    }

    @Override
    public Mono<Concert> updateConcert(Concert concert, String id) {
        return findOne(id).doOnSuccess(findConcert -> {
            findConcert.setConcertDate(concert.getConcertDate());
            findConcert.setName(concert.getName());
            findConcert.setBand(concert.getBand());
            concertRepository.save(findConcert).subscribe();
        });
    }

    @Override
    public Mono<Concert> findOne(String id) {
        return concertRepository.findById(id).
                switchIfEmpty(Mono.error(new Exception("No Concert found with Id: " + id)));
    }

    @Override
    public Flux<Concert> findByName(String name) {
        return concertRepository.findByName(name)
                .switchIfEmpty(Mono.error(new Exception("No Concert found with name Containing : " + name)));
    }

    @Override
    public Mono<Boolean> delete(String id) {
        return findOne(id).doOnSuccess(concert -> {
            concert.setDelete(true);
            concertRepository.save(concert).subscribe();
        }).flatMap(blog -> Mono.just(Boolean.TRUE));
    }
}
