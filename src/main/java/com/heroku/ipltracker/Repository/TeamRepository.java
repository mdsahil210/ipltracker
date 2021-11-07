package com.heroku.ipltracker.Repository;

import com.heroku.ipltracker.model.Team;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends CrudRepository<Team, Long> {
	
	
	//by looking at method name it will perform query
	//thats the jpa magic
	//finding the team from by teamName Team table
	Team findByTeamName(String teamName);

	Iterable<Team> findAll();
	
}
