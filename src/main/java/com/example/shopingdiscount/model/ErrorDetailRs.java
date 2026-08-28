package com.example.shopingdiscount.model;

import com.example.shopingdiscount.config.handler.ErrorRs;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Data
public class ErrorDetailRs implements Serializable {
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            timezone = "Asia/Bangkok"
    )
    private Timestamp timestamp;
    private List<ErrorRs> errors;
}
