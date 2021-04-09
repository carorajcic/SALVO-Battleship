package com.codeoftheweb.salvo.model;

import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private LocalDateTime joinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @JsonIgnore
    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Salvo> salvos = new HashSet<>();

    public Set<Ship> getShips() {
        return ships;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public GamePlayer() {
    }

    public GamePlayer(Game game, Player player) {
        this.joinDate = LocalDateTime.now();
        this.game = game;
        this.player = player;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    public long getId() {
        return this.id;
    }

    @JsonIgnore
    public Player getPlayer() {
        return player;
    }

    @JsonIgnore
    public Game getGame() {
        return game;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Set<Salvo> getSalvos() {
        return this.salvos;
    }

    public Map<String, Object> gamePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", id);
        dto.put("player", player.playerDTO());

        return dto;
    }

    public Map<String, Object> game_view() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreatedDate());
        //porque pide mas de un gamePlayer
        dto.put("gamePlayers", game.getGamePlayers().stream().map(gamePlayer1 -> gamePlayer1.gamePlayerDTO()).collect(Collectors.toList()));
        dto.put("ships", getShips().stream().map(Ship -> Ship.shipDTO()).collect(Collectors.toList()));
        dto.put("salvoes", getGame().getGamePlayers().stream().flatMap(gamePlayer -> gamePlayer.getSalvos().stream().map(salvo -> salvo.salvoDTO())).collect(Collectors.toList()));

        return dto;
    }
}


