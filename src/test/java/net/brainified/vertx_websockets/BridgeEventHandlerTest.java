package net.brainified.vertx_websockets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import io.vertx.core.json.JsonObject;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.SockJSSocket;

@RunWith(MockitoJUnitRunner.class)
public class BridgeEventHandlerTest {

  @Mock
  private BridgeEvent event;

  @Mock
  private ChatMessagePublisher publisher;

  @InjectMocks
  private BridgeEventHandler handlerSUT;

  @Before
  public void configureMocks() {
    final SocketAddress address = Mockito.mock(SocketAddress.class);
    when(address.toString()).thenReturn("remoteAddressValue");

    final SockJSSocket socket = Mockito.mock(SockJSSocket.class);
    when(socket.remoteAddress()).thenReturn(address);

    when(event.socket()).thenReturn(socket);

    final JsonObject rawMessage = new JsonObject();
    final JsonObject headers = new JsonObject();
    rawMessage.put("headers", headers);
    when(event.getRawMessage()).thenReturn(rawMessage);
  }

  @Test
  public void testSend() {
    when(event.type()).thenReturn(BridgeEventType.SEND);
    handlerSUT.handle(event);
    verifyZeroInteractions(publisher);
    assertEquals("remoteAddressValue", event.getRawMessage().getJsonObject("headers").getString("remoteAddress"));
  }

  @Test
  public void testSocketCreated() {
    when(event.type()).thenReturn(BridgeEventType.SOCKET_CREATED);
    handlerSUT.handle(event);
    verify(publisher).publish("remoteAddressValue joined");
  }

  @Test
  public void testSocketClosed() {
    when(event.type()).thenReturn(BridgeEventType.SOCKET_CLOSED);
    handlerSUT.handle(event);
    verify(publisher).publish("remoteAddressValue left");
  }

}
