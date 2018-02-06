package com.banmatrip.schedule.repository;

import com.banmatrip.schedule.domain.ScheduleJob;

import java.util.List;

public interface ScheduleJobMapper {
    public List<ScheduleJob> getScheduleJobs();
    public void update(ScheduleJob scheduleJob);
    public void deletebyId(String id);
    public ScheduleJob findbyId(String id);
    int insertSelective(ScheduleJob scheduleJob);
}
