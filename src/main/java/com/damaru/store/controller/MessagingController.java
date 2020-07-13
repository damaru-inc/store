package com.damaru.store.controller;

import com.damaru.store.entity.CategoryRepository;
import com.damaru.store.entity.Item;
import com.damaru.store.entity.ItemRepository;
import com.damaru.store.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessagingController {

    public static final Logger log = LoggerFactory.getLogger(MessagingController.class);

    @Autowired
    ItemViewChannel itemViewChannel;
    @Autowired
    QueryChannel queryChannel;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ItemRepository itemRepository;

    @PostConstruct
    public void init() throws Exception {
        itemViewChannel.initPublisher(itemPublishListener);
        queryChannel.subscribe(querySubscribeListener);
    }


    PublishListener itemPublishListener = new PublishListener() {

        @Override
        public void onResponse(String s) {
            log.info("itemPublshListener.onResponse: " + s);
        }

        @Override
        public void handleException(String s, Exception e, long l) {
            log.error("itemPublishListener.handleException: " + e.getMessage());
        }
    };

    QueryEvent.SubscribeListener querySubscribeListener = new QueryEvent.SubscribeListener() {

        @Override
        public void onReceive(QueryEvent queryEvent) {
            try {
                handleQuery(queryEvent);
            } catch (Exception e) {
                log.error("querySubscribeListener.onReceive: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public void handleException(Exception e) {
            log.error("querySubscribeListener.handleException: " + e.getMessage());
        }
    };

    private void handleQuery(QueryEvent queryEvent) throws Exception {
        log.info(queryEvent.toString());
        String topic = queryEvent.getTopic();
        int lastSlash = topic.lastIndexOf('/');
        String originatorId = topic.substring(lastSlash+1);
        log.info("originator: " + originatorId);

        List<Item> items = (List<Item>) itemRepository.findAll();
        log.info(items.toString());
        ItemViewArray.ItemView[] itemsArray = new ItemViewArray.ItemView[items.size()];
        List<ItemViewArray.ItemView> itemViews = items.stream()
                .map(item -> new ItemViewArray.ItemView(0.0, item.getDescription(), item.getId().intValue(),
                        item.getCategory().getDescription()))
                .collect(Collectors.toList());
        itemViews.toArray(itemsArray);
        ItemViewArray ret = new ItemViewArray(itemsArray);
        itemViewChannel.sendItemViewArray(ret, originatorId);
    }

}
