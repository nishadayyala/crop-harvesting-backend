package com.example.demo.dto;

public class DashboardStatsResponse {
    private long totalCrops;
    private long totalHarvestingRecords;
    private long pendingTasks;
    private long completedTasks;

    public DashboardStatsResponse(long totalCrops, long totalHarvestingRecords, long pendingTasks, long completedTasks) {
        this.totalCrops = totalCrops;
        this.totalHarvestingRecords = totalHarvestingRecords;
        this.pendingTasks = pendingTasks;
        this.completedTasks = completedTasks;
    }

    public long getTotalCrops() {
        return totalCrops;
    }

    public long getTotalHarvestingRecords() {
        return totalHarvestingRecords;
    }

    public long getPendingTasks() {
        return pendingTasks;
    }

    public long getCompletedTasks() {
        return completedTasks;
    }
}
