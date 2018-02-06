package com.banmatrip.schedule.service;

import com.banmatrip.schedule.domain.ScheduleJob;

import java.util.List;

public interface ScheduleService {

    public List<ScheduleJob> getScheduleJobs();

    public void update(ScheduleJob scheduleJob);

    public void deletebyId(String id);

    public void create(ScheduleJob scheduleJob);
}
