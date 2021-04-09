package com.codeoftheweb.salvo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
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
    private List<String> shipLocations;

    public Ship() {
    }

    public Ship(ShipType type, List<String> shipLocations, GamePlayer gamePlayer) {
        this.shipLocations = shipLocations;
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

    public List<String> getShipLocations() {
        return shipLocations;
    }

    public void setShipLocations(List<String> shipLocations) {
        this.shipLocations = shipLocations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public void addShipLocations(List<String> shipLocations) {
        this.shipLocations.addAll(shipLocations);
    }

    public Map<String, Object> shipDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("locations", getShipLocations());
        dto.put("type", type);

        return dto;
    }

}
