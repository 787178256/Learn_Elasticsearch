package com.learn.learn_es;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kimvra on 2019-05-28
 */
@Data
@AllArgsConstructor
public class Novel {
    private String title;

    private String author;

    private Integer wordCount;

    private long publishDate;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", this.title);
        map.put("author", this.author);
        map.put("word_count", this.wordCount);
        map.put("publish_date", publishDate);

        return map;
    }

    public static void main(String[] args) {
        System.out.println(new Date().getTime());
    }
}
