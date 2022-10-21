package com.coronavirus.app.services;

//class fetches data from the URL

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.coronavirus.app.models.LocationStats;

@Service // makes class a spring service
public class CoronaVirusDataService {

	private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

	private List<LocationStats> allStats = new ArrayList<>(); // instance of the LocationStats class

	public List<LocationStats> getAllStats() { // Getter method for Allstats
		return allStats;
	}

	@PostConstruct // Instructs spring to execute below method once it has constructed the service
					// above
	@Scheduled(cron = "* * 1 * * *") // scheduled the run of a method on a regular basis (very second
	public void fetchVirusData() throws IOException, InterruptedException { // fetches data from the URL

		List<LocationStats> newStats = new ArrayList<>(); // creates a new list to populate the above List (allStats)

		// Calling the Http URL
		HttpClient client = HttpClient.newHttpClient(); // create new object
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build(); // creates a new request
		HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString()); // gets the
																										// response
		StringReader csvBodyReader = new StringReader(httpResponse.body());

		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader); // Iterates
																										// through the
																										// URL data

		for (CSVRecord record : records) {
			LocationStats locationStat = new LocationStats(); // creates new instance of LocationStats
			locationStat.setState(record.get("Province/State")); // populates locationStats with Province/State
			locationStat.setCountry(record.get("Country/Region")); // populates locationStats with Country/Region
			int latestCases = Integer.parseInt(record.get(record.size() - 1)); // creates latestCases
			int prevDayCases = Integer.parseInt(record.get(record.size() - 2)); // creates prevDayCases
			locationStat.setLatestTotalCases(latestCases); // populates locationStats with latestCases
			locationStat.setDiffFromPrevDay(latestCases - prevDayCases); // populates locationStats with prevDayCases
			newStats.add(locationStat); // adds new stats to the locationStat list
		}
		this.allStats = newStats;

	}
}
