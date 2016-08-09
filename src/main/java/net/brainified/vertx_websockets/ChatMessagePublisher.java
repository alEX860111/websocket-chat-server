package net.brainified.vertx_websockets;

interface ChatMessagePublisher {

  void publish(String msg);

}