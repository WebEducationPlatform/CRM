package com.ewp.crm.models.whatsapp.whatsappDTO;

public class WhatsappMessageSendable {
    private String phone;
    private String body;



    public WhatsappMessageSendable() {
    }

    public WhatsappMessageSendable(String phone, String body) {
        this.phone = phone;
        this.body = body;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WhatsappMessageSendable)) return false;

        WhatsappMessageSendable that = (WhatsappMessageSendable) o;

        if (getPhone() != null ? !getPhone().equals(that.getPhone()) : that.getPhone() != null) return false;
        return getBody() != null ? getBody().equals(that.getBody()) : that.getBody() == null;
    }

    @Override
    public int hashCode() {
        int result = getPhone() != null ? getPhone().hashCode() : 0;
        result = 31 * result + (getBody() != null ? getBody().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WhatsappMessageSendable{" +
                "phone=" + phone +
                ", body='" + body + '\'' +
                '}';
    }
}
