package de.iolite.drivers.gesture;

import static de.iolite.drivers.basic.DriverConstants.PROPERTY_on_ID;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import de.iolite.drivers.framework.device.plain.PlainDevice;

public class Controller extends AbstractHandler {

	private PlainDevice dev;

	public Controller(PlainDevice dev) {
		this.dev = dev;
	}

	public void handle(String arg0, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		response.getWriter().println("<h1>Command: " + request.getPathInfo().replaceAll("/", "") + "\n" + "</h1>");
		
		if(dev!=null)
		{
			dev.notifyValue(PROPERTY_on_ID, "true");
		}
	}

}