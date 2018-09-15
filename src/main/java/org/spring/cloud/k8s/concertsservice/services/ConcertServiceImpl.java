package org.spring.cloud.k8s.concertsservice.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.cloud.k8s.concertsservice.model.Concert;
import org.spring.cloud.k8s.concertsservice.repo.ConcertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
public class ConcertServiceImpl implements ConcertService {

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
        for (String s : services) {
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
        // Get the concert from the local Repo
        Mono<Concert> concertMono = concertRepository.findById(id).
                switchIfEmpty(Mono.error(new Exception("No Concert found with Id: " + id)));
        // List Available Services (already filtered by type)
        List<String> services = discoveryClient.getServices();

        Optional<ServiceInstance> ticketsServiceForConcert = Optional.empty();
        // Get service instance to check for extra metadata to bind concert code with tickets services
        for (String ticketsService : services) {
            List<ServiceInstance> instances = discoveryClient.getInstances(ticketsService);
            ticketsServiceForConcert = instances.stream()
                    .filter(instance -> concertMono.block().getCode().equals(instance.getMetadata().get("code"))).findFirst();

        }

        if(ticketsServiceForConcert.isPresent()) {

            log.info("Tickets Service Discovered : " + ticketsServiceForConcert.get().getServiceId());

            WebClient webClient = WebClient.builder().baseUrl("http://" + ticketsServiceForConcert.get().getServiceId()).build();

            WebClient.RequestHeadersSpec<?> request = webClient.get().uri("/tickets");

            Mono<Integer> availableTickets = request
                    .retrieve()
                    .bodyToMono(Integer.class);

            log.info("Tickets : " + availableTickets.block());
            // Decorate concert with available Tickets
            Mono<Concert> decoratedConcert = concertMono.zipWith(availableTickets).map(
                    tuple -> {
                        tuple.getT1().setAvailableTickets(tuple.getT2().toString());
                        return tuple.getT1();
                    });


            return decoratedConcert;
        }else{
            log.info("No Tickets Service found for Concert Code: " + concertMono.block().getCode());
        }



        return concertMono;
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
