package com.hyber;

import io.realm.annotations.RealmModule;

@RealmModule(library = true, classes = {User.class, Session.class, Message.class})
public class HyberSchemaModule {
}
