package com.banmatrip.schedule.service.impl;

import com.banmatrip.schedule.domain.ScheduleJob;
import com.banmatrip.schedule.repository.ScheduleJobMapper;
import com.banmatrip.schedule.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

	@Autowired
	private ScheduleJobMapper scheduleJobMapper;


	@Override
	public List<ScheduleJob> getScheduleJobs() {
		return scheduleJobMapper.getScheduleJobs();
	}

	@Override
	public void update(ScheduleJob scheduleJob) {
		scheduleJobMapper.update( scheduleJob);
		
	}

	@Override
	public void deletebyId(String id) {
		scheduleJobMapper.deletebyId(id);
		
	}

	@Override
	public void create(ScheduleJob scheduleJob) {
		scheduleJobMapper.insertSelective(scheduleJob);
	}
}
