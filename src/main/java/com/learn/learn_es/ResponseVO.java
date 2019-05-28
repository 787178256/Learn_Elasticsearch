package com.learn.learn_es;

import lombok.Data;
import org.springframework.http.HttpStatus;


/**
 * Created by kimvra on 2019-05-28
 */
@Data
public class ResponseVO<T> {

    private T data;

}
