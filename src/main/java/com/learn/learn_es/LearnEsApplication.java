package com.learn.learn_es;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@SpringBootApplication
@RestController
public class LearnEsApplication {

    @Autowired
    private TransportClient transportClient;

    public static void main(String[] args) {
        SpringApplication.run(LearnEsApplication.class, args);
    }

    @GetMapping("get/book/novel")
    public Object getById(@RequestParam("id") String id) {
        GetResponse result = transportClient.prepareGet("book", "novel", id).get();
        return result.getSource();
    }

    @PostMapping("add/book/novel")
    public Object addNovel(@RequestParam("title")String title,
                           @RequestParam("author")String author,
                           @RequestParam("word_count")int wordCount,
                           @DateTimeFormat(pattern = "yyyy-MM-dd")
                           @RequestParam("publish_date")Date publishDate) throws IOException {
        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()
                .startObject()
                .field("title", title)
                .field("author", author)
                .field("word_count", wordCount)
                .field("publish_date", publishDate.getTime())
                .endObject();
        IndexResponse response = transportClient.prepareIndex("book", "novel").setSource(xContentBuilder).get();
        return response.getId();
    }

    @DeleteMapping("delete/book/novel")
    public Object deleteNovel(@RequestParam("id")String id) {
        DeleteResponse response = transportClient.prepareDelete("book", "novel", id).get();
        return response.getResult();
    }

    @PutMapping("update/book/novel")
    public Object updateNovel(@RequestParam("id")String id,
                              @RequestParam(value = "title", required = false)String title,
                              @RequestParam(value = "author", required = false)String author) throws Exception{
        UpdateRequest updateRequest = new UpdateRequest("book", "novel", id);
        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder().startObject();
        if (title != null) {
            xContentBuilder.field("title", title);
        }
        if (author != null) {
            xContentBuilder.field("author", author);
        }
        xContentBuilder.endObject();
        updateRequest.doc(xContentBuilder);
        return transportClient.update(updateRequest).get().getResult();
    }

    @PostMapping("query/book/novel")
    public Object query(@RequestParam(value = "author", required = false)String author,
                        @RequestParam(value = "title", required = false)String title,
                        @RequestParam(value = "gt_word_count", defaultValue = "0")int gtWordCount,
                        @RequestParam(value = "gt_word_count", required = false)Integer ltWordCount) {
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        if (author != null) {
            builder.must(QueryBuilders.matchQuery("author", author));
        }
        if (title != null) {
            builder.must(QueryBuilders.matchQuery("title", title));
        }
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("word_count").from(gtWordCount);
        if (ltWordCount != null && gtWordCount > 0) {
            rangeQueryBuilder.to(ltWordCount);
        }
        builder.filter(rangeQueryBuilder);
        return transportClient.prepareSearch("book")
                .setTypes("novel")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(builder)
                .setFrom(0).setSize(10).get();
    }

    @PostMapping("add/index")
    public Object addIndex(@RequestParam("index")String index,
                           @RequestParam("type")String type,
                           @RequestParam("id")String id) throws Exception{
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .field("user", "kimvra")
                .field("postData", new Date())
                .field("message", "trying out elasticseach")
                .endObject();
        IndexResponse response = transportClient.prepareIndex(index, type, id)
                .setSource(builder).get();
        Map<String, String> map = new HashMap<>();
        map.put("index: ", response.getIndex());
        map.put("type: ", response.getType());
        map.put("id: ", response.getId());

//        new BulkRequest().add(new IndexRequest("index", "type", "id").source(new HashMap()));
//        transportClient.bulk(new BulkRequest())
        return map;
    }

    @GetMapping("batchInsert")
    public Object batchInsert() throws Exception{
        List<Novel> novels = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Novel novel = new Novel("novel" + i, "author" + i, i, new Date().getTime());
            novels.add(novel);
        }
        BulkRequest bulkRequest = new BulkRequest();
        novels.stream().forEach(novel -> {
            bulkRequest.add(new IndexRequest("book", "novel").source(novel.toMap()));
        });
        BulkResponse response = transportClient.bulk(bulkRequest).get();
        return response;
    }
}
