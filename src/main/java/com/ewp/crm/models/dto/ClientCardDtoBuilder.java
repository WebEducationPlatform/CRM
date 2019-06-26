package com.ewp.crm.models.dto;

import com.ewp.crm.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClientCardDtoBuilder {

    private static Logger logger = LoggerFactory.getLogger(ClientCardDtoBuilder.class);

    final static String[] CLIENT_TARGET_FIELDS = {  "id",
                                                    "name",
                                                    "lastName",
                                                    "clientPhones",
                                                    "clientEmails",
                                                    "skype",
                                                    "birthDate",
                                                    "age",
                                                    "sex",
                                                    "city",
                                                    "country",
                                                    "comments",
                                                    "history",
                                                    "university",
                                                    "requestFrom",
                                                    "canCall",
                                                    "status",
                                                    "ownerUser",
                                                    "ownerMentor",
                                                    "socialProfiles",
                                                    "clientDescriptionComment",
                                                    "liveSkypeCall",
                                                    "contractLinkData",
                                                    "otherInformationLinkData"};

    final static String[] STATUS_TARGET_FIELDS = {  "id",
                                                    "name"};

    final static String[] USER_TARGET_FIELDS = {"id",
                                                "firstName",
                                                "lastName"};

    final static String[] COMMENT_TARGET_FIELDS = { "id",
                                                    "user",
                                                    "dateFormat",
                                                    "content",
                                                    "commentAnswers"};

    final static String[] COMMENT_ANSWER_TARGET_FIELDS = {  "id",
                                                            "user",
                                                            "dateFormat",
                                                            "content"};

    final static String[] HISTORY_TARGET_FIELDS = { "id",
                                                    "title",
                                                    "link",
                                                    "recordLink",
                                                    "date",
                                                    "type",
                                                    "message"};

    public static String buildClientCardDto(Client client, List<Status> statuses) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("client", convertClientToMap(client));
        map.put("statuses", mapStatusesToMapsList(statuses));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String result = "";
        try {
            result = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            logger.warn("Can't parse or generate JSON for card DTO with client id: " + client.getId());
            e.printStackTrace();
        }
        return result;
    }

    public static Map<String, Object> convertClientToMap(Client client) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (String field : CLIENT_TARGET_FIELDS) {
            Object value = null;
            switch (field) {
                case ("status") : {
                    value = convertStatusToMap(client.getStatus());
                    break;
                }
                case ("ownerUser") : {
                    value = convertUserToMap(client.getOwnerUser());
                    break;
                }
                case ("ownerMentor") : {
                    value = convertUserToMap(client.getOwnerMentor());
                    break;
                }
                case ("comments") : {
                    value = mapCommentsToMapsList(client.getComments());
                    break;
                }
                case ("history") : {
                    value = mapHistoryToMapsList(client.getHistory());
                    break;
                }
                default : {
                    value = invokeFieldGetter(client, field);
                }
            }
            map.put(field, value);
        }
        return map;
    }

    public static List<Map<String, Object>> mapStatusesToMapsList(List<Status> statuses) {
        return statuses.stream().map(x -> convertStatusToMap(x)).collect(Collectors.toList());
    }

    public static List<Map<String, Object>> mapCommentsToMapsList(List<Comment> comments) {
        return comments.stream().map(x -> convertCommentToMap(x)).collect(Collectors.toList());
    }

    public static List<Map<String, Object>> mapCommentAnswersToMapsList(List<CommentAnswer> commentAnswers) {
        return commentAnswers.stream().map(x -> convertCommentAnswerToMap(x)).collect(Collectors.toList());
    }

    public static List<Map<String, Object>> mapHistoryToMapsList(List<ClientHistory> history) {
        return history.stream().map(x -> convertHistoryToMap(x)).collect(Collectors.toList());
    }

    public static Map<String, Object> convertStatusToMap(Status status) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (String field : STATUS_TARGET_FIELDS) {
            map.put(field, invokeFieldGetter(status, field));
        }
        return map;
    }

    public static Map<String, Object> convertUserToMap(User user) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (String field : USER_TARGET_FIELDS) {
            map.put(field, invokeFieldGetter(user, field));
        }
        return map;
    }

    public static Map<String, Object> convertCommentToMap(Comment comment) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (String field : COMMENT_TARGET_FIELDS) {
            Object value = null;
            if (field.equals("user") && comment.getUser() != null) {
                value = convertUserToMap(comment.getUser());
            } else {
                if (field.equals("commentAnswers") && comment.getCommentAnswers() != null) {
                    value = mapCommentAnswersToMapsList(comment.getCommentAnswers());
                } else {
                    value = invokeFieldGetter(comment, field);
                }
            }
            map.put(field, value);
        }
        return map;
    }

    public static Map<String, Object> convertCommentAnswerToMap(CommentAnswer commentAnswer) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (String field : COMMENT_ANSWER_TARGET_FIELDS) {
            Object value = null;
            if (field.equals("user") && commentAnswer.getUser() != null) {
                value = convertUserToMap(commentAnswer.getUser());
            } else {
                value = invokeFieldGetter(commentAnswer, field);
            }
            map.put(field, value);
        }
        return map;
    }

    public static Map<String, Object> convertHistoryToMap(ClientHistory clientHistory) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (String field : HISTORY_TARGET_FIELDS) {
            map.put(field, invokeFieldGetter(clientHistory, field));
        }
        return map;
    }

    private static Object invokeFieldGetter(Object object, String field) {
        Object value = null;
        if (object != null) {
            try {
                value = BeanUtils.getPropertyDescriptor(object.getClass(), field).getReadMethod().invoke(object);
            } catch (IllegalAccessException e) {
                logger.warn("Tries to reflectively invoke a getter, but the currently executing method " +
                        "does not have access to the getter for object: " + object.getClass().toString() + " field: " + field);
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                logger.warn("InvocationTargetException is a checked exception that wraps an exception " +
                        "thrown by an invoked getter for object: " + object.getClass().toString() + " field: " + field);
                e.printStackTrace();
            }
        }
        return value;
    }
}