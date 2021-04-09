package com.codeoftheweb.salvo.model;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;


@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    private double points;

    private LocalDateTime finishDate;

    public Score() {
    }

    public Score(Game game, Player player, double points, LocalDateTime finishDate) {
        this.player = player;
        this.game = game;
        this.points = points;
        this.finishDate = finishDate;
    }

    public long getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDateTime finishDate) {
        this.finishDate = finishDate;
    }

    public double getScore() {
        return this.points;
    }

    public Map<String, Object> scoreDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("score", this.points);
        dto.put("player", this.player.getId());
        dto.put("finishDate", getFinishDate());

        return dto;
    }

}
