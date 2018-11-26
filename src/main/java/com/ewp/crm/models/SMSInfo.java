package com.ewp.crm.models;

import javax.persistence.*;
import java.io.Serializable;

/**
 * СМС (API: https://api.prostor-sms.ru/messages/v2)
 */
@Entity
@Table(name = "sms_info")
public class SMSInfo implements Serializable {

	private static final Long serialVersionUID = -1897381471234461980L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

    /**
     * Идентефикатор сообщения
     */
	@Column(name = "sms_id")
	private Long smsId;

    /**
     * Статус доставки
     */
	@Column(name = "delivery_status")
	private String deliveryStatus;

    /**
     * Текст сообщения
     */
	@Basic
	@Lob
	@Column(name = "message")
	private String message;

	/**
     * ?????????
     */
	@Basic
	@Column(name = "is_checked")
	private boolean isChecked = false;

    /**
     * Сообщения, отправленные клиенту (студенту)
     */
	@ManyToOne
	@JoinTable(name = "client_sms_info",
			joinColumns = {@JoinColumn(name = "sms_info_id", foreignKey = @ForeignKey(name = "FK_SMS_INFO"))},
			inverseJoinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))})
	private Client client;

    /**
     * Сообщения, отправленные работникУ или отправленные работникОМ
     */
	@ManyToOne
	@JoinTable(name = "worker_send_sms",
			joinColumns = {@JoinColumn(name = "sms_info_id", foreignKey = @ForeignKey(name = "FK_SMS_INFO"))},
			inverseJoinColumns = {@JoinColumn(name = "worker_id", foreignKey = @ForeignKey(name = "FK_USER"))})
	private User user;

	public SMSInfo() {
	}

	public SMSInfo(Long smsId, String message, User whoSend) {
		this.smsId = smsId;
		this.message = message;
		this.user = whoSend;
		this.deliveryStatus = "в очереди";
	}

	public Boolean getChecked() {
		return isChecked;
	}

	public void setChecked(boolean delivered) {
		isChecked = delivered;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getSmsId() {
		return smsId;
	}

	public void setSmsId(Long smsId) {
		this.smsId = smsId;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getDeliveryStatus() {
		return deliveryStatus;
	}

	public void setDeliveryStatus(String deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}
}
