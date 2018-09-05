package org.spring.cloud.k8s.concertsservice.model;


import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "concert")
public class Concert extends BaseEntity {

    @TextIndexed
    private String name;

    @Indexed
    private String band;

    private Date concertDate;

    private String availableTickets = "N/A";


    public Concert() {
    }

    public Concert(String name, String band, Date concertDate) {
        this.name = name;
        this.band = band;
        this.concertDate = concertDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public Date getConcertDate() {
        return concertDate;
    }

    public void setConcertDate(Date concertDate) {
        this.concertDate = concertDate;
    }

    public String getAvailableTickets() {
        return availableTickets;
    }

    public void setAvailableTickets(String availableTickets) {
        this.availableTickets = availableTickets;
    }
}
