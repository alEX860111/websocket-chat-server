package net.brainified.vertx_websockets;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import io.vertx.core.Vertx;

public final class Main {

  public static void main(String[] args) {
    final Injector injector = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
        // TODO Auto-generated method stub

      }
    });
    Vertx.vertx().deployVerticle(injector.getInstance(Verticle.class));
  }

}
