package com.ewp.crm.models.whatsapp.whatsappDTO;


import com.ewp.crm.models.whatsapp.WhatsappMessage;

import java.util.List;

public class WhatsappAcknowledgementDTO {
    private List<WhatsappAcknowledgement> ack;
    private List<WhatsappMessage> messages;
    private int instanceId;

    public WhatsappAcknowledgementDTO() {
    }

    public WhatsappAcknowledgementDTO(List<WhatsappAcknowledgement> ack, List<WhatsappMessage> messages, int instanceId) {
        this.ack = ack;
        this.messages = messages;
        this.instanceId = instanceId;
    }

    public List<WhatsappAcknowledgement> getAck() {
        return ack;
    }

    public void setAck(List<WhatsappAcknowledgement> ack) {
        this.ack = ack;
    }

    public List<WhatsappMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<WhatsappMessage> messages) {
        this.messages = messages;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WhatsappAcknowledgementDTO)) return false;

        WhatsappAcknowledgementDTO whatsappAcknowledgementDTO = (WhatsappAcknowledgementDTO) o;

        if (getInstanceId() != whatsappAcknowledgementDTO.getInstanceId()) return false;
        if (getAck() != null ? !getAck().equals(whatsappAcknowledgementDTO.getAck()) : whatsappAcknowledgementDTO.getAck() != null) return false;
        return getMessages() != null ? getMessages().equals(whatsappAcknowledgementDTO.getMessages()) : whatsappAcknowledgementDTO.getMessages() == null;
    }

    @Override
    public int hashCode() {
        int result = getAck() != null ? getAck().hashCode() : 0;
        result = 31 * result + (getMessages() != null ? getMessages().hashCode() : 0);
        result = 31 * result + getInstanceId();
        return result;
    }

    @Override
    public String toString() {
        return "WhatsappAcknowledgementDTO{" +
                "ack=" + ack +
                ", messages=" + messages +
                ", instanceId=" + instanceId +
                '}';
    }

}
