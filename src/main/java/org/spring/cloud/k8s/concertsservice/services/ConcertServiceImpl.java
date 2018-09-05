package org.spring.cloud.k8s.concertsservice.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.cloud.k8s.concertsservice.controllers.ConcertController;
import org.spring.cloud.k8s.concertsservice.model.Concert;
import org.spring.cloud.k8s.concertsservice.repo.ConcertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ConcertServiceImpl implements ConcertService{

    private static final Logger log = LoggerFactory.getLogger(ConcertServiceImpl.class);

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Override
    public Mono<Concert> createConcert(Concert concert) {
        return concertRepository.insert(concert);
    }

    @Override
    public Flux<Concert> findAll() {
        // For each concert:
        //  - look for a ticket service with the concert name
        //  -- If found get the amount of available tickets
        //  -- decorate the concert with the available tickets
        //  -
        List<String> services = discoveryClient.getServices();
        for(String s : services){
            log.info("Discovered Service: " + s);
        }
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

//        List<String> services = discoveryClient.getServices();
//        for(String s : services){
//            log.info("Discovered Service: " + s);
//        }

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
