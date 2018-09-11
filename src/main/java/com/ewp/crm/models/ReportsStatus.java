package com.ewp.crm.models;

import javax.persistence.*;

@Entity
@Table(name = "reports_status")
public class ReportsStatus {

    @Id
    @GeneratedValue
    @Column
    private long id;

    @Column
    private long dropOutStatus;

    @Column
    private long endLearningStatus;

    @Column
    private long inLearningStatus;

    @Column
    private long pauseLearnStatus;

    @Column
    private long trialLearnStatus;

    public ReportsStatus(long dropOutStatus, long endLearningStatus, long inLearningStatus, long pauseLearnStatus, long trialLearnStatus) {
        this.dropOutStatus = dropOutStatus;
        this.endLearningStatus = endLearningStatus;
        this.inLearningStatus = inLearningStatus;
        this.pauseLearnStatus = pauseLearnStatus;
        this.trialLearnStatus = trialLearnStatus;
    }

    public ReportsStatus() {
    }

    public long getDropOutStatus() {
        return dropOutStatus;
    }

    public void setDropOutStatus(long dropOutStatus) {
        this.dropOutStatus = dropOutStatus;
    }

    public long getEndLearningStatus() {
        return endLearningStatus;
    }

    public void setEndLearningStatus(long endLearningStatus) {
        this.endLearningStatus = endLearningStatus;
    }

    public long getInLearningStatus() {
        return inLearningStatus;
    }

    public void setInLearningStatus(long inLearningStatus) {
        this.inLearningStatus = inLearningStatus;
    }

    public long getPauseLearnStatus() {
        return pauseLearnStatus;
    }

    public void setPauseLearnStatus(long pauseLearnStatus) {
        this.pauseLearnStatus = pauseLearnStatus;
    }

    public long getTrialLearnStatus() {
        return trialLearnStatus;
    }

    public void setTrialLearnStatus(long trialLearnStatus) {
        this.trialLearnStatus = trialLearnStatus;
    }
}
