package de.iolite.drivers.gesture;



import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Handles the incoming requests to the jetty-server and passes the parameters to iolite.
 *
 * @author Johannes Hassler
 * @since 03.07.2018
 */
public class Controller extends AbstractHandler {

	private SimulatedGestureDevice dev;

	public Controller(SimulatedGestureDevice dev) {
		this.dev = dev;
	}

	public void handle(String arg0, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		
		
		if(request.getParameter("gesture")!=null)
		{
			String gesture = request.getParameter("gesture");
			response.getWriter().println("<h1>Gesture: " + gesture + "\n" + "</h1>");
			
			if(dev!=null)
			{
				dev.setRecognizedGesture(gesture);
				//dev.notifyValue(DriverConstants.PROPERTY_recognizedGesture_ID, gesture);
			}
		}
		else
		{
			response.getWriter().println("<h1>Gesture: "+ "</h1>");
		}

	}

}