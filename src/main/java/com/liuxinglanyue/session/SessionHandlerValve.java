package com.liuxinglanyue.session;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.catalina.Session;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class SessionHandlerValve extends ValveBase {
	private final static Log log = LogFactory.getLog(SessionHandlerValve.class);
	
	private AbstractSessionManager sessionManager;
	
	public void setSessionManager(AbstractSessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		try {
			this.getNext().invoke(request, response);
		} finally {
			//sessionManager.afterRequest();
			storeSession(request.getSessionInternal(false));
		}
	}
	
	/**
	 * 存储session
	 * @param session
	 * @throws IOException
	 */
	private void storeSession(Session session) throws IOException {
		if(null == session) {
			return;
		}
		
		if(session.isValid()) {
			log.trace("Request with session completed, saving session " + session.getId());
			if(null != session.getSession()) {
				log.trace("HTTP Session present, saving " + session.getId());
				sessionManager.save(session, sessionManager.getAlwaysSaveAfterRequest());
			} else {
				log.trace("No HTTP Session present, Not saving " + session.getId());
			}
		} else {
			log.trace("HTTP Session has been invalidated, removing :" + session.getId());
			sessionManager.remove(session);
		}
	}

}
