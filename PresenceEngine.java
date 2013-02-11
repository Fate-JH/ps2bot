import java.util.LinkedList;
import java.util.HashMap;
import org.pircbotx.PircBotX;
import org.pircbotx.Colors;

public class PresenceEngine implements Runnable {

	private HashMap<String,Boolean> presence;
	private PircBotX bot;
	private String channel;
	private boolean onSwitch;
	private long interval;

	public PresenceEngine(PircBotX bot, String channel) {
		presence = new HashMap<String,Boolean>();
		this.bot = bot;
		this.channel = channel;
		onSwitch = false;
		interval = 360000L;
	}

	public void setInterval(long set) {
		interval = set;
	}

	public void turnOn() {
		onSwitch = true;
	}

	public void turnOff() {
		onSwitch = false;
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(interval);
				if (onSwitch) {
				
					// go and get new data from source
					HashMap<String,Boolean> newpresence = GhettoPresenceChecker.getPresence();
					LinkedList<String> wentonline = new LinkedList<String>();
					LinkedList<String> wentoffline = new LinkedList<String>();
					
					// compare data to current list
					for (String name : newpresence.keySet()){
						//bot.sendMessage(channel, "Iterating over "+name);
						Boolean oldstatus = presence.get(name);
						Boolean newstatus = newpresence.get(name);
						if (oldstatus == null) {
							if (newstatus == true) {
								wentonline.add(name);
							}
						} else if (oldstatus == true) {
							if (newstatus == false) {
								wentoffline.add(name);
							}
						} else if (oldstatus == false) {
							if (newstatus == true) {
								wentonline.add(name);
							}
						}
					}
					for (String s : wentonline) {
						bot.sendMessage(channel, s+" is now "+Colors.GREEN+"online.");
					}
					for (String s : wentoffline) {
						bot.sendMessage(channel, s+" is now "+Colors.RED+"offline.");
					}
					presence = newpresence;
				}

			} catch (Exception e) {
				bot.sendMessage(channel, "Interval timer interrupted - restarting clock.");
			}
		}
	}

	
	
}