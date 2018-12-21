package com.ewp.crm.models.Whatsapp.WhatsappDTO;


import com.ewp.crm.models.Whatsapp.WhatsappMessage;

import java.util.List;

public class WhatsappAcknowledgementDTO {
    private List<WhatsappAcknowledgement> ack;
    private List<WhatsappMessage> whatsappMessages;
    private int instanceId;

    public WhatsappAcknowledgementDTO() {
    }

    public WhatsappAcknowledgementDTO(List<WhatsappAcknowledgement> ack, List<WhatsappMessage> whatsappMessages, int instanceId) {
        this.ack = ack;
        this.whatsappMessages = whatsappMessages;
        this.instanceId = instanceId;
    }

    public List<WhatsappAcknowledgement> getAck() {
        return ack;
    }

    public void setAck(List<WhatsappAcknowledgement> ack) {
        this.ack = ack;
    }

    public List<WhatsappMessage> getWhatsappMessages() {
        return whatsappMessages;
    }

    public void setWhatsappMessages(List<WhatsappMessage> whatsappMessages) {
        this.whatsappMessages = whatsappMessages;
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
        return getWhatsappMessages() != null ? getWhatsappMessages().equals(whatsappAcknowledgementDTO.getWhatsappMessages()) : whatsappAcknowledgementDTO.getWhatsappMessages() == null;
    }

    @Override
    public int hashCode() {
        int result = getAck() != null ? getAck().hashCode() : 0;
        result = 31 * result + (getWhatsappMessages() != null ? getWhatsappMessages().hashCode() : 0);
        result = 31 * result + getInstanceId();
        return result;
    }

    @Override
    public String toString() {
        return "WhatsappAcknowledgementDTO{" +
                "ack=" + ack +
                ", whatsappMessages=" + whatsappMessages +
                ", instanceId=" + instanceId +
                '}';
    }

}
