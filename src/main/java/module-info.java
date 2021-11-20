module de.bhtpaf.flagbomb {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires javafx.media;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires com.google.gson;
    requires javax.websocket.client.api;
    requires tyrus.websocket.core;
    requires java.desktop;

    exports de.bhtpaf.flagbomb;
    exports de.bhtpaf.flagbomb.controllers;
    exports de.bhtpaf.flagbomb.services;
    exports de.bhtpaf.flagbomb.helper;
    exports de.bhtpaf.flagbomb.helper.classes;
    exports de.bhtpaf.flagbomb.helper.classes.json;
    exports de.bhtpaf.flagbomb.helper.classes.webSocketData;
    exports de.bhtpaf.flagbomb.helper.classes.map;
    exports de.bhtpaf.flagbomb.helper.classes.map.items;
    exports de.bhtpaf.flagbomb.helper.responses;
}