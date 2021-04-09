package com.codeoftheweb.salvo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ShipType {

    @JsonProperty("carrier")
    CARRIER,
    @JsonProperty("battleship")
    BATTLESHIP,
    @JsonProperty("submarine")
    SUBMARINE,
    @JsonProperty("destroyer")
    DESTROYER,
    @JsonProperty("patrolboat")
    PATROL;

}
