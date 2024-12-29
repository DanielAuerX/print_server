package com.xyz.printserver.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class DocIdContainer {
    private HashMap<UUID, DocMetaData>container;
    private static final Logger log = LoggerFactory.getLogger(DocIdContainer.class);


    public DocIdContainer() {
        this.container = new HashMap<>();
    }

    public void add(final UUID key, final DocMetaData value){
        container.put(key, value);
    }

    public DocMetaData getValue(final UUID key){
        return container.get(key);
    }

    public void remove(final UUID key){
        DocMetaData value = container.remove(key);
        if (value == null){
            log.warn("the container does not contain the key {}", key.toString());
        }
    }
}
