package stubailey.simplechat;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WebSocketHandler extends TextWebSocketHandler {

    private List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    protected void handleTextMessage(WebSocketSession thisSession, TextMessage message) throws Exception {
        var sessionsToBeRemoved = new LinkedList<WebSocketSession>();
        sessions.forEach(session -> {
            // don't try to send a message to a closed session
            if (session.isOpen()) {
                try {
                    session.sendMessage(message);
                } catch (IOException e) {
                    // the participant has refreshed/navigated away/closed the browser
                    sessionsToBeRemoved.add(session);
                }
            }
        });
        sessionsToBeRemoved.forEach(sessions::remove);
        sessionsToBeRemoved.clear();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }
}
