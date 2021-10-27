module de.bhtpaf.pacbomb {
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

    exports de.bhtpaf.pacbomb;
    exports de.bhtpaf.pacbomb.controllers;
    exports de.bhtpaf.pacbomb.services;
    exports de.bhtpaf.pacbomb.helper.classes;
}