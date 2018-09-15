package org.spring.cloud.k8s.concertsservice.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "concert")
public class Concert {

    @Id
    private String id;

    @TextIndexed
    private String name;

    @Indexed
    private String band;

    private String code;

    private Date concertDate;

    private String availableTickets = "N/A";


    public Concert() {
    }

    public Concert(String name, String band, String code) {
        this.name = name;
        this.band = band;
        this.code = code;
        this.concertDate = new Date();
    }

    public Concert(String name, String band, String code, Date concertDate) {
        this.name = name;
        this.band = band;
        this.code = code;
        this.concertDate = concertDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAvailableTickets() {
        return availableTickets;
    }

    public void setAvailableTickets(String availableTickets) {
        this.availableTickets = availableTickets;
    }
}
