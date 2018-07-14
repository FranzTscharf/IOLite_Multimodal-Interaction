package de.iolite.apps.ioliteslackbot.messagecontroller;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.app.api.device.DeviceAPIException;
import de.iolite.app.api.device.access.Device;
import de.iolite.app.api.device.access.DeviceBooleanProperty;
import de.iolite.apps.ioliteslackbot.IoLiteSlackBotApp;
import de.iolite.drivers.basic.DriverConstants;

/**
 * Main Controller. This controller serves requests first
 *
 * @author Johannes Hassler
 * @since 06.07.2018
 */
public class MessageController {

	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);

	private IoLiteSlackBotApp app;
	private String request;
	private SlackletResponse response;

	// Controller
	private TurnOnController turnOnController;
	private GetInformationController getInformationController;

	public MessageController(IoLiteSlackBotApp app) {
		this.app = app;
		turnOnController = new TurnOnController(this);
		getInformationController = new GetInformationController(this);
	}

	public void analyze(SlackletRequest req, SlackletResponse resp) {
		request = req.getContent().toLowerCase();
		response = resp;

		if (request.contains("help")) {
			help();
		}
		if (request.contains("turn")) {
			turnOnController.turn();
		}

		if (request.contains("get")) {
			getInformationController.getAll();
		}

	}

	public Device findDeviceByName() {

		for (Device dev : app.getDeviceAPI().getDevices()) {
			if (request.contains(dev.getName().toLowerCase())) {
				return dev;
			}
		}

		return null;
	}

	public ArrayList<Device> getAllDevicesByProfile() {
		ArrayList<Device> devices = new ArrayList<>();
		for (Device dev : app.getDeviceAPI().getDevices()) {
			String profile = dev.getProfileIdentifier();
			profile = profile.substring(profile.indexOf("#") + 1, profile.length()).toLowerCase();

			if (request.contains(profile)) {
				devices.add(dev);
			}
		}

		return devices;
	}
	
	public void help()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("You can run the following commands:\n");
		
		sb.append("turnOn %device name%\n");
		sb.append("turnOff %device name%\n");
		sb.append("turnOnAll %device profile%\n");
		sb.append("turnOffAll %device profile%\n");
		sb.append("\n");
		sb.append("getAllDeviceNames\n");
		sb.append("getAllLocationNames\n");
		sb.append("getAllDeviceProfiles\n");
		
		response.reply(sb.toString());
		
		
	}

	public IoLiteSlackBotApp getApp() {
		return app;
	}

	public String getRequest() {
		return request;
	}

	public SlackletResponse getResponse() {
		return response;
	}

}