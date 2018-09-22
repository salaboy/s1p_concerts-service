package org.spring.cloud.k8s.concertsservice.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.cloud.k8s.concertsservice.config.ConcertsConfiguration;
import org.spring.cloud.k8s.concertsservice.model.Concert;
import org.spring.cloud.k8s.concertsservice.repo.ConcertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RefreshScope
public class ConcertServiceImpl implements ConcertService {

    private static final Logger log = LoggerFactory.getLogger(ConcertServiceImpl.class);

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private ConcertsConfiguration config;

    @Override
    public Mono<Concert> createConcert(Concert concert) {
        if (concert.getConcertDate() == null) {
            concert.setConcertDate(new Date());
        }
        return concertRepository.insert(concert);
    }


    @Override
    public Flux<Concert> findAll() {
        Flux<Concert> allConcerts = concertRepository.findAll();
        // For each concert:
        //  - look for a ticket service with the concert name
        //  -- If found get the amount of available tickets
        //  -- decorate the concert with the available tickets
        //  -
        log.info("Decorate all Services enabled?: " + config.getDecorate());
        Boolean decorate = new Boolean(config.getDecorate());
        if (decorate) {
            List<Concert> concerts = allConcerts.toStream().collect(Collectors.toList());
            concerts.forEach(concert -> findMatchingTicketsService(concert)
                    .ifPresent(serviceInstance -> decorateConcertWithTicketsInfo(concert, serviceInstance)));
            return Flux.fromIterable(concerts);
        }
        return allConcerts;
    }

    @Override
    public Mono<Concert> updateConcert(Concert concert, String id) {
        return findOne(id).doOnSuccess(findConcert -> {
            findConcert.setConcertDate(concert.getConcertDate());
            findConcert.setName(concert.getName());
            findConcert.setBand(concert.getBand());
            findConcert.setCode(concert.getCode());
            concertRepository.save(findConcert).subscribe();
        });
    }

    @Override
    public Mono<Concert> findOne(String id) {
        // Get the concert from the local Repo
        Mono<Concert> concertMono = concertRepository.findById(id).
                switchIfEmpty(Mono.error(new Exception("No Concert found with Id: " + id)));

        Optional<ServiceInstance> ticketsServiceForConcert = findMatchingTicketsService(concertMono.block());

        ticketsServiceForConcert.ifPresent(serviceInstance -> decorateConcertWithTicketsInfo(concertMono.block(), serviceInstance));

        return concertMono;
    }

    private Optional<ServiceInstance> findMatchingTicketsService(Concert concert) {
        List<String> services = discoveryClient.getServices();

        Optional<ServiceInstance> ticketsServiceForConcert = Optional.empty();

        // Get service instance to check for extra metadata to bind concert code with tickets services
        for (String ticketsService : services) {
            List<ServiceInstance> instances = discoveryClient.getInstances(ticketsService);
            ticketsServiceForConcert = instances.stream()
                    .filter(instance -> concert.getCode().equals(instance.getMetadata().get("code"))).findFirst();
        }
        return ticketsServiceForConcert;
    }

    private Mono<Concert> decorateConcertWithTicketsInfo(Concert concert, ServiceInstance ticketsServiceForConcert) {
        log.info("Decorating Concert with Service : " + ticketsServiceForConcert.getServiceId());

        WebClient webClient = WebClient.builder().baseUrl("http://" + ticketsServiceForConcert.getServiceId()).build();

        WebClient.RequestHeadersSpec<?> request = webClient.get().uri("/tickets");

        Mono<Integer> availableTickets = request
                .retrieve()
                .bodyToMono(Integer.class);

        String remainingTickets = availableTickets.block().toString();
        log.info("Available Tickets for " + concert.getName() + ": " + remainingTickets);

        concert.setAvailableTickets(remainingTickets);

        return Mono.just(concert);
//
//        // Decorate concert with available Tickets
//        return concertMono.zipWith(availableTickets).map(
//                tuple -> {
//                    tuple.getT1().setAvailableTickets(tuple.getT2().toString());
//                    return tuple.getT1();
//                });
    }

    @Override
    public Flux<Concert> findByName(String name) {
        return concertRepository.findByName(name)
                .switchIfEmpty(Mono.error(new Exception("No Concert found with name Containing : " + name)));
    }

    @Override
    public Mono<Void> delete(String id) {
        return concertRepository.deleteById(id);
    }
}
