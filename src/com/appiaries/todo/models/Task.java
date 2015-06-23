//
// Copyright (c) 2015 Appiaries Corporation. All rights reserved.
//
package com.appiaries.todo.models;

import android.support.annotation.NonNull;
import android.util.Log;

import com.appiaries.baas.sdk.ABCollection;
import com.appiaries.baas.sdk.ABDBObject;
import com.appiaries.baas.sdk.ABField;
import com.appiaries.baas.sdk.ABQuery;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@ABCollection("Tasks")
public class Task extends ABDBObject implements Comparable<Task>, Serializable {

    private static final String TAG = Task.class.getSimpleName();

    private static final long serialVersionUID = -6713532621492168723L;

    public enum Status {
        NEW, COMPLETED
    }

    public enum Type {
        NORMAL, IMPORTANT
    }

    public static class Field extends ABDBObject.Field {
        public static final ABField USER_ID     = new ABField("user_Id",      String.class); //typo: -> user_id
        public static final ABField TYPE        = new ABField("type",         int.class);
        public static final ABField TITLE       = new ABField("title",        String.class);
        public static final ABField BODY        = new ABField("body",         String.class);
        public static final ABField STATUS      = new ABField("status",       int.class);
        public static final ABField POSITION    = new ABField("position",     int.class);
        public static final ABField SCHEDULE_AT = new ABField("scheduled_at", Date.class);
    }

    public String getUserId() {
        return get(Field.USER_ID);
    }
    public void setUserId(String userId) {
        put(Field.USER_ID, userId);
    }
    public int getType() {
        return get(Field.TYPE);
    }
    public void setType(int type) {
        put(Field.TYPE, type);
    }
    public String getTitle() {
        return get(Field.TITLE);
    }
    public void setTitle(String title) {
        put(Field.TITLE, title);
    }
    public String getBody() {
        return get(Field.BODY);
    }
    public void setBody(String body) {
        put(Field.BODY, body);
    }
    public int getStatus() {
        return get(Field.STATUS);
    }
    public void setStatus(int status) {
        put(Field.STATUS, status);
    }
    public int getPosition() {
        return get(Field.POSITION);
    }
    public void setPosition(int position) {
        put(Field.POSITION, position);
    }
    public Date getScheduledAt() {
        return get(Field.SCHEDULE_AT);
    }
    public void setScheduleAt(Date scheduledAt) {
        put(Field.SCHEDULE_AT, scheduledAt);
    }

    public Task() {
        super("Tasks");
    }

    public static ABQuery query() {
        return ABQuery.query(Task.class);
    }

    @Override
    public Object inputDataFilter(String key, Object value) {
        Object filtered = super.inputDataFilter(key, value);
        if (key.equals(Field.SCHEDULE_AT.getKey())) {
            if (filtered instanceof Long) {
                // Long -> Date
                return new Date((Long)filtered);
            }
        }
        return filtered;
    }

    @Override
    public Object outputDataFilter(String key, Object value) {
        Object filtered = super.outputDataFilter(key, value);
        if (key.equals(Field.SCHEDULE_AT.getKey())) {
            if (filtered instanceof Date) {
                // Date -> Long
                return ((Date)filtered).getTime();
            }
        }
        return filtered;
    }

    public int compareTo(@NonNull Task another) {
        long anotherCreatedTime = another.getCreated().getTime();
        long selfCreatedTime = this.getCreated().getTime();
        if(selfCreatedTime > anotherCreatedTime){
            return 1;
        } else if(selfCreatedTime < anotherCreatedTime){
            return -1;
        }else
            return 0;
    }

}
