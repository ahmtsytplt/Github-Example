package com.asp.bean;

public class User {

	private String username;
	private String location;
	private String company;
	private int numberOfContributions;
	
	public void setUsername(String username) {
		this.username = username;
	}

	public void setLocation(String location) {
		if (location != "null" || "null".compareToIgnoreCase(company) != 0) {
			this.location = location;
		} else {
			this.location = "unavailable";
		}
	}

	public void setCompany(String company) {
		if (location != null || "null".compareToIgnoreCase(company) != 0) {
			this.company = company;
		} else {
			this.company = "unavailable";
		}
	}

	public void setNumberOfContributions(int numberOfContributions) {
		this.numberOfContributions = numberOfContributions;
	}
	@Override
	public String toString() {
		return "username:" + username + ", location:" + location + ", company:" + company
				+ ", numberOfContributions:" + numberOfContributions;
	}
	
	
	
	
	
}
