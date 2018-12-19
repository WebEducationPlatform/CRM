package com.ewp.crm.models.Whatsapp.WhatsappDTO;

public class WhatsappAcknowledgement {
    private String id;
    private long queueNumber;
    private String chatId;
    private Status status;

    public WhatsappAcknowledgement() {
    }

    public WhatsappAcknowledgement(String id, long queueNumber, String chatId, Status status) {
        this.id = id;
        this.queueNumber = queueNumber;
        this.chatId = chatId;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getQueueNumber() {
        return queueNumber;
    }

    public void setQueueNumber(long queueNumber) {
        this.queueNumber = queueNumber;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WhatsappAcknowledgement)) return false;

        WhatsappAcknowledgement whatsappAcknowledgement = (WhatsappAcknowledgement) o;

        if (queueNumber != whatsappAcknowledgement.queueNumber) return false;
        if (id != null ? !id.equals(whatsappAcknowledgement.id) : whatsappAcknowledgement.id != null) return false;
        if (chatId != null ? !chatId.equals(whatsappAcknowledgement.chatId) : whatsappAcknowledgement.chatId != null) return false;
        return status == whatsappAcknowledgement.status;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (int) (queueNumber ^ (queueNumber >>> 32));
        result = 31 * result + (chatId != null ? chatId.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WhatsappAcknowledgement{" +
                "id='" + id + '\'' +
                ", queueNumber=" + queueNumber +
                ", chatId='" + chatId + '\'' +
                ", status=" + status +
                '}';
    }

    public enum Status {
        sent(1),
        delivered(2),
        viewed(3);
        private final int priority;

        Status(int i) {
            this.priority = i;
        }
        public int getPriority(){
            return priority;
        }
    }

}
