package com.xeppaka.lentareader.service;

import com.xeppaka.lentareader.data.NewsType;

import java.util.Collection;

public interface ServiceCallbackListener {
    void onDatabaseObjectsCreate(int requestId, NewsType newsType, Collection<Long> newIds);
    void onDatabaseObjectsUpdate(int requestId, NewsType newsType, Collection<Long> ids);
    void onImagesUpdate(int requestId, NewsType newsType, Collection<Long> ids);
    
    void onFailed(int requestId, Exception e);
}