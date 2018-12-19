package com.ewp.crm.models.Whatsapp.WhatsappDTO;

public class WhatsappCheckDeliveryMsg {
    private String sent;
    private String message;
    private Long queueNumber;

    public WhatsappCheckDeliveryMsg() {

    }

    public WhatsappCheckDeliveryMsg(String sent, String message, Long queueNumber) {
        this.sent = sent;
        this.message = message;
        this.queueNumber = queueNumber;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getQueueNumber() {
        return queueNumber;
    }

    public void setQueueNumber(Long queueNumber) {
        this.queueNumber = queueNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WhatsappCheckDeliveryMsg)) return false;

        WhatsappCheckDeliveryMsg that = (WhatsappCheckDeliveryMsg) o;

        if (getSent() != null ? !getSent().equals(that.getSent()) : that.getSent() != null) return false;
        if (getMessage() != null ? !getMessage().equals(that.getMessage()) : that.getMessage() != null) return false;
        return getQueueNumber() != null ? getQueueNumber().equals(that.getQueueNumber()) : that.getQueueNumber() == null;
    }

    @Override
    public int hashCode() {
        int result = getSent() != null ? getSent().hashCode() : 0;
        result = 31 * result + (getMessage() != null ? getMessage().hashCode() : 0);
        result = 31 * result + (getQueueNumber() != null ? getQueueNumber().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WhatsappCheckDeliveryMsg{" +
                "sent='" + sent + '\'' +
                ", WhatsappMessage='" + message + '\'' +
                ", queueNumber='" + queueNumber + '\'' +
                '}';
    }
}

