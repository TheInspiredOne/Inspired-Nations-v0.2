package com.github.InspiredOne.InspiredNations;

import java.awt.Point;
import java.awt.Polygon;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.InspiredOne.InspiredNations.Economy.NPC;
import com.github.InspiredOne.InspiredNations.Regions.Cell;
import com.github.InspiredOne.InspiredNations.Regions.ChestShop;
import com.github.InspiredOne.InspiredNations.Regions.ChunkData;
import com.github.InspiredOne.InspiredNations.Regions.Chunks;
import com.github.InspiredOne.InspiredNations.Regions.Country;
import com.github.InspiredOne.InspiredNations.Regions.Cuboid;
import com.github.InspiredOne.InspiredNations.Regions.GoodBusiness;
import com.github.InspiredOne.InspiredNations.Regions.House;
import com.github.InspiredOne.InspiredNations.Regions.LocalBank;
import com.github.InspiredOne.InspiredNations.Regions.LocalPrison;
import com.github.InspiredOne.InspiredNations.Regions.Park;
import com.github.InspiredOne.InspiredNations.Regions.ServiceBusiness;
import com.github.InspiredOne.InspiredNations.Regions.Town;
import com.github.InspiredOne.InspiredNations.Regions.polygonPrism;

public class SaveFiles {

	// Grabbing instance of plugin
	InspiredNations plugin;
	public SaveFiles(InspiredNations instance) {
		plugin = instance;
	}
	
	// Local Variables
	public FileConfiguration dataFileConfig;
	public FileConfiguration PDC;
	public FileConfiguration PMC;
	public File dataFile;
	public File playerDataFile;
	
	// Handling variable values for initiation and termination of the plugin
	public void loadDataFile() {
		dataFile = new File(plugin.getDataFolder(), "countrydata.yml");
		playerDataFile = new File(plugin.getDataFolder(), "playerdata.yml");
		dataFileConfig = YamlConfiguration.loadConfiguration(dataFile);
		dataFileConfig.options().copyDefaults(true);
		PDC = YamlConfiguration.loadConfiguration(playerDataFile);
		PDC.options().copyDefaults(true);
		plugin.countrydata = new HashMap<String, Country>();
		plugin.playerdata = new HashMap<String, PlayerData>();
		plugin.countrydata = deserializeCountryData();
		plugin.playerdata = deserializePlayerData();
	}
	
	// Saves Data
	public void saveDataFile() {
		serializeCountryData();
		serializePlayerData();
		try {
			dataFileConfig.save(dataFile);
			PDC.save(playerDataFile);
		}
		catch (IOException ex) {
			plugin.logger.log(Level.SEVERE, "Could not save config to " + dataFile.toString(), ex);
		}
	}
	
	// Private methods to store and retrieve data.
	private void serializeCountryData() {
		int index = 0;
		dataFileConfig.addDefault("size", plugin.countrydata.size());

		for (;index < plugin.countrydata.size(); index++) {
			
			int count = 0;
			String key = index + "";
			String keytemp = (String) plugin.countrydata.keySet().toArray()[index];
			
			// Setting new values.
			Country country = plugin.countrydata.get(keytemp);
			dataFileConfig.addDefault(key + ".name", country.getName());
			dataFileConfig.addDefault(key + ".corulers.size", country.getCoRulers().size());
			serializeVector(country.getOffers(), key + ".residentoffer");
			serializeVector(country.getRequests(), key + ".residentrequest");
			for (int i = 0; i < country.getCoRulers().size(); i++) {
				dataFileConfig.addDefault(key + ".corulers." + i, country.getCoRulers().get(i));
			}
			dataFileConfig.addDefault(key + ".towns.size", country.getTowns().size());
			for (int i = 0; i < country.getTowns().size(); i++) {
				serializeTown(country.getTowns().get(i), key + ".towns." + i);
			}
			dataFileConfig.set(key + ".residents.size", country.getResidents().size());
			for (int i = 0; i < country.getResidents().size(); i++) {
				dataFileConfig.addDefault(key + ".residents." + i, country.getResidents().get(i));
			}
			dataFileConfig.addDefault(key + ".population", country.getPopulation());
			dataFileConfig.addDefault(key + ".area.size", country.getChunks().Chunks.size());
			for (int i = 0; i < country.getChunks().Chunks.size() ; i++) {
				dataFileConfig.addDefault(key + ".area." + i + ".x", country.getChunks().Chunks.get(i).point.x);
				dataFileConfig.addDefault(key + ".area." + i + ".y", country.getChunks().Chunks.get(i).point.y);
				dataFileConfig.addDefault(key + ".area." + i + ".world", country.getChunks().Chunks.get(i).world);
			}
			dataFileConfig.addDefault(key + ".parks.size", country.getParks().size());
			for (Park park : country.getParks()) {
				serializePark(park, key + ".parks." + count);
				count++;
			}
			dataFileConfig.addDefault(key + ".protectionLevel", country.getProtectionLevel());
			dataFileConfig.addDefault(key + ".militaryLevel", country.getMilitaryLevel());
			dataFileConfig.addDefault(key + ".money.plural", country.getPluralMoney());
			dataFileConfig.addDefault(key + ".money.singular", country.getSingularMoney());
			dataFileConfig.addDefault(key + ".money.taxrate", country.getTaxRate());
			dataFileConfig.addDefault(key + ".money.multiplyer", country.getMoneyMultiplyer().toString());
			dataFileConfig.addDefault(key + ".money.amount", country.getRawMoney().toString());
			dataFileConfig.addDefault(key + ".money.loan", country.getRawLoanAmount().toString());
			dataFileConfig.addDefault(key + ".money.maxloan", country.getRawMaxLoan().toString());
			dataFileConfig.addDefault(key + ".money.oldchunkbase", country.getOldChunkBase());
			dataFileConfig.addDefault(key + ".money.oldfedparkbase", country.getOldFedParkBase());
			dataFileConfig.addDefault(key + ".money.oldmilitarybase", country.getOldMilitaryBase());
			dataFileConfig.addDefault(key + ".money.npcaccount", country.getRawNPCaccount().toString());
			dataFileConfig.addDefault(key + ".money.refund", country.getRawRefund().toString());
			
			// Updating file.
			count = 0;
			dataFileConfig.set("size", plugin.countrydata.size());
			dataFileConfig.set(key + ".name", country.getName());
			dataFileConfig.set(key + ".corulers.size", country.getCoRulers().size());
			for (int i = 0; i < country.getCoRulers().size(); i++) {
				dataFileConfig.set(key + ".corulers." + i, country.getCoRulers().get(i));
			}
			dataFileConfig.set(key + ".towns.size", country.getTowns().size());
			dataFileConfig.set(key + ".residents.size", country.getResidents().size());
			for (int i = 0; i < country.getResidents().size(); i++) {
				dataFileConfig.set(key + ".residents." + i, country.getResidents().get(i));
			}
			dataFileConfig.set(key + ".population", country.getPopulation());
			dataFileConfig.set(key + ".area.size", country.getChunks().Chunks.size());
			for (int i = 0; i < country.getChunks().Chunks.size() ; i++) {
				dataFileConfig.set(key + ".area." + i + ".x", country.getChunks().Chunks.get(i).point.x);
				dataFileConfig.set(key + ".area." + i + ".y", country.getChunks().Chunks.get(i).point.y);
				dataFileConfig.set(key + ".area." + i + ".world", country.getChunks().Chunks.get(i).world);
			}
			dataFileConfig.set(key + ".parks.size", country.getParks().size());
					// serializePark() updates values as well.
			dataFileConfig.set(key + ".protectionLevel", country.getProtectionLevel());
			dataFileConfig.set(key + ".militaryLevel", country.getMilitaryLevel());
			dataFileConfig.set(key + ".money.plural", country.getPluralMoney());
			dataFileConfig.set(key + ".money.singular", country.getSingularMoney());
			dataFileConfig.set(key + ".money.taxrate", country.getTaxRate());
			dataFileConfig.set(key + ".money.multiplyer", country.getMoneyMultiplyer().toString());
			dataFileConfig.set(key + ".money.amount", country.getRawMoney().toString());
			dataFileConfig.set(key + ".money.loan", country.getRawLoanAmount().toString());
			dataFileConfig.set(key + ".money.maxloan", country.getRawMaxLoan().toString());
			dataFileConfig.set(key + ".money.oldchunkbase", country.getOldChunkBase());
			dataFileConfig.set(key + ".money.oldfedparkbase", country.getOldFedParkBase());
			dataFileConfig.set(key + ".money.oldmilitarybase", country.getOldMilitaryBase());
			dataFileConfig.set(key + ".money.npcaccount", country.getRawNPCaccount().toString());
			dataFileConfig.set(key + ".money.refund", country.getRawRefund().toString());
		}
	}
	
	public HashMap<String, Country> deserializeCountryData() {
		HashMap<String, Country> temp = new HashMap<String, Country>();
		plugin.chunks = new HashMap<ChunkData, String>();
		String name;
		for (int index = 0; index < dataFileConfig.getInt("size"); index++) {
			String key = index + "";
			name = dataFileConfig.getString(key + ".name");
			Country countrytemp = new Country(plugin, name);
			for (int j = 0; j < dataFileConfig.getInt(key + ".corulers.size"); j++) {
				countrytemp.addCoRuler(dataFileConfig.getString(key + ".corulers." + j));
			}
			for (int j = 0; j < dataFileConfig.getInt(key + ".towns.size"); j++) {
				countrytemp.addTown(deserializeTown(key + ".towns." + j));
			}
			for (int j = 0; j < dataFileConfig.getInt(key + ".residents.size"); j++) {
				countrytemp.addResident(dataFileConfig.getString(key + ".residents." + j));
			}
			for (int j = 0; j < dataFileConfig.getInt(key + ".parks.size") ; j++) {
				countrytemp.addPark(deserializePark(key + ".parks." + j));
			}
			for (int j = 0; j < dataFileConfig.getInt(key + ".area.size"); j ++) {
				ChunkData chunktemp = new ChunkData(new Point(dataFileConfig.getInt(key + ".area." + j + ".x"),  dataFileConfig.getInt(key + ".area." + j + ".y")),dataFileConfig.getString(key + ".area." + j + ".world"));
				countrytemp.addChunk(chunktemp);
			}
			countrytemp.setRequest(deserializeVector(key + ".residentrequest"));
			countrytemp.setOffer(deserializeVector(key + "residentoffer"));
			countrytemp.setProtectionLevel(dataFileConfig.getInt(key + ".protectionLevel"));
			countrytemp.setMilitaryLevel(dataFileConfig.getInt(key + ".militaryLevel"));
			countrytemp.setPluralMoney(dataFileConfig.getString(key + ".money.plural"));
			countrytemp.setSingularMoney(dataFileConfig.getString(key + ".money.singular"));
			countrytemp.setTaxRate(dataFileConfig.getDouble(key + ".money.taxrate"));
			countrytemp.setMoneyMultiplyer(new BigDecimal(dataFileConfig.getString(key + ".money.multiplyer")));
			countrytemp.setRawMoney(new BigDecimal(dataFileConfig.getString(key + ".money.amount")));
			countrytemp.setRawLoan(new BigDecimal(dataFileConfig.getString(key + ".money.loan")));
			countrytemp.setRawMaxLoan(new BigDecimal(dataFileConfig.getString(key + ".money.maxloan")));
			countrytemp.setOldChunkBase(dataFileConfig.getDouble(key + ".money.oldchunkbase"));
			countrytemp.setOldFedParkBase(dataFileConfig.getDouble(key + ".money.oldfedparkbase"));
			countrytemp.setOldMilitaryBase(dataFileConfig.getDouble(key + ".money.oldmilitarybase"));
			countrytemp.setRawNPCaccount(new BigDecimal(dataFileConfig.getString(key + ".money.npcaccount")));
			countrytemp.setRawRefund(new BigDecimal(dataFileConfig.getString(key + ".money.refund")));
			temp.put(countrytemp.getName(), countrytemp);
		}
		return temp;
	}
	
	public void serializePlayerData() {
		for (String key: plugin.playerdata.keySet()) {
			// Setting new values
			PlayerData PDI = plugin.playerdata.get(key);
			PDC.addDefault(key + ".inHouse", PDI.getIsInHouse());
			PDC.addDefault(key + ".houseIn", null);
			PDC.addDefault(key + ".inTown", PDI.getIsInTown());
			if (PDI.getIsInTown()) {
				PDC.addDefault(key + ".townIn", PDI.getTownIn().getName());
			}

			PDC.addDefault(key + ".inCapital", PDI.getInCapital());
			PDC.addDefault(key + ".inCountry", PDI.getIsInCountry());
			if (PDI.getIsInCountry()) {
				PDC.addDefault(key + ".countryIn", PDI.getCountryIn().getName());
			}
			else { 
				PDC.addDefault(key + ".countryIn", null);
			}
			PDC.addDefault(key + ".localPrisonIn", null);
			PDC.addDefault(key + ".localBankIn", null);
			PDC.addDefault(key + ".goodBusinessIn", null);
			PDC.addDefault(key + ".serviceBusinessIn", null);
			PDC.addDefault(key + ".prisonIn", null);
			PDC.addDefault(key + ".localParkIn", null);
			PDC.addDefault(key + ".federalParkIn", null);
			
			// NPC
			int count = 0;
			PDC.addDefault(key + ".npc.size", PDI.getNpcs().size());
			for(NPC npc:PDI.getNpcs()) {
				this.serializeNPC(npc, key + ".npc." + count);
				count ++;
			}
			
			// House
			PDC.addDefault(key + ".isHouseOwner", PDI.isHouseOwner());
			PDC.addDefault(key + ".houseOwned.size.size", PDI.getHouseOwned().size());
			for (int i = 0; i < PDI.getHouseOwned().size(); i++) {
				House housetemp = PDI.getHouseOwned().get(i);
				PDC.addDefault(key + ".houseOwned." + i + ".town", housetemp.getTown());
				PDC.addDefault(key + ".houseOwned." + i + ".index", PDI.getCountryResides().getTowns().get(housetemp.getTown())
						.getHouses().indexOf(housetemp));
			}
			PDC.addDefault(key + ".isTownMayor", PDI.getIsTownMayor());
			if (PDI.getIsTownMayor()) {
				PDC.addDefault(key + ".townMayored", PDI.getCountryResides().getTowns().indexOf(PDI.getTownMayored()));
			}
			else {
				PDC.addDefault(key + ".townMayored", null);
			}
			PDC.addDefault(key + ".isCountryRuler", PDI.getIsCountryRuler());
			if (PDI.getIsCountryRuler()) {
				PDC.addDefault(key + ".countryRuled", PDI.getCountryRuled().getName());
			}
			else {
				PDC.addDefault(key + ".countryRuled", null);
			}
			
			// Good Business
			PDC.addDefault(key + ".isGoodBusinessOwner", PDI.isGoodBusinessOwner());
			PDC.addDefault(key + ".goodBusinessOwned.size.size", PDI.getGoodBusinessOwned().size());
			for (int i = 0; i < PDI.getGoodBusinessOwned().size(); i++) {
				GoodBusiness businesstemp = PDI.getGoodBusinessOwned().get(i);
				PDC.addDefault(key + ".goodBusinessOwned." + i + ".town", businesstemp.getTown());
				PDC.addDefault(key + ".goodBusinessOwned." + i + ".index", PDI.getCountryResides().getTowns().get(businesstemp.getTown())
						.getGoodBusinesses().indexOf(businesstemp));
			}
			
			// Service Business
			PDC.addDefault(key + ".isServiceBusinessOwner", PDI.isServiceBusinessOwner());
			PDC.addDefault(key + ".serviceBusinessOwned.size.size", PDI.getServiceBusinessOwned().size());
			for (int i = 0; i < PDI.getServiceBusinessOwned().size(); i++) {
				ServiceBusiness businesstemp = PDI.getServiceBusinessOwned().get(i);
				PDC.addDefault(key + ".serviceBusinessOwned." + i + ".town", businesstemp.getTown());
				PDC.addDefault(key + ".serviceBusinessOwned." + i + ".index", PDI.getCountryResides().getTowns().get(businesstemp.getTown())
						.getServiceBusinesses().indexOf(businesstemp));
			}
			PDC.addDefault(key + ".isFederalPrisonJailed", PDI.isLocalPrisonJailed());
			PDC.addDefault(key + ".federalPrisonJailed", null);
			PDC.addDefault(key + ".isLocalPrisonJailed", PDI.isLocalPrisonJailed());
			PDC.addDefault(key + ".localPrisonJailed", null);
			PDC.addDefault(key + ".isTownResident", PDI.getIsTownResident());
			if (PDI.getIsTownResident()) {
				PDC.addDefault(key + ".townResides", PDI.getCountryResides().getTowns().indexOf(PDI.getTownResides()));
			}
			else {
				PDC.addDefault(key + ".townResides", null);
			}
			PDC.addDefault(key + ".isCountryResident", PDI.getIsCountryResident());
			if (PDI.getIsCountryResident()) {
				PDC.addDefault(key + ".countryResides", PDI.getCountryResides().getName());
			}
			else {
				PDC.addDefault(key + ".countryResides", null);
			}
			PDC.addDefault(key + ".rep.points", PDI.getRepPoints());
			PDC.addDefault(key + ".rep.score", PDI.getRepScore());
			PDC.addDefault(key + ".recipient.size", PDI.getRecipients().size());
			int iter = 0; // used to number label the recipients
			for(String name:PDI.getRecipients().keySet()) {
				PDC.addDefault(key + ".recipient." + iter + ".points", PDI.getRecipients().get(name));
				PDC.addDefault(key + ".recipient." + iter + ".name", name);
				iter++;
			}
			PDC.addDefault(key + ".money.amount", PDI.getRawMoney().toString());
			PDC.addDefault(key + ".money.inBankHigh", PDI.getRawMoneyInBankHigh().toString());
			PDC.addDefault(key + ".money.inBankLow", PDI.getRawMoneyInBankLow().toString());
			PDC.addDefault(key + ".money.multiplyer", PDI.getMoneyMultiplyer().toString());
			PDC.addDefault(key + ".money.plural", PDI.getPluralMoney());
			PDC.addDefault(key + ".money.singular", PDI.getSingularMoney());
			PDC.addDefault(key + ".money.housetax", PDI.getHouseTax());
			PDC.addDefault(key + ".money.goodbusinesstax", PDI.getGoodBusinessTax());
			PDC.addDefault(key + ".money.servicebusinesstax", PDI.getServiceBusinessTax());
			PDC.addDefault(key + ".money.oldhousetax", PDI.getOldHouseTax());
			PDC.addDefault(key + ".money.oldgoodbusinesstax", PDI.getOldGoodBusinessTax());
			PDC.addDefault(key + ".money.oldservicebusinesstax", PDI.getOldServiceBusinessTax());
			PDC.addDefault(key + ".money.loan", PDI.getRawLoanAmount().toString());
			PDC.addDefault(key + ".money.maxloan", PDI.getRawMaxLoan().toString());
			
			// Updating Values
			PDC.set(key + ".inHouse", PDI.getIsInHouse());
			PDC.set(key + ".houseIn", null);
			PDC.set(key + ".inTown", PDI.getIsInTown());
			if (PDI.getIsInTown()) {
				PDC.set(key + ".townIn", PDI.getTownIn().getName());
			}
			
			PDC.set(key + ".inCapital", PDI.getInCapital());
			PDC.set(key + ".inCountry", PDI.getIsInCountry());
			if (PDI.getIsInCountry()) {
				PDC.set(key + ".countryIn", PDI.getCountryIn().getName());
			}
			else { 
				PDC.set(key + ".countryIn", null);
			}
			
			if (PDI.getIsCountryRuler()) {
				PDC.set(key + ".countryRuled", PDI.getCountryRuled().getName());
			}
			else {
				PDC.set(key + ".countryRuled", null);
			}
			PDC.set(key + ".inLocalPrison", PDI.getIsInLocalPrison());
			PDC.set(key + ".localPrisonIn", null);
			PDC.set(key + ".inLocalBank", PDI.getIsInLocalBank());
			PDC.set(key + ".localBankIn", null);
			PDC.set(key + ".inGoodBusiness", PDI.getIsInGoodBusiness());
			PDC.set(key + ".goodBusinessIn", null);
			PDC.set(key + ".inServiceBusiness", PDI.getIsInServiceBusiness());
			PDC.set(key + ".serviceBusinessIn", null);
			PDC.set(key + ".prisonIn", null);
			PDC.set(key + ".inLocalPark", PDI.getIsInLocalPark());
			PDC.set(key + ".localParkIn", null);
			PDC.set(key + ".inFederalPark", PDI.getIsInFederalPark());
			PDC.set(key + ".federalPrisonIn", null);
			
			// NPC
			PDC.set(key + ".npc.size", PDI.getNpcs().size());
			
			// House
			PDC.set(key + ".isHouseOwner", PDI.isHouseOwner());
			PDC.set(key + ".houseOwned.size.size", PDI.getHouseOwned().size());
			for (int i = 0; i < PDI.getHouseOwned().size(); i++) {
				House housetemp = PDI.getHouseOwned().get(i);
				PDC.set(key + ".houseOwned." + i +".town", housetemp.getTown());
				PDC.set(key + ".houseOwned." + i + ".index", PDI.getCountryResides().getTowns().get(housetemp.getTown())
						.getHouses().indexOf(housetemp));
			}
			PDC.set(key + ".isTownMayor", PDI.getIsTownMayor());
			if (PDI.getIsTownMayor()) {
				PDC.set(key + ".townMayored",PDI.getCountryResides().getTowns().indexOf(PDI.getTownMayored()));
			}
			else {
				PDC.set(key + ".townMayored", null);
			}
			PDC.set(key + ".isCountryRuler", PDI.getIsCountryRuler());
			if (PDI.getIsCountryRuler()) {
				PDC.set(key + ".countryRuled", PDI.getCountryRuled().getName());
			}
			else {
				PDC.set(key + ".countryRuled", null);
			}
			
			// Good Business
			PDC.set(key + ".isGoodBusinessOwner", PDI.isGoodBusinessOwner());
			PDC.set(key + ".goodBusinessOwned.size.size", PDI.getGoodBusinessOwned().size());
			for (int i = 0; i < PDI.getGoodBusinessOwned().size(); i++) {
				GoodBusiness businesstemp = PDI.getGoodBusinessOwned().get(i);
				PDC.set(key + ".goodBusinessOwned." + i + ".town", businesstemp.getTown());
				PDC.set(key + ".goodBusinessOwned." + i + ".index", PDI.getCountryResides().getTowns().get(businesstemp.getTown())
						.getGoodBusinesses().indexOf(businesstemp));
			}
			
			// Service Business
			PDC.set(key + ".isServiceBusinessOwner", PDI.isServiceBusinessOwner());
			PDC.set(key + ".serviceBusinessOwned.size.size", PDI.getServiceBusinessOwned().size());
			for (int i = 0; i < PDI.getServiceBusinessOwned().size(); i++) {
				ServiceBusiness businesstemp = PDI.getServiceBusinessOwned().get(i);
				PDC.set(key + ".serviceBusinessOwned." + i + ".town", businesstemp.getTown());
				PDC.set(key + ".serviceBusinessOwned." + i + ".index", PDI.getCountryResides().getTowns().get(businesstemp.getTown())
						.getServiceBusinesses().indexOf(businesstemp));
			}
			PDC.set(key + ".isFederalPrisonJailed", PDI.isLocalPrisonJailed());
			PDC.set(key + ".federalPrisonJailed", null);
			PDC.set(key + ".isLocalPrisonJailed", PDI.isLocalPrisonJailed());
			PDC.set(key + ".localPrisonJailed", null);
			PDC.set(key + ".isTownResident", PDI.getIsTownResident());
			if (PDI.getIsTownResident()) {
				PDC.set(key + ".townResides", PDI.getCountryResides().getTowns().indexOf(PDI.getTownResides()));
			}
			else {
				PDC.set(key + ".townResides", null);
			}
			PDC.set(key + ".isCountryResident", PDI.getIsCountryResident());
			if (PDI.getIsCountryResident()) {
				PDC.set(key + ".countryResides", PDI.getCountryResides().getName());
			}
			else {
				PDC.set(key + ".countryResides", null);
			}
			PDC.set(key + ".rep.points", PDI.getRepPoints());
			PDC.set(key + ".rep.score", PDI.getRepScore());
			PDC.set(key + ".recipient.size", PDI.getRecipients().size());
			@SuppressWarnings("unused")
			int iterator = 0; // used to number label the recipients
			for(String name:PDI.getRecipients().keySet()) {
				PDC.set(key + ".recipient." + iter + ".points", PDI.getRecipients().get(name));
				PDC.set(key + ".recipient." + iter + ".name", name);
				iterator++;
			}
			PDC.set(key + ".money.amount", PDI.getRawMoney().toString());
			PDC.set(key + ".money.inBankHigh", PDI.getRawMoneyInBankHigh().toString());
			PDC.set(key + ".money.inBankLow", PDI.getRawMoneyInBankLow().toString());
			PDC.set(key + ".money.multiplyer", PDI.getMoneyMultiplyer().toString());
			PDC.set(key + ".money.plural", PDI.getPluralMoney());
			PDC.set(key + ".money.singular", PDI.getSingularMoney());
			PDC.set(key + ".money.housetax", PDI.getHouseTax());
			PDC.set(key + ".money.goodbusinesstax", PDI.getGoodBusinessTax());
			PDC.set(key + ".money.servicebusinesstax", PDI.getServiceBusinessTax());
			PDC.set(key + ".money.oldhousetax", PDI.getOldHouseTax());
			PDC.set(key + ".money.oldgoodbusinesstax", PDI.getOldGoodBusinessTax());
			PDC.set(key + ".money.oldservicebusinesstax", PDI.getOldServiceBusinessTax());
			PDC.set(key + ".money.loan", PDI.getRawLoanAmount().toString());
			PDC.set(key + ".money.maxloan", PDI.getRawMaxLoan().toString());
		}
	}
	
	public HashMap<String, PlayerData> deserializePlayerData() {
		HashMap<String, PlayerData> playerdata = new HashMap<String, PlayerData>();
		for (Iterator<?> i = PDC.getKeys(false).iterator(); i.hasNext();) {
			String key = (String) i.next();
			PlayerData PDI = new PlayerData(plugin, key);
			
			PDI.setHouseIn(null);

			PDI.setInCapital(PDC.getBoolean(key + ".inCapital"));
			
			// NPC
			if(PDC.getInt(key + ".npc.size") > plugin.getConfig().getInt("player_npc_pop")) {
				for(int j = 0; j<PDC.getInt(key + ".npc.size"); j++) {
					PDI.getNpcs().set(j, this.deSerializeNPC(key + ".npc." + j));
				}
			}
			else {
				for(int j = 0; j<plugin.getConfig().getInt("player_npc_pop"); j++) {
					PDI.getNpcs().set(j, this.deSerializeNPC(key + ".npc." + j));
				}
			}
			
			// House
			for (int j = 0; j < PDC.getInt(key + ".houseOwned.size.size"); j++) {
				PDI.addHouseOwned(plugin.countrydata.get(PDC.getString(key + ".countryResides")).getTowns().
						get(PDC.getInt(key + ".houseOwned." + j + ".town" )).getHouses().get(PDC.getInt(key + ".houseOwned." + j + ".index")));
			}
			
			// Good Business
			for (int j = 0; j < PDC.getInt(key + ".goodBusinessOwned.size.size"); j++) {
				PDI.addGoodBusinessOwned(plugin.countrydata.get(PDC.get(key + ".countryResides")).getTowns()
						.get(PDC.getInt(key + ".goodBusinessOwned." + j + ".town")).getGoodBusinesses().get(PDC.getInt(key + ".goodBusinessOwned." + j + ".index")));
			}
				
			// Service Business
			for (int j = 0; j < PDC.getInt(key + ".serviceBusinessOwned.size.size"); j++) {
				PDI.addServiceBusinessOwned(plugin.countrydata.get(PDC.get(key + ".countryResides")).getTowns()
						.get(PDC.getInt(key + ".serviceBusinessOwned." + j + ".town")).getServiceBusinesses().get(PDC.getInt(key + ".serviceBusinessOwned." + j + ".index")));
			}
			
			if (PDC.getBoolean(key + ".isTownMayor")) {
				int index = PDC.getInt(key + ".townMayored");
				PDI.setTownMayored(plugin.countrydata.get(PDC.getString(key + ".countryResides")).getTowns().get(index));	
			}
			else {
				PDI.setTownMayored(null);
			}
			PDI.setCountryRuled(plugin.countrydata.get(PDC.get(key + ".countryRuled")));

			PDI.setLocalPrisonJailed(null);
			if (PDC.getBoolean(key + ".isTownResident")) {
				int index = PDC.getInt(key + ".townResides");
				PDI.setTownResides(plugin.countrydata.get(PDC.getString(key + ".countryResides")).getTowns().get(index));
			}
			else {
				PDI.setTownResides(null);
			}
			
			PDI.setRepPoints(PDC.getDouble(key + ".rep.points"));
			PDI.setRepScore(PDC.getDouble(key + ".rep.score"));
			for(int j = 0; j < PDC.getInt(key + ".recipient.size"); j++) {
				PDI.getRecipients().put(PDC.getString(key + ".recipient." + j + ".name"), PDC.getDouble(key + ".recipient." + j + ".points"));
			}
			PDI.setCountryResides(plugin.countrydata.get(PDC.getString(key + ".countryResides")));
			PDI.setMoneyMultiplyer(new BigDecimal(PDC.getString(key + ".money.multiplyer")));
			PDI.setRawMoney(new BigDecimal(PDC.getString(key + ".money.amount")));
			PDI.setRawMoneyInBankHigh(new BigDecimal(PDC.getString(key + ".money.inBankHigh")));
			PDI.setRawMoneyInBankLow(new BigDecimal(PDC.getString(key + ".money.inBankLow")));
			PDI.setPluralMoney(PDC.getString(key + ".money.plural"));
			PDI.setSingularMoney(PDC.getString(key + ".money.singular"));
			PDI.setHouseTax(PDC.getDouble(key + ".money.housetax"));
			PDI.setGoodBusinessTax(PDC.getDouble(key + ".money.goodbusinesstax"));
			PDI.setServiceBusinessTax(PDC.getDouble(key + ".money.servicebusinesstax"));
			PDI.setOldHouseTax(PDC.getDouble(key + ".money.oldhousetax"));
			PDI.setOldGoodBusinessTax(PDC.getDouble(key + ".money.oldgoodbusinesstax"));
			PDI.setOldServiceBusinessTax(PDC.getDouble(key + ".money.oldservicebusinesstax"));
			PDI.setRawLoanAmount(new BigDecimal(PDC.getString(key + ".money.loan")));
			PDI.setRawMaxLoan(new BigDecimal(PDC.getString(key + ".money.maxloan")));
			playerdata.put(key, PDI);
		}
		return playerdata;
	}
	
	public void serializeTown(Town town, String key) {
		
		// Setting new values.
		Chunks chunks = town.getChunks();
		dataFileConfig.addDefault(key + ".name", town.getName());
		dataFileConfig.addDefault(key + ".country", town.getCountry());
		dataFileConfig.addDefault(key + ".coMayors.size", town.getCoMayors().size());
		serializeVector(town.getOffers(), key + ".residentoffer");
		serializeVector(town.getRequests(), key + ".residentrequest");
		for (int i = 0; i < town.getCoMayors().size(); i++) {
			dataFileConfig.addDefault(key + ".coMayors." + i, town.getCoMayors().get(i));
		}
		
		// Good Business
		dataFileConfig.addDefault(key + ".goodBusiness.size", town.getGoodBusinesses().size());
		for (int i = 0; i < town.getGoodBusinesses().size(); i++) {
			serializeGoodBusiness(town.getGoodBusinesses().get(i), key + ".goodBusiness." + i);
		}
		
		// Service Business
		dataFileConfig.addDefault(key + ".serviceBusiness.size", town.getServiceBusinesses().size());
		for (int i = 0; i < town.getServiceBusinesses().size(); i++) {
			serializeServiceBusiness(town.getServiceBusinesses().get(i), key + ".serviceBusiness." + i);
		}
		
		// residents
		dataFileConfig.addDefault(key + ".residents.size" , town.getResidents().size());
		for (int i = 0; i < town.getResidents().size(); i++) {
			dataFileConfig.addDefault(key + ".residents." + i, town.getResidents().get(i));
		}
		
		// park
		dataFileConfig.addDefault(key + ".park.size", town.getParks().size());
		for (int i = 0; i < town.getParks().size(); i++) {
			serializePark(town.getParks().get(i), key + ".park." + i);
		}
		
		// prison
		dataFileConfig.addDefault(key + ".hasprison", town.hasPrison());
		if (town.hasPrison()) {
			serializeLocalPrison(town.getPrison(), key + ".localprison");
		}
		
		// bank
		dataFileConfig.addDefault(key + ".hasbank", town.hasBank());
		if (town.hasBank()) {
			serializeLocalBank(town.getBank(), key + ".localbank");
		}
		
		// Houses
		dataFileConfig.addDefault(key + ".houses.size", town.getHouses().size());
		for (int i = 0; i < town.getHouses().size(); i++) {
			serializeHouse(town.getHouses().get(i), key + ".houses." + i);
		}
		

		dataFileConfig.addDefault(key + ".area.size", chunks.Chunks.size());
		for (int i = 0; i < chunks.Chunks.size() ; i++) {
			dataFileConfig.addDefault(key + ".area." + i + ".x", chunks.Chunks.get(i).point.x);
			dataFileConfig.addDefault(key + ".area." + i + ".y", chunks.Chunks.get(i).point.y);
			dataFileConfig.addDefault(key + ".area." + i + ".world", chunks.Chunks.get(i).world);
		}
		dataFileConfig.addDefault(key + ".protectionLevel", town.getProtectionLevel());
		dataFileConfig.addDefault(key + ".militarylevel", town.getMilitaryLevel());
		dataFileConfig.addDefault(key + ".nationTax", town.getNationTax());
		dataFileConfig.addDefault(key + ".nationTaxOld", town.getNationTaxOld());
		dataFileConfig.addDefault(key + ".houseTax", town.getHouseTax());
		dataFileConfig.addDefault(key + ".goodBusinessTax", town.getGoodBusinessTax());
		dataFileConfig.addDefault(key + ".serviceBusinessTax", town.getServiceBusinessTax());
		dataFileConfig.addDefault(key + ".money.plural", town.getPluralMoney());
		dataFileConfig.addDefault(key + ".money.singular", town.getSingularMoney());
		dataFileConfig.addDefault(key + ".money.amount", town.getRawMoney().toString());
		dataFileConfig.addDefault(key + ".money.multiplyer", town.getMoneyMultiplyer().toString());
		dataFileConfig.addDefault(key + ".money.loan", town.getRawLoan().toString());
		dataFileConfig.addDefault(key + ".money.maxLoan", town.getRawMaxLoan().toString());
		dataFileConfig.addDefault(key + ".money.refund", town.getRawRefund().toString());
		dataFileConfig.addDefault(key + ".isCapital", town.isCapital());
		
		// Updating values.
		dataFileConfig.set(key + ".name", town.getName());
		dataFileConfig.set(key + ".country", town.getCountry());
		dataFileConfig.set(key + ".coMayors.size", town.getCoMayors().size());
		for (int i = 0; i < town.getCoMayors().size(); i++) {
			dataFileConfig.set(key + ".coMayors." + i, town.getCoMayors().get(i));
		}
		dataFileConfig.set(key + ".houses.size", town.getHouses().size());
		dataFileConfig.set(key + ".goodBusiness.size", town.getGoodBusinesses().size());
		dataFileConfig.set(key + ".serviceBusiness.size", town.getServiceBusinesses().size());
		dataFileConfig.set(key + ".residents.size" , town.getResidents().size());
		for (int i = 0; i < town.getResidents().size(); i++) {
			dataFileConfig.set(key + ".residents." + i, town.getResidents().get(i));
		}
		dataFileConfig.set(key + ".park.size", town.getParks().size());
		dataFileConfig.set(key + ".hasprison", town.hasPrison());
		dataFileConfig.set(key + ".hasbank", town.hasBank());
		dataFileConfig.set(key + ".area.size", chunks.Chunks.size());
		for (int i = 0; i < chunks.Chunks.size() ; i++) {
			dataFileConfig.set(key + ".area." + i + ".x", chunks.Chunks.get(i).point.x);
			dataFileConfig.set(key + ".area." + i + ".y", chunks.Chunks.get(i).point.y);
			dataFileConfig.set(key + ".area." + i + ".world", chunks.Chunks.get(i).world);
		}
		dataFileConfig.set(key + ".protectionLevel", town.getProtectionLevel());
		dataFileConfig.set(key + ".militarylevel", town.getMilitaryLevel());
		dataFileConfig.set(key + ".nationTax", town.getNationTax());
		dataFileConfig.set(key + ".nationTaxOld", town.getNationTaxOld());
		dataFileConfig.set(key + ".houseTax", town.getHouseTax());
		dataFileConfig.set(key + ".goodBusinessTax", town.getGoodBusinessTax());
		dataFileConfig.set(key + ".serviceBusinessTax", town.getServiceBusinessTax());
		dataFileConfig.set(key + ".money.plural", town.getPluralMoney());
		dataFileConfig.set(key + ".money.singular", town.getSingularMoney());
		dataFileConfig.set(key + ".money.amount", town.getRawMoney().toString());
		dataFileConfig.set(key + ".money.multiplyer", town.getMoneyMultiplyer().toString());
		dataFileConfig.set(key + ".money.loan", town.getRawLoan().toString());
		dataFileConfig.set(key + ".money.maxLoan", town.getRawMaxLoan().toString());
		dataFileConfig.set(key + ".money.refund", town.getRawRefund().toString());
		dataFileConfig.set(key + ".isCapital", town.isCapital());
	}	
	
	public Town deserializeTown(String key) {
		Chunks area = new Chunks();
		String name = dataFileConfig.getString(key + ".name");
		String mayor = dataFileConfig.getString(key + ".mayor");
		Town town = new Town(plugin, name, mayor,dataFileConfig.getString(key + ".country"));
		for (int i = 0; i < dataFileConfig.getInt(key + ".coMayors.size"); i++) {
			town.addCoMayor(dataFileConfig.getString(key + ".coMayors." + i));
		}
		
		// residents
		for (int i = 0; i < dataFileConfig.getInt(key + ".residents.size"); i++) {
			town.addResident(dataFileConfig.getString(key + ".residents." + i));
		}
		
		// park
		for (int i = 0; i < dataFileConfig.getInt(key + ".park.size"); i++) {
			town.addPark(deserializePark(key + ".park." + i));
		}
		
		// bank
		if (dataFileConfig.getBoolean(key + ".hasbank")) {
			town.setBank(deserializeLocalBank(key + ".localbank"));
		}
		
		// prison
		if (dataFileConfig.getBoolean(key + ".hasprison")) {
			town.setPrison(deserializeLocalPrison(key + ".localprison"));
		}
		
		// House
		for (int i = 0; i < dataFileConfig.getInt(key + ".houses.size"); i++) {
			
			town.addHouse(deserializeHouse(key + ".houses." + i));
		}
		
		for (int i = 0; i < dataFileConfig.getInt(key + ".goodBusiness.size"); i++) {
			town.addGoodBusiness(deserializeGoodBusiness(key + ".goodBusiness." + i));
		}
		for (int i = 0; i < dataFileConfig.getInt(key + ".serviceBusiness.size"); i++) {
			town.addServiceBusiness(deserializeServiceBusiness(key + ".serviceBusiness." + i));
		}
		for (int j = 0; j < dataFileConfig.getInt(key + ".area.size"); j ++) {
			ChunkData chunktemp = new ChunkData(new Point(dataFileConfig.getInt(key + ".area." + j + ".x"), dataFileConfig.getInt(key + ".area." + j + ".y")), dataFileConfig.getString(key + ".area.world"));
			area.addChunk(chunktemp);
		}
		town.setChunks(area);
		town.setRequest(deserializeVector(key + ".residentrequest"));
		town.setOffer(deserializeVector(key + ".residentoffer"));
		town.setNationTax(dataFileConfig.getDouble(key + ".nationTax"));
		town.setNationTaxOld(dataFileConfig.getDouble(key + ".nationTaxOld"));
		town.setHouseTax(dataFileConfig.getDouble(key + ".houseTax"));
		town.setGoodBusinessTax(dataFileConfig.getDouble(key + ".goodBusinessTax"));
		town.setServiceBusinessTax(dataFileConfig.getDouble(key + ".serviceBusinessTax"));
		town.setMoneyMultiplyer(new BigDecimal(dataFileConfig.getString(key + ".money.multiplyer")));
		town.setRawMoney(new BigDecimal(dataFileConfig.getString(key + ".money.amount")));
		town.setPluralMoney(dataFileConfig.getString(key + ".money.plural"));
		town.setSingularMoney(dataFileConfig.getString(key + ".money.singular"));


		town.setProtectionLevel(dataFileConfig.getInt(key + ".protectionLevel"));
		town.setMilitaryLevel(dataFileConfig.getInt(key + ".militarylevel"));
		town.setRawLoan(new BigDecimal(dataFileConfig.getString(key + ".money.loan")));
		town.setRawMaxLoan(new BigDecimal(dataFileConfig.getString(key + ".money.maxLoan")));
		town.setRawRefund(new BigDecimal(dataFileConfig.getString(key + ".money.refund")));
		town.setIsCapital(dataFileConfig.getBoolean(key + ".isCapital"));
		return town;
	}
	
	public void serializeLocalBank(LocalBank bank, String key) {
		// Setting new values
		dataFileConfig.addDefault(key + ".country", bank.getCountry());
		dataFileConfig.addDefault(key + ".town", bank.getTown());
		dataFileConfig.addDefault(key + ".isCubeSpace", bank.isCubeSpace());
		dataFileConfig.addDefault(key + ".isPolySpace", bank.isPolySpace());
		
		int n = 0;
		dataFileConfig.addDefault(key + ".builders.size", bank.getBuilders().size());
		for (String builder: bank.getBuilders()) {
			dataFileConfig.addDefault(key + ".builders." + n, builder);
			n++;
		}
		n = 0;
		
		if (bank.isCubeSpace()) {
			dataFileConfig.addDefault(key + ".cube.1.xmin", bank.getCubeSpace().getXmin());
			dataFileConfig.addDefault(key + ".cube.1.ymin", bank.getCubeSpace().getYmin());
			dataFileConfig.addDefault(key + ".cube.1.zmin", bank.getCubeSpace().getZmin());
			dataFileConfig.addDefault(key + ".cube.2.xmax", bank.getCubeSpace().getXmax());
			dataFileConfig.addDefault(key + ".cube.2.ymax", bank.getCubeSpace().getYmax());
			dataFileConfig.addDefault(key + ".cube.2.zmax", bank.getCubeSpace().getZmax());
			dataFileConfig.addDefault(key + ".cube.3.world", bank.getCubeSpace().getWorld());
		}
		if (bank.isPolySpace()) {
			Polygon poly = bank.getPolySpace().getPolygon();
			int[] x = poly.xpoints;
			int[] y = poly.ypoints;
			
			dataFileConfig.addDefault(key + ".poly.size", poly.npoints);
			for (int i = 0; i < poly.npoints; i++) {
				dataFileConfig.addDefault(key + ".poly." + i + ".x", x[i]);
				dataFileConfig.addDefault(key + ".poly." + i + ".y", y[i]);
			}
			dataFileConfig.addDefault(key + ".poly.world", bank.getPolySpace().getWorld());
			dataFileConfig.addDefault(key + ".poly.ymax", bank.getPolySpace().getYMax());
			dataFileConfig.addDefault(key + ".poly.ymin", bank.getPolySpace().getYMin());
		}
		
		// Updating values
		dataFileConfig.set(key + ".country", bank.getCountry());
		dataFileConfig.set(key + ".town", bank.getTown());
		dataFileConfig.set(key + ".isCubeSpace", bank.isCubeSpace());
		dataFileConfig.set(key + ".isPolySpace", bank.isPolySpace());
		
		dataFileConfig.set(key + ".builders.size", bank.getBuilders().size());
		for (String builder: bank.getBuilders()) {
			dataFileConfig.set(key + ".builders." + n, builder);
			n++;
		}
		
		if (bank.isCubeSpace()) {
			dataFileConfig.set(key + ".cube.1.xmin", bank.getCubeSpace().getXmin());
			dataFileConfig.set(key + ".cube.1.ymin", bank.getCubeSpace().getYmin());
			dataFileConfig.set(key + ".cube.1.zmin", bank.getCubeSpace().getZmin());
			dataFileConfig.set(key + ".cube.2.xmax", bank.getCubeSpace().getXmax());
			dataFileConfig.set(key + ".cube.2.ymax", bank.getCubeSpace().getYmax());
			dataFileConfig.set(key + ".cube.2.zmax", bank.getCubeSpace().getZmax());
			dataFileConfig.set(key + ".cube.3.world", bank.getCubeSpace().getWorld());
		}
		if (bank.isPolySpace()) {
			Polygon poly = bank.getPolySpace().getPolygon();
			int[] x = poly.xpoints;
			int[] y = poly.ypoints;
			
			dataFileConfig.set(key + ".poly.size", poly.npoints);
			for (int i = 0; i < poly.npoints; i++) {
				dataFileConfig.set(key + ".poly." + i + ".x", x[i]);
				dataFileConfig.set(key + ".poly." + i + ".y", y[i]);
			}
			dataFileConfig.set(key + ".poly.world", bank.getPolySpace().getWorld());
			dataFileConfig.set(key + ".poly.ymax", bank.getPolySpace().getYMax());
			dataFileConfig.set(key + ".poly.ymin", bank.getPolySpace().getYMin());
		}
	}
	
	public LocalBank deserializeLocalBank(String key) {
		Vector<String> builders = new Vector<String>();
		LocalBank bank;
		for (int n = 0; n < dataFileConfig.getInt(key + ".builders.size"); n++) {
			builders.add(dataFileConfig.getString(key + ".builders." + n));
		}
		
		if (dataFileConfig.getBoolean(key + ".isCubeSpace")) {
			Cuboid cube = new Cuboid(dataFileConfig.getString(key + ".cube.3.world"));
			cube.setXmin(dataFileConfig.getInt(key + ".cube.1.xmin"));
			cube.setYmin(dataFileConfig.getInt(key + ".cube.1.ymin"));
			cube.setZmin(dataFileConfig.getInt(key + ".cube.1.zmin"));
			cube.setXmax(dataFileConfig.getInt(key + ".cube.2.xmax"));
			cube.setYmax(dataFileConfig.getInt(key + ".cube.2.ymax"));
			cube.setZmax(dataFileConfig.getInt(key + ".cube.2.zmax"));
			cube.setWorld(dataFileConfig.getString(key + ".cube.3.world"));
			bank = new LocalBank(plugin, cube, dataFileConfig.getString(key + ".country"), dataFileConfig.getInt(key + ".town"));
		}
		else {
			polygonPrism poly = new polygonPrism(dataFileConfig.getString(key + ".poly.world"));
			for (int i = 0; i < dataFileConfig.getInt(key + ".poly.size"); i++) {
				int x = dataFileConfig.getInt(key + ".poly." + i + ".x");
				int y = dataFileConfig.getInt(key + ".poly." + i + ".y");
				Point corner = new Point(x, y);
				poly.addVertex(corner);	
			}
			poly.setYMax(dataFileConfig.getInt(key + ".poly.ymax"));
			poly.setYMin(dataFileConfig.getInt(key + ".poly.ymin"));
			bank = new LocalBank(plugin, poly, dataFileConfig.getString(key + ".country"), dataFileConfig.getInt(key + ".town"));
		}
		bank.setBuilders(builders);
		return bank;
	}
	
	public void serializeLocalPrison(LocalPrison prison, String key) {
		// Setting new values
		
		int count = 0;
		
		dataFileConfig.addDefault(key + ".country", prison.getCountry());
		dataFileConfig.addDefault(key + ".town", prison.getTown());
		dataFileConfig.addDefault(key + ".isCubeSpace", prison.isCubeSpace());
		dataFileConfig.addDefault(key + ".isPolySpace", prison.isPolySpace());
		
		int n = 0;
		dataFileConfig.addDefault(key + ".builders.size", prison.getBuilders().size());
		for (String builder: prison.getBuilders()) {
			dataFileConfig.addDefault(key + ".builders." + n, builder);
			n++;
		}
		n = 0;
		
		if (prison.isCubeSpace()) {
			dataFileConfig.addDefault(key + ".cube.1.xmin", prison.getCubeSpace().getXmin());
			dataFileConfig.addDefault(key + ".cube.1.ymin", prison.getCubeSpace().getYmin());
			dataFileConfig.addDefault(key + ".cube.1.zmin", prison.getCubeSpace().getZmin());
			dataFileConfig.addDefault(key + ".cube.2.xmax", prison.getCubeSpace().getXmax());
			dataFileConfig.addDefault(key + ".cube.2.ymax", prison.getCubeSpace().getYmax());
			dataFileConfig.addDefault(key + ".cube.2.zmax", prison.getCubeSpace().getZmax());
			dataFileConfig.addDefault(key + ".cube.3.world", prison.getCubeSpace().getWorld());
		}
		if (prison.isPolySpace()) {
			Polygon poly = prison.getPolySpace().getPolygon();
			int[] x = poly.xpoints;
			int[] y = poly.ypoints;
			
			dataFileConfig.addDefault(key + ".poly.size", poly.npoints);
			for (int i = 0; i < poly.npoints; i++) {
				dataFileConfig.addDefault(key + ".poly." + i + ".x", x[i]);
				dataFileConfig.addDefault(key + ".poly." + i + ".y", y[i]);
			}
			dataFileConfig.addDefault(key + ".poly.world", prison.getPolySpace().getWorld());
			dataFileConfig.addDefault(key + ".poly.ymax", prison.getPolySpace().getYMax());
			dataFileConfig.addDefault(key + ".poly.ymin", prison.getPolySpace().getYMin());
		}
		dataFileConfig.addDefault(key + ".cell.size", prison.getCells().size());
		for (Iterator<String> i = prison.getCells().keySet().iterator(); i.hasNext();) {
			String name = i.next();
			dataFileConfig.addDefault(key + ".cell." + count + ".name" , name);
			dataFileConfig.addDefault(key + ".cell." + count + ".world", prison.getCells().get(name).getWorld().getName());
			dataFileConfig.addDefault(key + ".cell." + count + ".x", prison.getCells().get(name).getBlockX());
			dataFileConfig.addDefault(key + ".cell." + count + ".y", prison.getCells().get(name).getBlockY());
			dataFileConfig.addDefault(key + ".cell." + count + ".z", prison.getCells().get(name).getBlockZ());
			dataFileConfig.addDefault(key + ".cell." + count + ".pitch", prison.getCells().get(name).getPitch());
			dataFileConfig.addDefault(key + ".cell." + count + ".yaw", prison.getCells().get(name).getYaw());
			
			dataFileConfig.addDefault(key + ".cell." + count + ".occupants.size", prison.getCells().get(name).getOccupant().size());
			for (int j = 0; j < prison.getCells().get(name).getOccupant().size(); j++) {
				dataFileConfig.addDefault(key + ".cell." + count + ".occupants." + j, prison.getCells().get(name).getOccupant().get(j));
			}
			count++;
		}
		
		// Updating values
		
		count = 0;
		
		dataFileConfig.set(key + ".country", prison.getCountry());
		dataFileConfig.set(key + ".town", prison.getTown());
		dataFileConfig.set(key + ".isCubeSpace", prison.isCubeSpace());
		dataFileConfig.set(key + ".isPolySpace", prison.isPolySpace());
		
		dataFileConfig.set(key + ".builders.size", prison.getBuilders().size());
		for (String builder: prison.getBuilders()) {
			dataFileConfig.set(key + ".builders." + n, builder);
			n++;
		}
		
		if (prison.isCubeSpace()) {
			dataFileConfig.set(key + ".cube.1.xmin", prison.getCubeSpace().getXmin());
			dataFileConfig.set(key + ".cube.1.ymin", prison.getCubeSpace().getYmin());
			dataFileConfig.set(key + ".cube.1.zmin", prison.getCubeSpace().getZmin());
			dataFileConfig.set(key + ".cube.2.xmax", prison.getCubeSpace().getXmax());
			dataFileConfig.set(key + ".cube.2.ymax", prison.getCubeSpace().getYmax());
			dataFileConfig.set(key + ".cube.2.zmax", prison.getCubeSpace().getZmax());
			dataFileConfig.set(key + ".cube.3.world", prison.getCubeSpace().getWorld());
		}
		if (prison.isPolySpace()) {
			Polygon poly = prison.getPolySpace().getPolygon();
			int[] x = poly.xpoints;
			int[] y = poly.ypoints;
			
			dataFileConfig.set(key + ".poly.size", poly.npoints);
			for (int i = 0; i < poly.npoints; i++) {
				dataFileConfig.set(key + ".poly." + i + ".x", x[i]);
				dataFileConfig.set(key + ".poly." + i + ".y", y[i]);
			}
			dataFileConfig.set(key + ".poly.world", prison.getPolySpace().getWorld());
			dataFileConfig.set(key + ".poly.ymax", prison.getPolySpace().getYMax());
			dataFileConfig.set(key + ".poly.ymin", prison.getPolySpace().getYMin());
		}
		dataFileConfig.set(key + ".cell.size", prison.getCells().size());
		for (Iterator<String> i = prison.getCells().keySet().iterator(); i.hasNext();) {
			String name = i.next();
			dataFileConfig.set(key + ".cell." + count + ".name" , name);
			dataFileConfig.set(key + ".cell." + count + ".world", prison.getCells().get(name).getWorld().getName());
			dataFileConfig.set(key + ".cell." + count + ".x", prison.getCells().get(name).getBlockX());
			dataFileConfig.set(key + ".cell." + count + ".y", prison.getCells().get(name).getBlockY());
			dataFileConfig.set(key + ".cell." + count + ".z", prison.getCells().get(name).getBlockZ());
			dataFileConfig.set(key + ".cell." + count + ".pitch", prison.getCells().get(name).getPitch());
			dataFileConfig.set(key + ".cell." + count + ".yaw", prison.getCells().get(name).getYaw());
			
			dataFileConfig.set(key + ".cell." + count + ".occupied", prison.getCells().get(name).isOccupied());
			
			dataFileConfig.set(key + ".cell." + count + ".occupants.size", prison.getCells().get(name).getOccupant().size());
			for (int j = 0; j < prison.getCells().get(name).getOccupant().size(); j++) {
				dataFileConfig.set(key + ".cell." + count + ".occupants." + j, prison.getCells().get(name).getOccupant().get(j));
			}
			count++;
		}
	}
	
	public LocalPrison deserializeLocalPrison(String key) {
		
		LocalPrison prison = null;
		Vector<String> builders = new Vector<String>();
		for (int n = 0; n < dataFileConfig.getInt(key + ".builders.size"); n++) {
			builders.add(dataFileConfig.getString(key + ".builders." + n));
		}
		if (dataFileConfig.getBoolean(key + ".isCubeSpace")) {
			Cuboid cube = new Cuboid(dataFileConfig.getString(key + ".cube.3.world"));
			cube.setXmin(dataFileConfig.getInt(key + ".cube.1.xmin"));
			cube.setYmin(dataFileConfig.getInt(key + ".cube.1.ymin"));
			cube.setZmin(dataFileConfig.getInt(key + ".cube.1.zmin"));
			cube.setXmax(dataFileConfig.getInt(key + ".cube.2.xmax"));
			cube.setYmax(dataFileConfig.getInt(key + ".cube.2.ymax"));
			cube.setZmax(dataFileConfig.getInt(key + ".cube.2.zmax"));
			cube.setWorld(dataFileConfig.getString(key + ".cube.3.world"));
			prison = new LocalPrison(plugin, cube, dataFileConfig.getString(key + ".country"), dataFileConfig.getInt(key + ".town"));
		}
		else {
			polygonPrism poly = new polygonPrism(dataFileConfig.getString(key + ".poly.world"));
			for (int i = 0; i < dataFileConfig.getInt(key + ".poly.size"); i++) {
				int x = dataFileConfig.getInt(key + ".poly." + i + ".x");
				int y = dataFileConfig.getInt(key + ".poly." + i + ".y");
				Point corner = new Point(x, y);
				poly.addVertex(corner);	
			}
			poly.setYMax(dataFileConfig.getInt(key + ".poly.ymax"));
			poly.setYMin(dataFileConfig.getInt(key + ".poly.ymin"));
			prison = new LocalPrison(plugin, poly, dataFileConfig.getString(key + ".country"), dataFileConfig.getInt(key + ".town"));
		}
		
		for (int i = 0; i < dataFileConfig.getInt(key + ".cell.size"); i++) {
			Location spot = new Location(plugin.getServer().getWorld(dataFileConfig.getString(key + ".cell." + i + ".world")), dataFileConfig.getInt(key +
					".cell." + i + ".x"), dataFileConfig.getInt(key + ".cell." + i + ".y"), dataFileConfig.getInt(key + ".cell." + i + ".z"));
			Cell cell = new Cell(spot, dataFileConfig.getString(key + ".cell." + i + ".name"));
			
			for (int j = 0; j < dataFileConfig.getInt(key + ".cell." + i + ".occupants.size"); j++) {
				cell.addOccupant(dataFileConfig.getString(key + ".cell." + i + ".occupants." + j));
			}
			spot.setPitch((float) dataFileConfig.getDouble(key + ".cell." + i + ".pitch"));
			spot.setYaw((float) dataFileConfig.getDouble(key + ".cell." + i + ".yaw"));
			prison.addCell(dataFileConfig.getString(key + ".cell." + i + ".name"), cell);
		}
		prison.setBuilders(builders);
		return prison;
	}
	
	public void serializePark(Park park, String key) {
		// Setting new values
		dataFileConfig.addDefault(key + ".country", park.getCountry());
		dataFileConfig.addDefault(key + ".town", park.getTown());
		dataFileConfig.addDefault(key + ".isCubeSpace", park.isCubeSpace());
		dataFileConfig.addDefault(key + ".isPolySpace", park.isPolySpace());
		dataFileConfig.addDefault(key + ".name", park.getName());
		dataFileConfig.addDefault(key + ".isFederal", park.getIsFederal());
		dataFileConfig.addDefault(key + ".protectionlevel", park.getProtectionLevel());
		
		int n = 0;
		dataFileConfig.addDefault(key + ".builders.size", park.getBuilders().size());
		for (String builder: park.getBuilders()) {
			dataFileConfig.addDefault(key + ".builders." + n, builder);
			n++;
		}
		n = 0;
		
		if (park.isCubeSpace()) {
			dataFileConfig.addDefault(key + ".cube.1.xmin", park.getCubeSpace().getXmin());
			dataFileConfig.addDefault(key + ".cube.1.ymin", park.getCubeSpace().getYmin());
			dataFileConfig.addDefault(key + ".cube.1.zmin", park.getCubeSpace().getZmin());
			dataFileConfig.addDefault(key + ".cube.2.xmax", park.getCubeSpace().getXmax());
			dataFileConfig.addDefault(key + ".cube.2.ymax", park.getCubeSpace().getYmax());
			dataFileConfig.addDefault(key + ".cube.2.zmax", park.getCubeSpace().getZmax());
			dataFileConfig.addDefault(key + ".cube.3.world", park.getCubeSpace().getWorld());
		}
		if (park.isPolySpace()) {
			Polygon poly = park.getPolySpace().getPolygon();
			int[] x = poly.xpoints;
			int[] y = poly.ypoints;
			
			dataFileConfig.addDefault(key + ".poly.size", poly.npoints);
			for (int i = 0; i < poly.npoints; i++) {
				dataFileConfig.addDefault(key + ".poly." + i + ".x", x[i]);
				dataFileConfig.addDefault(key + ".poly." + i + ".y", y[i]);
			}
			dataFileConfig.addDefault(key + ".poly.world", park.getPolySpace().getWorld());
			dataFileConfig.addDefault(key + ".poly.ymax", park.getPolySpace().getYMax());
			dataFileConfig.addDefault(key + ".poly.ymin", park.getPolySpace().getYMin());
		}
		
		// Updating values
		dataFileConfig.set(key + ".country", park.getCountry());
		dataFileConfig.set(key + ".town", park.getTown());
		dataFileConfig.set(key + ".isCubeSpace", park.isCubeSpace());
		dataFileConfig.set(key + ".isPolySpace", park.isPolySpace());
		dataFileConfig.set(key + ".isFederal", park.getIsFederal());
		dataFileConfig.set(key + ".name", park.getName());
		dataFileConfig.addDefault(key + ".protectionlevel", park.getProtectionLevel());
		
		dataFileConfig.set(key + ".builders.size", park.getBuilders().size());
		for (String builder: park.getBuilders()) {
			dataFileConfig.set(key + ".builders." + n, builder);
			n++;
		}
		
		if (park.isCubeSpace()) {
			dataFileConfig.set(key + ".cube.1.xmin", park.getCubeSpace().getXmin());
			dataFileConfig.set(key + ".cube.1.ymin", park.getCubeSpace().getYmin());
			dataFileConfig.set(key + ".cube.1.zmin", park.getCubeSpace().getZmin());
			dataFileConfig.set(key + ".cube.2.xmax", park.getCubeSpace().getXmax());
			dataFileConfig.set(key + ".cube.2.ymax", park.getCubeSpace().getYmax());
			dataFileConfig.set(key + ".cube.2.zmax", park.getCubeSpace().getZmax());
			dataFileConfig.set(key + ".cube.3.world", park.getCubeSpace().getWorld());
		}
		if (park.isPolySpace()) {
			Polygon poly = park.getPolySpace().getPolygon();
			int[] x = poly.xpoints;
			int[] y = poly.ypoints;
			
			dataFileConfig.set(key + ".poly.size", poly.npoints);
			for (int i = 0; i < poly.npoints; i++) {
				dataFileConfig.set(key + ".poly." + i + ".x", x[i]);
				dataFileConfig.set(key + ".poly." + i + ".y", y[i]);
			}
			dataFileConfig.set(key + ".poly.world", park.getPolySpace().getWorld());
			dataFileConfig.set(key + ".poly.ymax", park.getPolySpace().getYMax());
			dataFileConfig.set(key + ".poly.ymin", park.getPolySpace().getYMin());
		}
	}
	
	public Park deserializePark(String key) {
		
		Park park = null;
		Vector<String> builders = new Vector<String>();
		for (int n = 0; n < dataFileConfig.getInt(key + ".builders.size"); n++) {
			builders.add(dataFileConfig.getString(key + ".builders." + n));
		}
		
		if (dataFileConfig.getBoolean(key + ".isCubeSpace")) {
			Cuboid cube = new Cuboid(dataFileConfig.getString(key + ".cube.3.world"));
			cube.setXmin(dataFileConfig.getInt(key + ".cube.1.xmin"));
			cube.setYmin(dataFileConfig.getInt(key + ".cube.1.ymin"));
			cube.setZmin(dataFileConfig.getInt(key + ".cube.1.zmin"));
			cube.setXmax(dataFileConfig.getInt(key + ".cube.2.xmax"));
			cube.setYmax(dataFileConfig.getInt(key + ".cube.2.ymax"));
			cube.setZmax(dataFileConfig.getInt(key + ".cube.2.zmax"));
			cube.setWorld(dataFileConfig.getString(key + ".cube.3.world"));
			park = new Park(plugin, cube, dataFileConfig.getString(key + ".country"), dataFileConfig.getInt(key + ".town"),dataFileConfig.getBoolean(key + ".isFederal"), dataFileConfig.getString(key + ".name"));
		}
		else {
			polygonPrism poly = new polygonPrism(dataFileConfig.getString(key + ".poly.world"));
			for (int i = 0; i < dataFileConfig.getInt(key + ".poly.size"); i++) {
				int x = dataFileConfig.getInt(key + ".poly." + i + ".x");
				int y = dataFileConfig.getInt(key + ".poly." + i + ".y");
				Point corner = new Point(x, y);
				poly.addVertex(corner);	
			}
			poly.setYMax(dataFileConfig.getInt(key + ".poly.ymax"));
			poly.setYMin(dataFileConfig.getInt(key + ".poly.ymin"));
			park = new Park(plugin, poly, dataFileConfig.getString(key + ".country"), dataFileConfig.getInt(key + ".town"),dataFileConfig.getBoolean(key + ".isFederal"), dataFileConfig.getString(key + ".name"));
		}
		park.setBuilders(builders);
		park.setProtectionLevel(dataFileConfig.getInt(key + ".protectionlevel"));
		return park;
	}
	
	public void serializeHouse(House house, String key) {
		// Setting new values
		dataFileConfig.addDefault(key + ".country", house.getCountry());
		dataFileConfig.addDefault(key + ".town", house.getTown());
		dataFileConfig.addDefault(key + ".isCubeSpace", house.isCubeSpace());
		dataFileConfig.addDefault(key + ".isPolySpace", house.isPolySpace());
		dataFileConfig.addDefault(key + ".owners.size", house.getOwners().size());
		dataFileConfig.addDefault(key + ".name", house.getName());
		dataFileConfig.addDefault(key + ".protectionlevel", house.getProtectionLevel());
		for (int i = 0; i < house.getOwners().size(); i++) {
			dataFileConfig.addDefault(key + ".owners." + i, house.getOwners().get(i));
		}
		
		serializeVector(house.getOwnerOffers(), key + ".owneroffers");
		serializeVector(house.getOwnerRequest(), key + ".ownerrequests");
		
		// builders
		int n = 0;
		for (String i:house.getBuilders()) {
			dataFileConfig.addDefault(key + ".builders." + n, i);
			n++;
		}
		dataFileConfig.addDefault(key + ".builders.size", n);
		n=0;
		
		if (house.isCubeSpace()) {
			dataFileConfig.addDefault(key + ".cube.1.xmin", house.getCubeSpace().getXmin());
			dataFileConfig.addDefault(key + ".cube.1.ymin", house.getCubeSpace().getYmin());
			dataFileConfig.addDefault(key + ".cube.1.zmin", house.getCubeSpace().getZmin());
			dataFileConfig.addDefault(key + ".cube.2.xmax", house.getCubeSpace().getXmax());
			dataFileConfig.addDefault(key + ".cube.2.ymax", house.getCubeSpace().getYmax());
			dataFileConfig.addDefault(key + ".cube.2.zmax", house.getCubeSpace().getZmax());
			dataFileConfig.addDefault(key + ".cube.3.world", house.getCubeSpace().getWorld());
		}
		if (house.isPolySpace()) {
			Polygon poly = house.getPolySpace().getPolygon();
			int[] x = poly.xpoints;
			int[] y = poly.ypoints;
			
			dataFileConfig.addDefault(key + ".poly.size", poly.npoints);
			for (int i = 0; i < poly.npoints; i++) {
				dataFileConfig.addDefault(key + ".poly." + i + ".x", x[i]);
				dataFileConfig.addDefault(key + ".poly." + i + ".y", y[i]);
			}
			dataFileConfig.addDefault(key + ".poly.world", house.getPolySpace().getWorld());
			dataFileConfig.addDefault(key + ".poly.ymax", house.getPolySpace().getYMax());
			dataFileConfig.addDefault(key + ".poly.ymin", house.getPolySpace().getYMin());
		}
		
		// Updating values
		dataFileConfig.set(key + ".country", house.getCountry());
		dataFileConfig.set(key + ".town", house.getTown());
		dataFileConfig.set(key + ".isCubeSpace", house.isCubeSpace());
		dataFileConfig.set(key + ".isPolySpace", house.isPolySpace());
		dataFileConfig.set(key + ".owners.size", house.getOwners().size());
		dataFileConfig.set(key + ".name", house.getName());
		dataFileConfig.set(key + ".protectionlevel", house.getProtectionLevel());
		for (int i = 0; i < house.getOwners().size(); i++) {
			dataFileConfig.set(key + ".owners." + i, house.getOwners().get(i));
		}
		// builders

		for (String i:house.getBuilders()) {
			dataFileConfig.set(key + ".builders." + n, i);
			n++;
		}
		dataFileConfig.set(key + ".builders.size", n);
		n=0;
		if (house.isCubeSpace()) {
			dataFileConfig.set(key + ".cube.1.xmin", house.getCubeSpace().getXmin());
			dataFileConfig.set(key + ".cube.1.ymin", house.getCubeSpace().getYmin());
			dataFileConfig.set(key + ".cube.1.zmin", house.getCubeSpace().getZmin());
			dataFileConfig.set(key + ".cube.2.xmax", house.getCubeSpace().getXmax());
			dataFileConfig.set(key + ".cube.2.ymax", house.getCubeSpace().getYmax());
			dataFileConfig.set(key + ".cube.2.zmax", house.getCubeSpace().getZmax());
			dataFileConfig.set(key + ".cube.3.world", house.getCubeSpace().getWorld());
		}
		if (house.isPolySpace()) {
			Polygon poly = house.getPolySpace().getPolygon();
			int[] x = poly.xpoints;
			int[] y = poly.ypoints;
			
			dataFileConfig.set(key + ".poly.size", poly.npoints);
			for (int i = 0; i < poly.npoints; i++) {
				dataFileConfig.set(key + ".poly." + i + ".x", x[i]);
				dataFileConfig.set(key + ".poly." + i + ".y", y[i]);
			}
			dataFileConfig.set(key + ".poly.world", house.getPolySpace().getWorld());
			dataFileConfig.set(key + ".poly.ymax", house.getPolySpace().getYMax());
			dataFileConfig.set(key + ".poly.ymin", house.getPolySpace().getYMin());
		}
	}

	public House deserializeHouse(String key) {
		House housetemp;
		Vector<String> owners = new Vector<String>();
		for (int i = 0; i < dataFileConfig.getInt(key + ".owners.size"); i++) {
			owners.add(dataFileConfig.getString(key + ".owners." + i));
		}
		Vector<String> builders = new Vector<String>();
		for (int n = 0; n < dataFileConfig.getInt(key + ".builders.size"); n++) {
			builders.add(dataFileConfig.getString(key + ".builders." + n));
		}
		Vector<String> ownerOffers = deserializeVector(key + ".owneroffers");
		Vector<String> ownerRequests = deserializeVector(key + ".ownerrequests");
		
		if (dataFileConfig.getBoolean(key + ".isCubeSpace")) {
			Cuboid cube = new Cuboid(dataFileConfig.getString(key + ".cube.3.world"));
			cube.setXmin(dataFileConfig.getInt(key + ".cube.1.xmin"));
			cube.setYmin(dataFileConfig.getInt(key + ".cube.1.ymin"));
			cube.setZmin(dataFileConfig.getInt(key + ".cube.1.zmin"));
			cube.setXmax(dataFileConfig.getInt(key + ".cube.2.xmax"));
			cube.setYmax(dataFileConfig.getInt(key + ".cube.2.ymax"));
			cube.setZmax(dataFileConfig.getInt(key + ".cube.2.zmax"));
			cube.setWorld(dataFileConfig.getString(key + ".cube.3.world"));
			housetemp = new House(plugin, cube, owners, dataFileConfig.getString(key + ".country"), dataFileConfig.getInt(key + ".town"),
					dataFileConfig.getString(key + ".name"));
		}
		else {
			polygonPrism poly = new polygonPrism(dataFileConfig.getString(key + ".poly.world"));
			for (int i = 0; i < dataFileConfig.getInt(key + ".poly.size"); i++) {
				int x = dataFileConfig.getInt(key + ".poly." + i + ".x");
				int y = dataFileConfig.getInt(key + ".poly." + i + ".y");
				Point corner = new Point(x, y);
				poly.addVertex(corner);	
			}
			poly.setYMax(dataFileConfig.getInt(key + ".poly.ymax"));
			poly.setYMin(dataFileConfig.getInt(key + ".poly.ymin"));
			housetemp = new House(plugin, poly, owners, dataFileConfig.getString(key + ".country"), dataFileConfig.getInt(key + ".town"),
					dataFileConfig.getString(key + ".name"));
		}
		housetemp.setProtectionLevel(dataFileConfig.getInt(key + ".protectionlevel"));
		housetemp.setBuilders(builders);
		housetemp.setOwnerOffers(ownerOffers);
		housetemp.setOwnerRequests(ownerRequests);
		return housetemp;
	}
	public void serializeGoodBusiness(GoodBusiness business, String key) {
		// Setting new values
		dataFileConfig.addDefault(key + ".country", business.getCountry());
		dataFileConfig.addDefault(key + ".town", business.getTown());
		dataFileConfig.addDefault(key + ".isCubeSpace", business.isCubeSpace());
		dataFileConfig.addDefault(key + ".isPolySpace", business.isPolySpace());
		dataFileConfig.addDefault(key + ".owners.size", business.getOwners().size());
		dataFileConfig.addDefault(key + ".name", business.getName());
		dataFileConfig.addDefault(key + ".protectionlevel", business.getProtectionLevel());
		
		int m = 0;
		dataFileConfig.addDefault(key + ".shops.size", business.getChestShop().size());
		for (ChestShop shop: business.getChestShop()) {
			serializeChestShop(shop, key + ".shops." + m);
			m++;
		}
		m = 0;
		
		int n = 0;
		dataFileConfig.addDefault(key + ".builders.size", business.getBuilders().size());
		for (String builder: business.getBuilders()) {
			dataFileConfig.addDefault(key + ".builders." + n, builder);
			n++;
		}
		n = 0;
		
		int p = 0;
		dataFileConfig.addDefault(key + ".owneroffer.size", business.getOwnerOffers().size());
		for (String offers: business.getOwnerOffers()) {
			dataFileConfig.addDefault(key + ".owneroffer." + p, offers);
			p++;
		}
		p = 0;
		
		int q = 0;
		dataFileConfig.addDefault(key + ".employoffer.size", business.getEmployOffers().size());
		for (String offers: business.getEmployOffers()) {
			dataFileConfig.addDefault(key + ".employoffer." + q, offers);
			q++;
		}
		q = 0;
		
		int s = 0;
		dataFileConfig.addDefault(key + ".ownerrequest.size", business.getOwnerRequest().size());
		for (String request: business.getOwnerRequest()) {
			dataFileConfig.addDefault(key + ".ownerrequest." + s, request);
			s++;
		}
		s = 0;
		
		int r = 0;
		dataFileConfig.addDefault(key + ".employrequest.size", business.getEmployRequest().size());
		for (String request: business.getEmployRequest()) {
			dataFileConfig.addDefault(key + ".employrequest." + r, request);
			r++;
		}
		r = 0;
		
		int t = 0;
		dataFileConfig.addDefault(key + ".employees.size", business.getEmployees().size());
		for (String employee: business.getEmployees()) {
			dataFileConfig.addDefault(key + ".employees." + t, employee);
			t++;
		}
		t = 0;
		
		for (int i = 0; i < business.getOwners().size(); i++) {
			dataFileConfig.addDefault(key + ".owners." + i, business.getOwners().get(i));
		}
		if (business.isCubeSpace()) {
			dataFileConfig.addDefault(key + ".cube.1.xmin", business.getCubeSpace().getXmin());
			dataFileConfig.addDefault(key + ".cube.1.ymin", business.getCubeSpace().getYmin());
			dataFileConfig.addDefault(key + ".cube.1.zmin", business.getCubeSpace().getZmin());
			dataFileConfig.addDefault(key + ".cube.2.xmax", business.getCubeSpace().getXmax());
			dataFileConfig.addDefault(key + ".cube.2.ymax", business.getCubeSpace().getYmax());
			dataFileConfig.addDefault(key + ".cube.2.zmax", business.getCubeSpace().getZmax());
			dataFileConfig.addDefault(key + ".cube.3.world", business.getCubeSpace().getWorld());
		}
		if (business.isPolySpace()) {
			Polygon poly = business.getPolySpace().getPolygon();
			int[] x = poly.xpoints;
			int[] y = poly.ypoints;
			
			dataFileConfig.addDefault(key + ".poly.size", poly.npoints);
			for (int i = 0; i < poly.npoints; i++) {
				dataFileConfig.addDefault(key + ".poly." + i + ".x", x[i]);
				dataFileConfig.addDefault(key + ".poly." + i + ".y", y[i]);
			}
			dataFileConfig.addDefault(key + ".poly.world", business.getPolySpace().getWorld());
			dataFileConfig.addDefault(key + ".poly.ymax", business.getPolySpace().getYMax());
			dataFileConfig.addDefault(key + ".poly.ymin", business.getPolySpace().getYMin());
		}
		
		// Updating values
		dataFileConfig.set(key + ".country", business.getCountry());
		dataFileConfig.set(key + ".town", business.getTown());
		dataFileConfig.set(key + ".isCubeSpace", business.isCubeSpace());
		dataFileConfig.set(key + ".isPolySpace", business.isPolySpace());
		dataFileConfig.set(key + ".owners.size", business.getOwners().size());
		dataFileConfig.set(key + ".name", business.getName());
		dataFileConfig.set(key + ".shops.size", business.getChestShop().size());
		dataFileConfig.set(key + ".protectionlevel", business.getProtectionLevel());
		
		dataFileConfig.set(key + ".builders.size", business.getBuilders().size());
		for (String builder: business.getBuilders()) {
			dataFileConfig.set(key + ".builders." + n, builder);
			n++;
		}
		
		dataFileConfig.set(key + ".owneroffer.size", business.getOwnerOffers().size());
		for (String offers: business.getOwnerOffers()) {
			dataFileConfig.set(key + ".owneroffer." + p, offers);
			p++;
		}
		p = 0;
		
		
		dataFileConfig.set(key + ".employoffer.size", business.getEmployOffers().size());
		for (String offers: business.getEmployOffers()) {
			dataFileConfig.set(key + ".employoffer." + q, offers);
			q++;
		}
		q = 0;
		
		dataFileConfig.set(key + ".ownerrequest.size", business.getOwnerRequest().size());
		for (String request: business.getOwnerRequest()) {
			dataFileConfig.set(key + ".ownerrequest." + s, request);
			s++;
		}
		s = 0;
		
		dataFileConfig.set(key + ".employrequest.size", business.getEmployRequest().size());
		for (String request: business.getEmployRequest()) {
			dataFileConfig.set(key + ".employrequest." + r, request);
			r++;
		}
		r = 0;
		
		dataFileConfig.set(key + ".employees.size", business.getEmployees().size());
		for (String employee: business.getEmployees()) {
			dataFileConfig.set(key + ".employees." + t, employee);
			t++;
		}
		t = 0;
		
		for (int i = 0; i < business.getOwners().size(); i++) {
			dataFileConfig.set(key + ".owners." + i, business.getOwners().get(i));
		}
		if (business.isCubeSpace()) {
			dataFileConfig.set(key + ".cube.1.xmin", business.getCubeSpace().getXmin());
			dataFileConfig.set(key + ".cube.1.ymin", business.getCubeSpace().getYmin());
			dataFileConfig.set(key + ".cube.1.zmin", business.getCubeSpace().getZmin());
			dataFileConfig.set(key + ".cube.2.xmax", business.getCubeSpace().getXmax());
			dataFileConfig.set(key + ".cube.2.ymax", business.getCubeSpace().getYmax());
			dataFileConfig.set(key + ".cube.2.zmax", business.getCubeSpace().getZmax());
			dataFileConfig.set(key + ".cube.3.world", business.getCubeSpace().getWorld());
		}
		if (business.isPolySpace()) {
			Polygon poly = business.getPolySpace().getPolygon();
			int[] x = poly.xpoints;
			int[] y = poly.ypoints;
			
			dataFileConfig.set(key + ".poly.size", poly.npoints);
			for (int i = 0; i < poly.npoints; i++) {
				dataFileConfig.set(key + ".poly." + i + ".x", x[i]);
				dataFileConfig.set(key + ".poly." + i + ".y", y[i]);
			}
			dataFileConfig.set(key + ".poly.world", business.getPolySpace().getWorld());
			dataFileConfig.set(key + ".poly.ymax", business.getPolySpace().getYMax());
			dataFileConfig.set(key + ".poly.ymin", business.getPolySpace().getYMin());
		}
	}
	public GoodBusiness deserializeGoodBusiness(String key) {
		GoodBusiness businesstemp;
		Vector<String> owners = new Vector<String>();
		for (int i = 0; i < dataFileConfig.getInt(key + ".owners.size"); i++) {
			owners.add(dataFileConfig.getString(key + ".owners." + i));
		}
		Vector<String> builders = new Vector<String>();
		for (int n = 0; n < dataFileConfig.getInt(key + ".builders.size"); n++) {
			builders.add(dataFileConfig.getString(key + ".builders." + n));
		}
		Vector<String> employees = new Vector<String>();
		for (int n = 0; n < dataFileConfig.getInt(key + ".employees.size"); n++) {
			employees.add(dataFileConfig.getString(key + ".employees." + n));
		}
		Vector<String> owneroffer = new Vector<String>();
		for (int n = 0; n < dataFileConfig.getInt(key + ".owneroffer.size"); n++) {
			owneroffer.add(dataFileConfig.getString(key + ".owneroffer." + n));
		}
		Vector<String> employoffer = new Vector<String>();
		for (int n = 0; n < dataFileConfig.getInt(key + ".employoffer.size"); n++) {
			employoffer.add(dataFileConfig.getString(key + ".employoffer." + n));
		}
		Vector<String> ownerrequest = new Vector<String>();
		for (int n = 0; n < dataFileConfig.getInt(key + ".ownerrequest.size"); n++) {
			ownerrequest.add(dataFileConfig.getString(key + ".ownerrequest." + n));
		}
		Vector<String> employrequest = new Vector<String>();
		for (int n = 0; n < dataFileConfig.getInt(key + ".employrequest.size"); n++) {
			employrequest.add(dataFileConfig.getString(key + ".employrequest." + n));
		}
		if (dataFileConfig.getBoolean(key + ".isCubeSpace")) {
			Cuboid cube = new Cuboid(dataFileConfig.getString(key + ".cube.3.world"));
			cube.setXmin(dataFileConfig.getInt(key + ".cube.1.xmin"));
			cube.setYmin(dataFileConfig.getInt(key + ".cube.1.ymin"));
			cube.setZmin(dataFileConfig.getInt(key + ".cube.1.zmin"));
			cube.setXmax(dataFileConfig.getInt(key + ".cube.2.xmax"));
			cube.setYmax(dataFileConfig.getInt(key + ".cube.2.ymax"));
			cube.setZmax(dataFileConfig.getInt(key + ".cube.2.zmax"));
			cube.setWorld(dataFileConfig.getString(key + ".cube.3.world"));
			businesstemp = new GoodBusiness(plugin, cube, owners, dataFileConfig.getString(key + ".country"), dataFileConfig.getInt(key + ".town"), 
					dataFileConfig.getString(key + ".name"));
		}
		else {
			polygonPrism poly = new polygonPrism(dataFileConfig.getString(key + ".poly.world"));
			for (int i = 0; i < dataFileConfig.getInt(key + ".poly.size"); i++) {
				int x = dataFileConfig.getInt(key + ".poly." + i + ".x");
				int y = dataFileConfig.getInt(key + ".poly." + i + ".y");
				Point corner = new Point(x, y);
				poly.addVertex(corner);	
			}
			poly.setYMax(dataFileConfig.getInt(key + ".poly.ymax"));
			poly.setYMin(dataFileConfig.getInt(key + ".poly.ymin"));
			businesstemp = new GoodBusiness(plugin, poly, owners, dataFileConfig.getString(key + ".country"), dataFileConfig.getInt(key + ".town"),
					dataFileConfig.getString(key + ".name"));
		}
		for (int i = 0; i < dataFileConfig.getInt(key + ".shops.size"); i++) {
			businesstemp.addChestShop(deserializeChestShop(key + ".shops." + i));
		}
		businesstemp.setBuilders(builders);
		businesstemp.setEmployees(employees);
		businesstemp.setOwnerOffers(owneroffer);
		businesstemp.setEmployOffers(employoffer);
		businesstemp.setOwnerRequests(ownerrequest);
		businesstemp.setEmployRequests(employrequest);
		businesstemp.setProtectionLevel(dataFileConfig.getInt(key + ".protectionlevel"));
		return businesstemp;
	}
	public void serializeServiceBusiness(ServiceBusiness business, String key) {
		// Setting new values
		dataFileConfig.addDefault(key + ".country", business.getCountry());
		dataFileConfig.addDefault(key + ".town", business.getTown());
		dataFileConfig.addDefault(key + ".isCubeSpace", business.isCubeSpace());
		dataFileConfig.addDefault(key + ".isPolySpace", business.isPolySpace());
		dataFileConfig.addDefault(key + ".owners.size", business.getOwners().size());
		dataFileConfig.addDefault(key + ".name", business.getName());
		dataFileConfig.addDefault(key + ".protectionlevel", business.getProtectionLevel());
		
		int n = 0;
		dataFileConfig.addDefault(key + ".builders.size", business.getBuilders().size());
		for (String builder: business.getBuilders()) {
			dataFileConfig.addDefault(key + ".builders." + n, builder);
			n++;
		}
		n = 0;
		
		int p = 0;
		dataFileConfig.addDefault(key + ".owneroffer.size", business.getOwnerOffers().size());
		for (String offers: business.getOwnerOffers()) {
			dataFileConfig.addDefault(key + ".owneroffer." + p, offers);
			p++;
		}
		p = 0;
		
		int q = 0;
		dataFileConfig.addDefault(key + ".employoffer.size", business.getEmployOffers().size());
		for (String offers: business.getEmployOffers()) {
			dataFileConfig.addDefault(key + ".employoffer." + q, offers);
			q++;
		}
		q = 0;
		
		int s = 0;
		dataFileConfig.addDefault(key + ".ownerrequest.size", business.getOwnerRequest().size());
		for (String request: business.getOwnerRequest()) {
			dataFileConfig.addDefault(key + ".ownerrequest." + s, request);
			s++;
		}
		s = 0;
		
		int r = 0;
		dataFileConfig.addDefault(key + ".employrequest.size", business.getEmployRequest().size());
		for (String request: business.getEmployRequest()) {
			dataFileConfig.addDefault(key + ".employrequest." + r, request);
			r++;
		}
		r = 0;
		
		int t = 0;
		dataFileConfig.addDefault(key + ".employees.size", business.getEmployees().size());
		for (String employee: business.getEmployees()) {
			dataFileConfig.addDefault(key + ".employees." + t, employee);
			t++;
		}
		t = 0;
		
		for (int i = 0; i < business.getOwners().size(); i++) {
			dataFileConfig.addDefault(key + ".owners." + i, business.getOwners().get(i));
		}
		if (business.isCubeSpace()) {
			dataFileConfig.addDefault(key + ".cube.1.xmin", business.getCubeSpace().getXmin());
			dataFileConfig.addDefault(key + ".cube.1.ymin", business.getCubeSpace().getYmin());
			dataFileConfig.addDefault(key + ".cube.1.zmin", business.getCubeSpace().getZmin());
			dataFileConfig.addDefault(key + ".cube.2.xmax", business.getCubeSpace().getXmax());
			dataFileConfig.addDefault(key + ".cube.2.ymax", business.getCubeSpace().getYmax());
			dataFileConfig.addDefault(key + ".cube.2.zmax", business.getCubeSpace().getZmax());
			dataFileConfig.addDefault(key + ".cube.3.world", business.getCubeSpace().getWorld());
		}
		if (business.isPolySpace()) {
			Polygon poly = business.getPolySpace().getPolygon();
			int[] x = poly.xpoints;
			int[] y = poly.ypoints;
			
			dataFileConfig.addDefault(key + ".poly.size", poly.npoints);
			for (int i = 0; i < poly.npoints; i++) {
				dataFileConfig.addDefault(key + ".poly." + i + ".x", x[i]);
				dataFileConfig.addDefault(key + ".poly." + i + ".y", y[i]);
			}
			dataFileConfig.addDefault(key + ".poly.world", business.getPolySpace().getWorld());
			dataFileConfig.addDefault(key + ".poly.ymax", business.getPolySpace().getYMax());
			dataFileConfig.addDefault(key + ".poly.ymin", business.getPolySpace().getYMin());
		}
		
		// Updating values
		dataFileConfig.set(key + ".country", business.getCountry());
		dataFileConfig.set(key + ".town", business.getTown());
		dataFileConfig.set(key + ".isCubeSpace", business.isCubeSpace());
		dataFileConfig.set(key + ".isPolySpace", business.isPolySpace());
		dataFileConfig.set(key + ".owners.size", business.getOwners().size());
		dataFileConfig.set(key + ".name", business.getName());
		dataFileConfig.set(key + ".protectionlevel", business.getProtectionLevel());
		
		dataFileConfig.set(key + ".builders.size", business.getBuilders().size());
		for (String builder: business.getBuilders()) {
			dataFileConfig.set(key + ".builders." + n, builder);
			n++;
		}
		
		dataFileConfig.set(key + ".owneroffer.size", business.getOwnerOffers().size());
		for (String offers: business.getOwnerOffers()) {
			dataFileConfig.set(key + ".owneroffer." + p, offers);
			p++;
		}
		p = 0;
		
		
		dataFileConfig.set(key + ".employoffer.size", business.getEmployOffers().size());
		for (String offers: business.getEmployOffers()) {
			dataFileConfig.set(key + ".employoffer." + q, offers);
			q++;
		}
		q = 0;
		
		dataFileConfig.set(key + ".ownerrequest.size", business.getOwnerRequest().size());
		for (String request: business.getOwnerRequest()) {
			dataFileConfig.set(key + ".ownerrequest." + s, request);
			s++;
		}
		s = 0;
		
		dataFileConfig.set(key + ".employrequest.size", business.getEmployRequest().size());
		for (String request: business.getEmployRequest()) {
			dataFileConfig.set(key + ".employrequest." + r, request);
			r++;
		}
		r = 0;
		
		dataFileConfig.set(key + ".employees.size", business.getEmployees().size());
		for (String employee: business.getEmployees()) {
			dataFileConfig.set(key + ".employees." + t, employee);
			t++;
		}
		t = 0;
		
		for (int i = 0; i < business.getOwners().size(); i++) {
			dataFileConfig.set(key + ".owners." + i, business.getOwners().get(i));
		}
		if (business.isCubeSpace()) {
			dataFileConfig.set(key + ".cube.1.xmin", business.getCubeSpace().getXmin());
			dataFileConfig.set(key + ".cube.1.ymin", business.getCubeSpace().getYmin());
			dataFileConfig.set(key + ".cube.1.zmin", business.getCubeSpace().getZmin());
			dataFileConfig.set(key + ".cube.2.xmax", business.getCubeSpace().getXmax());
			dataFileConfig.set(key + ".cube.2.ymax", business.getCubeSpace().getYmax());
			dataFileConfig.set(key + ".cube.2.zmax", business.getCubeSpace().getZmax());
			dataFileConfig.set(key + ".cube.3.world", business.getCubeSpace().getWorld());
		}
		if (business.isPolySpace()) {
			Polygon poly = business.getPolySpace().getPolygon();
			int[] x = poly.xpoints;
			int[] y = poly.ypoints;
			
			dataFileConfig.set(key + ".poly.size", poly.npoints);
			for (int i = 0; i < poly.npoints; i++) {
				dataFileConfig.set(key + ".poly." + i + ".x", x[i]);
				dataFileConfig.set(key + ".poly." + i + ".y", y[i]);
			}
			dataFileConfig.set(key + ".poly.world", business.getPolySpace().getWorld());
			dataFileConfig.set(key + ".poly.ymax", business.getPolySpace().getYMax());
			dataFileConfig.set(key + ".poly.ymin", business.getPolySpace().getYMin());
		}
	}
	public ServiceBusiness deserializeServiceBusiness(String key) {
		ServiceBusiness businesstemp;
		Vector<String> owners = new Vector<String>();
		for (int i = 0; i < dataFileConfig.getInt(key + ".owners.size"); i++) {
			owners.add(dataFileConfig.getString(key + ".owners." + i));
		}
		Vector<String> builders = new Vector<String>();
		for (int n = 0; n < dataFileConfig.getInt(key + ".builders.size"); n++) {
			builders.add(dataFileConfig.getString(key + ".builders." + n));
		}
		Vector<String> employees = new Vector<String>();
		for (int n = 0; n < dataFileConfig.getInt(key + ".employees.size"); n++) {
			employees.add(dataFileConfig.getString(key + ".employees." + n));
		}
		Vector<String> owneroffer = new Vector<String>();
		for (int n = 0; n < dataFileConfig.getInt(key + ".owneroffer.size"); n++) {
			owneroffer.add(dataFileConfig.getString(key + ".owneroffer." + n));
		}
		Vector<String> employoffer = new Vector<String>();
		for (int n = 0; n < dataFileConfig.getInt(key + ".employoffer.size"); n++) {
			employoffer.add(dataFileConfig.getString(key + ".employoffer." + n));
		}
		Vector<String> ownerrequest = new Vector<String>();
		for (int n = 0; n < dataFileConfig.getInt(key + ".ownerrequest.size"); n++) {
			ownerrequest.add(dataFileConfig.getString(key + ".ownerrequest." + n));
		}
		Vector<String> employrequest = new Vector<String>();
		for (int n = 0; n < dataFileConfig.getInt(key + ".employrequest.size"); n++) {
			employrequest.add(dataFileConfig.getString(key + ".employrequest." + n));
		}
		if (dataFileConfig.getBoolean(key + ".isCubeSpace")) {
			Cuboid cube = new Cuboid(dataFileConfig.getString(key + ".cube.3.world"));
			cube.setXmin(dataFileConfig.getInt(key + ".cube.1.xmin"));
			cube.setYmin(dataFileConfig.getInt(key + ".cube.1.ymin"));
			cube.setZmin(dataFileConfig.getInt(key + ".cube.1.zmin"));
			cube.setXmax(dataFileConfig.getInt(key + ".cube.2.xmax"));
			cube.setYmax(dataFileConfig.getInt(key + ".cube.2.ymax"));
			cube.setZmax(dataFileConfig.getInt(key + ".cube.2.zmax"));
			cube.setWorld(dataFileConfig.getString(key + ".cube.3.world"));
			businesstemp = new ServiceBusiness(plugin, cube, owners, dataFileConfig.getString(key + ".country"), dataFileConfig.getInt(key + ".town"),
					dataFileConfig.getString(key + ".name"));
		}
		else {
			polygonPrism poly = new polygonPrism(dataFileConfig.getString(key + ".poly.world"));
			for (int i = 0; i < dataFileConfig.getInt(key + ".poly.size"); i++) {
				int x = dataFileConfig.getInt(key + ".poly." + i + ".x");
				int y = dataFileConfig.getInt(key + ".poly." + i + ".y");
				Point corner = new Point(x, y);
				poly.addVertex(corner);	
			}
			poly.setYMax(dataFileConfig.getInt(key + ".poly.ymax"));
			poly.setYMin(dataFileConfig.getInt(key + ".poly.ymin"));
			businesstemp = new ServiceBusiness(plugin, poly, owners, dataFileConfig.getString(key + ".country"), dataFileConfig.getInt(key + ".town"),
					dataFileConfig.getString(key + ".name"));
		}
		businesstemp.setBuilders(builders);
		businesstemp.setEmployees(employees);
		businesstemp.setOwnerOffers(owneroffer);
		businesstemp.setEmployOffers(employoffer);
		businesstemp.setOwnerRequests(ownerrequest);
		businesstemp.setEmployRequests(employrequest);
		businesstemp.setProtectionLevel(dataFileConfig.getInt(key + ".protectionlevel"));
		return businesstemp;
	}
	
	public void serializeChestShop(ChestShop shop, String key) {
		// Adding new values
		dataFileConfig.addDefault(key + ".cost", shop.getPrice());
		dataFileConfig.addDefault(key + ".quantity", shop.getQuantity());
		dataFileConfig.addDefault(key + ".double", shop.getDoubleChest());
		dataFileConfig.addDefault(key + ".material", shop.getMaterial());
		dataFileConfig.addDefault(key + ".durability", shop.getMaterial().getDurability());
		if (shop.getDoubleChest()) {
			dataFileConfig.addDefault(key + ".1.world", shop.getSpot()[0].getWorld().getName());
			dataFileConfig.addDefault(key + ".1.x", shop.getSpot()[0].getBlockX());
			dataFileConfig.addDefault(key + ".1.y", shop.getSpot()[0].getBlockY());
			dataFileConfig.addDefault(key + ".1.z", shop.getSpot()[0].getBlockZ());
			dataFileConfig.addDefault(key + ".2.world", shop.getSpot()[1].getWorld().getName());
			dataFileConfig.addDefault(key + ".2.x", shop.getSpot()[1].getBlockX());
			dataFileConfig.addDefault(key + ".2.y", shop.getSpot()[1].getBlockY());
			dataFileConfig.addDefault(key + ".2.z", shop.getSpot()[1].getBlockZ());
			dataFileConfig.addDefault(key + ".3.world", shop.getSpot()[2].getWorld().getName());
			dataFileConfig.addDefault(key + ".3.x", shop.getSpot()[2].getBlockX());
			dataFileConfig.addDefault(key + ".3.y", shop.getSpot()[2].getBlockY());
			dataFileConfig.addDefault(key + ".3.z", shop.getSpot()[2].getBlockZ());
			dataFileConfig.addDefault(key + ".4.world", shop.getSpot()[3].getWorld().getName());
			dataFileConfig.addDefault(key + ".4.x", shop.getSpot()[3].getBlockX());
			dataFileConfig.addDefault(key + ".4.y", shop.getSpot()[3].getBlockY());
			dataFileConfig.addDefault(key + ".4.z", shop.getSpot()[3].getBlockZ());
		}
		else {
			dataFileConfig.addDefault(key + ".1.world", shop.getSpot()[0].getWorld().getName());
			dataFileConfig.addDefault(key + ".1.x", shop.getSpot()[0].getBlockX());
			dataFileConfig.addDefault(key + ".1.y", shop.getSpot()[0].getBlockY());
			dataFileConfig.addDefault(key + ".1.z", shop.getSpot()[0].getBlockZ());
			dataFileConfig.addDefault(key + ".3.world", shop.getSpot()[2].getWorld().getName());
			dataFileConfig.addDefault(key + ".3.x", shop.getSpot()[2].getBlockX());
			dataFileConfig.addDefault(key + ".3.y", shop.getSpot()[2].getBlockY());
			dataFileConfig.addDefault(key + ".3.z", shop.getSpot()[2].getBlockZ());
			dataFileConfig.addDefault(key + ".4.world", shop.getSpot()[3].getWorld().getName());
			dataFileConfig.addDefault(key + ".4.x", shop.getSpot()[3].getBlockX());
			dataFileConfig.addDefault(key + ".4.y", shop.getSpot()[3].getBlockY());
			dataFileConfig.addDefault(key + ".4.z", shop.getSpot()[3].getBlockZ());
		}
		
		// updating values
		dataFileConfig.set(key + ".cost", shop.getPrice());
		dataFileConfig.set(key + ".quantity", shop.getQuantity());
		dataFileConfig.set(key + ".double", shop.getDoubleChest());
		dataFileConfig.set(key + ".material", shop.getMaterial());
		dataFileConfig.set(key + ".durability", shop.getMaterial().getDurability());
		if (shop.getDoubleChest()) {
			dataFileConfig.set(key + ".1.world", shop.getSpot()[0].getWorld().getName());
			dataFileConfig.set(key + ".1.x", shop.getSpot()[0].getBlockX());
			dataFileConfig.set(key + ".1.y", shop.getSpot()[0].getBlockY());
			dataFileConfig.set(key + ".1.z", shop.getSpot()[0].getBlockZ());
			dataFileConfig.set(key + ".2.world", shop.getSpot()[1].getWorld().getName());
			dataFileConfig.set(key + ".2.x", shop.getSpot()[1].getBlockX());
			dataFileConfig.set(key + ".2.y", shop.getSpot()[1].getBlockY());
			dataFileConfig.set(key + ".2.z", shop.getSpot()[1].getBlockZ());
			dataFileConfig.set(key + ".3.world", shop.getSpot()[2].getWorld().getName());
			dataFileConfig.set(key + ".3.x", shop.getSpot()[2].getBlockX());
			dataFileConfig.set(key + ".3.y", shop.getSpot()[2].getBlockY());
			dataFileConfig.set(key + ".3.z", shop.getSpot()[2].getBlockZ());
			dataFileConfig.set(key + ".4.world", shop.getSpot()[3].getWorld().getName());
			dataFileConfig.set(key + ".4.x", shop.getSpot()[3].getBlockX());
			dataFileConfig.set(key + ".4.y", shop.getSpot()[3].getBlockY());
			dataFileConfig.set(key + ".4.z", shop.getSpot()[3].getBlockZ());
		}
		else {
			dataFileConfig.set(key + ".1.world", shop.getSpot()[0].getWorld().getName());
			dataFileConfig.set(key + ".1.x", shop.getSpot()[0].getBlockX());
			dataFileConfig.set(key + ".1.y", shop.getSpot()[0].getBlockY());
			dataFileConfig.set(key + ".1.z", shop.getSpot()[0].getBlockZ());
			dataFileConfig.set(key + ".3.world", shop.getSpot()[2].getWorld().getName());
			dataFileConfig.set(key + ".3.x", shop.getSpot()[2].getBlockX());
			dataFileConfig.set(key + ".3.y", shop.getSpot()[2].getBlockY());
			dataFileConfig.set(key + ".3.z", shop.getSpot()[2].getBlockZ());
			dataFileConfig.set(key + ".4.world", shop.getSpot()[3].getWorld().getName());
			dataFileConfig.set(key + ".4.x", shop.getSpot()[3].getBlockX());
			dataFileConfig.set(key + ".4.y", shop.getSpot()[3].getBlockY());
			dataFileConfig.set(key + ".4.z", shop.getSpot()[3].getBlockZ());
		}
	}
	
	public ChestShop deserializeChestShop(String key) {
		boolean doubleChest = dataFileConfig.getBoolean(key + ".double");
		BigDecimal price = new BigDecimal(dataFileConfig.getString(key + ".cost"));
		int quantity = dataFileConfig.getInt(key + ".quantity");
		ItemStack item = dataFileConfig.getItemStack(key + ".material");
		item.setDurability((short) dataFileConfig.getInt(key + ".durability"));
		Location[] chests = new Location[4];
		if (doubleChest) {
			chests[0] = new Location(plugin.getServer().getWorld(dataFileConfig.getString(key + ".1.world")), dataFileConfig.getDouble(key + ".1.x"),
					dataFileConfig.getDouble(key + ".1.y"), dataFileConfig.getDouble(key + ".1.z"));
			chests[1] = new Location(plugin.getServer().getWorld(dataFileConfig.getString(key + ".2.world")), dataFileConfig.getDouble(key + ".2.x"),
					dataFileConfig.getDouble(key + ".2.y"), dataFileConfig.getDouble(key + ".2.z"));
			chests[2] = new Location(plugin.getServer().getWorld(dataFileConfig.getString(key + ".3.world")), dataFileConfig.getDouble(key + ".3.x"),
					dataFileConfig.getDouble(key + ".3.y"), dataFileConfig.getDouble(key + ".3.z"));
			chests[3] = new Location(plugin.getServer().getWorld(dataFileConfig.getString(key + ".4.world")), dataFileConfig.getDouble(key + ".4.x"),
					dataFileConfig.getDouble(key + ".4.y"), dataFileConfig.getDouble(key + ".4.z"));
		}
		else {
			chests[0] = new Location(plugin.getServer().getWorld(dataFileConfig.getString(key + ".1.world")), dataFileConfig.getDouble(key + ".1.x"),
					dataFileConfig.getDouble(key + ".1.y"), dataFileConfig.getDouble(key + ".1.z"));
			chests[2] = new Location(plugin.getServer().getWorld(dataFileConfig.getString(key + ".3.world")), dataFileConfig.getDouble(key + ".3.x"),
					dataFileConfig.getDouble(key + ".3.y"), dataFileConfig.getDouble(key + ".3.z"));
			chests[3] = new Location(plugin.getServer().getWorld(dataFileConfig.getString(key + ".4.world")), dataFileConfig.getDouble(key + ".4.x"),
					dataFileConfig.getDouble(key + ".4.y"), dataFileConfig.getDouble(key + ".4.z"));
		}
		
		return new ChestShop(item, price, quantity, chests);
	}
	
	public void serializeNPC(NPC npc, String key) {
		
		PDC.addDefault(key + ".money", npc.getRawMoney().toString());
		PDC.addDefault(key + ".moneymultiplyer", npc.getMoneyMultiplyer().toString());
		int count = 0;
		PDC.addDefault(key + ".buy.size", npc.buyVector.length);
		for(BigDecimal buy:npc.buyVector) {
			PDC.addDefault(key + ".buy." + count, buy.toString());
			count++;
		}
		
		// Update
		PDC.set(key + ".money", npc.getRawMoney().toString());
		PDC.set(key + ".moneymultiplyer", npc.getMoneyMultiplyer().toString());
		int count2 = 0;
		PDC.addDefault(key + ".buy.size", npc.buyVector.length);
		for(BigDecimal buy:npc.buyVector) {
			PDC.set(key + ".buy." + count2, buy.toString());
			count2++;
		}
	}
	
	public NPC deSerializeNPC(String key) {
		NPC npc = new NPC(plugin);
		
		npc.setRawMoney(new BigDecimal(PDC.getString(key + ".money")));
		npc.setMoneyMultiplyer(new BigDecimal(PDC.getString(key + ".moneymultiplyer")));
		
		for(int i = 0; i<PDC.getInt(key + ".buy.size"); i++) {
			npc.buyVector[i] = new BigDecimal(PDC.getString(key + ".buy." + i));
		}
		
		return npc;
	}
	
	// Method to deserialize and serialize vectors
	public void serializeVector(Vector<String> object, String key) {
		int n = 0;
		dataFileConfig.addDefault(key + ".size", object.size());
		for(String i : object) {
			dataFileConfig.addDefault(key + "." + n, i);
			n +=1;
		}
		n = 0;
		dataFileConfig.set(key + ".size", object.size());
		for(String i : object) {
			dataFileConfig.set(key + "." + n, i);
			n +=1;
		}
	}
	
	public Vector<String> deserializeVector(String key) {
		Vector<String> value = new Vector<String>();

		for(int i = 0; i<dataFileConfig.getInt(key + ".size"); i+=1) {
			value.add(dataFileConfig.getString(key + "." + i));
		}
		return value;
	}
    public static String InventoryToString (Inventory invInventory)
    {
        String serialization = invInventory.getSize() + ";";
        for (int i = 0; i < invInventory.getSize(); i++)
        {
            ItemStack is = invInventory.getItem(i);
            if (is != null)
            {
                String serializedItemStack = new String();
               
                String isType = String.valueOf(is.getType().getId());
                serializedItemStack += "t@" + isType;
               
                if (is.getDurability() != 0)
                {
                    String isDurability = String.valueOf(is.getDurability());
                    serializedItemStack += ":d@" + isDurability;
                }
               
                if (is.getAmount() != 1)
                {
                    String isAmount = String.valueOf(is.getAmount());
                    serializedItemStack += ":a@" + isAmount;
                }
               
                Map<Enchantment,Integer> isEnch = is.getEnchantments();
                if (isEnch.size() > 0)
                {
                    for (Entry<Enchantment,Integer> ench : isEnch.entrySet())
                    {
                        serializedItemStack += ":e@" + ench.getKey().getId() + "@" + ench.getValue();
                    }
                }
               
                serialization += i + "#" + serializedItemStack + ";";
            }
        }
        return serialization;
    }
   
    public static Inventory StringToInventory (String invString)
    {
        String[] serializedBlocks = invString.split(";");
        String invInfo = serializedBlocks[0];
        Inventory deserializedInventory = Bukkit.getServer().createInventory(null, Integer.valueOf(invInfo));
       
        for (int i = 1; i < serializedBlocks.length; i++)
        {
            String[] serializedBlock = serializedBlocks[i].split("#");
            int stackPosition = Integer.valueOf(serializedBlock[0]);
           
            if (stackPosition >= deserializedInventory.getSize())
            {
                continue;
            }
           
            ItemStack is = null;
            Boolean createdItemStack = false;
           
            String[] serializedItemStack = serializedBlock[1].split(":");
            for (String itemInfo : serializedItemStack)
            {
                String[] itemAttribute = itemInfo.split("@");
                if (itemAttribute[0].equals("t"))
                {
                    is = new ItemStack(Material.getMaterial(Integer.valueOf(itemAttribute[1])));
                    createdItemStack = true;
                }
                else if (itemAttribute[0].equals("d") && createdItemStack)
                {
                    is.setDurability(Short.valueOf(itemAttribute[1]));
                }
                else if (itemAttribute[0].equals("a") && createdItemStack)
                {
                    is.setAmount(Integer.valueOf(itemAttribute[1]));
                }
                else if (itemAttribute[0].equals("e") && createdItemStack)
                {
                    is.addEnchantment(Enchantment.getById(Integer.valueOf(itemAttribute[1])), Integer.valueOf(itemAttribute[2]));
                }
            }
            deserializedInventory.setItem(stackPosition, is);
        }
       
        return deserializedInventory;
    }
}
