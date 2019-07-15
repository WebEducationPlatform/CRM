package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.*;

/**
 * Статус студента, новый клиент, учится, бросил, закончил и тп.
 */
@Entity
@Table(name = "status")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Status implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "status_id")
	private Long id;

	/**
	 * Название статуса клиента (inLearningStatus, trialLearnStatus и тд)
	 */
	@Column(name = "status_name", nullable = false, unique = true)
	@Filter(name="STATUSNAME", condition="name = : SName")
	private String name;

	/**
	 * Видимость колонки карточек клиентов с данным статусом на главной странице CRM
	 */
	@Basic
	@Column(name = "is_invisible")
	private Boolean isInvisible = false;

	/**
	 * Позиция колонки карточек клиентов с данным статусом на главной странице CRM
	 */
	@Basic
	@Column(name = "position")
	private Long position;

	/**
	 * Клиенты (студенты) с данным статусом
	 * OneToMany uses FetchType.LAZY by default.
	 * We use FetchMode.SUBSELECT for loading all elements of all collections.
	 */
	@JsonIgnore
	@OneToMany
	@Fetch(value = FetchMode.SUBSELECT)
	@JoinTable(name = "status_clients",
			joinColumns = {@JoinColumn(name = "status_id", foreignKey = @ForeignKey(name = "FK_STATUS"))},
			inverseJoinColumns = {@JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_USER"))})
	private List<Client> clients;

	/**
	 * ManyToMany uses FetchType.LAZY by default.
	 * We use FetchMode.SUBSELECT because we have limited quantity of roles,
	 * and almost all of them are in the session.
	 */
	@ManyToMany
	@Fetch(value = FetchMode.SUBSELECT)
	@JoinTable(name = "status_roles",
			joinColumns = {@JoinColumn(name = "status_id", foreignKey = @ForeignKey(name = "FK_STATUS"))},
			inverseJoinColumns = {@JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "FK_ROLE"))})
	private List<Role> role;

	public List<Role> getRole() {
		return role;
	}

	public void setRole(List<Role> role) {
		this.role = role;
	}

	/**
	 * Становится ли клиент студентом при присвении ему данного статуса
	 */
	@Column (name = "create_student")
	private boolean createStudent;

	/**
	 * Пробный период, дней, для данного статуса
	 */
	@Column(name = "trial_offset")
	private Integer trialOffset = 0;

	/**
	 * Дней до следующей оплаты
	 */
	@Column(name = "next_payment_offset")
	private Integer nextPaymentOffset = 0;

	/**
	 * OneToMany uses FetchType.LAZY by default.
	 * We use FetchMode.SUBSELECT because we have limited quantity of sortedStatuses,
	 * and almost all of them are in the session.
	 */
	@JsonIgnore
	@OneToMany(mappedBy = "sortedStatusesId.statusId")
	@Fetch(value = FetchMode.SUBSELECT)
	private Set<SortedStatuses> sortedStatuses = new HashSet<>();
	@JsonIgnore
	@OneToMany(mappedBy = "filterStatusesId.statusId")
	@Fetch(value = FetchMode.SUBSELECT)
	private Set<FilterStatuses> filterStatuses = new HashSet<>();


	public Status(String name, Boolean isInvisible, Long position, boolean createStudent, Integer trialOffset, Integer nextPaymentOffset) {
		this.name = name;
		this.isInvisible = isInvisible;
		this.position = position;
		this.createStudent = createStudent;
		this.trialOffset = trialOffset;
		this.nextPaymentOffset = nextPaymentOffset;
	}

	public Status(String name, Integer trialOffset, Integer nextPaymentOffset, List<Role> role) {
		this.name = name;
		this.trialOffset = trialOffset;
		this.nextPaymentOffset = nextPaymentOffset;
		this.role = role;
	}

	public Status(String name) {
		this.name = name;
		this.createStudent = false;
		this.trialOffset = 0;
		this.nextPaymentOffset = 0;
	}

	public Status() {
	}

	public Long getPosition() {
		return position;
	}

	public void setPosition(Long position) {
		this.position = position;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Client> getClients() {
		return clients;
	}

	public boolean isCreateStudent() {
		return createStudent;
	}

	public void setCreateStudent(boolean createStudent) {
		this.createStudent = createStudent;
	}

	public void setClients(List<Client> clients) {
		this.clients = clients;
	}

	public void addClient(Client client) {
		if (this.clients == null) {
			this.clients = new ArrayList<>();
		}
		this.clients.add(client);
	}

	public Integer getTrialOffset() {
		return trialOffset;
	}

	public void setTrialOffset(Integer trialOffset) {
		this.trialOffset = trialOffset;
	}

	public Integer getNextPaymentOffset() {
		return nextPaymentOffset;
	}

	public void setNextPaymentOffset(Integer nextPaymentOffset) {
		this.nextPaymentOffset = nextPaymentOffset;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Status)) return false;
		Status status = (Status) o;
		return Objects.equals(id, status.id) &&
				Objects.equals(name, status.name) &&
				Objects.equals(isInvisible, status.isInvisible);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, isInvisible);
	}

	@Override
	public String toString() {
		return name;
	}

	public Boolean getInvisible() {
		return isInvisible;
	}

	public void setInvisible(Boolean invisible) {
		isInvisible = invisible;
	}

	public Set<SortedStatuses> getSortedStatuses() {
		return sortedStatuses;
	}

	public void setSortedStatuses(Set<SortedStatuses> sortedStatuses) {
		this.sortedStatuses = sortedStatuses;
	}

	public Set<FilterStatuses> getFilterStatuses() {
		return filterStatuses;
	}

	public void setFilterStatuses(Set<FilterStatuses> filterStatuses) {
		this.filterStatuses = filterStatuses;
	}
}
