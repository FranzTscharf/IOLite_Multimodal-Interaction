package de.iolite.apps.ioliteslackbot.messagecontroller;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;


import javax.annotation.Nonnull;

import ai.api.model.Result;
import de.iolite.apps.ioliteslackbot.dialogflow.DialogFlowClientApplication;
import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.app.api.device.access.Device;
import de.iolite.app.api.environment.Location;
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

	//enum status
	public enum ConversationStatus{
		NewConversation, RequireLocation;
	}
	//Conversation
	int iterationNr;
	String prevCommand;
	String on_off;
	Enum<ConversationStatus> conversationStatus;

	// Controller
	private TurnOnController turnOnController;
	private InformationController informationController;
	private UC_BedController bedController;
	private UC_IAmHomeController homeController;
	private GetDeviceStateController getDeviceStateController; 
	private UseCaseController useCaseController;

	public MessageController(IoLiteSlackBotApp app) {
		this.app = app;
		this.turnOnController = new TurnOnController(this);
		this.informationController = new InformationController(this);
		this.bedController = new UC_BedController(this);
		this.homeController = new UC_IAmHomeController(this);
		this.getDeviceStateController = new GetDeviceStateController(this);
		this.useCaseController = new UseCaseController(this);
		iterationNr = 0;
		prevCommand = "";
		on_off = "on";
		conversationStatus = ConversationStatus.NewConversation;
	}

	public void analyze(SlackletRequest req, SlackletResponse resp) {
		this.request = req.getContent().toLowerCase();
		this.response = resp;

		resp.reply(conversationStatus.toString());

		if(iterationNr==0)
			prevCommand = request;

		if(conversationStatus == ConversationStatus.RequireLocation){
			useCaseController.useCase1_SwitchTheLightsInLocation();
			conversationStatus = ConversationStatus.NewConversation;
		}else if (prevCommand.contains("help")) {
			help();
		}else if(prevCommand.equals("turn on the lights") || prevCommand.equals("turn off the lights")){
			if (prevCommand.contains("on")){
				on_off = "on";
			}else {
				on_off = "off";
			}
			response.reply("In which room would you like to switch " + on_off +" the lights?");
			conversationStatus = ConversationStatus.RequireLocation;
			response.reply(conversationStatus.toString());
		}else if (prevCommand.contains("turn")) {
			turnOnController.turn();
		}
		else if (prevCommand.contains("status")) {
			getDeviceStateController.run2();
		}
		else if (prevCommand.contains("is")) {
			getDeviceStateController.run();
		}else if (prevCommand.contains("show")) {
			informationController.getAll();
		}else if (prevCommand.contains("bed")||prevCommand.contains("sleep")) {
			bedController.sleep_wakeup(false);
		}
		else if (prevCommand.contains("morning")||prevCommand.contains("wake up")) {
			bedController.sleep_wakeup(true);
		}else if (prevCommand.contains("leaving")||prevCommand.contains("going out")) {
			homeController.turnOffLamps();
		}else if (prevCommand.contains("i'm home")||prevCommand.contains("i am home")) {
			homeController.imHome();
		}
		else{
			//make a dialogflow request
			//!TODO load dialogflow apiKey
			DialogFlowClientApplication dfca =
					new DialogFlowClientApplication(
							"f8a3214ac92843b1b31f887d857db8da",app,req,resp);
			Result result = dfca.getNLPResponse(request);
			dfca.getDialogFlowTree(result);
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
	
	public ArrayList<Device> getAllDevicesByProfileAndRoom(String pProfile, String pRoom) {
		ArrayList<String> identifiers = new ArrayList<>();
		//Get all identifiers from one room
		for(Location location : app.getEnvironmentAPI().getLocations())
		{
			
			if(location.getName().toLowerCase().equals(pRoom.toLowerCase()))
			{
				for (de.iolite.app.api.environment.Device dev : location.getDevices()) {
					identifiers.add(dev.getIdentifier());
				}
			}
		}
		
		ArrayList<Device> devices = new ArrayList<>();
		for (Device dev : app.getDeviceAPI().getDevices()) {
			String profile = dev.getProfileIdentifier();
			profile = profile.substring(profile.indexOf("#") + 1, profile.length()).toLowerCase();

			if (pProfile.contains(profile)&&identifiers.contains(dev.getIdentifier())) {
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
	
	public void help()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("You can run the following commands:\n");
		sb.append("turn On %device name%\n");
		sb.append("turn Off %device name%\n");
		sb.append("turn On All %device profile%\n");
		sb.append("turn Off All %device profile%\n");
		sb.append("\n");
		sb.append("show All Device Names\n");
		sb.append("show All Location Names\n");
		sb.append("show All Device Profiles\n");
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