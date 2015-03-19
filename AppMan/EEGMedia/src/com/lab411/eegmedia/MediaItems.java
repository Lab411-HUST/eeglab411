package com.lab411.eegmedia;

public class MediaItems {

	private String name;
	private String link;
	private int rate; 

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public MediaItems(String name, String link, int rate) {
		this.name = name;
		this.link = link;
		this.rate = rate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

}
