package com.ewp.crm.repository.impl;

import com.ewp.crm.controllers.rest.IPTelephonyRestController;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.User;
import com.ewp.crm.models.dto.ClientHistoryDto;
import com.ewp.crm.repository.interfaces.ClientHistoryRepositoryCustom;
import com.ewp.crm.repository.interfaces.StatusDAO;
import com.ewp.crm.repository.interfaces.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ClientHistoryRepositoryImpl implements ClientHistoryRepositoryCustom {

    private static Logger logger = LoggerFactory.getLogger(ClientRepositoryImpl.class);

    private final String statusChangeMessageTemplate;
    private final String statusChangeMessageTemplateFull;
    private final EntityManager entityManager;
    private final StatusDAO statusDao;
    private final UserDAO userDao;

    @Autowired
    public ClientHistoryRepositoryImpl(EntityManager entityManager, StatusDAO statusDao, UserDAO userDao,
                                       Environment env) {
        this.entityManager = entityManager;
        this.statusDao = statusDao;
        this.userDao = userDao;
        statusChangeMessageTemplate = env.getProperty("messaging.client.history.history-change-message");
        statusChangeMessageTemplateFull = env.getProperty("messaging.client.history.history-change-message-full");
    }

    @Override
    public List<ClientHistoryDto> getAllDtoByClientId(long id, int page, int pageSize) {
        List<ClientHistoryDto> result = new ArrayList<>();

        List<Tuple> tuples = entityManager.createNativeQuery(
                " SELECT * FROM ( " +
                        " ( " +
                        " SELECT 'history' AS `type`, `h`.`date` AS `date`, `h`.`record_link` AS `record_link`, `h`.`link` AS `link`, `h`.`title` AS `title`, null AS `new_status_id`, null AS `source_status_id`, null AS `user_id` " +
                        " FROM `history` `h` " +
                        " LEFT JOIN `history_client` `hc` ON `h`.`history_id` = `hc`.`history_id` " +
                        " WHERE `hc`.`client_id` = :clientId " +
                        " ) " +
                        " UNION ALL " +
                        " ( " +
                        " SELECT 'status' AS `type`, `csch`.`date` AS `date`, null AS `record_link`, null AS `link`, null AS `title`, `csch`.`new_status_id` AS `new_status_id`, `csch`.`source_status_id` AS `source_status_id`, `csch`.`user_id` AS `user_id` " +
                        " FROM `client_status_changing_history` `csch` " +
                        " WHERE `csch`.`client_id` = :clientId " +
                        " )) `result` " +
                        " ORDER BY `result`.`date` DESC " +
                        " LIMIT :startFrom , :count ",
                Tuple.class)
                .setParameter("clientId", id)
                .setParameter("startFrom", page * pageSize)
                .setParameter("count", pageSize)
                .getResultList();

        for (Tuple tuple :tuples) {
            String type = (String) tuple.get("type");
            ZonedDateTime date = ((Timestamp) tuple.get("date")).toLocalDateTime().atZone(ZoneId.systemDefault());
            switch (type) {
                case "history":
                    String title = (String) tuple.get("title");
                    String link = (String) tuple.get("link");
                    String recordLink = (String) tuple.get("record_link");
                    if (title.contains(ClientHistory.Type.CALL.getInfo()) && !title.contains(ClientHistory.Type.CALL_WITHOUT_RECORD.getInfo()) &&
                            (recordLink == null || IPTelephonyRestController.INIT_RECORD_LINK.equals(recordLink))) {
                        title += ClientHistory.Type.CALL_WITHOUT_RECORD.getInfo();
                    }
                    result.add(new ClientHistoryDto(title, link, recordLink, date));
                    break;
                case "status":
                    Long newStatusId = ((BigInteger) tuple.get("new_status_id")).longValue();
                    BigInteger sourceStatusId = (BigInteger) tuple.get("source_status_id");
                    Long userId = ((BigInteger) tuple.get("user_id")).longValue();
                    String sourceStatusName = null;
                    if (sourceStatusId != null) {
                        sourceStatusName = statusDao.getStatusNameById(sourceStatusId.longValue());
                    }
                    String newStatusName = statusDao.getStatusNameById(newStatusId);
                    User user = userDao.getOne(userId);
                    if (sourceStatusName == null) {
                        title = String.format(statusChangeMessageTemplate, user.getFullName(), newStatusName);
                    } else {
                        title = String.format(statusChangeMessageTemplateFull, user.getFullName(), newStatusName, sourceStatusName);
                    }
                    result.add(new ClientHistoryDto(title, date));
                    break;
            }
        }

        return result;
    }

}
