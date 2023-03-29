package com.bofry.databroker.core.component;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Placeholder implements Serializable {

    private static final long serialVersionUID = 3074089687121250696L;

    /**
     * 'CALL ts_write_game_bet(%{*:json}, NULL)'
     */

    /**
     * - Type:
     *   - document  => %{*:json}
     *   - column    => %{player:json}
     * - Name:
     * - Handler
     *   - json
     *   - ....
     */

    // TODO Enum
    private String name;
    private String[] handlers;

    public boolean isDocument() {
        return this.name.equals("*");
    }

}
