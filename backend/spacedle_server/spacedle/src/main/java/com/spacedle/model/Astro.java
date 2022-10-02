package com.spacedle.model;

import java.io.File;
import java.util.UUID;

import com.spacedle.app.Utils;

public class Astro {
    private UUID id;
    private String nome, descricao;
    private Imgobj img[];

    public Astro() {
        id = UUID.randomUUID();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Imgobj[] getImg() {
        return img;
    }

    public void setImgLen(int len) {
        this.img = new Imgobj[len];
    }

    public void setImg(Imgobj img[]) {
        this.img = img;
    }

    public void setImgByFl(int i, int idFl, File fl) {
        Imgobj ob = new Imgobj();
        ob.setId(idFl);
        ob.setImg(Utils.getBase64(fl));
        this.img[i] = ob;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
