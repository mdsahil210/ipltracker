package com.heroku.ipltracker.data;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import com.heroku.ipltracker.model.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

  private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

  private final EntityManager em;

  @Autowired
  public JobCompletionNotificationListener(EntityManager em) {
    this.em = em;
  }

  @Override
  @Transactional
  public void afterJob(JobExecution jobExecution) {
    if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
      log.info("!!! JOB FINISHED! Time to verify the results");

      Map<String, Team> teamData = new HashMap<>();
      
      //jpql query for getting the distinct team1 and their count as team1
      //Result List will the list of object[] arrays where each object has two values e[0] a team name and e[1] their total occurence(count)
      //We are streaming the result list and mapping it to team instance
      // and the for each team we are inserting data into a teamData hashmap with team name as key and Team Object as value
      
      em.createQuery("select m.team1, count(*) from Match m group by m.team1",Object[].class)
      .getResultList()
      .stream()
      .map(e -> new Team((String)e[0], (long)e[1]))
      .forEach(team -> teamData.put(team.getTeamName(),team));
      
      //now as we already populated the teamData hashmap 
      //we'll update the matches count by querying on team2
      em.createQuery("select m.team2, count(*) from Match m group by m.team2",Object[].class)
      .getResultList()
      .stream()
      .forEach(e -> {
    	  Team team = teamData.get((String) e[0]);
    	  team.setTotalMatches(team.getTotalMatches() + (long) e[1]);
      });
      
      //possible bug.. if a team never played in first innings that is it will not be present in team1 column
      //right now we are ignoring those cases..as rarely there is a chance that in 8 seasons a team never played in first innings
      
      
      //query to find about the total wins for a team and populate that data in teamData HashMap
      em.createQuery("select m.matchWinner, count(*) from Match m group by m.matchWinner",Object[].class)
      .getResultList()
      .stream()
      .forEach(e -> {
    	  Team team = teamData.get((String) e[0]);
    	  if(team!=null) team.setTotalWins((long) e[1]);
      });
      
      teamData.values().forEach(team -> em.persist(team));
      teamData.values().forEach(team -> System.out.println(team));
      
    }
  }
}
