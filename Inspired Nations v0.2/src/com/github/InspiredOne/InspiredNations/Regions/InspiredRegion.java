package com.github.InspiredOne.InspiredNations.Regions;

import java.awt.Rectangle;
import java.math.BigDecimal;
import java.util.Vector;

import org.bukkit.Location;

import com.github.InspiredOne.InspiredNations.InspiredNations;
import com.github.InspiredOne.InspiredNations.Tools;
import com.github.InspiredOne.InspiredNations.Tools.version;

public abstract class InspiredRegion {
	Tools tools;
	String country;
	int town;
	InspiredNations plugin;
	polygonPrism polyspace = null;
	Cuboid cubespace = null;
	String name = "";
	Vector<String> builders = new Vector<String>();
	int protectionLevel = 1;

	
	public InspiredRegion(InspiredNations instance, Cuboid space,String countrytemp, int towntemp, String nametemp) {
		plugin = instance;
		cubespace = space;
		country = countrytemp;
		town = towntemp;
		name = nametemp;
		tools = new Tools(plugin);
	}
	
	public InspiredRegion(InspiredNations instance, polygonPrism space, String countrytemp, int towntemp, String nametemp) {
		plugin = instance;
		polyspace = space;
		country = countrytemp;
		town = towntemp;
		name = nametemp;
		tools = new Tools(plugin);
	}
	
	public void setAddress(String countrytemp, int towntemp) {
		countrytemp = tools.findCountry(countrytemp).get(0);
		country = countrytemp;
		town = towntemp;
	}
	
	public void setPolySpace(polygonPrism space) {
		polyspace = space;
		cubespace = null;
	}
	
	public void setCubeSpace(Cuboid space) {
		cubespace = space;
		polyspace = null;
	}
	

	
	public void setName(String nametemp) {
		name = nametemp;
	}
	
	public void setBuilders(Vector<String> builderstemp) {
		builders = builderstemp;
	}
	
	public void addBuilder(String builder) {
		builder = tools.findPerson(builder).get(0);
		builders.add(builder);
	}
	
	public void removeBuilder(String builder) {
		builder = tools.findPerson(builder).get(0);
		builders.remove(builder);
	}
	
	public Vector<String> getBuilders() {
		return builders;
	}
	
	public boolean isBuilder(String builder) {
		builder = tools.findPerson(builder).get(0);
		return builders.contains(builder);
	}
	
	public Cuboid getCubeSpace() {
		return cubespace;
	}
	
	public Object getRegion() {
		if (this.isCubeSpace()) {
			return this.getCubeSpace();
		}
		else return this.getPolySpace();
	}
	
	public polygonPrism getPolySpace() {
		return polyspace;
	}
	
	public InspiredNations getPlugin() {
		return plugin;
	}
	
	public String getCountry() {
		return country;
	}
	
	public int getTown() {
		return town;
	}
	
	public String getName() {
		return name;
	}
	
	public void setProtectionLevel(int level) {
		protectionLevel = level;
	}
	
	abstract public void changeProtectionLevel(int level);

	
	public int getProtectionLevel() {
		return protectionLevel;
	}
	
	public Boolean isPolySpace() {
		try {	
			if (polyspace.equals(null)) {
				return false;
			}
			else return true;
		}
		catch (Exception ex) {
			return false;
		}
	}
	
	public Boolean isCubeSpace() {
		try {	
			if (cubespace.equals(null)) {
				return false;
			}
			else return true;
		}
		catch (Exception ex) {
			return false;
		}
	}
	
	public boolean isInTown() {
		Town townIn = plugin.countrydata.get(country.toLowerCase()).getTowns().get(town);
		if (isPolySpace()) {
			Rectangle rect = polyspace.getPolygon().getBounds();
			for (int i = (int) rect.getMinX(); i < (int) rect.getMaxX(); i++) {
				for (int j = (int) rect.getMinY(); j < (int)rect.getMaxY(); j++) {
					for (int l = polyspace.getYMin(); l < polyspace.getYMax(); l++) {
						Location test = new Location(plugin.getServer().getWorld(polyspace.getWorld()), i, l, j);
						if ((!townIn.isIn(test)) && polyspace.isIn(test)) {
							return false;
						}
					}
				}
			}
		}
		if (isCubeSpace()) {
			for (int i = cubespace.getXmin(); i < cubespace.getXmax(); i++) {
				for (int j = cubespace.getYmin(); j < cubespace.getYmax(); j++) {
					for (int l = cubespace.getZmin(); l < cubespace.getZmax(); l++) {
						Location test = new Location(plugin.getServer().getWorld(cubespace.getWorld()), i, j, l);
						if ((!townIn.isIn(test))) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	public Boolean isIn(Location tile) {
		if (isPolySpace()) {
			return polyspace.isIn(tile);
		}
		else if (isCubeSpace()) {
			return cubespace.isIn(tile);
		}
		else return false;
	}
	
	public int Area() {
		if (isPolySpace()) {
			return polyspace.Area();
		}
		else if (isCubeSpace()) {
			return cubespace.Area();
		}
		else return 0;
	}
	
	public int Volume() {
		if (isPolySpace()) {
			return polyspace.Volume();
		}
		else if (isCubeSpace()) {
			return cubespace.Volume();
		}
		else return 0;
	}
	
	public int Perimeter() {
		if (isPolySpace()) {
			return polyspace.Perimeter();
		}
		else if (isCubeSpace()) {
			return cubespace.Perimeter();
		}
		else return 0;
	}
	

}
