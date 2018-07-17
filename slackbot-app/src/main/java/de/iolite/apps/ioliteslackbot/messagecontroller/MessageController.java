package de.iolite.apps.ioliteslackbot.messagecontroller;

import java.util.ArrayList;
import java.util.List;


import javax.annotation.Nonnull;

import ai.api.model.Location;
import ai.api.model.Result;
import de.iolite.apps.ioliteslackbot.dialogflow.DialogFlowClientApplication;
import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.app.api.device.access.Device;
import de.iolite.apps.ioliteslackbot.IoLiteSlackBotApp;

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
	
	//Conversation
	int iterationNr;
	String prevCommand;

	// Controller
	private TurnOnController turnOnController;
	private InformationController informationController;
	private UC_BedController bedController;
	private GetDeviceStateController getDeviceStateController; 
	private UseCaseController useCaseController;

	public MessageController(IoLiteSlackBotApp app) {
		this.app = app;
		this.turnOnController = new TurnOnController(this);
		this.informationController = new InformationController(this);
		this.bedController = new UC_BedController(this);
		this.getDeviceStateController = new GetDeviceStateController(this);
		this.useCaseController = new UseCaseController(this);
		iterationNr = 0;
		prevCommand = "";
		
	}

	public void analyze(SlackletRequest req, SlackletResponse resp) {
		this.request = req.getContent().toLowerCase();
		this.response = resp;
		
		if(iterationNr==0)
			prevCommand = request;

		if (prevCommand.contains("help")) {
			help();
		}else if(prevCommand.equals("turn on the lights")){
			ConversationStatusEnum.setStatus(ConversationStatusEnum.Status.RequireLocationInformation);
			response.reply("In which room do you want me to switch on the lights?");
		}else if (prevCommand.contains("turn")) {
			turnOnController.turn();
		}
		else if (prevCommand.contains("is")) {
			getDeviceStateController.run();
		}else if (prevCommand.contains("get")) {
			informationController.getAll();
		}else if (prevCommand.contains("bed")||prevCommand.contains("sleep")) {
			bedController.sleep();
		}else{
			//make a dialogflow request
			//!TODO load dialogflow apiKey
			DialogFlowClientApplication dfca =
					new DialogFlowClientApplication(
							"f8a3214ac92843b1b31f887d857db8da",app,req,resp);
			Result result = dfca.getNLPResponse(request);
			response.reply(result.getFulfillment().getSpeech());
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

	
	public ArrayList<Device> getAllDevicesByProfile(String pProfile) {
		ArrayList<Device> devices = new ArrayList<>();
		for (Device dev : app.getDeviceAPI().getDevices()) {
			String profile = dev.getProfileIdentifier();
			profile = profile.substring(profile.indexOf("#") + 1, profile.length()).toLowerCase();

			if (pProfile.contains(profile)) {
				devices.add(dev);
			}
		}

		return devices;
	}

	public List<de.iolite.app.api.environment.Location> getAllLocations(){
		List<de.iolite.app.api.environment.Location> rooms = new ArrayList<>();
		rooms = app.getEnvironmentAPI().getLocations();
		return rooms;
	}

	public void useCase1_getLocation(SlackletRequest req, SlackletResponse resp){

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
		sb.append("\n");
		sb.append("is %device name% still on?\n");
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
	
	public int getIterationNr() {
		return iterationNr;
	}
	
	public String getPrevCommand()
	{
		return prevCommand;
	}

	public void setIterationNr(int iterationNr) {
		this.iterationNr = iterationNr;
	}

	public void setPrevCommand(String prevCommand) {
		this.prevCommand = prevCommand;
	}

}