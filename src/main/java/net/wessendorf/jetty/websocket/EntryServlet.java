package net.wessendorf.jetty.websocket;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

/**
 * Simple 'ping' servlet...
 * 
 * @author matzew
 */
public final class EntryServlet extends WebSocketServlet
{
	private static final long serialVersionUID = 1L;
	private final Set<PingWebSocket> connectedClients = new CopyOnWriteArraySet<PingWebSocket>();

	/**
	 * Doing the upgrade of the http request
	 */
	@Override
	protected WebSocket doWebSocketConnect(HttpServletRequest request, String protocol)
	{
		return new PingWebSocket();
	}
	
	/**
	 * Here happens the _real_ communication, outside of vanilla HTTP...
	 */
	private class PingWebSocket implements WebSocket
	{
		Outbound outbound;
		public void onConnect(Outbound outbound)
		{
			// register me, after successful 'upgrade'
			this.outbound = outbound;
			connectedClients.add(this);
		}

		public void onDisconnect()
		{
			connectedClients.remove(this);
		}

		public void onMessage(byte frame, String data)
		{
			//ping it back...
			try
			{
				this.outbound.sendMessage("You said: " + data);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			// talk to ALL registered clients (windows)
//			for (PingWebSocket ws : connectedClients)
//			{
//				try
//				{
//					ws.outbound.sendMessage(frame, "You said: " + data);
//				}
//				catch (IOException e)
//				{
//					e.printStackTrace();
//				}
//			}
		}

		public void onFragment(boolean arg0, byte arg1, byte[] arg2, int arg3,
				int arg4)
		{
		}

		public void onMessage(byte arg0, byte[] arg1, int arg2, int arg3)
		{
		}
	}
}