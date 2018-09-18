package org.spring.cloud.k8s.concertsservice.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.cloud.k8s.concertsservice.model.Concert;
import org.spring.cloud.k8s.concertsservice.services.ConcertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/v1/concert")
public class ConcertController {

    private static final Logger log = LoggerFactory.getLogger(ConcertController.class);

    @Autowired
    private ConcertService concertService;

    @GetMapping
    public Flux<Concert> findAll() {
        log.debug("findAll Concert");
        return concertService.findAll();
    }

    @GetMapping("/find")
    public Flux<Concert> findByTitle(@RequestParam String concertName) {
        log.debug("findByTitle Blog with blogTitle : {}", concertName);
        return concertService.findByName(concertName);
    }

    @GetMapping("/{id}")
    public Mono<Concert> findOne(@PathVariable String id) {
        log.debug("findOne Concert with id : {}", id);
        return concertService.findOne(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Concert> create(@RequestBody Concert concert) {
        log.debug("create concert with Concert : {}", concert);
        return concertService.createConcert(concert);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        log.debug("delete Concert with id : {}", id);
        return concertService.delete(id);
    }

    @PutMapping("/{id}")
    public Mono<Concert> updateBlog(@RequestBody Concert concert, @PathVariable String id) {
        log.debug("updateBlog Blog with id : {} and blog : {}", id, concert);
        return concertService.updateConcert(concert, id);
    }

}
