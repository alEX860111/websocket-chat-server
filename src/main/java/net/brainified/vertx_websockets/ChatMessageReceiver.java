package net.brainified.vertx_websockets;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;

interface ChatMessageReceiver extends Handler<Message<Object>> {
}