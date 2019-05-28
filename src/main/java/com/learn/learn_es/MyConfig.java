package com.learn.learn_es;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by kimvra on 2019-05-28
 */
@Configuration
public class MyConfig {

    @Bean
    public TransportClient transportClient() throws UnknownHostException  {
        TransportAddress node = new TransportAddress(InetAddress.getByName("localhost"), 9300);
        Settings settings = Settings.builder().put("cluster.name", "kimvra").build();

        TransportClient transportClient = new PreBuiltTransportClient(settings);
        transportClient.addTransportAddress(node);

        return transportClient;
    }

}
