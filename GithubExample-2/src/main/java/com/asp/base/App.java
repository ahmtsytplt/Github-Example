package com.asp.base;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class App {

	private static HttpURLConnection connection;

	public static void main(String[] args) {

		// Instances for the application
		// List to hold project names
		ArrayList<String> projectList = new ArrayList<>();

		// to get repository names
		final String repositoryInfoURL = "https://api.github.com/orgs/apache/repos?sort=updated&direction=desc";

		// to get repository contributers' name
		final String committersInfoURLHead = "https://api.github.com/repos/";
		final String committersInfoURLTail = "/contributors?per_page=10";
		
		BufferedWriter writer = null;
		
		try {

			// variables for writing a file
			File userInfoFile = new File("statistics.txt");
			if (userInfoFile.createNewFile()) {
				System.out.println("File created: " + userInfoFile.getName());
			} else {
				userInfoFile.delete();
				userInfoFile.createNewFile();
				System.out.println("File already exists.");
			}

			writer = new BufferedWriter(new FileWriter("statistics.txt", true));
			
			// Define url to get project names
			URL requestUrl = new URL(repositoryInfoURL);

			// setup connection
			connection = (HttpURLConnection) requestUrl.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000); // if connection is not successfull after 5s, timeout
			connection.setReadTimeout(5000);
			// get response
			String projects = getResponse(connection);

			// if successful, parse the project response
			JSONArray projectsArray = new JSONArray(projects);
			
			if (projectsArray.toString().contains("exceeded")) {
				writer.write("You passed the request limit (1000 per hour)");
			}
			
			// add project names to list
			for (int i = 0; i < projectsArray.length(); i++) {
				projectList.add( projectsArray.getJSONObject(i).get("full_name").toString());
				if (projectList.size() >= 5) {
					break;
				}
			}

			// find top 10 committers for each project
			URL committersURL;
			for (String projectName : projectList) {
				committersURL = new URL(committersInfoURLHead + projectName + committersInfoURLTail);
				connection = (HttpURLConnection) committersURL.openConnection();

				String userNames = getResponse(connection);
				JSONArray usernameArray = new JSONArray(userNames);

				for (int i = 0; i < usernameArray.length(); i++) {
					//usernameList.add( usernameArray.getJSONObject(i).get("full_name").toString());
					URL userURL = new URL(usernameArray.getJSONObject(i).get("url").toString());
					
					//userURL = new URL(userInfoURL + username);
					connection = (HttpURLConnection) userURL.openConnection();

					// parse user info
					String userInfo = getResponse(connection);
					
					JSONObject profileObject = new JSONObject(userInfo);
					String userName = profileObject.get("login").toString();
					String location = profileObject.get("location").toString();
					String company = profileObject.get("company").toString();
					int numberOfCommits = (int) usernameArray.getJSONObject(i).get("contributions");
					//System.out.println(profileObject.get("login"));
					writeUserInfoToFile(writer, projectName, userName, location, company, numberOfCommits);
						
				}	
			}
		} catch (Exception e) {
			
			try {
				writer.append(e.toString());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			connection.disconnect();

		}
	}


	public static String getResponse (HttpURLConnection connection) throws IOException  {
		BufferedReader reader;
		String line;
		StringBuilder responseContent = new StringBuilder();
		// check if successful
		int status = connection.getResponseCode();

		if (status >= 300) {
			reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
			while ((line = reader.readLine()) != null) {
				responseContent.append(line);
			}
			reader.close();
		}
		else {
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while ((line = reader.readLine()) != null) {
				responseContent.append(line);
			}
			reader.close();
		}

		return responseContent.toString();
	}

	public static void writeUserInfoToFile(BufferedWriter writer, String projectName, String username, String location, String company, int numberOfCommits) throws IOException {
		writer.append("repo: " + projectName + " - ");
		writer.append("user: " + username + ", ");
		writer.append("location: " + location + ", ");
		writer.append("company: " + company + ", ");
		writer.append("contributions: " + numberOfCommits + "\n");
	}

}


