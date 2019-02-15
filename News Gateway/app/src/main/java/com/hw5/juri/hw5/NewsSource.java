package com.hw5.juri.hw5;

import java.io.Serializable;

public class NewsSource implements Serializable{
    private String sourceId;
    private String sourceName;
    private String category;

    public NewsSource(String sourceId, String sourceName, String category){
        this.sourceId = sourceId;
        this.sourceName = sourceName;
        this.category = category;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return sourceName;
    }
}
