package de.iolite.drivers.lamp;

import static de.iolite.drivers.basic.DriverConstants.PROPERTY_on_ID;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import de.iolite.drivers.framework.DataPointConfiguration;
import de.iolite.drivers.framework.device.Device;
import de.iolite.drivers.framework.device.plain.PlainDeviceFactory;

public class HelloHandler extends AbstractHandler
{
	Set<Device> existingDevices;
	
	public HelloHandler(Set<Device> existingDevices)
	{
		this.existingDevices = existingDevices;
	}
  
	public void handle(String arg0, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		String s = "";
		 for(Device device : existingDevices)
		 {
			 
		for(DataPointConfiguration t:device.getPropertyConfiguration().values())
		{
			s = s ;
		}
		// PlainDeviceFactory.create(device).notifyValue(PROPERTY_on_ID, "true");;
		 }
		 response.setContentType("text/html;charset=utf-8");
	        response.setStatus(HttpServletResponse.SC_OK);
	        baseRequest.setHandled(true);
	        response.getWriter().println("<h1>Command: "+request.getPathInfo().replaceAll("/", "")+"\n"+s+"</h1>");
	        
	     
		
	}
}