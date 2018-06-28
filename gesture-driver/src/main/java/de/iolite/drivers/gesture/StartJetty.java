package de.iolite.drivers.gesture;

import javax.annotation.Nonnull;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.drivers.framework.device.plain.PlainDevice;

public class StartJetty implements Runnable {
	
	PlainDevice dev;
	


	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(StartJetty.class);
	
	public StartJetty(PlainDevice dev) {
		this.dev = dev;
	}

	@Override
	public void run() {

		try {
			
			Server server = new Server(8000);

			ResourceHandler resource_handler = new ResourceHandler();

			resource_handler.setDirectoriesListed(true);
			resource_handler.setResourceBase(".");

			HandlerList handlers = new HandlerList();
			handlers.setHandlers(new Handler[] { new Controller(dev) });
			server.setHandler(handlers);
			server.start();
			server.join();
		} catch (Exception ex) {
			LOGGER.debug(ex.toString());
		}
		
	}

}
