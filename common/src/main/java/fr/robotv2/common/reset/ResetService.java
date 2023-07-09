package fr.robotv2.common.reset;

import it.sauronsoftware.cron4j.Predictor;
import it.sauronsoftware.cron4j.Scheduler;

import java.util.TimeZone;

public class ResetService {

    private final String id;
    private final String cronSyntax;
    private final TimeZone timeZone;

    private final Scheduler scheduler;

    private long nextExecution;

    public ResetService(String id, String cronSyntax, TimeZone timeZone) {
        this.id = id;
        this.cronSyntax = cronSyntax;
        this.timeZone = timeZone;
        this.scheduler = new Scheduler();
        this.calculateNextExecution();
    }

    public String getId() {
        return this.id;
    }

    public String getCronSyntax() {
        return this.cronSyntax;
    }

    public Scheduler getScheduler() {
        return this.scheduler;
    }

    public long getNextExecution() {
        return this.nextExecution;
    }

    public void calculateNextExecution() {
        this.nextExecution = new Predictor(this.cronSyntax).nextMatchingTime();
    }

    public void prepareScheduler(ResetPublisher publisher) {

        if(scheduler.isStarted()) {
            throw new IllegalStateException("scheduler is running");
        }

        scheduler.setTimeZone(timeZone);
        scheduler.schedule(cronSyntax, () -> {
            publisher.publishReset(id);
            calculateNextExecution();
        });
    }

    public void start() {
        if(!scheduler.isStarted()) {
            scheduler.start();
        }
    }
}
