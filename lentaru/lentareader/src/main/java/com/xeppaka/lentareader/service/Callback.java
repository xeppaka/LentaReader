package com.xeppaka.lentareader.service;

public interface Callback {
//    void onDatabaseObjectsCreate(int requestId, NewsType newsType, Collection<Long> newIds);
//    void onDatabaseObjectsUpdate(int requestId, NewsType newsType, Collection<Long> ids);
//    void onImagesUpdate(int requestId, NewsType newsType, Collection<Long> ids);
//
//    void onFailed(int requestId, Exception e);
    void onSuccess();
    void onFailure();
}