package model;

import java.util.Date;

public class Metrics {

    private int id;
    private float cpuPercent;
    private float ramUsage;
    private String diskUsage;
    private String host;
    private Date dateTime;

    public Metrics(String host, Date dateTime) {
        this.host = host;
        this.dateTime = dateTime;
    }

    public int getId() {
        return id;
    }

    public float getCpuPercent() {
        return cpuPercent;
    }

    public void setCpuPercent(float cpuPercent) {
        this.cpuPercent = cpuPercent;
    }

    public float getRamUsage() {
        return ramUsage;
    }

    public void setRamUsage(float ramUsage) {
        this.ramUsage = ramUsage;
    }

    public String getDiskUsage() {
        return diskUsage;
    }

    public void setDiskUsage(String diskUsage) {
        this.diskUsage = diskUsage;
    }

    public String getHost() {
        return host;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "Metrics{" +
                " cpuPercent=" + cpuPercent +
                ", ramUsage=" + ramUsage +
                ", diskUsage='" + diskUsage + '\'' +
                ", host='" + host + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }
}
