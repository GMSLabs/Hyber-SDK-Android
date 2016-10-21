package com.gms_worldwide.hybersdk;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by Andrew Kochura.
 */
class HyberAlphaNameDBModel implements Serializable {

    /**
     * The Id.
     */
// id is generated by the database and set on the object default (id = 1) for current user
    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField(columnName = "name")
    private String name = "";

    /**
     * Instantiates a Hyber alpha name db model.
     */
    public HyberAlphaNameDBModel() {
    }

    /**
     * Instantiates a Hyber alpha name db model.
     *
     * @param name the name
     */
    public HyberAlphaNameDBModel(String name) {
        this.name = name;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }
}