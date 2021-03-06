package com.github.InspiredOne.InspiredNations.ManageTown;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import com.github.InspiredOne.InspiredNations.InspiredNations;
import com.github.InspiredOne.InspiredNations.HUD.Menu;

public class SelectPrison1 extends Menu {

	// Constructor
	public SelectPrison1(InspiredNations instance, Player playertemp, int errortemp) {
		super(instance, playertemp, errortemp);
		town = PDI.getTownMayored();
	}

	@Override
	public String getPromptText(ConversationContext arg0) {
		inputs.setSize(2);
		inputs.set(0,"Cuboid");
		inputs.set(1,"Polygon Prism");
		return tools.writeRegionSelection1("Prison", inputs, error);
	}
	
	@Override
	public Prompt acceptInput(ConversationContext arg0, String arg) {
		int answer = 0;
		if (arg.startsWith("/")) {
			arg = arg.substring(1);
		}
		if (arg.equalsIgnoreCase("back")) {
			PM.localPrison(false);
			PM.selectCuboid(false);
			PM.selectPolygon(false);
			return new TownGovernmentRegions(plugin, player, 0);
		}
		String[] args = arg.split(" ");
		if (args[0].equalsIgnoreCase("say"))  {
			if(args.length > 1) {
				PMeth.SendChat(tools.formatSpace(tools.subArray(args, 1, args.length - 1)));
			}
			return new SelectPrison1(plugin, player, 0);
		}
		
		try {
			answer = Integer.decode(args[0])-1;
		}
		catch (Exception ex) {
			return new SelectPrison1(plugin, player,1);
		}
		
		if (answer > inputs.size()-1) {
			return new SelectPrison1(plugin, player, 2);
		}
		
		if (inputs.get(answer).equals("Cuboid")) {
			PM.selectCuboid(true);
			PM.selectPolygon(false);
			return new SelectPrison2(plugin, player, 0);
		}
		
		else if(inputs.get(answer).equals("Polygon Prism")) {
			PM.selectPolygon(true);
			PM.selectCuboid(false);
			return new SelectPrison2(plugin, player, 0);
		}
		
		return new SelectPrison1(plugin, player, 2);
	}
}
