package com.codeoftheweb.salvo.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    private ShipType type;

    @ElementCollection
    @Column(name = "shipLocation")
    private List<String> locations = new ArrayList<>();

    public Ship() {
    }

    public Ship(ShipType type, List<String> locations, GamePlayer gamePlayer) {
        this.locations = locations;
        this.type = type;
        this.gamePlayer = gamePlayer;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ShipType getType() {
        return type;
    }

    public void setShipType(ShipType type) {
        this.type = type;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> shipLocations) {
        this.locations = shipLocations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public void addLocations(List<String> shipLocations) {
        this.locations.addAll(shipLocations);
    }

    public Map<String, Object> shipDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("locations", getLocations());
        dto.put("type", type);

        return dto;
    }

}
