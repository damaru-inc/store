package com.damaru.store;

import com.damaru.store.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
//import com.damaru.store

@SpringBootApplication
@ComponentScan("com.damaru")
public class StoreApplication implements CommandLineRunner {

    public static final Logger log = LoggerFactory.getLogger(StoreApplication.class);

    @Autowired
    ItemViewChannel itemViewChannel;
    @Autowired
    QueryChannel queryChannel;

    public static void main(String[] args) {
        SpringApplication.run(StoreApplication.class, args);
    }

    PublishListener itemPublishListener = new PublishListener() {

        @Override
        public void onResponse(String s) {
            log.info("itemPublshListener.onResponse: " + s);
        }

        @Override
        public void handleException(String s, Exception e, long l) {
            log.error(e.getMessage());
        }
    };

    QueryEvent.SubscribeListener querySubscribeListener = new QueryEvent.SubscribeListener() {

        @Override
        public void onReceive(QueryEvent queryEvent) {
            try {
                handleQuery(queryEvent);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        @Override
        public void handleException(Exception e) {

        }
    };

    ItemViewEvent.SubscribeListener itemSubscribeListener = new ItemViewEvent.SubscribeListener() {

        @Override
        public void onReceive(ItemViewEvent itemViewEvent) {
            log.info("Got " + itemViewEvent);
        }

        @Override
        public void handleException(Exception e) {
            log.error(e.getMessage());
        }
    };

    @Override
    public void run(String... args) throws Exception {
        itemViewChannel.initPublisher(itemPublishListener);
        queryChannel.subscribe(querySubscribeListener);
        itemViewChannel.subscribe(itemSubscribeListener);
    }

    private void handleQuery(QueryEvent queryEvent) throws Exception {
        log.info(queryEvent.toString());
        String topic = queryEvent.getTopic();
        int lastSlash = topic.lastIndexOf('/');
        String originatorId = topic.substring(lastSlash+1);
        log.info("originator: " + originatorId);

        // TODO: get the data from the database.
        ItemViewArray.ItemView item = new ItemViewArray.ItemView();
        item.setCategory("coffee");
        item.setDescription("Komodo Dragon");
        item.setPrice(23.45);
        ItemViewArray.ItemView[] items = { item };
        ItemViewArray arr = new ItemViewArray(items);
        itemViewChannel.sendItemViewArray(arr, originatorId);
    }
}
