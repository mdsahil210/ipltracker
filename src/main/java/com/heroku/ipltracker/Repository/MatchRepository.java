package com.heroku.ipltracker.Repository;

import java.time.LocalDate;
import java.util.List;

import com.heroku.ipltracker.model.Match;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface MatchRepository extends CrudRepository<Match, Long> {
	//writing sql queries using method names
	//get all matches where Team1=teamName1 OR Team2=teamName2 in decreasing order of Date
	//Pageable <- we are jpa to give me data in page by page manner and only provide requested no. of pages as result
	List<Match> getByTeam1OrTeam2OrderByDateDesc(String teamName1,String team2, Pageable pageable);
	
	
	//getting matches of particular team in a year
	//taking care of operator precedence of AND > OR
	//getByTeam1Or(Team2AndDateBetween)OrderByDateDesc <--- This doesn't work
	//parameters of the query method need to be given in correct order
//	List<Match> getByTeam1AndDateBetweenOrTeam2AndDateBetweenOrderByDateDesc(String team1, LocalDate date1, LocalDate date2,
//																			String team2, LocalDate date3, LocalDate date4);
	
	@Query("select m from Match m where (m.team1 = :teamName or m.team2 = :teamName) and m.date between :startDate and :endDate order by date desc")
	List<Match> getMatchesByTeamBetweenDates(
			@Param("teamName") String teamName,
			@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate
			);
	
	@Query("select m from Match m where (m.team1 = :teamName1 and m.team2 = :teamName2) or (m.team1 = :teamName2 and m.team2 = :teamName1) order by date desc")
	List<Match> getMatchesBetweenTeams(
			@Param("teamName1") String teamName1,
			@Param("teamName2") String teamName2
			);
	
	
	
	// Same kind of Thing can be done by Creating a DAO Layer and then that DAO will call the MatchRepository query methods
	
	// But it is not required always as
	//now java allows default methods inside interfaces
	default List<Match> findLatestMatchesByTeam(String teamName,int count){
		return getByTeam1OrTeam2OrderByDateDesc(teamName,teamName, PageRequest.of(0, 4));
	}
}
