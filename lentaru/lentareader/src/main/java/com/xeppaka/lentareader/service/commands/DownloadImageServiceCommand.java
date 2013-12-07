package com.xeppaka.lentareader.service.commands;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.xeppaka.lentareader.data.dao.daoobjects.ImageDao;
import com.xeppaka.lentareader.downloader.LentaHttpImageDownloader;
import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.utils.LentaConstants;

import java.io.IOException;

/**
 * Created by nnm on 11/22/13.
 */
public class DownloadImageServiceCommand extends RunnableServiceCommand {
    private Bundle result;
    private String url;
    private Context context;

    public DownloadImageServiceCommand(int requestId, Context context, String url, ResultReceiver resultReceiver) {
        super(requestId, resultReceiver);

        this.url = url;
        this.context = context;
    }

    @Override
    public void execute() throws Exception {
        Log.d(LentaConstants.LoggerServiceTag, "Command started: " + getClass().getSimpleName());

        try {
            Bitmap newBitmap = LentaHttpImageDownloader.downloadBitmap(url);
            ImageDao.newInstance().create(url, newBitmap);

            Log.d(LentaConstants.LoggerServiceTag, "Successfuly downloaded and saved image: " + url);
        } catch (HttpStatusCodeException e) {
            Log.e(LentaConstants.LoggerServiceTag,
                    "Error loading image from URL: " + url, e);
            throw e;
        } catch (IOException e) {
            Log.e(LentaConstants.LoggerServiceTag,
                    "Error loading image from URL: " + url, e);
            throw e;
        }
    }
}
