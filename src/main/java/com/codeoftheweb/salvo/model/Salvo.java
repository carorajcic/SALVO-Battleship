package com.codeoftheweb.salvo.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private int turn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name = "salvoLocations")
    private List<String> locations = new ArrayList<>();

    public Salvo() {
    }

    public Salvo(GamePlayer gp, int turn, List<String> locations) {
        this.turn = turn;
        this.gamePlayer = gp;
        this.locations = locations;
    }

    public long getId() {
        return id;
    }

   /* public void setTurn(int turn){
        this.turn = turn;
    } */

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public int getTurn() {
        return turn;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void addLocations(List<String> locations) {
        this.locations = locations;
    }

    public Map<String, Object> salvoDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", this.getTurn());
        dto.put("player", this.getGamePlayer().getPlayer().getId());
        dto.put("locations", this.getLocations());

        return dto;
    }


}
