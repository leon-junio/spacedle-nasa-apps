package com.spacedle.model;

import java.util.UUID;

public class Imgobj {
    private int id;
    private UUID uuid;
    private String img;

    public Imgobj() {
        uuid = UUID.randomUUID();
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

}
