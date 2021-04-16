package com.codeoftheweb.salvo.model;

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

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Ship> ships = new HashSet<>();

    @OrderBy
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

    public void setId(long id) {
        this.id = id;
    }

    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }

    public void setSalvos(Set<Salvo> salvos) {
        this.salvos = salvos;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    public long getId() {
        return this.id;
    }

    public Player getPlayer() {
        return player;
    }

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

    public void addShip(Ship ship) {
        ship.setGamePlayer(this);
        ships.add(ship);
    }

    public void addSalvo(Salvo salvo) {
        this.salvos.add(salvo);
        salvo.setGamePlayer(this);
    }

    public Set<Ship> getShip() {
        return ships;
    }

    public GamePlayer getOpponent() {
        return this.getGame().getGamePlayers()
                .stream().filter(gamePlayer -> gamePlayer.getId() != this.getId()).findFirst().orElse(new GamePlayer());
    }

    private int lastTurn(GamePlayer gp) {
        return gp.getSalvos()
                .stream()
                .mapToInt(salvo -> salvo.getTurn())
                .max()
                .orElse(0);
    }

    public Map<String, Object> gamePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("gpid", getId());
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


