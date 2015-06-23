//
// Copyright (c) 2015 Appiaries Corporation. All rights reserved.
//
package com.appiaries.todo.models;

import com.appiaries.baas.sdk.ABCollection;
import com.appiaries.baas.sdk.ABField;
import com.appiaries.baas.sdk.ABUser;

import java.util.Map;

@ABCollection
public class User extends ABUser {

    public static class Field extends ABUser.Field {
        public static final ABField NICKNAME = new ABField("nickname", String.class);
    }

    public User() {
        super();
    }

    public User(Map<String, Object> map) {
        super(map);
    }

}
