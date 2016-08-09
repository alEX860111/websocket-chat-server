package net.brainified.vertx_websockets;

import javax.inject.Inject;

import io.vertx.core.eventbus.Message;

final class ChatMessageReceiverImpl implements ChatMessageReceiver {

  private final ChatMessagePublisher publisher;

  @Inject
  public ChatMessageReceiverImpl(final ChatMessagePublisher publisher) {
    this.publisher = publisher;
  }

  @Override
  public void handle(final Message<Object> message) {
    publisher.publish(message.headers().get("remoteAddress") + ": " + message.body());
  }

}
