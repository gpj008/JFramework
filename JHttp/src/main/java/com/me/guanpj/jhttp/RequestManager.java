package com.me.guanpj.jhttp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Jie on 2017/4/9.
 */

public class RequestManager {

    private static RequestManager mInstance;
    private Map<String, ArrayList<Request>> mRequests;
    private Executor mExcutor;

    public static RequestManager getInstance(){
        if(mInstance == null){
            mInstance = new RequestManager();
        }
        return mInstance;
    }

    private RequestManager(){
        mRequests = new HashMap<>();
        mExcutor = Executors.newFixedThreadPool(5);
    }

    public void performRequest(Request request){
        request.excete(mExcutor);
        if(request.tag == null || "".equals(request.tag.trim())){
            return;
        }
        if(!mRequests.containsKey(request.tag)){
            ArrayList<Request> requestsWithTag = new ArrayList<>();
            mRequests.put(request.tag, requestsWithTag);
        }
        mRequests.get(request.tag).add(request);
    }

    public void cancelRequest(String tag){
        cancelRequest(tag, false);
    }

    public void cancelRequest(String tag, boolean force){
        if(tag == null || "".equals(tag.trim())){
            return;
        }
        if (mRequests.containsKey(tag)) {
            ArrayList<Request> need2Cancel = mRequests.remove(tag);
            for (Request request : need2Cancel) {
                if(!request.isCancelled) {
                    request.cancel(force);
                }
            }
        }
    }

    public void cancelAll(){
        for(Map.Entry<String, ArrayList<Request>> entry : mRequests.entrySet()){
            ArrayList<Request> need2Cancel = entry.getValue();
            for (Request request : need2Cancel) {
                if(!request.isCancelled) {
                    request.cancel(true);
                }
            }
        }
        mRequests.clear();
    }
}
