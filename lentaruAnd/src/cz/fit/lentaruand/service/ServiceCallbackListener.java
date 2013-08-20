package cz.fit.lentaruand.service;

import java.util.Collection;

import cz.fit.lentaruand.data.NewsType;

public interface ServiceCallbackListener {
    void onDatabaseObjectsCreated(NewsType newsType, Collection<Long> newIds);
    void onDatabaseObjectCreated(NewsType newsType, long newId);
    void onDatabaseObjectsUpdated(NewsType newsType, Collection<Long> ids);
    void onDatabaseObjectUpdated(NewsType newsType, long id);
    void onImageUpdated(NewsType newsType, long id);
}