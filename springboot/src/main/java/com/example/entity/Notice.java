package com.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 公告
 */
@Data
public class Notice implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String title;
    private String content;
    /** 发布日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate time;
    /** 发布人 */
    private String user;
}
