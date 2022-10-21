package com.coronavirus.app.controllers;

//adds stats to the list

import java.util.List;
import java.util.function.ToIntFunction;

import org.springframework.beans.factory.annotation.Autowired;

//class renders starts in an html in a UI format

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.coronavirus.app.models.LocationStats;
import com.coronavirus.app.services.CoronaVirusDataService;



@Controller                                                         // ensures that all methods return data in UI format
public class HomeController {
	
	   @Autowired                                                  //autowires relationships between our collaborating beans
	    CoronaVirusDataService coronaVirusDataService;
	   
	    @GetMapping("/")                                          //gets the URL
	    public String home(Model model) {                         //
	        List<LocationStats> allStats = coronaVirusDataService.getAllStats();                              //creates  a list                          
	        int totalReportedCases = allStats.stream().mapToInt(new ToIntFunction<LocationStats>() {
				@Override
				public int applyAsInt(LocationStats stat) {
					return stat.getLatestTotalCases();
				}
			}).sum();    //creates totalReportedCases
	        int totalNewCases = allStats.stream().mapToInt(new ToIntFunction<LocationStats>() {
				@Override
				public int applyAsInt(LocationStats stat) {
					return stat.getDiffFromPrevDay();
				}
			}).sum();          //creates totalNewCases
	        model.addAttribute("locationStats", allStats);                                                    //adds stats to the list
	        model.addAttribute("totalReportedCases", totalReportedCases);                                     //adds stats to the list
	        model.addAttribute("totalNewCases", totalNewCases);                                              //adds stats to the list

	        return "home";
	    }
	}