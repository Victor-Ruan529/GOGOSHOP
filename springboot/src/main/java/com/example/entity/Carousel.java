package com.example.entity;

import java.io.Serializable;

public class Carousel extends Account implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String img;

    private String name;

    /** 创建时间 */
    private String time;
    /** 创建人 */
    private String user;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public Integer getId(){
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
