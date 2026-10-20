package com.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 轮播图
 */
@Data
public class Carousel implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String img;
    private String name;
    /** 发布日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate time;
    /** 发布人 */
    private String user;
}
