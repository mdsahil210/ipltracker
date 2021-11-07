package com.heroku.ipltracker.controller;

import java.time.LocalDate;
import java.util.List;

import com.heroku.ipltracker.Repository.MatchRepository;
import com.heroku.ipltracker.Repository.TeamRepository;
import com.heroku.ipltracker.model.Match;
import com.heroku.ipltracker.model.Team;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class TeamController {
	
	private TeamRepository teamRepository;
	private MatchRepository matchRepository;
	
	//autowiring the teamRepository using constructor injection
	public TeamController(TeamRepository teamRepository, MatchRepository matchRepository) {
		this.teamRepository = teamRepository;
		this.matchRepository = matchRepository;
	}
	
	@GetMapping("/team")
	public Iterable<Team> getAllTeams(){
		return this.teamRepository.findAll();
	}
	
	@GetMapping("/team/{teamName}")
	public Team getTeam(@PathVariable String teamName) {
		Team team = this.teamRepository.findByTeamName(teamName);
		//we are saying get me page 0 and 4 records
//		Pageable pageable = PageRequest.of(0, 4);
//		List<Match> matches = this.matchRepository.getByTeam1OrTeam2OrderByDateDesc(teamName, teamName, pageable);
		//in Controller we should not do data domain (check import) things there moving above lines in MatchRepository
		team.setMatches(matchRepository.findLatestMatchesByTeam(teamName, 4));
		return team;
	}
	
	//Using year as a query param  e.g: /team/sunrisers hyderabad/matches?year=2019
	@GetMapping("/team/{teamName}/matches")
	public List<Match> getMatchForTeam(@PathVariable String teamName, @RequestParam int year){
		LocalDate startDate = LocalDate.of(year, 1, 1);
		LocalDate endDate = LocalDate.of(year + 1, 1, 1);
//		return matchRepository.getByTeam1AndDateBetweenOrTeam2AndDateBetweenOrderByDateDesc(
//				teamName,
//				startDate,
//				endDate,
//				teamName,
//				startDate,
//				endDate
//				);
		return matchRepository.getMatchesByTeamBetweenDates(teamName,startDate,endDate);
	}
	
	
	@GetMapping("/head-to-head")
	public List<Match> getHeadToHeadMatches(@RequestParam List<String> team){
		String team1 = team.get(0);
		String team2 = team.get(1);
		return matchRepository.getMatchesBetweenTeams(team1, team2);
	}

}
