package com.heroku.ipltracker.data;

import java.time.LocalDate;

import com.heroku.ipltracker.model.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

public class MatchDataProcessor implements ItemProcessor<MatchInput, Match> {

  private static final Logger log = LoggerFactory.getLogger(MatchDataProcessor.class);

  @Override
  public Match process(final MatchInput matchInput) throws Exception {
	  // process method will take the MatchInput data(which is of identical format as that of csv
	  // and will process it into Match data(which is the actual data will we store in DB)
	  Match match = new Match();
	  match.setId(Long.parseLong(matchInput.getId()));
	  match.setCity(matchInput.getCity());
	  match.setDate(LocalDate.parse(matchInput.getDate()));
	  match.setPlayerOfMatch(matchInput.getPlayer_of_match());
	  match.setVenue(matchInput.getVenue());
	  
	  //team1 in MatchInput is home team and we are changing meaning of team1 in Match to team batting first
	  String firstInningsTeam, secondInningsTeam;
	  if("bat".equals(matchInput.getToss_decision())) {
		  firstInningsTeam = matchInput.getToss_winner();
		  secondInningsTeam = matchInput.getToss_winner().equals(matchInput.getTeam1())
				  ? matchInput.getTeam2() : matchInput.getTeam1();
	  } else {
		  secondInningsTeam = matchInput.getToss_winner();
		  firstInningsTeam = matchInput.getToss_winner().equals(matchInput.getTeam1())
				  ? matchInput.getTeam2() : matchInput.getTeam1();
	  }
	  
	  match.setTeam1(firstInningsTeam);
	  match.setTeam2(secondInningsTeam);
	  
	  match.setTossWinner(matchInput.getToss_winner());
	  match.setTossDecision(matchInput.getToss_decision());
	  match.setMatchWinner(matchInput.getWinner());
	  match.setResult(matchInput.getResult());
	  match.setResultMargin(matchInput.getResult_margin());
	  match.setUmpire1(matchInput.getUmpire1());
	  match.setUmpire2(matchInput.getUmpire2());
	  
	  return match;
	  
  }

}