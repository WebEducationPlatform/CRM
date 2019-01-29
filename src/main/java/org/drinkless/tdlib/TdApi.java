package org.drinkless.tdlib;

public class TdApi {
    public abstract static class Object {
        public native String toString();

        public abstract int getConstructor();
    }

    public abstract static class Function extends Object {
        public native String toString();
    }

    public static class AccountTtl extends Object {
        public int days;

        public AccountTtl() {
        }

        public AccountTtl(int days) {
            this.days = days;
        }

        public static final int CONSTRUCTOR = 1324495492;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Address extends Object {
        public String countryCode;
        public String state;
        public String city;
        public String streetLine1;
        public String streetLine2;
        public String postalCode;

        public Address() {
        }

        public Address(String countryCode, String state, String city, String streetLine1, String streetLine2, String postalCode) {
            this.countryCode = countryCode;
            this.state = state;
            this.city = city;
            this.streetLine1 = streetLine1;
            this.streetLine2 = streetLine2;
            this.postalCode = postalCode;
        }

        public static final int CONSTRUCTOR = -2043654342;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Animation extends Object {
        public int duration;
        public int width;
        public int height;
        public String fileName;
        public String mimeType;
        public PhotoSize thumbnail;
        public File animation;

        public Animation() {
        }

        public Animation(int duration, int width, int height, String fileName, String mimeType, PhotoSize thumbnail, File animation) {
            this.duration = duration;
            this.width = width;
            this.height = height;
            this.fileName = fileName;
            this.mimeType = mimeType;
            this.thumbnail = thumbnail;
            this.animation = animation;
        }

        public static final int CONSTRUCTOR = -1723168340;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Animations extends Object {
        public Animation[] animations;

        public Animations() {
        }

        public Animations(Animation[] animations) {
            this.animations = animations;
        }

        public static final int CONSTRUCTOR = 344216945;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Audio extends Object {
        public int duration;
        public String title;
        public String performer;
        public String fileName;
        public String mimeType;
        public PhotoSize albumCoverThumbnail;
        public File audio;

        public Audio() {
        }

        public Audio(int duration, String title, String performer, String fileName, String mimeType, PhotoSize albumCoverThumbnail, File audio) {
            this.duration = duration;
            this.title = title;
            this.performer = performer;
            this.fileName = fileName;
            this.mimeType = mimeType;
            this.albumCoverThumbnail = albumCoverThumbnail;
            this.audio = audio;
        }

        public static final int CONSTRUCTOR = 383148432;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AuthenticationCodeInfo extends Object {
        public String phoneNumber;
        public AuthenticationCodeType type;
        public AuthenticationCodeType nextType;
        public int timeout;

        public AuthenticationCodeInfo() {
        }

        public AuthenticationCodeInfo(String phoneNumber, AuthenticationCodeType type, AuthenticationCodeType nextType, int timeout) {
            this.phoneNumber = phoneNumber;
            this.type = type;
            this.nextType = nextType;
            this.timeout = timeout;
        }

        public static final int CONSTRUCTOR = -860345416;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class AuthenticationCodeType extends Object {
    }

    public static class AuthenticationCodeTypeTelegramMessage extends AuthenticationCodeType {
        public int length;

        public AuthenticationCodeTypeTelegramMessage() {
        }

        public AuthenticationCodeTypeTelegramMessage(int length) {
            this.length = length;
        }

        public static final int CONSTRUCTOR = 2079628074;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AuthenticationCodeTypeSms extends AuthenticationCodeType {
        public int length;

        public AuthenticationCodeTypeSms() {
        }

        public AuthenticationCodeTypeSms(int length) {
            this.length = length;
        }

        public static final int CONSTRUCTOR = 962650760;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AuthenticationCodeTypeCall extends AuthenticationCodeType {
        public int length;

        public AuthenticationCodeTypeCall() {
        }

        public AuthenticationCodeTypeCall(int length) {
            this.length = length;
        }

        public static final int CONSTRUCTOR = 1636265063;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AuthenticationCodeTypeFlashCall extends AuthenticationCodeType {
        public String pattern;

        public AuthenticationCodeTypeFlashCall() {
        }

        public AuthenticationCodeTypeFlashCall(String pattern) {
            this.pattern = pattern;
        }

        public static final int CONSTRUCTOR = 1395882402;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class AuthorizationState extends Object {
    }

    public static class AuthorizationStateWaitTdlibParameters extends AuthorizationState {

        public AuthorizationStateWaitTdlibParameters() {
        }

        public static final int CONSTRUCTOR = 904720988;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AuthorizationStateWaitEncryptionKey extends AuthorizationState {
        public boolean isEncrypted;

        public AuthorizationStateWaitEncryptionKey() {
        }

        public AuthorizationStateWaitEncryptionKey(boolean isEncrypted) {
            this.isEncrypted = isEncrypted;
        }

        public static final int CONSTRUCTOR = 612103496;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AuthorizationStateWaitPhoneNumber extends AuthorizationState {

        public AuthorizationStateWaitPhoneNumber() {
        }

        public static final int CONSTRUCTOR = 306402531;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AuthorizationStateWaitCode extends AuthorizationState {
        public boolean isRegistered;
        public TermsOfService termsOfService;
        public AuthenticationCodeInfo codeInfo;

        public AuthorizationStateWaitCode() {
        }

        public AuthorizationStateWaitCode(boolean isRegistered, TermsOfService termsOfService, AuthenticationCodeInfo codeInfo) {
            this.isRegistered = isRegistered;
            this.termsOfService = termsOfService;
            this.codeInfo = codeInfo;
        }

        public static final int CONSTRUCTOR = -122899120;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AuthorizationStateWaitPassword extends AuthorizationState {
        public String passwordHint;
        public boolean hasRecoveryEmailAddress;
        public String recoveryEmailAddressPattern;

        public AuthorizationStateWaitPassword() {
        }

        public AuthorizationStateWaitPassword(String passwordHint, boolean hasRecoveryEmailAddress, String recoveryEmailAddressPattern) {
            this.passwordHint = passwordHint;
            this.hasRecoveryEmailAddress = hasRecoveryEmailAddress;
            this.recoveryEmailAddressPattern = recoveryEmailAddressPattern;
        }

        public static final int CONSTRUCTOR = 187548796;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AuthorizationStateReady extends AuthorizationState {

        public AuthorizationStateReady() {
        }

        public static final int CONSTRUCTOR = -1834871737;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AuthorizationStateLoggingOut extends AuthorizationState {

        public AuthorizationStateLoggingOut() {
        }

        public static final int CONSTRUCTOR = 154449270;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AuthorizationStateClosing extends AuthorizationState {

        public AuthorizationStateClosing() {
        }

        public static final int CONSTRUCTOR = 445855311;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AuthorizationStateClosed extends AuthorizationState {

        public AuthorizationStateClosed() {
        }

        public static final int CONSTRUCTOR = 1526047584;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class BasicGroup extends Object {
        public int id;
        public int memberCount;
        public ChatMemberStatus status;
        public boolean everyoneIsAdministrator;
        public boolean isActive;
        public int upgradedToSupergroupId;

        public BasicGroup() {
        }

        public BasicGroup(int id, int memberCount, ChatMemberStatus status, boolean everyoneIsAdministrator, boolean isActive, int upgradedToSupergroupId) {
            this.id = id;
            this.memberCount = memberCount;
            this.status = status;
            this.everyoneIsAdministrator = everyoneIsAdministrator;
            this.isActive = isActive;
            this.upgradedToSupergroupId = upgradedToSupergroupId;
        }

        public static final int CONSTRUCTOR = 1572712718;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class BasicGroupFullInfo extends Object {
        public int creatorUserId;
        public ChatMember[] members;
        public String inviteLink;

        public BasicGroupFullInfo() {
        }

        public BasicGroupFullInfo(int creatorUserId, ChatMember[] members, String inviteLink) {
            this.creatorUserId = creatorUserId;
            this.members = members;
            this.inviteLink = inviteLink;
        }

        public static final int CONSTRUCTOR = 952266076;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class BotCommand extends Object {
        public String command;
        public String description;

        public BotCommand() {
        }

        public BotCommand(String command, String description) {
            this.command = command;
            this.description = description;
        }

        public static final int CONSTRUCTOR = -1032140601;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class BotInfo extends Object {
        public String description;
        public BotCommand[] commands;

        public BotInfo() {
        }

        public BotInfo(String description, BotCommand[] commands) {
            this.description = description;
            this.commands = commands;
        }

        public static final int CONSTRUCTOR = 1296528907;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Call extends Object {
        public int id;
        public int userId;
        public boolean isOutgoing;
        public CallState state;

        public Call() {
        }

        public Call(int id, int userId, boolean isOutgoing, CallState state) {
            this.id = id;
            this.userId = userId;
            this.isOutgoing = isOutgoing;
            this.state = state;
        }

        public static final int CONSTRUCTOR = -1837599107;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CallConnection extends Object {
        public long id;
        public String ip;
        public String ipv6;
        public int port;
        public byte[] peerTag;

        public CallConnection() {
        }

        public CallConnection(long id, String ip, String ipv6, int port, byte[] peerTag) {
            this.id = id;
            this.ip = ip;
            this.ipv6 = ipv6;
            this.port = port;
            this.peerTag = peerTag;
        }

        public static final int CONSTRUCTOR = 1318542714;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class CallDiscardReason extends Object {
    }

    public static class CallDiscardReasonEmpty extends CallDiscardReason {

        public CallDiscardReasonEmpty() {
        }

        public static final int CONSTRUCTOR = -1258917949;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CallDiscardReasonMissed extends CallDiscardReason {

        public CallDiscardReasonMissed() {
        }

        public static final int CONSTRUCTOR = 1680358012;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CallDiscardReasonDeclined extends CallDiscardReason {

        public CallDiscardReasonDeclined() {
        }

        public static final int CONSTRUCTOR = -1729926094;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CallDiscardReasonDisconnected extends CallDiscardReason {

        public CallDiscardReasonDisconnected() {
        }

        public static final int CONSTRUCTOR = -1342872670;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CallDiscardReasonHungUp extends CallDiscardReason {

        public CallDiscardReasonHungUp() {
        }

        public static final int CONSTRUCTOR = 438216166;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CallId extends Object {
        public int id;

        public CallId() {
        }

        public CallId(int id) {
            this.id = id;
        }

        public static final int CONSTRUCTOR = 65717769;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CallProtocol extends Object {
        public boolean udpP2p;
        public boolean udpReflector;
        public int minLayer;
        public int maxLayer;

        public CallProtocol() {
        }

        public CallProtocol(boolean udpP2p, boolean udpReflector, int minLayer, int maxLayer) {
            this.udpP2p = udpP2p;
            this.udpReflector = udpReflector;
            this.minLayer = minLayer;
            this.maxLayer = maxLayer;
        }

        public static final int CONSTRUCTOR = -1042830667;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class CallState extends Object {
    }

    public static class CallStatePending extends CallState {
        public boolean isCreated;
        public boolean isReceived;

        public CallStatePending() {
        }

        public CallStatePending(boolean isCreated, boolean isReceived) {
            this.isCreated = isCreated;
            this.isReceived = isReceived;
        }

        public static final int CONSTRUCTOR = 1073048620;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CallStateExchangingKeys extends CallState {

        public CallStateExchangingKeys() {
        }

        public static final int CONSTRUCTOR = -1848149403;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CallStateReady extends CallState {
        public CallProtocol protocol;
        public CallConnection[] connections;
        public String config;
        public byte[] encryptionKey;
        public String[] emojis;

        public CallStateReady() {
        }

        public CallStateReady(CallProtocol protocol, CallConnection[] connections, String config, byte[] encryptionKey, String[] emojis) {
            this.protocol = protocol;
            this.connections = connections;
            this.config = config;
            this.encryptionKey = encryptionKey;
            this.emojis = emojis;
        }

        public static final int CONSTRUCTOR = 1518705438;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CallStateHangingUp extends CallState {

        public CallStateHangingUp() {
        }

        public static final int CONSTRUCTOR = -2133790038;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CallStateDiscarded extends CallState {
        public CallDiscardReason reason;
        public boolean needRating;
        public boolean needDebugInformation;

        public CallStateDiscarded() {
        }

        public CallStateDiscarded(CallDiscardReason reason, boolean needRating, boolean needDebugInformation) {
            this.reason = reason;
            this.needRating = needRating;
            this.needDebugInformation = needDebugInformation;
        }

        public static final int CONSTRUCTOR = -190853167;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CallStateError extends CallState {
        public Error error;

        public CallStateError() {
        }

        public CallStateError(Error error) {
            this.error = error;
        }

        public static final int CONSTRUCTOR = -975215467;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CallbackQueryAnswer extends Object {
        public String text;
        public boolean showAlert;
        public String url;

        public CallbackQueryAnswer() {
        }

        public CallbackQueryAnswer(String text, boolean showAlert, String url) {
            this.text = text;
            this.showAlert = showAlert;
            this.url = url;
        }

        public static final int CONSTRUCTOR = 360867933;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class CallbackQueryPayload extends Object {
    }

    public static class CallbackQueryPayloadData extends CallbackQueryPayload {
        public byte[] data;

        public CallbackQueryPayloadData() {
        }

        public CallbackQueryPayloadData(byte[] data) {
            this.data = data;
        }

        public static final int CONSTRUCTOR = -1977729946;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CallbackQueryPayloadGame extends CallbackQueryPayload {
        public String gameShortName;

        public CallbackQueryPayloadGame() {
        }

        public CallbackQueryPayloadGame(String gameShortName) {
            this.gameShortName = gameShortName;
        }

        public static final int CONSTRUCTOR = 1303571512;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Chat extends Object {
        public long id;
        public ChatType type;
        public String title;
        public ChatPhoto photo;
        public Message lastMessage;
        public long order;
        public boolean isPinned;
        public boolean isMarkedAsUnread;
        public boolean isSponsored;
        public boolean canBeReported;
        public boolean defaultDisableNotification;
        public int unreadCount;
        public long lastReadInboxMessageId;
        public long lastReadOutboxMessageId;
        public int unreadMentionCount;
        public ChatNotificationSettings notificationSettings;
        public long replyMarkupMessageId;
        public DraftMessage draftMessage;
        public String clientData;

        public Chat() {
        }

        public Chat(long id, ChatType type, String title, ChatPhoto photo, Message lastMessage, long order, boolean isPinned, boolean isMarkedAsUnread, boolean isSponsored, boolean canBeReported, boolean defaultDisableNotification, int unreadCount, long lastReadInboxMessageId, long lastReadOutboxMessageId, int unreadMentionCount, ChatNotificationSettings notificationSettings, long replyMarkupMessageId, DraftMessage draftMessage, String clientData) {
            this.id = id;
            this.type = type;
            this.title = title;
            this.photo = photo;
            this.lastMessage = lastMessage;
            this.order = order;
            this.isPinned = isPinned;
            this.isMarkedAsUnread = isMarkedAsUnread;
            this.isSponsored = isSponsored;
            this.canBeReported = canBeReported;
            this.defaultDisableNotification = defaultDisableNotification;
            this.unreadCount = unreadCount;
            this.lastReadInboxMessageId = lastReadInboxMessageId;
            this.lastReadOutboxMessageId = lastReadOutboxMessageId;
            this.unreadMentionCount = unreadMentionCount;
            this.notificationSettings = notificationSettings;
            this.replyMarkupMessageId = replyMarkupMessageId;
            this.draftMessage = draftMessage;
            this.clientData = clientData;
        }

        public static final int CONSTRUCTOR = -4728182;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class ChatAction extends Object {
    }

    public static class ChatActionTyping extends ChatAction {

        public ChatActionTyping() {
        }

        public static final int CONSTRUCTOR = 380122167;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatActionRecordingVideo extends ChatAction {

        public ChatActionRecordingVideo() {
        }

        public static final int CONSTRUCTOR = 216553362;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatActionUploadingVideo extends ChatAction {
        public int progress;

        public ChatActionUploadingVideo() {
        }

        public ChatActionUploadingVideo(int progress) {
            this.progress = progress;
        }

        public static final int CONSTRUCTOR = 1234185270;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatActionRecordingVoiceNote extends ChatAction {

        public ChatActionRecordingVoiceNote() {
        }

        public static final int CONSTRUCTOR = -808850058;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatActionUploadingVoiceNote extends ChatAction {
        public int progress;

        public ChatActionUploadingVoiceNote() {
        }

        public ChatActionUploadingVoiceNote(int progress) {
            this.progress = progress;
        }

        public static final int CONSTRUCTOR = -613643666;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatActionUploadingPhoto extends ChatAction {
        public int progress;

        public ChatActionUploadingPhoto() {
        }

        public ChatActionUploadingPhoto(int progress) {
            this.progress = progress;
        }

        public static final int CONSTRUCTOR = 654240583;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatActionUploadingDocument extends ChatAction {
        public int progress;

        public ChatActionUploadingDocument() {
        }

        public ChatActionUploadingDocument(int progress) {
            this.progress = progress;
        }

        public static final int CONSTRUCTOR = 167884362;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatActionChoosingLocation extends ChatAction {

        public ChatActionChoosingLocation() {
        }

        public static final int CONSTRUCTOR = -2017893596;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatActionChoosingContact extends ChatAction {

        public ChatActionChoosingContact() {
        }

        public static final int CONSTRUCTOR = -1222507496;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatActionStartPlayingGame extends ChatAction {

        public ChatActionStartPlayingGame() {
        }

        public static final int CONSTRUCTOR = -865884164;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatActionRecordingVideoNote extends ChatAction {

        public ChatActionRecordingVideoNote() {
        }

        public static final int CONSTRUCTOR = 16523393;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatActionUploadingVideoNote extends ChatAction {
        public int progress;

        public ChatActionUploadingVideoNote() {
        }

        public ChatActionUploadingVideoNote(int progress) {
            this.progress = progress;
        }

        public static final int CONSTRUCTOR = 1172364918;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatActionCancel extends ChatAction {

        public ChatActionCancel() {
        }

        public static final int CONSTRUCTOR = 1160523958;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatEvent extends Object {
        public long id;
        public int date;
        public int userId;
        public ChatEventAction action;

        public ChatEvent() {
        }

        public ChatEvent(long id, int date, int userId, ChatEventAction action) {
            this.id = id;
            this.date = date;
            this.userId = userId;
            this.action = action;
        }

        public static final int CONSTRUCTOR = -609912404;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class ChatEventAction extends Object {
    }

    public static class ChatEventMessageEdited extends ChatEventAction {
        public Message oldMessage;
        public Message newMessage;

        public ChatEventMessageEdited() {
        }

        public ChatEventMessageEdited(Message oldMessage, Message newMessage) {
            this.oldMessage = oldMessage;
            this.newMessage = newMessage;
        }

        public static final int CONSTRUCTOR = -430967304;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatEventMessageDeleted extends ChatEventAction {
        public Message message;

        public ChatEventMessageDeleted() {
        }

        public ChatEventMessageDeleted(Message message) {
            this.message = message;
        }

        public static final int CONSTRUCTOR = -892974601;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatEventMessagePinned extends ChatEventAction {
        public Message message;

        public ChatEventMessagePinned() {
        }

        public ChatEventMessagePinned(Message message) {
            this.message = message;
        }

        public static final int CONSTRUCTOR = 438742298;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatEventMessageUnpinned extends ChatEventAction {

        public ChatEventMessageUnpinned() {
        }

        public static final int CONSTRUCTOR = 2002594849;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatEventMemberJoined extends ChatEventAction {

        public ChatEventMemberJoined() {
        }

        public static final int CONSTRUCTOR = -235468508;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatEventMemberLeft extends ChatEventAction {

        public ChatEventMemberLeft() {
        }

        public static final int CONSTRUCTOR = -948420593;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatEventMemberInvited extends ChatEventAction {
        public int userId;
        public ChatMemberStatus status;

        public ChatEventMemberInvited() {
        }

        public ChatEventMemberInvited(int userId, ChatMemberStatus status) {
            this.userId = userId;
            this.status = status;
        }

        public static final int CONSTRUCTOR = -2093688706;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatEventMemberPromoted extends ChatEventAction {
        public int userId;
        public ChatMemberStatus oldStatus;
        public ChatMemberStatus newStatus;

        public ChatEventMemberPromoted() {
        }

        public ChatEventMemberPromoted(int userId, ChatMemberStatus oldStatus, ChatMemberStatus newStatus) {
            this.userId = userId;
            this.oldStatus = oldStatus;
            this.newStatus = newStatus;
        }

        public static final int CONSTRUCTOR = 1887176186;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatEventMemberRestricted extends ChatEventAction {
        public int userId;
        public ChatMemberStatus oldStatus;
        public ChatMemberStatus newStatus;

        public ChatEventMemberRestricted() {
        }

        public ChatEventMemberRestricted(int userId, ChatMemberStatus oldStatus, ChatMemberStatus newStatus) {
            this.userId = userId;
            this.oldStatus = oldStatus;
            this.newStatus = newStatus;
        }

        public static final int CONSTRUCTOR = 584946294;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatEventTitleChanged extends ChatEventAction {
        public String oldTitle;
        public String newTitle;

        public ChatEventTitleChanged() {
        }

        public ChatEventTitleChanged(String oldTitle, String newTitle) {
            this.oldTitle = oldTitle;
            this.newTitle = newTitle;
        }

        public static final int CONSTRUCTOR = 1134103250;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatEventDescriptionChanged extends ChatEventAction {
        public String oldDescription;
        public String newDescription;

        public ChatEventDescriptionChanged() {
        }

        public ChatEventDescriptionChanged(String oldDescription, String newDescription) {
            this.oldDescription = oldDescription;
            this.newDescription = newDescription;
        }

        public static final int CONSTRUCTOR = 39112478;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatEventUsernameChanged extends ChatEventAction {
        public String oldUsername;
        public String newUsername;

        public ChatEventUsernameChanged() {
        }

        public ChatEventUsernameChanged(String oldUsername, String newUsername) {
            this.oldUsername = oldUsername;
            this.newUsername = newUsername;
        }

        public static final int CONSTRUCTOR = 1728558443;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatEventPhotoChanged extends ChatEventAction {
        public ChatPhoto oldPhoto;
        public ChatPhoto newPhoto;

        public ChatEventPhotoChanged() {
        }

        public ChatEventPhotoChanged(ChatPhoto oldPhoto, ChatPhoto newPhoto) {
            this.oldPhoto = oldPhoto;
            this.newPhoto = newPhoto;
        }

        public static final int CONSTRUCTOR = -811572541;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatEventInvitesToggled extends ChatEventAction {
        public boolean anyoneCanInvite;

        public ChatEventInvitesToggled() {
        }

        public ChatEventInvitesToggled(boolean anyoneCanInvite) {
            this.anyoneCanInvite = anyoneCanInvite;
        }

        public static final int CONSTRUCTOR = 568706937;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatEventSignMessagesToggled extends ChatEventAction {
        public boolean signMessages;

        public ChatEventSignMessagesToggled() {
        }

        public ChatEventSignMessagesToggled(boolean signMessages) {
            this.signMessages = signMessages;
        }

        public static final int CONSTRUCTOR = -1313265634;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatEventStickerSetChanged extends ChatEventAction {
        public long oldStickerSetId;
        public long newStickerSetId;

        public ChatEventStickerSetChanged() {
        }

        public ChatEventStickerSetChanged(long oldStickerSetId, long newStickerSetId) {
            this.oldStickerSetId = oldStickerSetId;
            this.newStickerSetId = newStickerSetId;
        }

        public static final int CONSTRUCTOR = -1243130481;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatEventIsAllHistoryAvailableToggled extends ChatEventAction {
        public boolean isAllHistoryAvailable;

        public ChatEventIsAllHistoryAvailableToggled() {
        }

        public ChatEventIsAllHistoryAvailableToggled(boolean isAllHistoryAvailable) {
            this.isAllHistoryAvailable = isAllHistoryAvailable;
        }

        public static final int CONSTRUCTOR = -1599063019;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatEventLogFilters extends Object {
        public boolean messageEdits;
        public boolean messageDeletions;
        public boolean messagePins;
        public boolean memberJoins;
        public boolean memberLeaves;
        public boolean memberInvites;
        public boolean memberPromotions;
        public boolean memberRestrictions;
        public boolean infoChanges;
        public boolean settingChanges;

        public ChatEventLogFilters() {
        }

        public ChatEventLogFilters(boolean messageEdits, boolean messageDeletions, boolean messagePins, boolean memberJoins, boolean memberLeaves, boolean memberInvites, boolean memberPromotions, boolean memberRestrictions, boolean infoChanges, boolean settingChanges) {
            this.messageEdits = messageEdits;
            this.messageDeletions = messageDeletions;
            this.messagePins = messagePins;
            this.memberJoins = memberJoins;
            this.memberLeaves = memberLeaves;
            this.memberInvites = memberInvites;
            this.memberPromotions = memberPromotions;
            this.memberRestrictions = memberRestrictions;
            this.infoChanges = infoChanges;
            this.settingChanges = settingChanges;
        }

        public static final int CONSTRUCTOR = 941939684;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatEvents extends Object {
        public ChatEvent[] events;

        public ChatEvents() {
        }

        public ChatEvents(ChatEvent[] events) {
            this.events = events;
        }

        public static final int CONSTRUCTOR = -585329664;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatInviteLink extends Object {
        public String inviteLink;

        public ChatInviteLink() {
        }

        public ChatInviteLink(String inviteLink) {
            this.inviteLink = inviteLink;
        }

        public static final int CONSTRUCTOR = -882072492;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatInviteLinkInfo extends Object {
        public long chatId;
        public ChatType type;
        public String title;
        public ChatPhoto photo;
        public int memberCount;
        public int[] memberUserIds;
        public boolean isPublic;

        public ChatInviteLinkInfo() {
        }

        public ChatInviteLinkInfo(long chatId, ChatType type, String title, ChatPhoto photo, int memberCount, int[] memberUserIds, boolean isPublic) {
            this.chatId = chatId;
            this.type = type;
            this.title = title;
            this.photo = photo;
            this.memberCount = memberCount;
            this.memberUserIds = memberUserIds;
            this.isPublic = isPublic;
        }

        public static final int CONSTRUCTOR = -323394424;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatMember extends Object {
        public int userId;
        public int inviterUserId;
        public int joinedChatDate;
        public ChatMemberStatus status;
        public BotInfo botInfo;

        public ChatMember() {
        }

        public ChatMember(int userId, int inviterUserId, int joinedChatDate, ChatMemberStatus status, BotInfo botInfo) {
            this.userId = userId;
            this.inviterUserId = inviterUserId;
            this.joinedChatDate = joinedChatDate;
            this.status = status;
            this.botInfo = botInfo;
        }

        public static final int CONSTRUCTOR = -806137076;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class ChatMemberStatus extends Object {
    }

    public static class ChatMemberStatusCreator extends ChatMemberStatus {
        public boolean isMember;

        public ChatMemberStatusCreator() {
        }

        public ChatMemberStatusCreator(boolean isMember) {
            this.isMember = isMember;
        }

        public static final int CONSTRUCTOR = 1756320508;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatMemberStatusAdministrator extends ChatMemberStatus {
        public boolean canBeEdited;
        public boolean canChangeInfo;
        public boolean canPostMessages;
        public boolean canEditMessages;
        public boolean canDeleteMessages;
        public boolean canInviteUsers;
        public boolean canRestrictMembers;
        public boolean canPinMessages;
        public boolean canPromoteMembers;

        public ChatMemberStatusAdministrator() {
        }

        public ChatMemberStatusAdministrator(boolean canBeEdited, boolean canChangeInfo, boolean canPostMessages, boolean canEditMessages, boolean canDeleteMessages, boolean canInviteUsers, boolean canRestrictMembers, boolean canPinMessages, boolean canPromoteMembers) {
            this.canBeEdited = canBeEdited;
            this.canChangeInfo = canChangeInfo;
            this.canPostMessages = canPostMessages;
            this.canEditMessages = canEditMessages;
            this.canDeleteMessages = canDeleteMessages;
            this.canInviteUsers = canInviteUsers;
            this.canRestrictMembers = canRestrictMembers;
            this.canPinMessages = canPinMessages;
            this.canPromoteMembers = canPromoteMembers;
        }

        public static final int CONSTRUCTOR = 45106688;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatMemberStatusMember extends ChatMemberStatus {

        public ChatMemberStatusMember() {
        }

        public static final int CONSTRUCTOR = 844723285;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatMemberStatusRestricted extends ChatMemberStatus {
        public boolean isMember;
        public int restrictedUntilDate;
        public boolean canSendMessages;
        public boolean canSendMediaMessages;
        public boolean canSendOtherMessages;
        public boolean canAddWebPagePreviews;

        public ChatMemberStatusRestricted() {
        }

        public ChatMemberStatusRestricted(boolean isMember, int restrictedUntilDate, boolean canSendMessages, boolean canSendMediaMessages, boolean canSendOtherMessages, boolean canAddWebPagePreviews) {
            this.isMember = isMember;
            this.restrictedUntilDate = restrictedUntilDate;
            this.canSendMessages = canSendMessages;
            this.canSendMediaMessages = canSendMediaMessages;
            this.canSendOtherMessages = canSendOtherMessages;
            this.canAddWebPagePreviews = canAddWebPagePreviews;
        }

        public static final int CONSTRUCTOR = 2068116214;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatMemberStatusLeft extends ChatMemberStatus {

        public ChatMemberStatusLeft() {
        }

        public static final int CONSTRUCTOR = -5815259;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatMemberStatusBanned extends ChatMemberStatus {
        public int bannedUntilDate;

        public ChatMemberStatusBanned() {
        }

        public ChatMemberStatusBanned(int bannedUntilDate) {
            this.bannedUntilDate = bannedUntilDate;
        }

        public static final int CONSTRUCTOR = -1653518666;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatMembers extends Object {
        public int totalCount;
        public ChatMember[] members;

        public ChatMembers() {
        }

        public ChatMembers(int totalCount, ChatMember[] members) {
            this.totalCount = totalCount;
            this.members = members;
        }

        public static final int CONSTRUCTOR = -497558622;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class ChatMembersFilter extends Object {
    }

    public static class ChatMembersFilterAdministrators extends ChatMembersFilter {

        public ChatMembersFilterAdministrators() {
        }

        public static final int CONSTRUCTOR = -1266893796;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatMembersFilterMembers extends ChatMembersFilter {

        public ChatMembersFilterMembers() {
        }

        public static final int CONSTRUCTOR = 670504342;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatMembersFilterRestricted extends ChatMembersFilter {

        public ChatMembersFilterRestricted() {
        }

        public static final int CONSTRUCTOR = 1256282813;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatMembersFilterBanned extends ChatMembersFilter {

        public ChatMembersFilterBanned() {
        }

        public static final int CONSTRUCTOR = -1863102648;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatMembersFilterBots extends ChatMembersFilter {

        public ChatMembersFilterBots() {
        }

        public static final int CONSTRUCTOR = -1422567288;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatNotificationSettings extends Object {
        public boolean useDefaultMuteFor;
        public int muteFor;
        public boolean useDefaultSound;
        public String sound;
        public boolean useDefaultShowPreview;
        public boolean showPreview;

        public ChatNotificationSettings() {
        }

        public ChatNotificationSettings(boolean useDefaultMuteFor, int muteFor, boolean useDefaultSound, String sound, boolean useDefaultShowPreview, boolean showPreview) {
            this.useDefaultMuteFor = useDefaultMuteFor;
            this.muteFor = muteFor;
            this.useDefaultSound = useDefaultSound;
            this.sound = sound;
            this.useDefaultShowPreview = useDefaultShowPreview;
            this.showPreview = showPreview;
        }

        public static final int CONSTRUCTOR = 1949924445;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatPhoto extends Object {
        public File small;
        public File big;

        public ChatPhoto() {
        }

        public ChatPhoto(File small, File big) {
            this.small = small;
            this.big = big;
        }

        public static final int CONSTRUCTOR = -217062456;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class ChatReportReason extends Object {
    }

    public static class ChatReportReasonSpam extends ChatReportReason {

        public ChatReportReasonSpam() {
        }

        public static final int CONSTRUCTOR = -510848863;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatReportReasonViolence extends ChatReportReason {

        public ChatReportReasonViolence() {
        }

        public static final int CONSTRUCTOR = -1330235395;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatReportReasonPornography extends ChatReportReason {

        public ChatReportReasonPornography() {
        }

        public static final int CONSTRUCTOR = 722614385;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatReportReasonCopyright extends ChatReportReason {

        public ChatReportReasonCopyright() {
        }

        public static final int CONSTRUCTOR = 986898080;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatReportReasonCustom extends ChatReportReason {
        public String text;

        public ChatReportReasonCustom() {
        }

        public ChatReportReasonCustom(String text) {
            this.text = text;
        }

        public static final int CONSTRUCTOR = 544575454;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatReportSpamState extends Object {
        public boolean canReportSpam;

        public ChatReportSpamState() {
        }

        public ChatReportSpamState(boolean canReportSpam) {
            this.canReportSpam = canReportSpam;
        }

        public static final int CONSTRUCTOR = -1919240972;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class ChatType extends Object {
    }

    public static class ChatTypePrivate extends ChatType {
        public int userId;

        public ChatTypePrivate() {
        }

        public ChatTypePrivate(int userId) {
            this.userId = userId;
        }

        public static final int CONSTRUCTOR = 1700720838;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatTypeBasicGroup extends ChatType {
        public int basicGroupId;

        public ChatTypeBasicGroup() {
        }

        public ChatTypeBasicGroup(int basicGroupId) {
            this.basicGroupId = basicGroupId;
        }

        public static final int CONSTRUCTOR = 21815278;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatTypeSupergroup extends ChatType {
        public int supergroupId;
        public boolean isChannel;

        public ChatTypeSupergroup() {
        }

        public ChatTypeSupergroup(int supergroupId, boolean isChannel) {
            this.supergroupId = supergroupId;
            this.isChannel = isChannel;
        }

        public static final int CONSTRUCTOR = 955152366;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChatTypeSecret extends ChatType {
        public int secretChatId;
        public int userId;

        public ChatTypeSecret() {
        }

        public ChatTypeSecret(int secretChatId, int userId) {
            this.secretChatId = secretChatId;
            this.userId = userId;
        }

        public static final int CONSTRUCTOR = 136722563;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Chats extends Object {
        public long[] chatIds;

        public Chats() {
        }

        public Chats(long[] chatIds) {
            this.chatIds = chatIds;
        }

        public static final int CONSTRUCTOR = -1687756019;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class CheckChatUsernameResult extends Object {
    }

    public static class CheckChatUsernameResultOk extends CheckChatUsernameResult {

        public CheckChatUsernameResultOk() {
        }

        public static final int CONSTRUCTOR = -1498956964;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CheckChatUsernameResultUsernameInvalid extends CheckChatUsernameResult {

        public CheckChatUsernameResultUsernameInvalid() {
        }

        public static final int CONSTRUCTOR = -636979370;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CheckChatUsernameResultUsernameOccupied extends CheckChatUsernameResult {

        public CheckChatUsernameResultUsernameOccupied() {
        }

        public static final int CONSTRUCTOR = 1320892201;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CheckChatUsernameResultPublicChatsTooMuch extends CheckChatUsernameResult {

        public CheckChatUsernameResultPublicChatsTooMuch() {
        }

        public static final int CONSTRUCTOR = 858247741;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CheckChatUsernameResultPublicGroupsUnavailable extends CheckChatUsernameResult {

        public CheckChatUsernameResultPublicGroupsUnavailable() {
        }

        public static final int CONSTRUCTOR = -51833641;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ConnectedWebsite extends Object {
        public long id;
        public String domainName;
        public int botUserId;
        public String browser;
        public String platform;
        public int logInDate;
        public int lastActiveDate;
        public String ip;
        public String location;

        public ConnectedWebsite() {
        }

        public ConnectedWebsite(long id, String domainName, int botUserId, String browser, String platform, int logInDate, int lastActiveDate, String ip, String location) {
            this.id = id;
            this.domainName = domainName;
            this.botUserId = botUserId;
            this.browser = browser;
            this.platform = platform;
            this.logInDate = logInDate;
            this.lastActiveDate = lastActiveDate;
            this.ip = ip;
            this.location = location;
        }

        public static final int CONSTRUCTOR = -1538986855;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ConnectedWebsites extends Object {
        public ConnectedWebsite[] websites;

        public ConnectedWebsites() {
        }

        public ConnectedWebsites(ConnectedWebsite[] websites) {
            this.websites = websites;
        }

        public static final int CONSTRUCTOR = -1727949694;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class ConnectionState extends Object {
    }

    public static class ConnectionStateWaitingForNetwork extends ConnectionState {

        public ConnectionStateWaitingForNetwork() {
        }

        public static final int CONSTRUCTOR = 1695405912;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ConnectionStateConnectingToProxy extends ConnectionState {

        public ConnectionStateConnectingToProxy() {
        }

        public static final int CONSTRUCTOR = -93187239;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ConnectionStateConnecting extends ConnectionState {

        public ConnectionStateConnecting() {
        }

        public static final int CONSTRUCTOR = -1298400670;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ConnectionStateUpdating extends ConnectionState {

        public ConnectionStateUpdating() {
        }

        public static final int CONSTRUCTOR = -188104009;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ConnectionStateReady extends ConnectionState {

        public ConnectionStateReady() {
        }

        public static final int CONSTRUCTOR = 48608492;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Contact extends Object {
        public String phoneNumber;
        public String firstName;
        public String lastName;
        public String vcard;
        public int userId;

        public Contact() {
        }

        public Contact(String phoneNumber, String firstName, String lastName, String vcard, int userId) {
            this.phoneNumber = phoneNumber;
            this.firstName = firstName;
            this.lastName = lastName;
            this.vcard = vcard;
            this.userId = userId;
        }

        public static final int CONSTRUCTOR = -1483002540;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Count extends Object {
        public int count;

        public Count() {
        }

        public Count(int count) {
            this.count = count;
        }

        public static final int CONSTRUCTOR = 1295577348;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CustomRequestResult extends Object {
        public String result;

        public CustomRequestResult() {
        }

        public CustomRequestResult(String result) {
            this.result = result;
        }

        public static final int CONSTRUCTOR = -2009960452;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Date extends Object {
        public int day;
        public int month;
        public int year;

        public Date() {
        }

        public Date(int day, int month, int year) {
            this.day = day;
            this.month = month;
            this.year = year;
        }

        public static final int CONSTRUCTOR = -277956960;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DatedFile extends Object {
        public File file;
        public int date;

        public DatedFile() {
        }

        public DatedFile(File file, int date) {
            this.file = file;
            this.date = date;
        }

        public static final int CONSTRUCTOR = -1840795491;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DeepLinkInfo extends Object {
        public FormattedText text;
        public boolean needUpdateApplication;

        public DeepLinkInfo() {
        }

        public DeepLinkInfo(FormattedText text, boolean needUpdateApplication) {
            this.text = text;
            this.needUpdateApplication = needUpdateApplication;
        }

        public static final int CONSTRUCTOR = 1864081662;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class DeviceToken extends Object {
    }

    public static class DeviceTokenGoogleCloudMessaging extends DeviceToken {
        public String token;

        public DeviceTokenGoogleCloudMessaging() {
        }

        public DeviceTokenGoogleCloudMessaging(String token) {
            this.token = token;
        }

        public static final int CONSTRUCTOR = 1092220225;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DeviceTokenApplePush extends DeviceToken {
        public String deviceToken;
        public boolean isAppSandbox;

        public DeviceTokenApplePush() {
        }

        public DeviceTokenApplePush(String deviceToken, boolean isAppSandbox) {
            this.deviceToken = deviceToken;
            this.isAppSandbox = isAppSandbox;
        }

        public static final int CONSTRUCTOR = 387541955;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DeviceTokenApplePushVoIP extends DeviceToken {
        public String deviceToken;
        public boolean isAppSandbox;

        public DeviceTokenApplePushVoIP() {
        }

        public DeviceTokenApplePushVoIP(String deviceToken, boolean isAppSandbox) {
            this.deviceToken = deviceToken;
            this.isAppSandbox = isAppSandbox;
        }

        public static final int CONSTRUCTOR = -327676505;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DeviceTokenWindowsPush extends DeviceToken {
        public String accessToken;

        public DeviceTokenWindowsPush() {
        }

        public DeviceTokenWindowsPush(String accessToken) {
            this.accessToken = accessToken;
        }

        public static final int CONSTRUCTOR = -1410514289;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DeviceTokenMicrosoftPush extends DeviceToken {
        public String channelUri;

        public DeviceTokenMicrosoftPush() {
        }

        public DeviceTokenMicrosoftPush(String channelUri) {
            this.channelUri = channelUri;
        }

        public static final int CONSTRUCTOR = 1224269900;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DeviceTokenMicrosoftPushVoIP extends DeviceToken {
        public String channelUri;

        public DeviceTokenMicrosoftPushVoIP() {
        }

        public DeviceTokenMicrosoftPushVoIP(String channelUri) {
            this.channelUri = channelUri;
        }

        public static final int CONSTRUCTOR = -785603759;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DeviceTokenWebPush extends DeviceToken {
        public String endpoint;
        public String p256dhBase64url;
        public String authBase64url;

        public DeviceTokenWebPush() {
        }

        public DeviceTokenWebPush(String endpoint, String p256dhBase64url, String authBase64url) {
            this.endpoint = endpoint;
            this.p256dhBase64url = p256dhBase64url;
            this.authBase64url = authBase64url;
        }

        public static final int CONSTRUCTOR = -1694507273;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DeviceTokenSimplePush extends DeviceToken {
        public String endpoint;

        public DeviceTokenSimplePush() {
        }

        public DeviceTokenSimplePush(String endpoint) {
            this.endpoint = endpoint;
        }

        public static final int CONSTRUCTOR = 49584736;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DeviceTokenUbuntuPush extends DeviceToken {
        public String token;

        public DeviceTokenUbuntuPush() {
        }

        public DeviceTokenUbuntuPush(String token) {
            this.token = token;
        }

        public static final int CONSTRUCTOR = 1782320422;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DeviceTokenBlackBerryPush extends DeviceToken {
        public String token;

        public DeviceTokenBlackBerryPush() {
        }

        public DeviceTokenBlackBerryPush(String token) {
            this.token = token;
        }

        public static final int CONSTRUCTOR = 1559167234;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DeviceTokenTizenPush extends DeviceToken {
        public String regId;

        public DeviceTokenTizenPush() {
        }

        public DeviceTokenTizenPush(String regId) {
            this.regId = regId;
        }

        public static final int CONSTRUCTOR = -1359947213;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Document extends Object {
        public String fileName;
        public String mimeType;
        public PhotoSize thumbnail;
        public File document;

        public Document() {
        }

        public Document(String fileName, String mimeType, PhotoSize thumbnail, File document) {
            this.fileName = fileName;
            this.mimeType = mimeType;
            this.thumbnail = thumbnail;
            this.document = document;
        }

        public static final int CONSTRUCTOR = -736037786;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DraftMessage extends Object {
        public long replyToMessageId;
        public InputMessageContent inputMessageText;

        public DraftMessage() {
        }

        public DraftMessage(long replyToMessageId, InputMessageContent inputMessageText) {
            this.replyToMessageId = replyToMessageId;
            this.inputMessageText = inputMessageText;
        }

        public static final int CONSTRUCTOR = 1902914742;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class EmailAddressAuthenticationCodeInfo extends Object {
        public String emailAddressPattern;
        public int length;

        public EmailAddressAuthenticationCodeInfo() {
        }

        public EmailAddressAuthenticationCodeInfo(String emailAddressPattern, int length) {
            this.emailAddressPattern = emailAddressPattern;
            this.length = length;
        }

        public static final int CONSTRUCTOR = 1151066659;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class EncryptedCredentials extends Object {
        public byte[] data;
        public byte[] hash;
        public byte[] secret;

        public EncryptedCredentials() {
        }

        public EncryptedCredentials(byte[] data, byte[] hash, byte[] secret) {
            this.data = data;
            this.hash = hash;
            this.secret = secret;
        }

        public static final int CONSTRUCTOR = 1331106766;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class EncryptedPassportElement extends Object {
        public PassportElementType type;
        public byte[] data;
        public DatedFile frontSide;
        public DatedFile reverseSide;
        public DatedFile selfie;
        public DatedFile[] translation;
        public DatedFile[] files;
        public String value;
        public String hash;

        public EncryptedPassportElement() {
        }

        public EncryptedPassportElement(PassportElementType type, byte[] data, DatedFile frontSide, DatedFile reverseSide, DatedFile selfie, DatedFile[] translation, DatedFile[] files, String value, String hash) {
            this.type = type;
            this.data = data;
            this.frontSide = frontSide;
            this.reverseSide = reverseSide;
            this.selfie = selfie;
            this.translation = translation;
            this.files = files;
            this.value = value;
            this.hash = hash;
        }

        public static final int CONSTRUCTOR = 2002386193;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Error extends Object {
        public int code;
        public String message;

        public Error() {
        }

        public Error(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public static final int CONSTRUCTOR = -1679978726;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class File extends Object {
        public int id;
        public int size;
        public int expectedSize;
        public LocalFile local;
        public RemoteFile remote;

        public File() {
        }

        public File(int id, int size, int expectedSize, LocalFile local, RemoteFile remote) {
            this.id = id;
            this.size = size;
            this.expectedSize = expectedSize;
            this.local = local;
            this.remote = remote;
        }

        public static final int CONSTRUCTOR = 766337656;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class FileType extends Object {
    }

    public static class FileTypeNone extends FileType {

        public FileTypeNone() {
        }

        public static final int CONSTRUCTOR = 2003009189;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class FileTypeAnimation extends FileType {

        public FileTypeAnimation() {
        }

        public static final int CONSTRUCTOR = -290816582;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class FileTypeAudio extends FileType {

        public FileTypeAudio() {
        }

        public static final int CONSTRUCTOR = -709112160;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class FileTypeDocument extends FileType {

        public FileTypeDocument() {
        }

        public static final int CONSTRUCTOR = -564722929;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class FileTypePhoto extends FileType {

        public FileTypePhoto() {
        }

        public static final int CONSTRUCTOR = -1718914651;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class FileTypeProfilePhoto extends FileType {

        public FileTypeProfilePhoto() {
        }

        public static final int CONSTRUCTOR = 1795089315;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class FileTypeSecret extends FileType {

        public FileTypeSecret() {
        }

        public static final int CONSTRUCTOR = -1871899401;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class FileTypeSecretThumbnail extends FileType {

        public FileTypeSecretThumbnail() {
        }

        public static final int CONSTRUCTOR = -1401326026;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class FileTypeSecure extends FileType {

        public FileTypeSecure() {
        }

        public static final int CONSTRUCTOR = -1419133146;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class FileTypeSticker extends FileType {

        public FileTypeSticker() {
        }

        public static final int CONSTRUCTOR = 475233385;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class FileTypeThumbnail extends FileType {

        public FileTypeThumbnail() {
        }

        public static final int CONSTRUCTOR = -12443298;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class FileTypeUnknown extends FileType {

        public FileTypeUnknown() {
        }

        public static final int CONSTRUCTOR = -2011566768;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class FileTypeVideo extends FileType {

        public FileTypeVideo() {
        }

        public static final int CONSTRUCTOR = 1430816539;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class FileTypeVideoNote extends FileType {

        public FileTypeVideoNote() {
        }

        public static final int CONSTRUCTOR = -518412385;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class FileTypeVoiceNote extends FileType {

        public FileTypeVoiceNote() {
        }

        public static final int CONSTRUCTOR = -588681661;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class FileTypeWallpaper extends FileType {

        public FileTypeWallpaper() {
        }

        public static final int CONSTRUCTOR = 1854930076;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class FormattedText extends Object {
        public String text;
        public TextEntity[] entities;

        public FormattedText() {
        }

        public FormattedText(String text, TextEntity[] entities) {
            this.text = text;
            this.entities = entities;
        }

        public static final int CONSTRUCTOR = -252624564;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class FoundMessages extends Object {
        public Message[] messages;
        public long nextFromSearchId;

        public FoundMessages() {
        }

        public FoundMessages(Message[] messages, long nextFromSearchId) {
            this.messages = messages;
            this.nextFromSearchId = nextFromSearchId;
        }

        public static final int CONSTRUCTOR = 2135623881;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Game extends Object {
        public long id;
        public String shortName;
        public String title;
        public FormattedText text;
        public String description;
        public Photo photo;
        public Animation animation;

        public Game() {
        }

        public Game(long id, String shortName, String title, FormattedText text, String description, Photo photo, Animation animation) {
            this.id = id;
            this.shortName = shortName;
            this.title = title;
            this.text = text;
            this.description = description;
            this.photo = photo;
            this.animation = animation;
        }

        public static final int CONSTRUCTOR = -1565597752;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GameHighScore extends Object {
        public int position;
        public int userId;
        public int score;

        public GameHighScore() {
        }

        public GameHighScore(int position, int userId, int score) {
            this.position = position;
            this.userId = userId;
            this.score = score;
        }

        public static final int CONSTRUCTOR = -30778358;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GameHighScores extends Object {
        public GameHighScore[] scores;

        public GameHighScores() {
        }

        public GameHighScores(GameHighScore[] scores) {
            this.scores = scores;
        }

        public static final int CONSTRUCTOR = -725770727;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Hashtags extends Object {
        public String[] hashtags;

        public Hashtags() {
        }

        public Hashtags(String[] hashtags) {
            this.hashtags = hashtags;
        }

        public static final int CONSTRUCTOR = 676798885;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class IdentityDocument extends Object {
        public String number;
        public Date expiryDate;
        public DatedFile frontSide;
        public DatedFile reverseSide;
        public DatedFile selfie;
        public DatedFile[] translation;

        public IdentityDocument() {
        }

        public IdentityDocument(String number, Date expiryDate, DatedFile frontSide, DatedFile reverseSide, DatedFile selfie, DatedFile[] translation) {
            this.number = number;
            this.expiryDate = expiryDate;
            this.frontSide = frontSide;
            this.reverseSide = reverseSide;
            this.selfie = selfie;
            this.translation = translation;
        }

        public static final int CONSTRUCTOR = 445952972;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ImportedContacts extends Object {
        public int[] userIds;
        public int[] importerCount;

        public ImportedContacts() {
        }

        public ImportedContacts(int[] userIds, int[] importerCount) {
            this.userIds = userIds;
            this.importerCount = importerCount;
        }

        public static final int CONSTRUCTOR = -741685354;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InlineKeyboardButton extends Object {
        public String text;
        public InlineKeyboardButtonType type;

        public InlineKeyboardButton() {
        }

        public InlineKeyboardButton(String text, InlineKeyboardButtonType type) {
            this.text = text;
            this.type = type;
        }

        public static final int CONSTRUCTOR = -372105704;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class InlineKeyboardButtonType extends Object {
    }

    public static class InlineKeyboardButtonTypeUrl extends InlineKeyboardButtonType {
        public String url;

        public InlineKeyboardButtonTypeUrl() {
        }

        public InlineKeyboardButtonTypeUrl(String url) {
            this.url = url;
        }

        public static final int CONSTRUCTOR = 1130741420;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InlineKeyboardButtonTypeCallback extends InlineKeyboardButtonType {
        public byte[] data;

        public InlineKeyboardButtonTypeCallback() {
        }

        public InlineKeyboardButtonTypeCallback(byte[] data) {
            this.data = data;
        }

        public static final int CONSTRUCTOR = -1127515139;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InlineKeyboardButtonTypeCallbackGame extends InlineKeyboardButtonType {

        public InlineKeyboardButtonTypeCallbackGame() {
        }

        public static final int CONSTRUCTOR = -383429528;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InlineKeyboardButtonTypeSwitchInline extends InlineKeyboardButtonType {
        public String query;
        public boolean inCurrentChat;

        public InlineKeyboardButtonTypeSwitchInline() {
        }

        public InlineKeyboardButtonTypeSwitchInline(String query, boolean inCurrentChat) {
            this.query = query;
            this.inCurrentChat = inCurrentChat;
        }

        public static final int CONSTRUCTOR = -2035563307;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InlineKeyboardButtonTypeBuy extends InlineKeyboardButtonType {

        public InlineKeyboardButtonTypeBuy() {
        }

        public static final int CONSTRUCTOR = 1360739440;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class InlineQueryResult extends Object {
    }

    public static class InlineQueryResultArticle extends InlineQueryResult {
        public String id;
        public String url;
        public boolean hideUrl;
        public String title;
        public String description;
        public PhotoSize thumbnail;

        public InlineQueryResultArticle() {
        }

        public InlineQueryResultArticle(String id, String url, boolean hideUrl, String title, String description, PhotoSize thumbnail) {
            this.id = id;
            this.url = url;
            this.hideUrl = hideUrl;
            this.title = title;
            this.description = description;
            this.thumbnail = thumbnail;
        }

        public static final int CONSTRUCTOR = -518366710;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InlineQueryResultContact extends InlineQueryResult {
        public String id;
        public Contact contact;
        public PhotoSize thumbnail;

        public InlineQueryResultContact() {
        }

        public InlineQueryResultContact(String id, Contact contact, PhotoSize thumbnail) {
            this.id = id;
            this.contact = contact;
            this.thumbnail = thumbnail;
        }

        public static final int CONSTRUCTOR = 410081985;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InlineQueryResultLocation extends InlineQueryResult {
        public String id;
        public Location location;
        public String title;
        public PhotoSize thumbnail;

        public InlineQueryResultLocation() {
        }

        public InlineQueryResultLocation(String id, Location location, String title, PhotoSize thumbnail) {
            this.id = id;
            this.location = location;
            this.title = title;
            this.thumbnail = thumbnail;
        }

        public static final int CONSTRUCTOR = -158305341;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InlineQueryResultVenue extends InlineQueryResult {
        public String id;
        public Venue venue;
        public PhotoSize thumbnail;

        public InlineQueryResultVenue() {
        }

        public InlineQueryResultVenue(String id, Venue venue, PhotoSize thumbnail) {
            this.id = id;
            this.venue = venue;
            this.thumbnail = thumbnail;
        }

        public static final int CONSTRUCTOR = -1592932211;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InlineQueryResultGame extends InlineQueryResult {
        public String id;
        public Game game;

        public InlineQueryResultGame() {
        }

        public InlineQueryResultGame(String id, Game game) {
            this.id = id;
            this.game = game;
        }

        public static final int CONSTRUCTOR = 1706916987;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InlineQueryResultAnimation extends InlineQueryResult {
        public String id;
        public Animation animation;
        public String title;

        public InlineQueryResultAnimation() {
        }

        public InlineQueryResultAnimation(String id, Animation animation, String title) {
            this.id = id;
            this.animation = animation;
            this.title = title;
        }

        public static final int CONSTRUCTOR = 2009984267;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InlineQueryResultAudio extends InlineQueryResult {
        public String id;
        public Audio audio;

        public InlineQueryResultAudio() {
        }

        public InlineQueryResultAudio(String id, Audio audio) {
            this.id = id;
            this.audio = audio;
        }

        public static final int CONSTRUCTOR = 842650360;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InlineQueryResultDocument extends InlineQueryResult {
        public String id;
        public Document document;
        public String title;
        public String description;

        public InlineQueryResultDocument() {
        }

        public InlineQueryResultDocument(String id, Document document, String title, String description) {
            this.id = id;
            this.document = document;
            this.title = title;
            this.description = description;
        }

        public static final int CONSTRUCTOR = -1491268539;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InlineQueryResultPhoto extends InlineQueryResult {
        public String id;
        public Photo photo;
        public String title;
        public String description;

        public InlineQueryResultPhoto() {
        }

        public InlineQueryResultPhoto(String id, Photo photo, String title, String description) {
            this.id = id;
            this.photo = photo;
            this.title = title;
            this.description = description;
        }

        public static final int CONSTRUCTOR = 1848319440;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InlineQueryResultSticker extends InlineQueryResult {
        public String id;
        public Sticker sticker;

        public InlineQueryResultSticker() {
        }

        public InlineQueryResultSticker(String id, Sticker sticker) {
            this.id = id;
            this.sticker = sticker;
        }

        public static final int CONSTRUCTOR = -1848224245;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InlineQueryResultVideo extends InlineQueryResult {
        public String id;
        public Video video;
        public String title;
        public String description;

        public InlineQueryResultVideo() {
        }

        public InlineQueryResultVideo(String id, Video video, String title, String description) {
            this.id = id;
            this.video = video;
            this.title = title;
            this.description = description;
        }

        public static final int CONSTRUCTOR = -1373158683;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InlineQueryResultVoiceNote extends InlineQueryResult {
        public String id;
        public VoiceNote voiceNote;
        public String title;

        public InlineQueryResultVoiceNote() {
        }

        public InlineQueryResultVoiceNote(String id, VoiceNote voiceNote, String title) {
            this.id = id;
            this.voiceNote = voiceNote;
            this.title = title;
        }

        public static final int CONSTRUCTOR = -1897393105;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InlineQueryResults extends Object {
        public long inlineQueryId;
        public String nextOffset;
        public InlineQueryResult[] results;
        public String switchPmText;
        public String switchPmParameter;

        public InlineQueryResults() {
        }

        public InlineQueryResults(long inlineQueryId, String nextOffset, InlineQueryResult[] results, String switchPmText, String switchPmParameter) {
            this.inlineQueryId = inlineQueryId;
            this.nextOffset = nextOffset;
            this.results = results;
            this.switchPmText = switchPmText;
            this.switchPmParameter = switchPmParameter;
        }

        public static final int CONSTRUCTOR = 1000709656;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class InputCredentials extends Object {
    }

    public static class InputCredentialsSaved extends InputCredentials {
        public String savedCredentialsId;

        public InputCredentialsSaved() {
        }

        public InputCredentialsSaved(String savedCredentialsId) {
            this.savedCredentialsId = savedCredentialsId;
        }

        public static final int CONSTRUCTOR = -2034385364;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputCredentialsNew extends InputCredentials {
        public String data;
        public boolean allowSave;

        public InputCredentialsNew() {
        }

        public InputCredentialsNew(String data, boolean allowSave) {
            this.data = data;
            this.allowSave = allowSave;
        }

        public static final int CONSTRUCTOR = -829689558;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputCredentialsAndroidPay extends InputCredentials {
        public String data;

        public InputCredentialsAndroidPay() {
        }

        public InputCredentialsAndroidPay(String data) {
            this.data = data;
        }

        public static final int CONSTRUCTOR = 1979566832;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputCredentialsApplePay extends InputCredentials {
        public String data;

        public InputCredentialsApplePay() {
        }

        public InputCredentialsApplePay(String data) {
            this.data = data;
        }

        public static final int CONSTRUCTOR = -1246570799;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class InputFile extends Object {
    }

    public static class InputFileId extends InputFile {
        public int id;

        public InputFileId() {
        }

        public InputFileId(int id) {
            this.id = id;
        }

        public static final int CONSTRUCTOR = 1788906253;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputFileRemote extends InputFile {
        public String id;

        public InputFileRemote() {
        }

        public InputFileRemote(String id) {
            this.id = id;
        }

        public static final int CONSTRUCTOR = -107574466;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputFileLocal extends InputFile {
        public String path;

        public InputFileLocal() {
        }

        public InputFileLocal(String path) {
            this.path = path;
        }

        public static final int CONSTRUCTOR = 2056030919;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputFileGenerated extends InputFile {
        public String originalPath;
        public String conversion;
        public int expectedSize;

        public InputFileGenerated() {
        }

        public InputFileGenerated(String originalPath, String conversion, int expectedSize) {
            this.originalPath = originalPath;
            this.conversion = conversion;
            this.expectedSize = expectedSize;
        }

        public static final int CONSTRUCTOR = -1781351885;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputIdentityDocument extends Object {
        public String number;
        public Date expiryDate;
        public InputFile frontSide;
        public InputFile reverseSide;
        public InputFile selfie;
        public InputFile[] translation;

        public InputIdentityDocument() {
        }

        public InputIdentityDocument(String number, Date expiryDate, InputFile frontSide, InputFile reverseSide, InputFile selfie, InputFile[] translation) {
            this.number = number;
            this.expiryDate = expiryDate;
            this.frontSide = frontSide;
            this.reverseSide = reverseSide;
            this.selfie = selfie;
            this.translation = translation;
        }

        public static final int CONSTRUCTOR = -381776063;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class InputInlineQueryResult extends Object {
    }

    public static class InputInlineQueryResultAnimatedGif extends InputInlineQueryResult {
        public String id;
        public String title;
        public String thumbnailUrl;
        public String gifUrl;
        public int gifDuration;
        public int gifWidth;
        public int gifHeight;
        public ReplyMarkup replyMarkup;
        public InputMessageContent inputMessageContent;

        public InputInlineQueryResultAnimatedGif() {
        }

        public InputInlineQueryResultAnimatedGif(String id, String title, String thumbnailUrl, String gifUrl, int gifDuration, int gifWidth, int gifHeight, ReplyMarkup replyMarkup, InputMessageContent inputMessageContent) {
            this.id = id;
            this.title = title;
            this.thumbnailUrl = thumbnailUrl;
            this.gifUrl = gifUrl;
            this.gifDuration = gifDuration;
            this.gifWidth = gifWidth;
            this.gifHeight = gifHeight;
            this.replyMarkup = replyMarkup;
            this.inputMessageContent = inputMessageContent;
        }

        public static final int CONSTRUCTOR = -891474894;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputInlineQueryResultAnimatedMpeg4 extends InputInlineQueryResult {
        public String id;
        public String title;
        public String thumbnailUrl;
        public String mpeg4Url;
        public int mpeg4Duration;
        public int mpeg4Width;
        public int mpeg4Height;
        public ReplyMarkup replyMarkup;
        public InputMessageContent inputMessageContent;

        public InputInlineQueryResultAnimatedMpeg4() {
        }

        public InputInlineQueryResultAnimatedMpeg4(String id, String title, String thumbnailUrl, String mpeg4Url, int mpeg4Duration, int mpeg4Width, int mpeg4Height, ReplyMarkup replyMarkup, InputMessageContent inputMessageContent) {
            this.id = id;
            this.title = title;
            this.thumbnailUrl = thumbnailUrl;
            this.mpeg4Url = mpeg4Url;
            this.mpeg4Duration = mpeg4Duration;
            this.mpeg4Width = mpeg4Width;
            this.mpeg4Height = mpeg4Height;
            this.replyMarkup = replyMarkup;
            this.inputMessageContent = inputMessageContent;
        }

        public static final int CONSTRUCTOR = -1629529888;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputInlineQueryResultArticle extends InputInlineQueryResult {
        public String id;
        public String url;
        public boolean hideUrl;
        public String title;
        public String description;
        public String thumbnailUrl;
        public int thumbnailWidth;
        public int thumbnailHeight;
        public ReplyMarkup replyMarkup;
        public InputMessageContent inputMessageContent;

        public InputInlineQueryResultArticle() {
        }

        public InputInlineQueryResultArticle(String id, String url, boolean hideUrl, String title, String description, String thumbnailUrl, int thumbnailWidth, int thumbnailHeight, ReplyMarkup replyMarkup, InputMessageContent inputMessageContent) {
            this.id = id;
            this.url = url;
            this.hideUrl = hideUrl;
            this.title = title;
            this.description = description;
            this.thumbnailUrl = thumbnailUrl;
            this.thumbnailWidth = thumbnailWidth;
            this.thumbnailHeight = thumbnailHeight;
            this.replyMarkup = replyMarkup;
            this.inputMessageContent = inputMessageContent;
        }

        public static final int CONSTRUCTOR = 1973670156;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputInlineQueryResultAudio extends InputInlineQueryResult {
        public String id;
        public String title;
        public String performer;
        public String audioUrl;
        public int audioDuration;
        public ReplyMarkup replyMarkup;
        public InputMessageContent inputMessageContent;

        public InputInlineQueryResultAudio() {
        }

        public InputInlineQueryResultAudio(String id, String title, String performer, String audioUrl, int audioDuration, ReplyMarkup replyMarkup, InputMessageContent inputMessageContent) {
            this.id = id;
            this.title = title;
            this.performer = performer;
            this.audioUrl = audioUrl;
            this.audioDuration = audioDuration;
            this.replyMarkup = replyMarkup;
            this.inputMessageContent = inputMessageContent;
        }

        public static final int CONSTRUCTOR = 1260139988;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputInlineQueryResultContact extends InputInlineQueryResult {
        public String id;
        public Contact contact;
        public String thumbnailUrl;
        public int thumbnailWidth;
        public int thumbnailHeight;
        public ReplyMarkup replyMarkup;
        public InputMessageContent inputMessageContent;

        public InputInlineQueryResultContact() {
        }

        public InputInlineQueryResultContact(String id, Contact contact, String thumbnailUrl, int thumbnailWidth, int thumbnailHeight, ReplyMarkup replyMarkup, InputMessageContent inputMessageContent) {
            this.id = id;
            this.contact = contact;
            this.thumbnailUrl = thumbnailUrl;
            this.thumbnailWidth = thumbnailWidth;
            this.thumbnailHeight = thumbnailHeight;
            this.replyMarkup = replyMarkup;
            this.inputMessageContent = inputMessageContent;
        }

        public static final int CONSTRUCTOR = 1846064594;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputInlineQueryResultDocument extends InputInlineQueryResult {
        public String id;
        public String title;
        public String description;
        public String documentUrl;
        public String mimeType;
        public String thumbnailUrl;
        public int thumbnailWidth;
        public int thumbnailHeight;
        public ReplyMarkup replyMarkup;
        public InputMessageContent inputMessageContent;

        public InputInlineQueryResultDocument() {
        }

        public InputInlineQueryResultDocument(String id, String title, String description, String documentUrl, String mimeType, String thumbnailUrl, int thumbnailWidth, int thumbnailHeight, ReplyMarkup replyMarkup, InputMessageContent inputMessageContent) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.documentUrl = documentUrl;
            this.mimeType = mimeType;
            this.thumbnailUrl = thumbnailUrl;
            this.thumbnailWidth = thumbnailWidth;
            this.thumbnailHeight = thumbnailHeight;
            this.replyMarkup = replyMarkup;
            this.inputMessageContent = inputMessageContent;
        }

        public static final int CONSTRUCTOR = 578801869;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputInlineQueryResultGame extends InputInlineQueryResult {
        public String id;
        public String gameShortName;
        public ReplyMarkup replyMarkup;

        public InputInlineQueryResultGame() {
        }

        public InputInlineQueryResultGame(String id, String gameShortName, ReplyMarkup replyMarkup) {
            this.id = id;
            this.gameShortName = gameShortName;
            this.replyMarkup = replyMarkup;
        }

        public static final int CONSTRUCTOR = 966074327;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputInlineQueryResultLocation extends InputInlineQueryResult {
        public String id;
        public Location location;
        public int livePeriod;
        public String title;
        public String thumbnailUrl;
        public int thumbnailWidth;
        public int thumbnailHeight;
        public ReplyMarkup replyMarkup;
        public InputMessageContent inputMessageContent;

        public InputInlineQueryResultLocation() {
        }

        public InputInlineQueryResultLocation(String id, Location location, int livePeriod, String title, String thumbnailUrl, int thumbnailWidth, int thumbnailHeight, ReplyMarkup replyMarkup, InputMessageContent inputMessageContent) {
            this.id = id;
            this.location = location;
            this.livePeriod = livePeriod;
            this.title = title;
            this.thumbnailUrl = thumbnailUrl;
            this.thumbnailWidth = thumbnailWidth;
            this.thumbnailHeight = thumbnailHeight;
            this.replyMarkup = replyMarkup;
            this.inputMessageContent = inputMessageContent;
        }

        public static final int CONSTRUCTOR = -1887650218;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputInlineQueryResultPhoto extends InputInlineQueryResult {
        public String id;
        public String title;
        public String description;
        public String thumbnailUrl;
        public String photoUrl;
        public int photoWidth;
        public int photoHeight;
        public ReplyMarkup replyMarkup;
        public InputMessageContent inputMessageContent;

        public InputInlineQueryResultPhoto() {
        }

        public InputInlineQueryResultPhoto(String id, String title, String description, String thumbnailUrl, String photoUrl, int photoWidth, int photoHeight, ReplyMarkup replyMarkup, InputMessageContent inputMessageContent) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.thumbnailUrl = thumbnailUrl;
            this.photoUrl = photoUrl;
            this.photoWidth = photoWidth;
            this.photoHeight = photoHeight;
            this.replyMarkup = replyMarkup;
            this.inputMessageContent = inputMessageContent;
        }

        public static final int CONSTRUCTOR = -1123338721;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputInlineQueryResultSticker extends InputInlineQueryResult {
        public String id;
        public String thumbnailUrl;
        public String stickerUrl;
        public int stickerWidth;
        public int stickerHeight;
        public ReplyMarkup replyMarkup;
        public InputMessageContent inputMessageContent;

        public InputInlineQueryResultSticker() {
        }

        public InputInlineQueryResultSticker(String id, String thumbnailUrl, String stickerUrl, int stickerWidth, int stickerHeight, ReplyMarkup replyMarkup, InputMessageContent inputMessageContent) {
            this.id = id;
            this.thumbnailUrl = thumbnailUrl;
            this.stickerUrl = stickerUrl;
            this.stickerWidth = stickerWidth;
            this.stickerHeight = stickerHeight;
            this.replyMarkup = replyMarkup;
            this.inputMessageContent = inputMessageContent;
        }

        public static final int CONSTRUCTOR = 274007129;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputInlineQueryResultVenue extends InputInlineQueryResult {
        public String id;
        public Venue venue;
        public String thumbnailUrl;
        public int thumbnailWidth;
        public int thumbnailHeight;
        public ReplyMarkup replyMarkup;
        public InputMessageContent inputMessageContent;

        public InputInlineQueryResultVenue() {
        }

        public InputInlineQueryResultVenue(String id, Venue venue, String thumbnailUrl, int thumbnailWidth, int thumbnailHeight, ReplyMarkup replyMarkup, InputMessageContent inputMessageContent) {
            this.id = id;
            this.venue = venue;
            this.thumbnailUrl = thumbnailUrl;
            this.thumbnailWidth = thumbnailWidth;
            this.thumbnailHeight = thumbnailHeight;
            this.replyMarkup = replyMarkup;
            this.inputMessageContent = inputMessageContent;
        }

        public static final int CONSTRUCTOR = 541704509;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputInlineQueryResultVideo extends InputInlineQueryResult {
        public String id;
        public String title;
        public String description;
        public String thumbnailUrl;
        public String videoUrl;
        public String mimeType;
        public int videoWidth;
        public int videoHeight;
        public int videoDuration;
        public ReplyMarkup replyMarkup;
        public InputMessageContent inputMessageContent;

        public InputInlineQueryResultVideo() {
        }

        public InputInlineQueryResultVideo(String id, String title, String description, String thumbnailUrl, String videoUrl, String mimeType, int videoWidth, int videoHeight, int videoDuration, ReplyMarkup replyMarkup, InputMessageContent inputMessageContent) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.thumbnailUrl = thumbnailUrl;
            this.videoUrl = videoUrl;
            this.mimeType = mimeType;
            this.videoWidth = videoWidth;
            this.videoHeight = videoHeight;
            this.videoDuration = videoDuration;
            this.replyMarkup = replyMarkup;
            this.inputMessageContent = inputMessageContent;
        }

        public static final int CONSTRUCTOR = 1724073191;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputInlineQueryResultVoiceNote extends InputInlineQueryResult {
        public String id;
        public String title;
        public String voiceNoteUrl;
        public int voiceNoteDuration;
        public ReplyMarkup replyMarkup;
        public InputMessageContent inputMessageContent;

        public InputInlineQueryResultVoiceNote() {
        }

        public InputInlineQueryResultVoiceNote(String id, String title, String voiceNoteUrl, int voiceNoteDuration, ReplyMarkup replyMarkup, InputMessageContent inputMessageContent) {
            this.id = id;
            this.title = title;
            this.voiceNoteUrl = voiceNoteUrl;
            this.voiceNoteDuration = voiceNoteDuration;
            this.replyMarkup = replyMarkup;
            this.inputMessageContent = inputMessageContent;
        }

        public static final int CONSTRUCTOR = -1790072503;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class InputMessageContent extends Object {
    }

    public static class InputMessageText extends InputMessageContent {
        public FormattedText text;
        public boolean disableWebPagePreview;
        public boolean clearDraft;

        public InputMessageText() {
        }

        public InputMessageText(FormattedText text, boolean disableWebPagePreview, boolean clearDraft) {
            this.text = text;
            this.disableWebPagePreview = disableWebPagePreview;
            this.clearDraft = clearDraft;
        }

        public static final int CONSTRUCTOR = 247050392;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputMessageAnimation extends InputMessageContent {
        public InputFile animation;
        public InputThumbnail thumbnail;
        public int duration;
        public int width;
        public int height;
        public FormattedText caption;

        public InputMessageAnimation() {
        }

        public InputMessageAnimation(InputFile animation, InputThumbnail thumbnail, int duration, int width, int height, FormattedText caption) {
            this.animation = animation;
            this.thumbnail = thumbnail;
            this.duration = duration;
            this.width = width;
            this.height = height;
            this.caption = caption;
        }

        public static final int CONSTRUCTOR = 926542724;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputMessageAudio extends InputMessageContent {
        public InputFile audio;
        public InputThumbnail albumCoverThumbnail;
        public int duration;
        public String title;
        public String performer;
        public FormattedText caption;

        public InputMessageAudio() {
        }

        public InputMessageAudio(InputFile audio, InputThumbnail albumCoverThumbnail, int duration, String title, String performer, FormattedText caption) {
            this.audio = audio;
            this.albumCoverThumbnail = albumCoverThumbnail;
            this.duration = duration;
            this.title = title;
            this.performer = performer;
            this.caption = caption;
        }

        public static final int CONSTRUCTOR = -626786126;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputMessageDocument extends InputMessageContent {
        public InputFile document;
        public InputThumbnail thumbnail;
        public FormattedText caption;

        public InputMessageDocument() {
        }

        public InputMessageDocument(InputFile document, InputThumbnail thumbnail, FormattedText caption) {
            this.document = document;
            this.thumbnail = thumbnail;
            this.caption = caption;
        }

        public static final int CONSTRUCTOR = 937970604;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputMessagePhoto extends InputMessageContent {
        public InputFile photo;
        public InputThumbnail thumbnail;
        public int[] addedStickerFileIds;
        public int width;
        public int height;
        public FormattedText caption;
        public int ttl;

        public InputMessagePhoto() {
        }

        public InputMessagePhoto(InputFile photo, InputThumbnail thumbnail, int[] addedStickerFileIds, int width, int height, FormattedText caption, int ttl) {
            this.photo = photo;
            this.thumbnail = thumbnail;
            this.addedStickerFileIds = addedStickerFileIds;
            this.width = width;
            this.height = height;
            this.caption = caption;
            this.ttl = ttl;
        }

        public static final int CONSTRUCTOR = 1648801584;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputMessageSticker extends InputMessageContent {
        public InputFile sticker;
        public InputThumbnail thumbnail;
        public int width;
        public int height;

        public InputMessageSticker() {
        }

        public InputMessageSticker(InputFile sticker, InputThumbnail thumbnail, int width, int height) {
            this.sticker = sticker;
            this.thumbnail = thumbnail;
            this.width = width;
            this.height = height;
        }

        public static final int CONSTRUCTOR = 740776325;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputMessageVideo extends InputMessageContent {
        public InputFile video;
        public InputThumbnail thumbnail;
        public int[] addedStickerFileIds;
        public int duration;
        public int width;
        public int height;
        public boolean supportsStreaming;
        public FormattedText caption;
        public int ttl;

        public InputMessageVideo() {
        }

        public InputMessageVideo(InputFile video, InputThumbnail thumbnail, int[] addedStickerFileIds, int duration, int width, int height, boolean supportsStreaming, FormattedText caption, int ttl) {
            this.video = video;
            this.thumbnail = thumbnail;
            this.addedStickerFileIds = addedStickerFileIds;
            this.duration = duration;
            this.width = width;
            this.height = height;
            this.supportsStreaming = supportsStreaming;
            this.caption = caption;
            this.ttl = ttl;
        }

        public static final int CONSTRUCTOR = -2108486755;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputMessageVideoNote extends InputMessageContent {
        public InputFile videoNote;
        public InputThumbnail thumbnail;
        public int duration;
        public int length;

        public InputMessageVideoNote() {
        }

        public InputMessageVideoNote(InputFile videoNote, InputThumbnail thumbnail, int duration, int length) {
            this.videoNote = videoNote;
            this.thumbnail = thumbnail;
            this.duration = duration;
            this.length = length;
        }

        public static final int CONSTRUCTOR = 279108859;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputMessageVoiceNote extends InputMessageContent {
        public InputFile voiceNote;
        public int duration;
        public byte[] waveform;
        public FormattedText caption;

        public InputMessageVoiceNote() {
        }

        public InputMessageVoiceNote(InputFile voiceNote, int duration, byte[] waveform, FormattedText caption) {
            this.voiceNote = voiceNote;
            this.duration = duration;
            this.waveform = waveform;
            this.caption = caption;
        }

        public static final int CONSTRUCTOR = 2136519657;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputMessageLocation extends InputMessageContent {
        public Location location;
        public int livePeriod;

        public InputMessageLocation() {
        }

        public InputMessageLocation(Location location, int livePeriod) {
            this.location = location;
            this.livePeriod = livePeriod;
        }

        public static final int CONSTRUCTOR = -1624179655;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputMessageVenue extends InputMessageContent {
        public Venue venue;

        public InputMessageVenue() {
        }

        public InputMessageVenue(Venue venue) {
            this.venue = venue;
        }

        public static final int CONSTRUCTOR = 1447926269;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputMessageContact extends InputMessageContent {
        public Contact contact;

        public InputMessageContact() {
        }

        public InputMessageContact(Contact contact) {
            this.contact = contact;
        }

        public static final int CONSTRUCTOR = -982446849;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputMessageGame extends InputMessageContent {
        public int botUserId;
        public String gameShortName;

        public InputMessageGame() {
        }

        public InputMessageGame(int botUserId, String gameShortName) {
            this.botUserId = botUserId;
            this.gameShortName = gameShortName;
        }

        public static final int CONSTRUCTOR = -1728000914;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputMessageInvoice extends InputMessageContent {
        public Invoice invoice;
        public String title;
        public String description;
        public String photoUrl;
        public int photoSize;
        public int photoWidth;
        public int photoHeight;
        public byte[] payload;
        public String providerToken;
        public String providerData;
        public String startParameter;

        public InputMessageInvoice() {
        }

        public InputMessageInvoice(Invoice invoice, String title, String description, String photoUrl, int photoSize, int photoWidth, int photoHeight, byte[] payload, String providerToken, String providerData, String startParameter) {
            this.invoice = invoice;
            this.title = title;
            this.description = description;
            this.photoUrl = photoUrl;
            this.photoSize = photoSize;
            this.photoWidth = photoWidth;
            this.photoHeight = photoHeight;
            this.payload = payload;
            this.providerToken = providerToken;
            this.providerData = providerData;
            this.startParameter = startParameter;
        }

        public static final int CONSTRUCTOR = 1038812175;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputMessageForwarded extends InputMessageContent {
        public long fromChatId;
        public long messageId;
        public boolean inGameShare;

        public InputMessageForwarded() {
        }

        public InputMessageForwarded(long fromChatId, long messageId, boolean inGameShare) {
            this.fromChatId = fromChatId;
            this.messageId = messageId;
            this.inGameShare = inGameShare;
        }

        public static final int CONSTRUCTOR = 1561363198;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class InputPassportElement extends Object {
    }

    public static class InputPassportElementPersonalDetails extends InputPassportElement {
        public PersonalDetails personalDetails;

        public InputPassportElementPersonalDetails() {
        }

        public InputPassportElementPersonalDetails(PersonalDetails personalDetails) {
            this.personalDetails = personalDetails;
        }

        public static final int CONSTRUCTOR = 164791359;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputPassportElementPassport extends InputPassportElement {
        public InputIdentityDocument passport;

        public InputPassportElementPassport() {
        }

        public InputPassportElementPassport(InputIdentityDocument passport) {
            this.passport = passport;
        }

        public static final int CONSTRUCTOR = -497011356;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputPassportElementDriverLicense extends InputPassportElement {
        public InputIdentityDocument driverLicense;

        public InputPassportElementDriverLicense() {
        }

        public InputPassportElementDriverLicense(InputIdentityDocument driverLicense) {
            this.driverLicense = driverLicense;
        }

        public static final int CONSTRUCTOR = 304813264;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputPassportElementIdentityCard extends InputPassportElement {
        public InputIdentityDocument identityCard;

        public InputPassportElementIdentityCard() {
        }

        public InputPassportElementIdentityCard(InputIdentityDocument identityCard) {
            this.identityCard = identityCard;
        }

        public static final int CONSTRUCTOR = -9963390;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputPassportElementInternalPassport extends InputPassportElement {
        public InputIdentityDocument internalPassport;

        public InputPassportElementInternalPassport() {
        }

        public InputPassportElementInternalPassport(InputIdentityDocument internalPassport) {
            this.internalPassport = internalPassport;
        }

        public static final int CONSTRUCTOR = 715360043;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputPassportElementAddress extends InputPassportElement {
        public Address address;

        public InputPassportElementAddress() {
        }

        public InputPassportElementAddress(Address address) {
            this.address = address;
        }

        public static final int CONSTRUCTOR = 461630480;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputPassportElementUtilityBill extends InputPassportElement {
        public InputPersonalDocument utilityBill;

        public InputPassportElementUtilityBill() {
        }

        public InputPassportElementUtilityBill(InputPersonalDocument utilityBill) {
            this.utilityBill = utilityBill;
        }

        public static final int CONSTRUCTOR = 1389203841;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputPassportElementBankStatement extends InputPassportElement {
        public InputPersonalDocument bankStatement;

        public InputPassportElementBankStatement() {
        }

        public InputPassportElementBankStatement(InputPersonalDocument bankStatement) {
            this.bankStatement = bankStatement;
        }

        public static final int CONSTRUCTOR = -26585208;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputPassportElementRentalAgreement extends InputPassportElement {
        public InputPersonalDocument rentalAgreement;

        public InputPassportElementRentalAgreement() {
        }

        public InputPassportElementRentalAgreement(InputPersonalDocument rentalAgreement) {
            this.rentalAgreement = rentalAgreement;
        }

        public static final int CONSTRUCTOR = 1736154155;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputPassportElementPassportRegistration extends InputPassportElement {
        public InputPersonalDocument passportRegistration;

        public InputPassportElementPassportRegistration() {
        }

        public InputPassportElementPassportRegistration(InputPersonalDocument passportRegistration) {
            this.passportRegistration = passportRegistration;
        }

        public static final int CONSTRUCTOR = 1314562128;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputPassportElementTemporaryRegistration extends InputPassportElement {
        public InputPersonalDocument temporaryRegistration;

        public InputPassportElementTemporaryRegistration() {
        }

        public InputPassportElementTemporaryRegistration(InputPersonalDocument temporaryRegistration) {
            this.temporaryRegistration = temporaryRegistration;
        }

        public static final int CONSTRUCTOR = -1913238047;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputPassportElementPhoneNumber extends InputPassportElement {
        public String phoneNumber;

        public InputPassportElementPhoneNumber() {
        }

        public InputPassportElementPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public static final int CONSTRUCTOR = 1319357497;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputPassportElementEmailAddress extends InputPassportElement {
        public String emailAddress;

        public InputPassportElementEmailAddress() {
        }

        public InputPassportElementEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public static final int CONSTRUCTOR = -248605659;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputPassportElementError extends Object {
        public PassportElementType type;
        public String message;
        public InputPassportElementErrorSource source;

        public InputPassportElementError() {
        }

        public InputPassportElementError(PassportElementType type, String message, InputPassportElementErrorSource source) {
            this.type = type;
            this.message = message;
            this.source = source;
        }

        public static final int CONSTRUCTOR = 285756898;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class InputPassportElementErrorSource extends Object {
    }

    public static class InputPassportElementErrorSourceUnspecified extends InputPassportElementErrorSource {
        public byte[] elementHash;

        public InputPassportElementErrorSourceUnspecified() {
        }

        public InputPassportElementErrorSourceUnspecified(byte[] elementHash) {
            this.elementHash = elementHash;
        }

        public static final int CONSTRUCTOR = 267230319;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputPassportElementErrorSourceDataField extends InputPassportElementErrorSource {
        public String fieldName;
        public byte[] dataHash;

        public InputPassportElementErrorSourceDataField() {
        }

        public InputPassportElementErrorSourceDataField(String fieldName, byte[] dataHash) {
            this.fieldName = fieldName;
            this.dataHash = dataHash;
        }

        public static final int CONSTRUCTOR = -426795002;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputPassportElementErrorSourceFrontSide extends InputPassportElementErrorSource {
        public byte[] fileHash;

        public InputPassportElementErrorSourceFrontSide() {
        }

        public InputPassportElementErrorSourceFrontSide(byte[] fileHash) {
            this.fileHash = fileHash;
        }

        public static final int CONSTRUCTOR = 588023741;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputPassportElementErrorSourceReverseSide extends InputPassportElementErrorSource {
        public byte[] fileHash;

        public InputPassportElementErrorSourceReverseSide() {
        }

        public InputPassportElementErrorSourceReverseSide(byte[] fileHash) {
            this.fileHash = fileHash;
        }

        public static final int CONSTRUCTOR = 413072891;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputPassportElementErrorSourceSelfie extends InputPassportElementErrorSource {
        public byte[] fileHash;

        public InputPassportElementErrorSourceSelfie() {
        }

        public InputPassportElementErrorSourceSelfie(byte[] fileHash) {
            this.fileHash = fileHash;
        }

        public static final int CONSTRUCTOR = -773575528;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputPassportElementErrorSourceTranslationFile extends InputPassportElementErrorSource {
        public byte[] fileHash;

        public InputPassportElementErrorSourceTranslationFile() {
        }

        public InputPassportElementErrorSourceTranslationFile(byte[] fileHash) {
            this.fileHash = fileHash;
        }

        public static final int CONSTRUCTOR = 505842299;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputPassportElementErrorSourceTranslationFiles extends InputPassportElementErrorSource {
        public byte[][] fileHashes;

        public InputPassportElementErrorSourceTranslationFiles() {
        }

        public InputPassportElementErrorSourceTranslationFiles(byte[][] fileHashes) {
            this.fileHashes = fileHashes;
        }

        public static final int CONSTRUCTOR = -527254048;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputPassportElementErrorSourceFile extends InputPassportElementErrorSource {
        public byte[] fileHash;

        public InputPassportElementErrorSourceFile() {
        }

        public InputPassportElementErrorSourceFile(byte[] fileHash) {
            this.fileHash = fileHash;
        }

        public static final int CONSTRUCTOR = -298492469;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputPassportElementErrorSourceFiles extends InputPassportElementErrorSource {
        public byte[][] fileHashes;

        public InputPassportElementErrorSourceFiles() {
        }

        public InputPassportElementErrorSourceFiles(byte[][] fileHashes) {
            this.fileHashes = fileHashes;
        }

        public static final int CONSTRUCTOR = -2008541640;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputPersonalDocument extends Object {
        public InputFile[] files;
        public InputFile[] translation;

        public InputPersonalDocument() {
        }

        public InputPersonalDocument(InputFile[] files, InputFile[] translation) {
            this.files = files;
            this.translation = translation;
        }

        public static final int CONSTRUCTOR = 1676966826;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputSticker extends Object {
        public InputFile pngSticker;
        public String emojis;
        public MaskPosition maskPosition;

        public InputSticker() {
        }

        public InputSticker(InputFile pngSticker, String emojis, MaskPosition maskPosition) {
            this.pngSticker = pngSticker;
            this.emojis = emojis;
            this.maskPosition = maskPosition;
        }

        public static final int CONSTRUCTOR = -1998602205;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class InputThumbnail extends Object {
        public InputFile thumbnail;
        public int width;
        public int height;

        public InputThumbnail() {
        }

        public InputThumbnail(InputFile thumbnail, int width, int height) {
            this.thumbnail = thumbnail;
            this.width = width;
            this.height = height;
        }

        public static final int CONSTRUCTOR = 1582387236;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Invoice extends Object {
        public String currency;
        public LabeledPricePart[] priceParts;
        public boolean isTest;
        public boolean needName;
        public boolean needPhoneNumber;
        public boolean needEmailAddress;
        public boolean needShippingAddress;
        public boolean sendPhoneNumberToProvider;
        public boolean sendEmailAddressToProvider;
        public boolean isFlexible;

        public Invoice() {
        }

        public Invoice(String currency, LabeledPricePart[] priceParts, boolean isTest, boolean needName, boolean needPhoneNumber, boolean needEmailAddress, boolean needShippingAddress, boolean sendPhoneNumberToProvider, boolean sendEmailAddressToProvider, boolean isFlexible) {
            this.currency = currency;
            this.priceParts = priceParts;
            this.isTest = isTest;
            this.needName = needName;
            this.needPhoneNumber = needPhoneNumber;
            this.needEmailAddress = needEmailAddress;
            this.needShippingAddress = needShippingAddress;
            this.sendPhoneNumberToProvider = sendPhoneNumberToProvider;
            this.sendEmailAddressToProvider = sendEmailAddressToProvider;
            this.isFlexible = isFlexible;
        }

        public static final int CONSTRUCTOR = -368451690;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class KeyboardButton extends Object {
        public String text;
        public KeyboardButtonType type;

        public KeyboardButton() {
        }

        public KeyboardButton(String text, KeyboardButtonType type) {
            this.text = text;
            this.type = type;
        }

        public static final int CONSTRUCTOR = -2069836172;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class KeyboardButtonType extends Object {
    }

    public static class KeyboardButtonTypeText extends KeyboardButtonType {

        public KeyboardButtonTypeText() {
        }

        public static final int CONSTRUCTOR = -1773037256;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class KeyboardButtonTypeRequestPhoneNumber extends KeyboardButtonType {

        public KeyboardButtonTypeRequestPhoneNumber() {
        }

        public static final int CONSTRUCTOR = -1529235527;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class KeyboardButtonTypeRequestLocation extends KeyboardButtonType {

        public KeyboardButtonTypeRequestLocation() {
        }

        public static final int CONSTRUCTOR = -125661955;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class LabeledPricePart extends Object {
        public String label;
        public long amount;

        public LabeledPricePart() {
        }

        public LabeledPricePart(String label, long amount) {
            this.label = label;
            this.amount = amount;
        }

        public static final int CONSTRUCTOR = 552789798;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class LanguagePackInfo extends Object {
        public String id;
        public String name;
        public String nativeName;
        public int localStringCount;

        public LanguagePackInfo() {
        }

        public LanguagePackInfo(String id, String name, String nativeName, int localStringCount) {
            this.id = id;
            this.name = name;
            this.nativeName = nativeName;
            this.localStringCount = localStringCount;
        }

        public static final int CONSTRUCTOR = 1716484242;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class LanguagePackString extends Object {
        public String key;
        public LanguagePackStringValue value;

        public LanguagePackString() {
        }

        public LanguagePackString(String key, LanguagePackStringValue value) {
            this.key = key;
            this.value = value;
        }

        public static final int CONSTRUCTOR = 1307632736;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class LanguagePackStringValue extends Object {
    }

    public static class LanguagePackStringValueOrdinary extends LanguagePackStringValue {
        public String value;

        public LanguagePackStringValueOrdinary() {
        }

        public LanguagePackStringValueOrdinary(String value) {
            this.value = value;
        }

        public static final int CONSTRUCTOR = -249256352;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class LanguagePackStringValuePluralized extends LanguagePackStringValue {
        public String zeroValue;
        public String oneValue;
        public String twoValue;
        public String fewValue;
        public String manyValue;
        public String otherValue;

        public LanguagePackStringValuePluralized() {
        }

        public LanguagePackStringValuePluralized(String zeroValue, String oneValue, String twoValue, String fewValue, String manyValue, String otherValue) {
            this.zeroValue = zeroValue;
            this.oneValue = oneValue;
            this.twoValue = twoValue;
            this.fewValue = fewValue;
            this.manyValue = manyValue;
            this.otherValue = otherValue;
        }

        public static final int CONSTRUCTOR = 1906840261;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class LanguagePackStringValueDeleted extends LanguagePackStringValue {

        public LanguagePackStringValueDeleted() {
        }

        public static final int CONSTRUCTOR = 1834792698;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class LanguagePackStrings extends Object {
        public LanguagePackString[] strings;

        public LanguagePackStrings() {
        }

        public LanguagePackStrings(LanguagePackString[] strings) {
            this.strings = strings;
        }

        public static final int CONSTRUCTOR = 1172082922;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class LinkState extends Object {
    }

    public static class LinkStateNone extends LinkState {

        public LinkStateNone() {
        }

        public static final int CONSTRUCTOR = 951430287;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class LinkStateKnowsPhoneNumber extends LinkState {

        public LinkStateKnowsPhoneNumber() {
        }

        public static final int CONSTRUCTOR = 380898199;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class LinkStateIsContact extends LinkState {

        public LinkStateIsContact() {
        }

        public static final int CONSTRUCTOR = -1000499465;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class LocalFile extends Object {
        public String path;
        public boolean canBeDownloaded;
        public boolean canBeDeleted;
        public boolean isDownloadingActive;
        public boolean isDownloadingCompleted;
        public int downloadedPrefixSize;
        public int downloadedSize;

        public LocalFile() {
        }

        public LocalFile(String path, boolean canBeDownloaded, boolean canBeDeleted, boolean isDownloadingActive, boolean isDownloadingCompleted, int downloadedPrefixSize, int downloadedSize) {
            this.path = path;
            this.canBeDownloaded = canBeDownloaded;
            this.canBeDeleted = canBeDeleted;
            this.isDownloadingActive = isDownloadingActive;
            this.isDownloadingCompleted = isDownloadingCompleted;
            this.downloadedPrefixSize = downloadedPrefixSize;
            this.downloadedSize = downloadedSize;
        }

        public static final int CONSTRUCTOR = 847939462;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class LocalizationTargetInfo extends Object {
        public LanguagePackInfo[] languagePacks;

        public LocalizationTargetInfo() {
        }

        public LocalizationTargetInfo(LanguagePackInfo[] languagePacks) {
            this.languagePacks = languagePacks;
        }

        public static final int CONSTRUCTOR = -2048670809;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Location extends Object {
        public double latitude;
        public double longitude;

        public Location() {
        }

        public Location(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public static final int CONSTRUCTOR = 749028016;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class MaskPoint extends Object {
    }

    public static class MaskPointForehead extends MaskPoint {

        public MaskPointForehead() {
        }

        public static final int CONSTRUCTOR = 1027512005;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MaskPointEyes extends MaskPoint {

        public MaskPointEyes() {
        }

        public static final int CONSTRUCTOR = 1748310861;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MaskPointMouth extends MaskPoint {

        public MaskPointMouth() {
        }

        public static final int CONSTRUCTOR = 411773406;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MaskPointChin extends MaskPoint {

        public MaskPointChin() {
        }

        public static final int CONSTRUCTOR = 534995335;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MaskPosition extends Object {
        public MaskPoint point;
        public double xShift;
        public double yShift;
        public double scale;

        public MaskPosition() {
        }

        public MaskPosition(MaskPoint point, double xShift, double yShift, double scale) {
            this.point = point;
            this.xShift = xShift;
            this.yShift = yShift;
            this.scale = scale;
        }

        public static final int CONSTRUCTOR = -2097433026;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Message extends Object {
        public long id;
        public int senderUserId;
        public long chatId;
        public MessageSendingState sendingState;
        public boolean isOutgoing;
        public boolean canBeEdited;
        public boolean canBeForwarded;
        public boolean canBeDeletedOnlyForSelf;
        public boolean canBeDeletedForAllUsers;
        public boolean isChannelPost;
        public boolean containsUnreadMention;
        public int date;
        public int editDate;
        public MessageForwardInfo forwardInfo;
        public long replyToMessageId;
        public int ttl;
        public double ttlExpiresIn;
        public int viaBotUserId;
        public String authorSignature;
        public int views;
        public long mediaAlbumId;
        public MessageContent content;
        public ReplyMarkup replyMarkup;

        public Message() {
        }

        public Message(long id, int senderUserId, long chatId, MessageSendingState sendingState, boolean isOutgoing, boolean canBeEdited, boolean canBeForwarded, boolean canBeDeletedOnlyForSelf, boolean canBeDeletedForAllUsers, boolean isChannelPost, boolean containsUnreadMention, int date, int editDate, MessageForwardInfo forwardInfo, long replyToMessageId, int ttl, double ttlExpiresIn, int viaBotUserId, String authorSignature, int views, long mediaAlbumId, MessageContent content, ReplyMarkup replyMarkup) {
            this.id = id;
            this.senderUserId = senderUserId;
            this.chatId = chatId;
            this.sendingState = sendingState;
            this.isOutgoing = isOutgoing;
            this.canBeEdited = canBeEdited;
            this.canBeForwarded = canBeForwarded;
            this.canBeDeletedOnlyForSelf = canBeDeletedOnlyForSelf;
            this.canBeDeletedForAllUsers = canBeDeletedForAllUsers;
            this.isChannelPost = isChannelPost;
            this.containsUnreadMention = containsUnreadMention;
            this.date = date;
            this.editDate = editDate;
            this.forwardInfo = forwardInfo;
            this.replyToMessageId = replyToMessageId;
            this.ttl = ttl;
            this.ttlExpiresIn = ttlExpiresIn;
            this.viaBotUserId = viaBotUserId;
            this.authorSignature = authorSignature;
            this.views = views;
            this.mediaAlbumId = mediaAlbumId;
            this.content = content;
            this.replyMarkup = replyMarkup;
        }

        public static final int CONSTRUCTOR = -675737627;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class MessageContent extends Object {
    }

    public static class MessageText extends MessageContent {
        public FormattedText text;
        public WebPage webPage;

        public MessageText() {
        }

        public MessageText(FormattedText text, WebPage webPage) {
            this.text = text;
            this.webPage = webPage;
        }

        public static final int CONSTRUCTOR = 1989037971;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageAnimation extends MessageContent {
        public Animation animation;
        public FormattedText caption;
        public boolean isSecret;

        public MessageAnimation() {
        }

        public MessageAnimation(Animation animation, FormattedText caption, boolean isSecret) {
            this.animation = animation;
            this.caption = caption;
            this.isSecret = isSecret;
        }

        public static final int CONSTRUCTOR = 1306939396;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageAudio extends MessageContent {
        public Audio audio;
        public FormattedText caption;

        public MessageAudio() {
        }

        public MessageAudio(Audio audio, FormattedText caption) {
            this.audio = audio;
            this.caption = caption;
        }

        public static final int CONSTRUCTOR = 276722716;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageDocument extends MessageContent {
        public Document document;
        public FormattedText caption;

        public MessageDocument() {
        }

        public MessageDocument(Document document, FormattedText caption) {
            this.document = document;
            this.caption = caption;
        }

        public static final int CONSTRUCTOR = 596945783;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessagePhoto extends MessageContent {
        public Photo photo;
        public FormattedText caption;
        public boolean isSecret;

        public MessagePhoto() {
        }

        public MessagePhoto(Photo photo, FormattedText caption, boolean isSecret) {
            this.photo = photo;
            this.caption = caption;
            this.isSecret = isSecret;
        }

        public static final int CONSTRUCTOR = -1851395174;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageExpiredPhoto extends MessageContent {

        public MessageExpiredPhoto() {
        }

        public static final int CONSTRUCTOR = -1404641801;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageSticker extends MessageContent {
        public Sticker sticker;

        public MessageSticker() {
        }

        public MessageSticker(Sticker sticker) {
            this.sticker = sticker;
        }

        public static final int CONSTRUCTOR = 1779022878;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageVideo extends MessageContent {
        public Video video;
        public FormattedText caption;
        public boolean isSecret;

        public MessageVideo() {
        }

        public MessageVideo(Video video, FormattedText caption, boolean isSecret) {
            this.video = video;
            this.caption = caption;
            this.isSecret = isSecret;
        }

        public static final int CONSTRUCTOR = 2021281344;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageExpiredVideo extends MessageContent {

        public MessageExpiredVideo() {
        }

        public static final int CONSTRUCTOR = -1212209981;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageVideoNote extends MessageContent {
        public VideoNote videoNote;
        public boolean isViewed;
        public boolean isSecret;

        public MessageVideoNote() {
        }

        public MessageVideoNote(VideoNote videoNote, boolean isViewed, boolean isSecret) {
            this.videoNote = videoNote;
            this.isViewed = isViewed;
            this.isSecret = isSecret;
        }

        public static final int CONSTRUCTOR = 963323014;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageVoiceNote extends MessageContent {
        public VoiceNote voiceNote;
        public FormattedText caption;
        public boolean isListened;

        public MessageVoiceNote() {
        }

        public MessageVoiceNote(VoiceNote voiceNote, FormattedText caption, boolean isListened) {
            this.voiceNote = voiceNote;
            this.caption = caption;
            this.isListened = isListened;
        }

        public static final int CONSTRUCTOR = 527777781;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageLocation extends MessageContent {
        public Location location;
        public int livePeriod;
        public int expiresIn;

        public MessageLocation() {
        }

        public MessageLocation(Location location, int livePeriod, int expiresIn) {
            this.location = location;
            this.livePeriod = livePeriod;
            this.expiresIn = expiresIn;
        }

        public static final int CONSTRUCTOR = -1301887786;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageVenue extends MessageContent {
        public Venue venue;

        public MessageVenue() {
        }

        public MessageVenue(Venue venue) {
            this.venue = venue;
        }

        public static final int CONSTRUCTOR = -2146492043;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageContact extends MessageContent {
        public Contact contact;

        public MessageContact() {
        }

        public MessageContact(Contact contact) {
            this.contact = contact;
        }

        public static final int CONSTRUCTOR = -512684966;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageGame extends MessageContent {
        public Game game;

        public MessageGame() {
        }

        public MessageGame(Game game) {
            this.game = game;
        }

        public static final int CONSTRUCTOR = -69441162;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageInvoice extends MessageContent {
        public String title;
        public String description;
        public Photo photo;
        public String currency;
        public long totalAmount;
        public String startParameter;
        public boolean isTest;
        public boolean needShippingAddress;
        public long receiptMessageId;

        public MessageInvoice() {
        }

        public MessageInvoice(String title, String description, Photo photo, String currency, long totalAmount, String startParameter, boolean isTest, boolean needShippingAddress, long receiptMessageId) {
            this.title = title;
            this.description = description;
            this.photo = photo;
            this.currency = currency;
            this.totalAmount = totalAmount;
            this.startParameter = startParameter;
            this.isTest = isTest;
            this.needShippingAddress = needShippingAddress;
            this.receiptMessageId = receiptMessageId;
        }

        public static final int CONSTRUCTOR = -1916671476;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageCall extends MessageContent {
        public CallDiscardReason discardReason;
        public int duration;

        public MessageCall() {
        }

        public MessageCall(CallDiscardReason discardReason, int duration) {
            this.discardReason = discardReason;
            this.duration = duration;
        }

        public static final int CONSTRUCTOR = 366512596;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageBasicGroupChatCreate extends MessageContent {
        public String title;
        public int[] memberUserIds;

        public MessageBasicGroupChatCreate() {
        }

        public MessageBasicGroupChatCreate(String title, int[] memberUserIds) {
            this.title = title;
            this.memberUserIds = memberUserIds;
        }

        public static final int CONSTRUCTOR = 1575377646;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageSupergroupChatCreate extends MessageContent {
        public String title;

        public MessageSupergroupChatCreate() {
        }

        public MessageSupergroupChatCreate(String title) {
            this.title = title;
        }

        public static final int CONSTRUCTOR = -434325733;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageChatChangeTitle extends MessageContent {
        public String title;

        public MessageChatChangeTitle() {
        }

        public MessageChatChangeTitle(String title) {
            this.title = title;
        }

        public static final int CONSTRUCTOR = 748272449;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageChatChangePhoto extends MessageContent {
        public Photo photo;

        public MessageChatChangePhoto() {
        }

        public MessageChatChangePhoto(Photo photo) {
            this.photo = photo;
        }

        public static final int CONSTRUCTOR = 319630249;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageChatDeletePhoto extends MessageContent {

        public MessageChatDeletePhoto() {
        }

        public static final int CONSTRUCTOR = -184374809;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageChatAddMembers extends MessageContent {
        public int[] memberUserIds;

        public MessageChatAddMembers() {
        }

        public MessageChatAddMembers(int[] memberUserIds) {
            this.memberUserIds = memberUserIds;
        }

        public static final int CONSTRUCTOR = 401228326;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageChatJoinByLink extends MessageContent {

        public MessageChatJoinByLink() {
        }

        public static final int CONSTRUCTOR = 1846493311;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageChatDeleteMember extends MessageContent {
        public int userId;

        public MessageChatDeleteMember() {
        }

        public MessageChatDeleteMember(int userId) {
            this.userId = userId;
        }

        public static final int CONSTRUCTOR = 1164414043;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageChatUpgradeTo extends MessageContent {
        public int supergroupId;

        public MessageChatUpgradeTo() {
        }

        public MessageChatUpgradeTo(int supergroupId) {
            this.supergroupId = supergroupId;
        }

        public static final int CONSTRUCTOR = 1957816681;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageChatUpgradeFrom extends MessageContent {
        public String title;
        public int basicGroupId;

        public MessageChatUpgradeFrom() {
        }

        public MessageChatUpgradeFrom(String title, int basicGroupId) {
            this.title = title;
            this.basicGroupId = basicGroupId;
        }

        public static final int CONSTRUCTOR = 1642272558;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessagePinMessage extends MessageContent {
        public long messageId;

        public MessagePinMessage() {
        }

        public MessagePinMessage(long messageId) {
            this.messageId = messageId;
        }

        public static final int CONSTRUCTOR = 953503801;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageScreenshotTaken extends MessageContent {

        public MessageScreenshotTaken() {
        }

        public static final int CONSTRUCTOR = -1564971605;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageChatSetTtl extends MessageContent {
        public int ttl;

        public MessageChatSetTtl() {
        }

        public MessageChatSetTtl(int ttl) {
            this.ttl = ttl;
        }

        public static final int CONSTRUCTOR = 1810060209;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageCustomServiceAction extends MessageContent {
        public String text;

        public MessageCustomServiceAction() {
        }

        public MessageCustomServiceAction(String text) {
            this.text = text;
        }

        public static final int CONSTRUCTOR = 1435879282;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageGameScore extends MessageContent {
        public long gameMessageId;
        public long gameId;
        public int score;

        public MessageGameScore() {
        }

        public MessageGameScore(long gameMessageId, long gameId, int score) {
            this.gameMessageId = gameMessageId;
            this.gameId = gameId;
            this.score = score;
        }

        public static final int CONSTRUCTOR = 1344904575;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessagePaymentSuccessful extends MessageContent {
        public long invoiceMessageId;
        public String currency;
        public long totalAmount;

        public MessagePaymentSuccessful() {
        }

        public MessagePaymentSuccessful(long invoiceMessageId, String currency, long totalAmount) {
            this.invoiceMessageId = invoiceMessageId;
            this.currency = currency;
            this.totalAmount = totalAmount;
        }

        public static final int CONSTRUCTOR = -595962993;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessagePaymentSuccessfulBot extends MessageContent {
        public long invoiceMessageId;
        public String currency;
        public long totalAmount;
        public byte[] invoicePayload;
        public String shippingOptionId;
        public OrderInfo orderInfo;
        public String telegramPaymentChargeId;
        public String providerPaymentChargeId;

        public MessagePaymentSuccessfulBot() {
        }

        public MessagePaymentSuccessfulBot(long invoiceMessageId, String currency, long totalAmount, byte[] invoicePayload, String shippingOptionId, OrderInfo orderInfo, String telegramPaymentChargeId, String providerPaymentChargeId) {
            this.invoiceMessageId = invoiceMessageId;
            this.currency = currency;
            this.totalAmount = totalAmount;
            this.invoicePayload = invoicePayload;
            this.shippingOptionId = shippingOptionId;
            this.orderInfo = orderInfo;
            this.telegramPaymentChargeId = telegramPaymentChargeId;
            this.providerPaymentChargeId = providerPaymentChargeId;
        }

        public static final int CONSTRUCTOR = -412310696;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageContactRegistered extends MessageContent {

        public MessageContactRegistered() {
        }

        public static final int CONSTRUCTOR = -1502020353;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageWebsiteConnected extends MessageContent {
        public String domainName;

        public MessageWebsiteConnected() {
        }

        public MessageWebsiteConnected(String domainName) {
            this.domainName = domainName;
        }

        public static final int CONSTRUCTOR = -1074551800;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessagePassportDataSent extends MessageContent {
        public PassportElementType[] types;

        public MessagePassportDataSent() {
        }

        public MessagePassportDataSent(PassportElementType[] types) {
            this.types = types;
        }

        public static final int CONSTRUCTOR = 1017405171;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessagePassportDataReceived extends MessageContent {
        public EncryptedPassportElement[] elements;
        public EncryptedCredentials credentials;

        public MessagePassportDataReceived() {
        }

        public MessagePassportDataReceived(EncryptedPassportElement[] elements, EncryptedCredentials credentials) {
            this.elements = elements;
            this.credentials = credentials;
        }

        public static final int CONSTRUCTOR = -1367863624;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageUnsupported extends MessageContent {

        public MessageUnsupported() {
        }

        public static final int CONSTRUCTOR = -1816726139;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class MessageForwardInfo extends Object {
    }

    public static class MessageForwardedFromUser extends MessageForwardInfo {
        public int senderUserId;
        public int date;
        public long forwardedFromChatId;
        public long forwardedFromMessageId;

        public MessageForwardedFromUser() {
        }

        public MessageForwardedFromUser(int senderUserId, int date, long forwardedFromChatId, long forwardedFromMessageId) {
            this.senderUserId = senderUserId;
            this.date = date;
            this.forwardedFromChatId = forwardedFromChatId;
            this.forwardedFromMessageId = forwardedFromMessageId;
        }

        public static final int CONSTRUCTOR = 1004332765;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageForwardedPost extends MessageForwardInfo {
        public long chatId;
        public String authorSignature;
        public int date;
        public long messageId;
        public long forwardedFromChatId;
        public long forwardedFromMessageId;

        public MessageForwardedPost() {
        }

        public MessageForwardedPost(long chatId, String authorSignature, int date, long messageId, long forwardedFromChatId, long forwardedFromMessageId) {
            this.chatId = chatId;
            this.authorSignature = authorSignature;
            this.date = date;
            this.messageId = messageId;
            this.forwardedFromChatId = forwardedFromChatId;
            this.forwardedFromMessageId = forwardedFromMessageId;
        }

        public static final int CONSTRUCTOR = -244050875;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class MessageSendingState extends Object {
    }

    public static class MessageSendingStatePending extends MessageSendingState {

        public MessageSendingStatePending() {
        }

        public static final int CONSTRUCTOR = -1381803582;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class MessageSendingStateFailed extends MessageSendingState {

        public MessageSendingStateFailed() {
        }

        public static final int CONSTRUCTOR = -546610323;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Messages extends Object {
        public int totalCount;
        public Message[] messages;

        public Messages() {
        }

        public Messages(int totalCount, Message[] messages) {
            this.totalCount = totalCount;
            this.messages = messages;
        }

        public static final int CONSTRUCTOR = -16498159;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class NetworkStatistics extends Object {
        public int sinceDate;
        public NetworkStatisticsEntry[] entries;

        public NetworkStatistics() {
        }

        public NetworkStatistics(int sinceDate, NetworkStatisticsEntry[] entries) {
            this.sinceDate = sinceDate;
            this.entries = entries;
        }

        public static final int CONSTRUCTOR = 1615554212;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class NetworkStatisticsEntry extends Object {
    }

    public static class NetworkStatisticsEntryFile extends NetworkStatisticsEntry {
        public FileType fileType;
        public NetworkType networkType;
        public long sentBytes;
        public long receivedBytes;

        public NetworkStatisticsEntryFile() {
        }

        public NetworkStatisticsEntryFile(FileType fileType, NetworkType networkType, long sentBytes, long receivedBytes) {
            this.fileType = fileType;
            this.networkType = networkType;
            this.sentBytes = sentBytes;
            this.receivedBytes = receivedBytes;
        }

        public static final int CONSTRUCTOR = 188452706;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class NetworkStatisticsEntryCall extends NetworkStatisticsEntry {
        public NetworkType networkType;
        public long sentBytes;
        public long receivedBytes;
        public double duration;

        public NetworkStatisticsEntryCall() {
        }

        public NetworkStatisticsEntryCall(NetworkType networkType, long sentBytes, long receivedBytes, double duration) {
            this.networkType = networkType;
            this.sentBytes = sentBytes;
            this.receivedBytes = receivedBytes;
            this.duration = duration;
        }

        public static final int CONSTRUCTOR = 737000365;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class NetworkType extends Object {
    }

    public static class NetworkTypeNone extends NetworkType {

        public NetworkTypeNone() {
        }

        public static final int CONSTRUCTOR = -1971691759;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class NetworkTypeMobile extends NetworkType {

        public NetworkTypeMobile() {
        }

        public static final int CONSTRUCTOR = 819228239;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class NetworkTypeMobileRoaming extends NetworkType {

        public NetworkTypeMobileRoaming() {
        }

        public static final int CONSTRUCTOR = -1435199760;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class NetworkTypeWiFi extends NetworkType {

        public NetworkTypeWiFi() {
        }

        public static final int CONSTRUCTOR = -633872070;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class NetworkTypeOther extends NetworkType {

        public NetworkTypeOther() {
        }

        public static final int CONSTRUCTOR = 1942128539;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class NotificationSettingsScope extends Object {
    }

    public static class NotificationSettingsScopePrivateChats extends NotificationSettingsScope {

        public NotificationSettingsScopePrivateChats() {
        }

        public static final int CONSTRUCTOR = 937446759;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class NotificationSettingsScopeGroupChats extends NotificationSettingsScope {

        public NotificationSettingsScopeGroupChats() {
        }

        public static final int CONSTRUCTOR = 1212142067;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Ok extends Object {

        public Ok() {
        }

        public static final int CONSTRUCTOR = -722616727;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class OptionValue extends Object {
    }

    public static class OptionValueBoolean extends OptionValue {
        public boolean value;

        public OptionValueBoolean() {
        }

        public OptionValueBoolean(boolean value) {
            this.value = value;
        }

        public static final int CONSTRUCTOR = 63135518;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class OptionValueEmpty extends OptionValue {

        public OptionValueEmpty() {
        }

        public static final int CONSTRUCTOR = 918955155;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class OptionValueInteger extends OptionValue {
        public int value;

        public OptionValueInteger() {
        }

        public OptionValueInteger(int value) {
            this.value = value;
        }

        public static final int CONSTRUCTOR = -1400911104;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class OptionValueString extends OptionValue {
        public String value;

        public OptionValueString() {
        }

        public OptionValueString(String value) {
            this.value = value;
        }

        public static final int CONSTRUCTOR = 756248212;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class OrderInfo extends Object {
        public String name;
        public String phoneNumber;
        public String emailAddress;
        public Address shippingAddress;

        public OrderInfo() {
        }

        public OrderInfo(String name, String phoneNumber, String emailAddress, Address shippingAddress) {
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.emailAddress = emailAddress;
            this.shippingAddress = shippingAddress;
        }

        public static final int CONSTRUCTOR = 783997294;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class PageBlock extends Object {
    }

    public static class PageBlockTitle extends PageBlock {
        public RichText title;

        public PageBlockTitle() {
        }

        public PageBlockTitle(RichText title) {
            this.title = title;
        }

        public static final int CONSTRUCTOR = 1629664784;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PageBlockSubtitle extends PageBlock {
        public RichText subtitle;

        public PageBlockSubtitle() {
        }

        public PageBlockSubtitle(RichText subtitle) {
            this.subtitle = subtitle;
        }

        public static final int CONSTRUCTOR = 264524263;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PageBlockAuthorDate extends PageBlock {
        public RichText author;
        public int publishDate;

        public PageBlockAuthorDate() {
        }

        public PageBlockAuthorDate(RichText author, int publishDate) {
            this.author = author;
            this.publishDate = publishDate;
        }

        public static final int CONSTRUCTOR = 1300231184;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PageBlockHeader extends PageBlock {
        public RichText header;

        public PageBlockHeader() {
        }

        public PageBlockHeader(RichText header) {
            this.header = header;
        }

        public static final int CONSTRUCTOR = 1402854811;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PageBlockSubheader extends PageBlock {
        public RichText subheader;

        public PageBlockSubheader() {
        }

        public PageBlockSubheader(RichText subheader) {
            this.subheader = subheader;
        }

        public static final int CONSTRUCTOR = 1263956774;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PageBlockParagraph extends PageBlock {
        public RichText text;

        public PageBlockParagraph() {
        }

        public PageBlockParagraph(RichText text) {
            this.text = text;
        }

        public static final int CONSTRUCTOR = 1182402406;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PageBlockPreformatted extends PageBlock {
        public RichText text;
        public String language;

        public PageBlockPreformatted() {
        }

        public PageBlockPreformatted(RichText text, String language) {
            this.text = text;
            this.language = language;
        }

        public static final int CONSTRUCTOR = -1066346178;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PageBlockFooter extends PageBlock {
        public RichText footer;

        public PageBlockFooter() {
        }

        public PageBlockFooter(RichText footer) {
            this.footer = footer;
        }

        public static final int CONSTRUCTOR = 886429480;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PageBlockDivider extends PageBlock {

        public PageBlockDivider() {
        }

        public static final int CONSTRUCTOR = -618614392;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PageBlockAnchor extends PageBlock {
        public String name;

        public PageBlockAnchor() {
        }

        public PageBlockAnchor(String name) {
            this.name = name;
        }

        public static final int CONSTRUCTOR = -837994576;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PageBlockList extends PageBlock {
        public RichText[] items;
        public boolean isOrdered;

        public PageBlockList() {
        }

        public PageBlockList(RichText[] items, boolean isOrdered) {
            this.items = items;
            this.isOrdered = isOrdered;
        }

        public static final int CONSTRUCTOR = -1752631674;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PageBlockBlockQuote extends PageBlock {
        public RichText text;
        public RichText caption;

        public PageBlockBlockQuote() {
        }

        public PageBlockBlockQuote(RichText text, RichText caption) {
            this.text = text;
            this.caption = caption;
        }

        public static final int CONSTRUCTOR = -37421406;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PageBlockPullQuote extends PageBlock {
        public RichText text;
        public RichText caption;

        public PageBlockPullQuote() {
        }

        public PageBlockPullQuote(RichText text, RichText caption) {
            this.text = text;
            this.caption = caption;
        }

        public static final int CONSTRUCTOR = -1799498665;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PageBlockAnimation extends PageBlock {
        public Animation animation;
        public RichText caption;
        public boolean needAutoplay;

        public PageBlockAnimation() {
        }

        public PageBlockAnimation(Animation animation, RichText caption, boolean needAutoplay) {
            this.animation = animation;
            this.caption = caption;
            this.needAutoplay = needAutoplay;
        }

        public static final int CONSTRUCTOR = 1639478661;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PageBlockAudio extends PageBlock {
        public Audio audio;
        public RichText caption;

        public PageBlockAudio() {
        }

        public PageBlockAudio(Audio audio, RichText caption) {
            this.audio = audio;
            this.caption = caption;
        }

        public static final int CONSTRUCTOR = 1898245855;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PageBlockPhoto extends PageBlock {
        public Photo photo;
        public RichText caption;

        public PageBlockPhoto() {
        }

        public PageBlockPhoto(Photo photo, RichText caption) {
            this.photo = photo;
            this.caption = caption;
        }

        public static final int CONSTRUCTOR = -637181825;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PageBlockVideo extends PageBlock {
        public Video video;
        public RichText caption;
        public boolean needAutoplay;
        public boolean isLooped;

        public PageBlockVideo() {
        }

        public PageBlockVideo(Video video, RichText caption, boolean needAutoplay, boolean isLooped) {
            this.video = video;
            this.caption = caption;
            this.needAutoplay = needAutoplay;
            this.isLooped = isLooped;
        }

        public static final int CONSTRUCTOR = 1610373002;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PageBlockCover extends PageBlock {
        public PageBlock cover;

        public PageBlockCover() {
        }

        public PageBlockCover(PageBlock cover) {
            this.cover = cover;
        }

        public static final int CONSTRUCTOR = 972174080;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PageBlockEmbedded extends PageBlock {
        public String url;
        public String html;
        public Photo posterPhoto;
        public int width;
        public int height;
        public RichText caption;
        public boolean isFullWidth;
        public boolean allowScrolling;

        public PageBlockEmbedded() {
        }

        public PageBlockEmbedded(String url, String html, Photo posterPhoto, int width, int height, RichText caption, boolean isFullWidth, boolean allowScrolling) {
            this.url = url;
            this.html = html;
            this.posterPhoto = posterPhoto;
            this.width = width;
            this.height = height;
            this.caption = caption;
            this.isFullWidth = isFullWidth;
            this.allowScrolling = allowScrolling;
        }

        public static final int CONSTRUCTOR = -211257334;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PageBlockEmbeddedPost extends PageBlock {
        public String url;
        public String author;
        public Photo authorPhoto;
        public int date;
        public PageBlock[] pageBlocks;
        public RichText caption;

        public PageBlockEmbeddedPost() {
        }

        public PageBlockEmbeddedPost(String url, String author, Photo authorPhoto, int date, PageBlock[] pageBlocks, RichText caption) {
            this.url = url;
            this.author = author;
            this.authorPhoto = authorPhoto;
            this.date = date;
            this.pageBlocks = pageBlocks;
            this.caption = caption;
        }

        public static final int CONSTRUCTOR = 1049948772;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PageBlockCollage extends PageBlock {
        public PageBlock[] pageBlocks;
        public RichText caption;

        public PageBlockCollage() {
        }

        public PageBlockCollage(PageBlock[] pageBlocks, RichText caption) {
            this.pageBlocks = pageBlocks;
            this.caption = caption;
        }

        public static final int CONSTRUCTOR = 911142202;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PageBlockSlideshow extends PageBlock {
        public PageBlock[] pageBlocks;
        public RichText caption;

        public PageBlockSlideshow() {
        }

        public PageBlockSlideshow(PageBlock[] pageBlocks, RichText caption) {
            this.pageBlocks = pageBlocks;
            this.caption = caption;
        }

        public static final int CONSTRUCTOR = 178557514;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PageBlockChatLink extends PageBlock {
        public String title;
        public ChatPhoto photo;
        public String username;

        public PageBlockChatLink() {
        }

        public PageBlockChatLink(String title, ChatPhoto photo, String username) {
            this.title = title;
            this.photo = photo;
            this.username = username;
        }

        public static final int CONSTRUCTOR = 214606645;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportAuthorizationForm extends Object {
        public int id;
        public PassportRequiredElement[] requiredElements;
        public PassportElement[] elements;
        public PassportElementError[] errors;
        public String privacyPolicyUrl;

        public PassportAuthorizationForm() {
        }

        public PassportAuthorizationForm(int id, PassportRequiredElement[] requiredElements, PassportElement[] elements, PassportElementError[] errors, String privacyPolicyUrl) {
            this.id = id;
            this.requiredElements = requiredElements;
            this.elements = elements;
            this.errors = errors;
            this.privacyPolicyUrl = privacyPolicyUrl;
        }

        public static final int CONSTRUCTOR = -1780995944;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class PassportElement extends Object {
    }

    public static class PassportElementPersonalDetails extends PassportElement {
        public PersonalDetails personalDetails;

        public PassportElementPersonalDetails() {
        }

        public PassportElementPersonalDetails(PersonalDetails personalDetails) {
            this.personalDetails = personalDetails;
        }

        public static final int CONSTRUCTOR = 1217724035;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementPassport extends PassportElement {
        public IdentityDocument passport;

        public PassportElementPassport() {
        }

        public PassportElementPassport(IdentityDocument passport) {
            this.passport = passport;
        }

        public static final int CONSTRUCTOR = -263985373;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementDriverLicense extends PassportElement {
        public IdentityDocument driverLicense;

        public PassportElementDriverLicense() {
        }

        public PassportElementDriverLicense(IdentityDocument driverLicense) {
            this.driverLicense = driverLicense;
        }

        public static final int CONSTRUCTOR = 1643580589;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementIdentityCard extends PassportElement {
        public IdentityDocument identityCard;

        public PassportElementIdentityCard() {
        }

        public PassportElementIdentityCard(IdentityDocument identityCard) {
            this.identityCard = identityCard;
        }

        public static final int CONSTRUCTOR = 2083775797;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementInternalPassport extends PassportElement {
        public IdentityDocument internalPassport;

        public PassportElementInternalPassport() {
        }

        public PassportElementInternalPassport(IdentityDocument internalPassport) {
            this.internalPassport = internalPassport;
        }

        public static final int CONSTRUCTOR = 36220295;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementAddress extends PassportElement {
        public Address address;

        public PassportElementAddress() {
        }

        public PassportElementAddress(Address address) {
            this.address = address;
        }

        public static final int CONSTRUCTOR = -782625232;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementUtilityBill extends PassportElement {
        public PersonalDocument utilityBill;

        public PassportElementUtilityBill() {
        }

        public PassportElementUtilityBill(PersonalDocument utilityBill) {
            this.utilityBill = utilityBill;
        }

        public static final int CONSTRUCTOR = -234611246;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementBankStatement extends PassportElement {
        public PersonalDocument bankStatement;

        public PassportElementBankStatement() {
        }

        public PassportElementBankStatement(PersonalDocument bankStatement) {
            this.bankStatement = bankStatement;
        }

        public static final int CONSTRUCTOR = -366464408;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementRentalAgreement extends PassportElement {
        public PersonalDocument rentalAgreement;

        public PassportElementRentalAgreement() {
        }

        public PassportElementRentalAgreement(PersonalDocument rentalAgreement) {
            this.rentalAgreement = rentalAgreement;
        }

        public static final int CONSTRUCTOR = -290141400;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementPassportRegistration extends PassportElement {
        public PersonalDocument passportRegistration;

        public PassportElementPassportRegistration() {
        }

        public PassportElementPassportRegistration(PersonalDocument passportRegistration) {
            this.passportRegistration = passportRegistration;
        }

        public static final int CONSTRUCTOR = 618323071;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementTemporaryRegistration extends PassportElement {
        public PersonalDocument temporaryRegistration;

        public PassportElementTemporaryRegistration() {
        }

        public PassportElementTemporaryRegistration(PersonalDocument temporaryRegistration) {
            this.temporaryRegistration = temporaryRegistration;
        }

        public static final int CONSTRUCTOR = 1237626864;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementPhoneNumber extends PassportElement {
        public String phoneNumber;

        public PassportElementPhoneNumber() {
        }

        public PassportElementPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public static final int CONSTRUCTOR = -1320118375;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementEmailAddress extends PassportElement {
        public String emailAddress;

        public PassportElementEmailAddress() {
        }

        public PassportElementEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public static final int CONSTRUCTOR = -1528129531;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementError extends Object {
        public PassportElementType type;
        public String message;
        public PassportElementErrorSource source;

        public PassportElementError() {
        }

        public PassportElementError(PassportElementType type, String message, PassportElementErrorSource source) {
            this.type = type;
            this.message = message;
            this.source = source;
        }

        public static final int CONSTRUCTOR = -1861902395;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class PassportElementErrorSource extends Object {
    }

    public static class PassportElementErrorSourceUnspecified extends PassportElementErrorSource {

        public PassportElementErrorSourceUnspecified() {
        }

        public static final int CONSTRUCTOR = -378320830;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementErrorSourceDataField extends PassportElementErrorSource {
        public String fieldName;

        public PassportElementErrorSourceDataField() {
        }

        public PassportElementErrorSourceDataField(String fieldName) {
            this.fieldName = fieldName;
        }

        public static final int CONSTRUCTOR = -308650776;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementErrorSourceFrontSide extends PassportElementErrorSource {

        public PassportElementErrorSourceFrontSide() {
        }

        public static final int CONSTRUCTOR = 1895658292;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementErrorSourceReverseSide extends PassportElementErrorSource {

        public PassportElementErrorSourceReverseSide() {
        }

        public static final int CONSTRUCTOR = 1918630391;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementErrorSourceSelfie extends PassportElementErrorSource {

        public PassportElementErrorSourceSelfie() {
        }

        public static final int CONSTRUCTOR = -797043672;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementErrorSourceTranslationFile extends PassportElementErrorSource {

        public PassportElementErrorSourceTranslationFile() {
        }

        public static final int CONSTRUCTOR = -643919323;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementErrorSourceTranslationFiles extends PassportElementErrorSource {

        public PassportElementErrorSourceTranslationFiles() {
        }

        public static final int CONSTRUCTOR = 581280796;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementErrorSourceFile extends PassportElementErrorSource {

        public PassportElementErrorSourceFile() {
        }

        public static final int CONSTRUCTOR = -226596202;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementErrorSourceFiles extends PassportElementErrorSource {

        public PassportElementErrorSourceFiles() {
        }

        public static final int CONSTRUCTOR = 1894164178;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class PassportElementType extends Object {
    }

    public static class PassportElementTypePersonalDetails extends PassportElementType {

        public PassportElementTypePersonalDetails() {
        }

        public static final int CONSTRUCTOR = -1032136365;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementTypePassport extends PassportElementType {

        public PassportElementTypePassport() {
        }

        public static final int CONSTRUCTOR = -436360376;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementTypeDriverLicense extends PassportElementType {

        public PassportElementTypeDriverLicense() {
        }

        public static final int CONSTRUCTOR = 1827298379;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementTypeIdentityCard extends PassportElementType {

        public PassportElementTypeIdentityCard() {
        }

        public static final int CONSTRUCTOR = -502356132;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementTypeInternalPassport extends PassportElementType {

        public PassportElementTypeInternalPassport() {
        }

        public static final int CONSTRUCTOR = -793781959;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementTypeAddress extends PassportElementType {

        public PassportElementTypeAddress() {
        }

        public static final int CONSTRUCTOR = 496327874;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementTypeUtilityBill extends PassportElementType {

        public PassportElementTypeUtilityBill() {
        }

        public static final int CONSTRUCTOR = 627084906;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementTypeBankStatement extends PassportElementType {

        public PassportElementTypeBankStatement() {
        }

        public static final int CONSTRUCTOR = 574095667;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementTypeRentalAgreement extends PassportElementType {

        public PassportElementTypeRentalAgreement() {
        }

        public static final int CONSTRUCTOR = -2060583280;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementTypePassportRegistration extends PassportElementType {

        public PassportElementTypePassportRegistration() {
        }

        public static final int CONSTRUCTOR = -159478209;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementTypeTemporaryRegistration extends PassportElementType {

        public PassportElementTypeTemporaryRegistration() {
        }

        public static final int CONSTRUCTOR = 1092498527;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementTypePhoneNumber extends PassportElementType {

        public PassportElementTypePhoneNumber() {
        }

        public static final int CONSTRUCTOR = -995361172;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElementTypeEmailAddress extends PassportElementType {

        public PassportElementTypeEmailAddress() {
        }

        public static final int CONSTRUCTOR = -79321405;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportElements extends Object {
        public PassportElement[] elements;

        public PassportElements() {
        }

        public PassportElements(PassportElement[] elements) {
            this.elements = elements;
        }

        public static final int CONSTRUCTOR = 1264617556;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportRequiredElement extends Object {
        public PassportSuitableElement[] suitableElements;

        public PassportRequiredElement() {
        }

        public PassportRequiredElement(PassportSuitableElement[] suitableElements) {
            this.suitableElements = suitableElements;
        }

        public static final int CONSTRUCTOR = -1983641651;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PassportSuitableElement extends Object {
        public PassportElementType type;
        public boolean isSelfieRequired;
        public boolean isTranslationRequired;
        public boolean isNativeNameRequired;

        public PassportSuitableElement() {
        }

        public PassportSuitableElement(PassportElementType type, boolean isSelfieRequired, boolean isTranslationRequired, boolean isNativeNameRequired) {
            this.type = type;
            this.isSelfieRequired = isSelfieRequired;
            this.isTranslationRequired = isTranslationRequired;
            this.isNativeNameRequired = isNativeNameRequired;
        }

        public static final int CONSTRUCTOR = -789019876;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PasswordState extends Object {
        public boolean hasPassword;
        public String passwordHint;
        public boolean hasRecoveryEmailAddress;
        public boolean hasPassportData;
        public String unconfirmedRecoveryEmailAddressPattern;

        public PasswordState() {
        }

        public PasswordState(boolean hasPassword, String passwordHint, boolean hasRecoveryEmailAddress, boolean hasPassportData, String unconfirmedRecoveryEmailAddressPattern) {
            this.hasPassword = hasPassword;
            this.passwordHint = passwordHint;
            this.hasRecoveryEmailAddress = hasRecoveryEmailAddress;
            this.hasPassportData = hasPassportData;
            this.unconfirmedRecoveryEmailAddressPattern = unconfirmedRecoveryEmailAddressPattern;
        }

        public static final int CONSTRUCTOR = -1209872059;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PaymentForm extends Object {
        public Invoice invoice;
        public String url;
        public PaymentsProviderStripe paymentsProvider;
        public OrderInfo savedOrderInfo;
        public SavedCredentials savedCredentials;
        public boolean canSaveCredentials;
        public boolean needPassword;

        public PaymentForm() {
        }

        public PaymentForm(Invoice invoice, String url, PaymentsProviderStripe paymentsProvider, OrderInfo savedOrderInfo, SavedCredentials savedCredentials, boolean canSaveCredentials, boolean needPassword) {
            this.invoice = invoice;
            this.url = url;
            this.paymentsProvider = paymentsProvider;
            this.savedOrderInfo = savedOrderInfo;
            this.savedCredentials = savedCredentials;
            this.canSaveCredentials = canSaveCredentials;
            this.needPassword = needPassword;
        }

        public static final int CONSTRUCTOR = -200418230;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PaymentReceipt extends Object {
        public int date;
        public int paymentsProviderUserId;
        public Invoice invoice;
        public OrderInfo orderInfo;
        public ShippingOption shippingOption;
        public String credentialsTitle;

        public PaymentReceipt() {
        }

        public PaymentReceipt(int date, int paymentsProviderUserId, Invoice invoice, OrderInfo orderInfo, ShippingOption shippingOption, String credentialsTitle) {
            this.date = date;
            this.paymentsProviderUserId = paymentsProviderUserId;
            this.invoice = invoice;
            this.orderInfo = orderInfo;
            this.shippingOption = shippingOption;
            this.credentialsTitle = credentialsTitle;
        }

        public static final int CONSTRUCTOR = -1171223545;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PaymentResult extends Object {
        public boolean success;
        public String verificationUrl;

        public PaymentResult() {
        }

        public PaymentResult(boolean success, String verificationUrl) {
            this.success = success;
            this.verificationUrl = verificationUrl;
        }

        public static final int CONSTRUCTOR = -804263843;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PaymentsProviderStripe extends Object {
        public String publishableKey;
        public boolean needCountry;
        public boolean needPostalCode;
        public boolean needCardholderName;

        public PaymentsProviderStripe() {
        }

        public PaymentsProviderStripe(String publishableKey, boolean needCountry, boolean needPostalCode, boolean needCardholderName) {
            this.publishableKey = publishableKey;
            this.needCountry = needCountry;
            this.needPostalCode = needPostalCode;
            this.needCardholderName = needCardholderName;
        }

        public static final int CONSTRUCTOR = 1090791032;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PersonalDetails extends Object {
        public String firstName;
        public String middleName;
        public String lastName;
        public String nativeFirstName;
        public String nativeMiddleName;
        public String nativeLastName;
        public Date birthdate;
        public String gender;
        public String countryCode;
        public String residenceCountryCode;

        public PersonalDetails() {
        }

        public PersonalDetails(String firstName, String middleName, String lastName, String nativeFirstName, String nativeMiddleName, String nativeLastName, Date birthdate, String gender, String countryCode, String residenceCountryCode) {
            this.firstName = firstName;
            this.middleName = middleName;
            this.lastName = lastName;
            this.nativeFirstName = nativeFirstName;
            this.nativeMiddleName = nativeMiddleName;
            this.nativeLastName = nativeLastName;
            this.birthdate = birthdate;
            this.gender = gender;
            this.countryCode = countryCode;
            this.residenceCountryCode = residenceCountryCode;
        }

        public static final int CONSTRUCTOR = -1061656137;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PersonalDocument extends Object {
        public DatedFile[] files;
        public DatedFile[] translation;

        public PersonalDocument() {
        }

        public PersonalDocument(DatedFile[] files, DatedFile[] translation) {
            this.files = files;
            this.translation = translation;
        }

        public static final int CONSTRUCTOR = -1011634661;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Photo extends Object {
        public long id;
        public boolean hasStickers;
        public PhotoSize[] sizes;

        public Photo() {
        }

        public Photo(long id, boolean hasStickers, PhotoSize[] sizes) {
            this.id = id;
            this.hasStickers = hasStickers;
            this.sizes = sizes;
        }

        public static final int CONSTRUCTOR = -1949521787;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PhotoSize extends Object {
        public String type;
        public File photo;
        public int width;
        public int height;

        public PhotoSize() {
        }

        public PhotoSize(String type, File photo, int width, int height) {
            this.type = type;
            this.photo = photo;
            this.width = width;
            this.height = height;
        }

        public static final int CONSTRUCTOR = 421980227;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ProfilePhoto extends Object {
        public long id;
        public File small;
        public File big;

        public ProfilePhoto() {
        }

        public ProfilePhoto(long id, File small, File big) {
            this.id = id;
            this.small = small;
            this.big = big;
        }

        public static final int CONSTRUCTOR = 978085937;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Proxies extends Object {
        public Proxy[] proxies;

        public Proxies() {
        }

        public Proxies(Proxy[] proxies) {
            this.proxies = proxies;
        }

        public static final int CONSTRUCTOR = 1200447205;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Proxy extends Object {
        public int id;
        public String server;
        public int port;
        public int lastUsedDate;
        public boolean isEnabled;
        public ProxyType type;

        public Proxy() {
        }

        public Proxy(int id, String server, int port, int lastUsedDate, boolean isEnabled, ProxyType type) {
            this.id = id;
            this.server = server;
            this.port = port;
            this.lastUsedDate = lastUsedDate;
            this.isEnabled = isEnabled;
            this.type = type;
        }

        public static final int CONSTRUCTOR = 196049779;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class ProxyType extends Object {
    }

    public static class ProxyTypeSocks5 extends ProxyType {
        public String username;
        public String password;

        public ProxyTypeSocks5() {
        }

        public ProxyTypeSocks5(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public static final int CONSTRUCTOR = -890027341;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ProxyTypeHttp extends ProxyType {
        public String username;
        public String password;
        public boolean httpOnly;

        public ProxyTypeHttp() {
        }

        public ProxyTypeHttp(String username, String password, boolean httpOnly) {
            this.username = username;
            this.password = password;
            this.httpOnly = httpOnly;
        }

        public static final int CONSTRUCTOR = -1547188361;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ProxyTypeMtproto extends ProxyType {
        public String secret;

        public ProxyTypeMtproto() {
        }

        public ProxyTypeMtproto(String secret) {
            this.secret = secret;
        }

        public static final int CONSTRUCTOR = -1964826627;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PublicMessageLink extends Object {
        public String link;
        public String html;

        public PublicMessageLink() {
        }

        public PublicMessageLink(String link, String html) {
            this.link = link;
            this.html = html;
        }

        public static final int CONSTRUCTOR = -679603433;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class RecoveryEmailAddress extends Object {
        public String recoveryEmailAddress;

        public RecoveryEmailAddress() {
        }

        public RecoveryEmailAddress(String recoveryEmailAddress) {
            this.recoveryEmailAddress = recoveryEmailAddress;
        }

        public static final int CONSTRUCTOR = 1290526187;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class RemoteFile extends Object {
        public String id;
        public boolean isUploadingActive;
        public boolean isUploadingCompleted;
        public int uploadedSize;

        public RemoteFile() {
        }

        public RemoteFile(String id, boolean isUploadingActive, boolean isUploadingCompleted, int uploadedSize) {
            this.id = id;
            this.isUploadingActive = isUploadingActive;
            this.isUploadingCompleted = isUploadingCompleted;
            this.uploadedSize = uploadedSize;
        }

        public static final int CONSTRUCTOR = 1761289748;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class ReplyMarkup extends Object {
    }

    public static class ReplyMarkupRemoveKeyboard extends ReplyMarkup {
        public boolean isPersonal;

        public ReplyMarkupRemoveKeyboard() {
        }

        public ReplyMarkupRemoveKeyboard(boolean isPersonal) {
            this.isPersonal = isPersonal;
        }

        public static final int CONSTRUCTOR = -691252879;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ReplyMarkupForceReply extends ReplyMarkup {
        public boolean isPersonal;

        public ReplyMarkupForceReply() {
        }

        public ReplyMarkupForceReply(boolean isPersonal) {
            this.isPersonal = isPersonal;
        }

        public static final int CONSTRUCTOR = 1039104593;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ReplyMarkupShowKeyboard extends ReplyMarkup {
        public KeyboardButton[][] rows;
        public boolean resizeKeyboard;
        public boolean oneTime;
        public boolean isPersonal;

        public ReplyMarkupShowKeyboard() {
        }

        public ReplyMarkupShowKeyboard(KeyboardButton[][] rows, boolean resizeKeyboard, boolean oneTime, boolean isPersonal) {
            this.rows = rows;
            this.resizeKeyboard = resizeKeyboard;
            this.oneTime = oneTime;
            this.isPersonal = isPersonal;
        }

        public static final int CONSTRUCTOR = -992627133;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ReplyMarkupInlineKeyboard extends ReplyMarkup {
        public InlineKeyboardButton[][] rows;

        public ReplyMarkupInlineKeyboard() {
        }

        public ReplyMarkupInlineKeyboard(InlineKeyboardButton[][] rows) {
            this.rows = rows;
        }

        public static final int CONSTRUCTOR = -619317658;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class RichText extends Object {
    }

    public static class RichTextPlain extends RichText {
        public String text;

        public RichTextPlain() {
        }

        public RichTextPlain(String text) {
            this.text = text;
        }

        public static final int CONSTRUCTOR = 482617702;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class RichTextBold extends RichText {
        public RichText text;

        public RichTextBold() {
        }

        public RichTextBold(RichText text) {
            this.text = text;
        }

        public static final int CONSTRUCTOR = 1670844268;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class RichTextItalic extends RichText {
        public RichText text;

        public RichTextItalic() {
        }

        public RichTextItalic(RichText text) {
            this.text = text;
        }

        public static final int CONSTRUCTOR = 1853354047;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class RichTextUnderline extends RichText {
        public RichText text;

        public RichTextUnderline() {
        }

        public RichTextUnderline(RichText text) {
            this.text = text;
        }

        public static final int CONSTRUCTOR = -536019572;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class RichTextStrikethrough extends RichText {
        public RichText text;

        public RichTextStrikethrough() {
        }

        public RichTextStrikethrough(RichText text) {
            this.text = text;
        }

        public static final int CONSTRUCTOR = 723413585;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class RichTextFixed extends RichText {
        public RichText text;

        public RichTextFixed() {
        }

        public RichTextFixed(RichText text) {
            this.text = text;
        }

        public static final int CONSTRUCTOR = -1271496249;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class RichTextUrl extends RichText {
        public RichText text;
        public String url;

        public RichTextUrl() {
        }

        public RichTextUrl(RichText text, String url) {
            this.text = text;
            this.url = url;
        }

        public static final int CONSTRUCTOR = 1967248447;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class RichTextEmailAddress extends RichText {
        public RichText text;
        public String emailAddress;

        public RichTextEmailAddress() {
        }

        public RichTextEmailAddress(RichText text, String emailAddress) {
            this.text = text;
            this.emailAddress = emailAddress;
        }

        public static final int CONSTRUCTOR = 40018679;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class RichTexts extends RichText {
        public RichText[] texts;

        public RichTexts() {
        }

        public RichTexts(RichText[] texts) {
            this.texts = texts;
        }

        public static final int CONSTRUCTOR = 1647457821;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SavedCredentials extends Object {
        public String id;
        public String title;

        public SavedCredentials() {
        }

        public SavedCredentials(String id, String title) {
            this.id = id;
            this.title = title;
        }

        public static final int CONSTRUCTOR = -370273060;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ScopeNotificationSettings extends Object {
        public int muteFor;
        public String sound;
        public boolean showPreview;

        public ScopeNotificationSettings() {
        }

        public ScopeNotificationSettings(int muteFor, String sound, boolean showPreview) {
            this.muteFor = muteFor;
            this.sound = sound;
            this.showPreview = showPreview;
        }

        public static final int CONSTRUCTOR = 607439283;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class SearchMessagesFilter extends Object {
    }

    public static class SearchMessagesFilterEmpty extends SearchMessagesFilter {

        public SearchMessagesFilterEmpty() {
        }

        public static final int CONSTRUCTOR = -869395657;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchMessagesFilterAnimation extends SearchMessagesFilter {

        public SearchMessagesFilterAnimation() {
        }

        public static final int CONSTRUCTOR = -155713339;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchMessagesFilterAudio extends SearchMessagesFilter {

        public SearchMessagesFilterAudio() {
        }

        public static final int CONSTRUCTOR = 867505275;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchMessagesFilterDocument extends SearchMessagesFilter {

        public SearchMessagesFilterDocument() {
        }

        public static final int CONSTRUCTOR = 1526331215;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchMessagesFilterPhoto extends SearchMessagesFilter {

        public SearchMessagesFilterPhoto() {
        }

        public static final int CONSTRUCTOR = 925932293;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchMessagesFilterVideo extends SearchMessagesFilter {

        public SearchMessagesFilterVideo() {
        }

        public static final int CONSTRUCTOR = 115538222;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchMessagesFilterVoiceNote extends SearchMessagesFilter {

        public SearchMessagesFilterVoiceNote() {
        }

        public static final int CONSTRUCTOR = 1841439357;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchMessagesFilterPhotoAndVideo extends SearchMessagesFilter {

        public SearchMessagesFilterPhotoAndVideo() {
        }

        public static final int CONSTRUCTOR = 1352130963;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchMessagesFilterUrl extends SearchMessagesFilter {

        public SearchMessagesFilterUrl() {
        }

        public static final int CONSTRUCTOR = -1828724341;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchMessagesFilterChatPhoto extends SearchMessagesFilter {

        public SearchMessagesFilterChatPhoto() {
        }

        public static final int CONSTRUCTOR = -1247751329;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchMessagesFilterCall extends SearchMessagesFilter {

        public SearchMessagesFilterCall() {
        }

        public static final int CONSTRUCTOR = 1305231012;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchMessagesFilterMissedCall extends SearchMessagesFilter {

        public SearchMessagesFilterMissedCall() {
        }

        public static final int CONSTRUCTOR = 970663098;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchMessagesFilterVideoNote extends SearchMessagesFilter {

        public SearchMessagesFilterVideoNote() {
        }

        public static final int CONSTRUCTOR = 564323321;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchMessagesFilterVoiceAndVideoNote extends SearchMessagesFilter {

        public SearchMessagesFilterVoiceAndVideoNote() {
        }

        public static final int CONSTRUCTOR = 664174819;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchMessagesFilterMention extends SearchMessagesFilter {

        public SearchMessagesFilterMention() {
        }

        public static final int CONSTRUCTOR = 2001258652;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchMessagesFilterUnreadMention extends SearchMessagesFilter {

        public SearchMessagesFilterUnreadMention() {
        }

        public static final int CONSTRUCTOR = -95769149;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Seconds extends Object {
        public double seconds;

        public Seconds() {
        }

        public Seconds(double seconds) {
            this.seconds = seconds;
        }

        public static final int CONSTRUCTOR = 959899022;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SecretChat extends Object {
        public int id;
        public int userId;
        public SecretChatState state;
        public boolean isOutbound;
        public int ttl;
        public byte[] keyHash;
        public int layer;

        public SecretChat() {
        }

        public SecretChat(int id, int userId, SecretChatState state, boolean isOutbound, int ttl, byte[] keyHash, int layer) {
            this.id = id;
            this.userId = userId;
            this.state = state;
            this.isOutbound = isOutbound;
            this.ttl = ttl;
            this.keyHash = keyHash;
            this.layer = layer;
        }

        public static final int CONSTRUCTOR = 1279231629;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class SecretChatState extends Object {
    }

    public static class SecretChatStatePending extends SecretChatState {

        public SecretChatStatePending() {
        }

        public static final int CONSTRUCTOR = -1637050756;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SecretChatStateReady extends SecretChatState {

        public SecretChatStateReady() {
        }

        public static final int CONSTRUCTOR = -1611352087;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SecretChatStateClosed extends SecretChatState {

        public SecretChatStateClosed() {
        }

        public static final int CONSTRUCTOR = -1945106707;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Session extends Object {
        public long id;
        public boolean isCurrent;
        public int apiId;
        public String applicationName;
        public String applicationVersion;
        public boolean isOfficialApplication;
        public String deviceModel;
        public String platform;
        public String systemVersion;
        public int logInDate;
        public int lastActiveDate;
        public String ip;
        public String country;
        public String region;

        public Session() {
        }

        public Session(long id, boolean isCurrent, int apiId, String applicationName, String applicationVersion, boolean isOfficialApplication, String deviceModel, String platform, String systemVersion, int logInDate, int lastActiveDate, String ip, String country, String region) {
            this.id = id;
            this.isCurrent = isCurrent;
            this.apiId = apiId;
            this.applicationName = applicationName;
            this.applicationVersion = applicationVersion;
            this.isOfficialApplication = isOfficialApplication;
            this.deviceModel = deviceModel;
            this.platform = platform;
            this.systemVersion = systemVersion;
            this.logInDate = logInDate;
            this.lastActiveDate = lastActiveDate;
            this.ip = ip;
            this.country = country;
            this.region = region;
        }

        public static final int CONSTRUCTOR = 1715359000;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Sessions extends Object {
        public Session[] sessions;

        public Sessions() {
        }

        public Sessions(Session[] sessions) {
            this.sessions = sessions;
        }

        public static final int CONSTRUCTOR = -463118121;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ShippingOption extends Object {
        public String id;
        public String title;
        public LabeledPricePart[] priceParts;

        public ShippingOption() {
        }

        public ShippingOption(String id, String title, LabeledPricePart[] priceParts) {
            this.id = id;
            this.title = title;
            this.priceParts = priceParts;
        }

        public static final int CONSTRUCTOR = 1425690001;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Sticker extends Object {
        public long setId;
        public int width;
        public int height;
        public String emoji;
        public boolean isMask;
        public MaskPosition maskPosition;
        public PhotoSize thumbnail;
        public File sticker;

        public Sticker() {
        }

        public Sticker(long setId, int width, int height, String emoji, boolean isMask, MaskPosition maskPosition, PhotoSize thumbnail, File sticker) {
            this.setId = setId;
            this.width = width;
            this.height = height;
            this.emoji = emoji;
            this.isMask = isMask;
            this.maskPosition = maskPosition;
            this.thumbnail = thumbnail;
            this.sticker = sticker;
        }

        public static final int CONSTRUCTOR = -876442962;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class StickerEmojis extends Object {
        public String[] emojis;

        public StickerEmojis() {
        }

        public StickerEmojis(String[] emojis) {
            this.emojis = emojis;
        }

        public static final int CONSTRUCTOR = -1781588570;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class StickerSet extends Object {
        public long id;
        public String title;
        public String name;
        public boolean isInstalled;
        public boolean isArchived;
        public boolean isOfficial;
        public boolean isMasks;
        public boolean isViewed;
        public Sticker[] stickers;
        public StickerEmojis[] emojis;

        public StickerSet() {
        }

        public StickerSet(long id, String title, String name, boolean isInstalled, boolean isArchived, boolean isOfficial, boolean isMasks, boolean isViewed, Sticker[] stickers, StickerEmojis[] emojis) {
            this.id = id;
            this.title = title;
            this.name = name;
            this.isInstalled = isInstalled;
            this.isArchived = isArchived;
            this.isOfficial = isOfficial;
            this.isMasks = isMasks;
            this.isViewed = isViewed;
            this.stickers = stickers;
            this.emojis = emojis;
        }

        public static final int CONSTRUCTOR = 72047469;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class StickerSetInfo extends Object {
        public long id;
        public String title;
        public String name;
        public boolean isInstalled;
        public boolean isArchived;
        public boolean isOfficial;
        public boolean isMasks;
        public boolean isViewed;
        public int size;
        public Sticker[] covers;

        public StickerSetInfo() {
        }

        public StickerSetInfo(long id, String title, String name, boolean isInstalled, boolean isArchived, boolean isOfficial, boolean isMasks, boolean isViewed, int size, Sticker[] covers) {
            this.id = id;
            this.title = title;
            this.name = name;
            this.isInstalled = isInstalled;
            this.isArchived = isArchived;
            this.isOfficial = isOfficial;
            this.isMasks = isMasks;
            this.isViewed = isViewed;
            this.size = size;
            this.covers = covers;
        }

        public static final int CONSTRUCTOR = 1469837113;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class StickerSets extends Object {
        public int totalCount;
        public StickerSetInfo[] sets;

        public StickerSets() {
        }

        public StickerSets(int totalCount, StickerSetInfo[] sets) {
            this.totalCount = totalCount;
            this.sets = sets;
        }

        public static final int CONSTRUCTOR = -1883828812;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Stickers extends Object {
        public Sticker[] stickers;

        public Stickers() {
        }

        public Stickers(Sticker[] stickers) {
            this.stickers = stickers;
        }

        public static final int CONSTRUCTOR = 1974859260;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class StorageStatistics extends Object {
        public long size;
        public int count;
        public StorageStatisticsByChat[] byChat;

        public StorageStatistics() {
        }

        public StorageStatistics(long size, int count, StorageStatisticsByChat[] byChat) {
            this.size = size;
            this.count = count;
            this.byChat = byChat;
        }

        public static final int CONSTRUCTOR = 217237013;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class StorageStatisticsByChat extends Object {
        public long chatId;
        public long size;
        public int count;
        public StorageStatisticsByFileType[] byFileType;

        public StorageStatisticsByChat() {
        }

        public StorageStatisticsByChat(long chatId, long size, int count, StorageStatisticsByFileType[] byFileType) {
            this.chatId = chatId;
            this.size = size;
            this.count = count;
            this.byFileType = byFileType;
        }

        public static final int CONSTRUCTOR = 635434531;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class StorageStatisticsByFileType extends Object {
        public FileType fileType;
        public long size;
        public int count;

        public StorageStatisticsByFileType() {
        }

        public StorageStatisticsByFileType(FileType fileType, long size, int count) {
            this.fileType = fileType;
            this.size = size;
            this.count = count;
        }

        public static final int CONSTRUCTOR = 714012840;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class StorageStatisticsFast extends Object {
        public long filesSize;
        public int fileCount;
        public long databaseSize;

        public StorageStatisticsFast() {
        }

        public StorageStatisticsFast(long filesSize, int fileCount, long databaseSize) {
            this.filesSize = filesSize;
            this.fileCount = fileCount;
            this.databaseSize = databaseSize;
        }

        public static final int CONSTRUCTOR = -2005401007;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Supergroup extends Object {
        public int id;
        public String username;
        public int date;
        public ChatMemberStatus status;
        public int memberCount;
        public boolean anyoneCanInvite;
        public boolean signMessages;
        public boolean isChannel;
        public boolean isVerified;
        public String restrictionReason;

        public Supergroup() {
        }

        public Supergroup(int id, String username, int date, ChatMemberStatus status, int memberCount, boolean anyoneCanInvite, boolean signMessages, boolean isChannel, boolean isVerified, String restrictionReason) {
            this.id = id;
            this.username = username;
            this.date = date;
            this.status = status;
            this.memberCount = memberCount;
            this.anyoneCanInvite = anyoneCanInvite;
            this.signMessages = signMessages;
            this.isChannel = isChannel;
            this.isVerified = isVerified;
            this.restrictionReason = restrictionReason;
        }

        public static final int CONSTRUCTOR = -1737513476;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SupergroupFullInfo extends Object {
        public String description;
        public int memberCount;
        public int administratorCount;
        public int restrictedCount;
        public int bannedCount;
        public boolean canGetMembers;
        public boolean canSetUsername;
        public boolean canSetStickerSet;
        public boolean isAllHistoryAvailable;
        public long stickerSetId;
        public String inviteLink;
        public long pinnedMessageId;
        public int upgradedFromBasicGroupId;
        public long upgradedFromMaxMessageId;

        public SupergroupFullInfo() {
        }

        public SupergroupFullInfo(String description, int memberCount, int administratorCount, int restrictedCount, int bannedCount, boolean canGetMembers, boolean canSetUsername, boolean canSetStickerSet, boolean isAllHistoryAvailable, long stickerSetId, String inviteLink, long pinnedMessageId, int upgradedFromBasicGroupId, long upgradedFromMaxMessageId) {
            this.description = description;
            this.memberCount = memberCount;
            this.administratorCount = administratorCount;
            this.restrictedCount = restrictedCount;
            this.bannedCount = bannedCount;
            this.canGetMembers = canGetMembers;
            this.canSetUsername = canSetUsername;
            this.canSetStickerSet = canSetStickerSet;
            this.isAllHistoryAvailable = isAllHistoryAvailable;
            this.stickerSetId = stickerSetId;
            this.inviteLink = inviteLink;
            this.pinnedMessageId = pinnedMessageId;
            this.upgradedFromBasicGroupId = upgradedFromBasicGroupId;
            this.upgradedFromMaxMessageId = upgradedFromMaxMessageId;
        }

        public static final int CONSTRUCTOR = -1482349223;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class SupergroupMembersFilter extends Object {
    }

    public static class SupergroupMembersFilterRecent extends SupergroupMembersFilter {

        public SupergroupMembersFilterRecent() {
        }

        public static final int CONSTRUCTOR = 1178199509;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SupergroupMembersFilterAdministrators extends SupergroupMembersFilter {

        public SupergroupMembersFilterAdministrators() {
        }

        public static final int CONSTRUCTOR = -2097380265;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SupergroupMembersFilterSearch extends SupergroupMembersFilter {
        public String query;

        public SupergroupMembersFilterSearch() {
        }

        public SupergroupMembersFilterSearch(String query) {
            this.query = query;
        }

        public static final int CONSTRUCTOR = -1696358469;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SupergroupMembersFilterRestricted extends SupergroupMembersFilter {
        public String query;

        public SupergroupMembersFilterRestricted() {
        }

        public SupergroupMembersFilterRestricted(String query) {
            this.query = query;
        }

        public static final int CONSTRUCTOR = -1107800034;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SupergroupMembersFilterBanned extends SupergroupMembersFilter {
        public String query;

        public SupergroupMembersFilterBanned() {
        }

        public SupergroupMembersFilterBanned(String query) {
            this.query = query;
        }

        public static final int CONSTRUCTOR = -1210621683;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SupergroupMembersFilterBots extends SupergroupMembersFilter {

        public SupergroupMembersFilterBots() {
        }

        public static final int CONSTRUCTOR = 492138918;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TMeUrl extends Object {
        public String url;
        public TMeUrlType type;

        public TMeUrl() {
        }

        public TMeUrl(String url, TMeUrlType type) {
            this.url = url;
            this.type = type;
        }

        public static final int CONSTRUCTOR = -1140786622;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class TMeUrlType extends Object {
    }

    public static class TMeUrlTypeUser extends TMeUrlType {
        public int userId;

        public TMeUrlTypeUser() {
        }

        public TMeUrlTypeUser(int userId) {
            this.userId = userId;
        }

        public static final int CONSTRUCTOR = -1198700130;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TMeUrlTypeSupergroup extends TMeUrlType {
        public long supergroupId;

        public TMeUrlTypeSupergroup() {
        }

        public TMeUrlTypeSupergroup(long supergroupId) {
            this.supergroupId = supergroupId;
        }

        public static final int CONSTRUCTOR = -1353369944;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TMeUrlTypeChatInvite extends TMeUrlType {
        public ChatInviteLinkInfo info;

        public TMeUrlTypeChatInvite() {
        }

        public TMeUrlTypeChatInvite(ChatInviteLinkInfo info) {
            this.info = info;
        }

        public static final int CONSTRUCTOR = 313907785;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TMeUrlTypeStickerSet extends TMeUrlType {
        public long stickerSetId;

        public TMeUrlTypeStickerSet() {
        }

        public TMeUrlTypeStickerSet(long stickerSetId) {
            this.stickerSetId = stickerSetId;
        }

        public static final int CONSTRUCTOR = 1602473196;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TMeUrls extends Object {
        public TMeUrl[] urls;

        public TMeUrls() {
        }

        public TMeUrls(TMeUrl[] urls) {
            this.urls = urls;
        }

        public static final int CONSTRUCTOR = -1130595098;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TdlibParameters extends Object {
        public boolean useTestDc;
        public String databaseDirectory;
        public String filesDirectory;
        public boolean useFileDatabase;
        public boolean useChatInfoDatabase;
        public boolean useMessageDatabase;
        public boolean useSecretChats;
        public int apiId;
        public String apiHash;
        public String systemLanguageCode;
        public String deviceModel;
        public String systemVersion;
        public String applicationVersion;
        public boolean enableStorageOptimizer;
        public boolean ignoreFileNames;

        public TdlibParameters() {
        }

        public TdlibParameters(boolean useTestDc, String databaseDirectory, String filesDirectory, boolean useFileDatabase, boolean useChatInfoDatabase, boolean useMessageDatabase, boolean useSecretChats, int apiId, String apiHash, String systemLanguageCode, String deviceModel, String systemVersion, String applicationVersion, boolean enableStorageOptimizer, boolean ignoreFileNames) {
            this.useTestDc = useTestDc;
            this.databaseDirectory = databaseDirectory;
            this.filesDirectory = filesDirectory;
            this.useFileDatabase = useFileDatabase;
            this.useChatInfoDatabase = useChatInfoDatabase;
            this.useMessageDatabase = useMessageDatabase;
            this.useSecretChats = useSecretChats;
            this.apiId = apiId;
            this.apiHash = apiHash;
            this.systemLanguageCode = systemLanguageCode;
            this.deviceModel = deviceModel;
            this.systemVersion = systemVersion;
            this.applicationVersion = applicationVersion;
            this.enableStorageOptimizer = enableStorageOptimizer;
            this.ignoreFileNames = ignoreFileNames;
        }

        public static final int CONSTRUCTOR = -761520773;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TemporaryPasswordState extends Object {
        public boolean hasPassword;
        public int validFor;

        public TemporaryPasswordState() {
        }

        public TemporaryPasswordState(boolean hasPassword, int validFor) {
            this.hasPassword = hasPassword;
            this.validFor = validFor;
        }

        public static final int CONSTRUCTOR = 939837410;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TermsOfService extends Object {
        public FormattedText text;
        public int minUserAge;
        public boolean showPopup;

        public TermsOfService() {
        }

        public TermsOfService(FormattedText text, int minUserAge, boolean showPopup) {
            this.text = text;
            this.minUserAge = minUserAge;
            this.showPopup = showPopup;
        }

        public static final int CONSTRUCTOR = 739422597;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TestBytes extends Object {
        public byte[] value;

        public TestBytes() {
        }

        public TestBytes(byte[] value) {
            this.value = value;
        }

        public static final int CONSTRUCTOR = -1541225250;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TestInt extends Object {
        public int value;

        public TestInt() {
        }

        public TestInt(int value) {
            this.value = value;
        }

        public static final int CONSTRUCTOR = -574804983;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TestString extends Object {
        public String value;

        public TestString() {
        }

        public TestString(String value) {
            this.value = value;
        }

        public static final int CONSTRUCTOR = -27891572;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TestVectorInt extends Object {
        public int[] value;

        public TestVectorInt() {
        }

        public TestVectorInt(int[] value) {
            this.value = value;
        }

        public static final int CONSTRUCTOR = 593682027;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TestVectorIntObject extends Object {
        public TestInt[] value;

        public TestVectorIntObject() {
        }

        public TestVectorIntObject(TestInt[] value) {
            this.value = value;
        }

        public static final int CONSTRUCTOR = 125891546;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TestVectorString extends Object {
        public String[] value;

        public TestVectorString() {
        }

        public TestVectorString(String[] value) {
            this.value = value;
        }

        public static final int CONSTRUCTOR = 79339995;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TestVectorStringObject extends Object {
        public TestString[] value;

        public TestVectorStringObject() {
        }

        public TestVectorStringObject(TestString[] value) {
            this.value = value;
        }

        public static final int CONSTRUCTOR = 80780537;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Text extends Object {
        public String text;

        public Text() {
        }

        public Text(String text) {
            this.text = text;
        }

        public static final int CONSTRUCTOR = 578181272;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TextEntities extends Object {
        public TextEntity[] entities;

        public TextEntities() {
        }

        public TextEntities(TextEntity[] entities) {
            this.entities = entities;
        }

        public static final int CONSTRUCTOR = -933199172;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TextEntity extends Object {
        public int offset;
        public int length;
        public TextEntityType type;

        public TextEntity() {
        }

        public TextEntity(int offset, int length, TextEntityType type) {
            this.offset = offset;
            this.length = length;
            this.type = type;
        }

        public static final int CONSTRUCTOR = -1951688280;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class TextEntityType extends Object {
    }

    public static class TextEntityTypeMention extends TextEntityType {

        public TextEntityTypeMention() {
        }

        public static final int CONSTRUCTOR = 934535013;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TextEntityTypeHashtag extends TextEntityType {

        public TextEntityTypeHashtag() {
        }

        public static final int CONSTRUCTOR = -1023958307;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TextEntityTypeCashtag extends TextEntityType {

        public TextEntityTypeCashtag() {
        }

        public static final int CONSTRUCTOR = 1222915915;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TextEntityTypeBotCommand extends TextEntityType {

        public TextEntityTypeBotCommand() {
        }

        public static final int CONSTRUCTOR = -1150997581;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TextEntityTypeUrl extends TextEntityType {

        public TextEntityTypeUrl() {
        }

        public static final int CONSTRUCTOR = -1312762756;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TextEntityTypeEmailAddress extends TextEntityType {

        public TextEntityTypeEmailAddress() {
        }

        public static final int CONSTRUCTOR = 1425545249;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TextEntityTypeBold extends TextEntityType {

        public TextEntityTypeBold() {
        }

        public static final int CONSTRUCTOR = -1128210000;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TextEntityTypeItalic extends TextEntityType {

        public TextEntityTypeItalic() {
        }

        public static final int CONSTRUCTOR = -118253987;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TextEntityTypeCode extends TextEntityType {

        public TextEntityTypeCode() {
        }

        public static final int CONSTRUCTOR = -974534326;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TextEntityTypePre extends TextEntityType {

        public TextEntityTypePre() {
        }

        public static final int CONSTRUCTOR = 1648958606;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TextEntityTypePreCode extends TextEntityType {
        public String language;

        public TextEntityTypePreCode() {
        }

        public TextEntityTypePreCode(String language) {
            this.language = language;
        }

        public static final int CONSTRUCTOR = -945325397;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TextEntityTypeTextUrl extends TextEntityType {
        public String url;

        public TextEntityTypeTextUrl() {
        }

        public TextEntityTypeTextUrl(String url) {
            this.url = url;
        }

        public static final int CONSTRUCTOR = 445719651;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TextEntityTypeMentionName extends TextEntityType {
        public int userId;

        public TextEntityTypeMentionName() {
        }

        public TextEntityTypeMentionName(int userId) {
            this.userId = userId;
        }

        public static final int CONSTRUCTOR = -791517091;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TextEntityTypePhoneNumber extends TextEntityType {

        public TextEntityTypePhoneNumber() {
        }

        public static final int CONSTRUCTOR = -1160140246;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class TextParseMode extends Object {
    }

    public static class TextParseModeMarkdown extends TextParseMode {

        public TextParseModeMarkdown() {
        }

        public static final int CONSTRUCTOR = 969225580;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TextParseModeHTML extends TextParseMode {

        public TextParseModeHTML() {
        }

        public static final int CONSTRUCTOR = 1660208627;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class TopChatCategory extends Object {
    }

    public static class TopChatCategoryUsers extends TopChatCategory {

        public TopChatCategoryUsers() {
        }

        public static final int CONSTRUCTOR = 1026706816;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TopChatCategoryBots extends TopChatCategory {

        public TopChatCategoryBots() {
        }

        public static final int CONSTRUCTOR = -1577129195;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TopChatCategoryGroups extends TopChatCategory {

        public TopChatCategoryGroups() {
        }

        public static final int CONSTRUCTOR = 1530056846;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TopChatCategoryChannels extends TopChatCategory {

        public TopChatCategoryChannels() {
        }

        public static final int CONSTRUCTOR = -500825885;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TopChatCategoryInlineBots extends TopChatCategory {

        public TopChatCategoryInlineBots() {
        }

        public static final int CONSTRUCTOR = 377023356;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TopChatCategoryCalls extends TopChatCategory {

        public TopChatCategoryCalls() {
        }

        public static final int CONSTRUCTOR = 356208861;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class Update extends Object {
    }

    public static class UpdateAuthorizationState extends Update {
        public AuthorizationState authorizationState;

        public UpdateAuthorizationState() {
        }

        public UpdateAuthorizationState(AuthorizationState authorizationState) {
            this.authorizationState = authorizationState;
        }

        public static final int CONSTRUCTOR = 1622347490;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateNewMessage extends Update {
        public Message message;
        public boolean disableNotification;
        public boolean containsMention;

        public UpdateNewMessage() {
        }

        public UpdateNewMessage(Message message, boolean disableNotification, boolean containsMention) {
            this.message = message;
            this.disableNotification = disableNotification;
            this.containsMention = containsMention;
        }

        public static final int CONSTRUCTOR = 238944219;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateMessageSendAcknowledged extends Update {
        public long chatId;
        public long messageId;

        public UpdateMessageSendAcknowledged() {
        }

        public UpdateMessageSendAcknowledged(long chatId, long messageId) {
            this.chatId = chatId;
            this.messageId = messageId;
        }

        public static final int CONSTRUCTOR = 1302843961;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateMessageSendSucceeded extends Update {
        public Message message;
        public long oldMessageId;

        public UpdateMessageSendSucceeded() {
        }

        public UpdateMessageSendSucceeded(Message message, long oldMessageId) {
            this.message = message;
            this.oldMessageId = oldMessageId;
        }

        public static final int CONSTRUCTOR = 1815715197;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateMessageSendFailed extends Update {
        public Message message;
        public long oldMessageId;
        public int errorCode;
        public String errorMessage;

        public UpdateMessageSendFailed() {
        }

        public UpdateMessageSendFailed(Message message, long oldMessageId, int errorCode, String errorMessage) {
            this.message = message;
            this.oldMessageId = oldMessageId;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        public static final int CONSTRUCTOR = -1032335779;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateMessageContent extends Update {
        public long chatId;
        public long messageId;
        public MessageContent newContent;

        public UpdateMessageContent() {
        }

        public UpdateMessageContent(long chatId, long messageId, MessageContent newContent) {
            this.chatId = chatId;
            this.messageId = messageId;
            this.newContent = newContent;
        }

        public static final int CONSTRUCTOR = 506903332;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateMessageEdited extends Update {
        public long chatId;
        public long messageId;
        public int editDate;
        public ReplyMarkup replyMarkup;

        public UpdateMessageEdited() {
        }

        public UpdateMessageEdited(long chatId, long messageId, int editDate, ReplyMarkup replyMarkup) {
            this.chatId = chatId;
            this.messageId = messageId;
            this.editDate = editDate;
            this.replyMarkup = replyMarkup;
        }

        public static final int CONSTRUCTOR = -559545626;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateMessageViews extends Update {
        public long chatId;
        public long messageId;
        public int views;

        public UpdateMessageViews() {
        }

        public UpdateMessageViews(long chatId, long messageId, int views) {
            this.chatId = chatId;
            this.messageId = messageId;
            this.views = views;
        }

        public static final int CONSTRUCTOR = -1854131125;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateMessageContentOpened extends Update {
        public long chatId;
        public long messageId;

        public UpdateMessageContentOpened() {
        }

        public UpdateMessageContentOpened(long chatId, long messageId) {
            this.chatId = chatId;
            this.messageId = messageId;
        }

        public static final int CONSTRUCTOR = -1520523131;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateMessageMentionRead extends Update {
        public long chatId;
        public long messageId;
        public int unreadMentionCount;

        public UpdateMessageMentionRead() {
        }

        public UpdateMessageMentionRead(long chatId, long messageId, int unreadMentionCount) {
            this.chatId = chatId;
            this.messageId = messageId;
            this.unreadMentionCount = unreadMentionCount;
        }

        public static final int CONSTRUCTOR = -252228282;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateNewChat extends Update {
        public Chat chat;

        public UpdateNewChat() {
        }

        public UpdateNewChat(Chat chat) {
            this.chat = chat;
        }

        public static final int CONSTRUCTOR = 2075757773;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateChatTitle extends Update {
        public long chatId;
        public String title;

        public UpdateChatTitle() {
        }

        public UpdateChatTitle(long chatId, String title) {
            this.chatId = chatId;
            this.title = title;
        }

        public static final int CONSTRUCTOR = -175405660;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateChatPhoto extends Update {
        public long chatId;
        public ChatPhoto photo;

        public UpdateChatPhoto() {
        }

        public UpdateChatPhoto(long chatId, ChatPhoto photo) {
            this.chatId = chatId;
            this.photo = photo;
        }

        public static final int CONSTRUCTOR = -209353966;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateChatLastMessage extends Update {
        public long chatId;
        public Message lastMessage;
        public long order;

        public UpdateChatLastMessage() {
        }

        public UpdateChatLastMessage(long chatId, Message lastMessage, long order) {
            this.chatId = chatId;
            this.lastMessage = lastMessage;
            this.order = order;
        }

        public static final int CONSTRUCTOR = 580348828;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateChatOrder extends Update {
        public long chatId;
        public long order;

        public UpdateChatOrder() {
        }

        public UpdateChatOrder(long chatId, long order) {
            this.chatId = chatId;
            this.order = order;
        }

        public static final int CONSTRUCTOR = -1601888026;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateChatIsPinned extends Update {
        public long chatId;
        public boolean isPinned;
        public long order;

        public UpdateChatIsPinned() {
        }

        public UpdateChatIsPinned(long chatId, boolean isPinned, long order) {
            this.chatId = chatId;
            this.isPinned = isPinned;
            this.order = order;
        }

        public static final int CONSTRUCTOR = 488876260;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateChatIsMarkedAsUnread extends Update {
        public long chatId;
        public boolean isMarkedAsUnread;

        public UpdateChatIsMarkedAsUnread() {
        }

        public UpdateChatIsMarkedAsUnread(long chatId, boolean isMarkedAsUnread) {
            this.chatId = chatId;
            this.isMarkedAsUnread = isMarkedAsUnread;
        }

        public static final int CONSTRUCTOR = 1468347188;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateChatIsSponsored extends Update {
        public long chatId;
        public boolean isSponsored;
        public long order;

        public UpdateChatIsSponsored() {
        }

        public UpdateChatIsSponsored(long chatId, boolean isSponsored, long order) {
            this.chatId = chatId;
            this.isSponsored = isSponsored;
            this.order = order;
        }

        public static final int CONSTRUCTOR = -1196180070;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateChatDefaultDisableNotification extends Update {
        public long chatId;
        public boolean defaultDisableNotification;

        public UpdateChatDefaultDisableNotification() {
        }

        public UpdateChatDefaultDisableNotification(long chatId, boolean defaultDisableNotification) {
            this.chatId = chatId;
            this.defaultDisableNotification = defaultDisableNotification;
        }

        public static final int CONSTRUCTOR = 464087707;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateChatReadInbox extends Update {
        public long chatId;
        public long lastReadInboxMessageId;
        public int unreadCount;

        public UpdateChatReadInbox() {
        }

        public UpdateChatReadInbox(long chatId, long lastReadInboxMessageId, int unreadCount) {
            this.chatId = chatId;
            this.lastReadInboxMessageId = lastReadInboxMessageId;
            this.unreadCount = unreadCount;
        }

        public static final int CONSTRUCTOR = -797952281;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateChatReadOutbox extends Update {
        public long chatId;
        public long lastReadOutboxMessageId;

        public UpdateChatReadOutbox() {
        }

        public UpdateChatReadOutbox(long chatId, long lastReadOutboxMessageId) {
            this.chatId = chatId;
            this.lastReadOutboxMessageId = lastReadOutboxMessageId;
        }

        public static final int CONSTRUCTOR = 708334213;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateChatUnreadMentionCount extends Update {
        public long chatId;
        public int unreadMentionCount;

        public UpdateChatUnreadMentionCount() {
        }

        public UpdateChatUnreadMentionCount(long chatId, int unreadMentionCount) {
            this.chatId = chatId;
            this.unreadMentionCount = unreadMentionCount;
        }

        public static final int CONSTRUCTOR = -2131461348;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateChatNotificationSettings extends Update {
        public long chatId;
        public ChatNotificationSettings notificationSettings;

        public UpdateChatNotificationSettings() {
        }

        public UpdateChatNotificationSettings(long chatId, ChatNotificationSettings notificationSettings) {
            this.chatId = chatId;
            this.notificationSettings = notificationSettings;
        }

        public static final int CONSTRUCTOR = -803163050;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateScopeNotificationSettings extends Update {
        public NotificationSettingsScope scope;
        public ScopeNotificationSettings notificationSettings;

        public UpdateScopeNotificationSettings() {
        }

        public UpdateScopeNotificationSettings(NotificationSettingsScope scope, ScopeNotificationSettings notificationSettings) {
            this.scope = scope;
            this.notificationSettings = notificationSettings;
        }

        public static final int CONSTRUCTOR = -1203975309;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateChatReplyMarkup extends Update {
        public long chatId;
        public long replyMarkupMessageId;

        public UpdateChatReplyMarkup() {
        }

        public UpdateChatReplyMarkup(long chatId, long replyMarkupMessageId) {
            this.chatId = chatId;
            this.replyMarkupMessageId = replyMarkupMessageId;
        }

        public static final int CONSTRUCTOR = 1309386144;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateChatDraftMessage extends Update {
        public long chatId;
        public DraftMessage draftMessage;
        public long order;

        public UpdateChatDraftMessage() {
        }

        public UpdateChatDraftMessage(long chatId, DraftMessage draftMessage, long order) {
            this.chatId = chatId;
            this.draftMessage = draftMessage;
            this.order = order;
        }

        public static final int CONSTRUCTOR = -1436617498;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateDeleteMessages extends Update {
        public long chatId;
        public long[] messageIds;
        public boolean isPermanent;
        public boolean fromCache;

        public UpdateDeleteMessages() {
        }

        public UpdateDeleteMessages(long chatId, long[] messageIds, boolean isPermanent, boolean fromCache) {
            this.chatId = chatId;
            this.messageIds = messageIds;
            this.isPermanent = isPermanent;
            this.fromCache = fromCache;
        }

        public static final int CONSTRUCTOR = 1669252686;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateUserChatAction extends Update {
        public long chatId;
        public int userId;
        public ChatAction action;

        public UpdateUserChatAction() {
        }

        public UpdateUserChatAction(long chatId, int userId, ChatAction action) {
            this.chatId = chatId;
            this.userId = userId;
            this.action = action;
        }

        public static final int CONSTRUCTOR = 1444133514;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateUserStatus extends Update {
        public int userId;
        public UserStatus status;

        public UpdateUserStatus() {
        }

        public UpdateUserStatus(int userId, UserStatus status) {
            this.userId = userId;
            this.status = status;
        }

        public static final int CONSTRUCTOR = -1443545195;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateUser extends Update {
        public User user;

        public UpdateUser() {
        }

        public UpdateUser(User user) {
            this.user = user;
        }

        public static final int CONSTRUCTOR = 1183394041;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateBasicGroup extends Update {
        public BasicGroup basicGroup;

        public UpdateBasicGroup() {
        }

        public UpdateBasicGroup(BasicGroup basicGroup) {
            this.basicGroup = basicGroup;
        }

        public static final int CONSTRUCTOR = -1003239581;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateSupergroup extends Update {
        public Supergroup supergroup;

        public UpdateSupergroup() {
        }

        public UpdateSupergroup(Supergroup supergroup) {
            this.supergroup = supergroup;
        }

        public static final int CONSTRUCTOR = -76782300;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateSecretChat extends Update {
        public SecretChat secretChat;

        public UpdateSecretChat() {
        }

        public UpdateSecretChat(SecretChat secretChat) {
            this.secretChat = secretChat;
        }

        public static final int CONSTRUCTOR = -1666903253;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateUserFullInfo extends Update {
        public int userId;
        public UserFullInfo userFullInfo;

        public UpdateUserFullInfo() {
        }

        public UpdateUserFullInfo(int userId, UserFullInfo userFullInfo) {
            this.userId = userId;
            this.userFullInfo = userFullInfo;
        }

        public static final int CONSTRUCTOR = 222103874;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateBasicGroupFullInfo extends Update {
        public int basicGroupId;
        public BasicGroupFullInfo basicGroupFullInfo;

        public UpdateBasicGroupFullInfo() {
        }

        public UpdateBasicGroupFullInfo(int basicGroupId, BasicGroupFullInfo basicGroupFullInfo) {
            this.basicGroupId = basicGroupId;
            this.basicGroupFullInfo = basicGroupFullInfo;
        }

        public static final int CONSTRUCTOR = 924030531;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateSupergroupFullInfo extends Update {
        public int supergroupId;
        public SupergroupFullInfo supergroupFullInfo;

        public UpdateSupergroupFullInfo() {
        }

        public UpdateSupergroupFullInfo(int supergroupId, SupergroupFullInfo supergroupFullInfo) {
            this.supergroupId = supergroupId;
            this.supergroupFullInfo = supergroupFullInfo;
        }

        public static final int CONSTRUCTOR = 1288828758;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateServiceNotification extends Update {
        public String type;
        public MessageContent content;

        public UpdateServiceNotification() {
        }

        public UpdateServiceNotification(String type, MessageContent content) {
            this.type = type;
            this.content = content;
        }

        public static final int CONSTRUCTOR = 1318622637;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateFile extends Update {
        public File file;

        public UpdateFile() {
        }

        public UpdateFile(File file) {
            this.file = file;
        }

        public static final int CONSTRUCTOR = 114132831;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateFileGenerationStart extends Update {
        public long generationId;
        public String originalPath;
        public String destinationPath;
        public String conversion;

        public UpdateFileGenerationStart() {
        }

        public UpdateFileGenerationStart(long generationId, String originalPath, String destinationPath, String conversion) {
            this.generationId = generationId;
            this.originalPath = originalPath;
            this.destinationPath = destinationPath;
            this.conversion = conversion;
        }

        public static final int CONSTRUCTOR = 216817388;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateFileGenerationStop extends Update {
        public long generationId;

        public UpdateFileGenerationStop() {
        }

        public UpdateFileGenerationStop(long generationId) {
            this.generationId = generationId;
        }

        public static final int CONSTRUCTOR = -1894449685;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateCall extends Update {
        public Call call;

        public UpdateCall() {
        }

        public UpdateCall(Call call) {
            this.call = call;
        }

        public static final int CONSTRUCTOR = 1337184477;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateUserPrivacySettingRules extends Update {
        public UserPrivacySetting setting;
        public UserPrivacySettingRules rules;

        public UpdateUserPrivacySettingRules() {
        }

        public UpdateUserPrivacySettingRules(UserPrivacySetting setting, UserPrivacySettingRules rules) {
            this.setting = setting;
            this.rules = rules;
        }

        public static final int CONSTRUCTOR = -912960778;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateUnreadMessageCount extends Update {
        public int unreadCount;
        public int unreadUnmutedCount;

        public UpdateUnreadMessageCount() {
        }

        public UpdateUnreadMessageCount(int unreadCount, int unreadUnmutedCount) {
            this.unreadCount = unreadCount;
            this.unreadUnmutedCount = unreadUnmutedCount;
        }

        public static final int CONSTRUCTOR = -824420376;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateUnreadChatCount extends Update {
        public int unreadCount;
        public int unreadUnmutedCount;
        public int markedAsUnreadCount;
        public int markedAsUnreadUnmutedCount;

        public UpdateUnreadChatCount() {
        }

        public UpdateUnreadChatCount(int unreadCount, int unreadUnmutedCount, int markedAsUnreadCount, int markedAsUnreadUnmutedCount) {
            this.unreadCount = unreadCount;
            this.unreadUnmutedCount = unreadUnmutedCount;
            this.markedAsUnreadCount = markedAsUnreadCount;
            this.markedAsUnreadUnmutedCount = markedAsUnreadUnmutedCount;
        }

        public static final int CONSTRUCTOR = 891150304;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateOption extends Update {
        public String name;
        public OptionValue value;

        public UpdateOption() {
        }

        public UpdateOption(String name, OptionValue value) {
            this.name = name;
            this.value = value;
        }

        public static final int CONSTRUCTOR = 900822020;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateInstalledStickerSets extends Update {
        public boolean isMasks;
        public long[] stickerSetIds;

        public UpdateInstalledStickerSets() {
        }

        public UpdateInstalledStickerSets(boolean isMasks, long[] stickerSetIds) {
            this.isMasks = isMasks;
            this.stickerSetIds = stickerSetIds;
        }

        public static final int CONSTRUCTOR = 1125575977;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateTrendingStickerSets extends Update {
        public StickerSets stickerSets;

        public UpdateTrendingStickerSets() {
        }

        public UpdateTrendingStickerSets(StickerSets stickerSets) {
            this.stickerSets = stickerSets;
        }

        public static final int CONSTRUCTOR = 450714593;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateRecentStickers extends Update {
        public boolean isAttached;
        public int[] stickerIds;

        public UpdateRecentStickers() {
        }

        public UpdateRecentStickers(boolean isAttached, int[] stickerIds) {
            this.isAttached = isAttached;
            this.stickerIds = stickerIds;
        }

        public static final int CONSTRUCTOR = 1906403540;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateFavoriteStickers extends Update {
        public int[] stickerIds;

        public UpdateFavoriteStickers() {
        }

        public UpdateFavoriteStickers(int[] stickerIds) {
            this.stickerIds = stickerIds;
        }

        public static final int CONSTRUCTOR = 1662240999;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateSavedAnimations extends Update {
        public int[] animationIds;

        public UpdateSavedAnimations() {
        }

        public UpdateSavedAnimations(int[] animationIds) {
            this.animationIds = animationIds;
        }

        public static final int CONSTRUCTOR = 65563814;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateLanguagePackStrings extends Update {
        public String localizationTarget;
        public String languagePackId;
        public LanguagePackString[] strings;

        public UpdateLanguagePackStrings() {
        }

        public UpdateLanguagePackStrings(String localizationTarget, String languagePackId, LanguagePackString[] strings) {
            this.localizationTarget = localizationTarget;
            this.languagePackId = languagePackId;
            this.strings = strings;
        }

        public static final int CONSTRUCTOR = -1056319886;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateConnectionState extends Update {
        public ConnectionState state;

        public UpdateConnectionState() {
        }

        public UpdateConnectionState(ConnectionState state) {
            this.state = state;
        }

        public static final int CONSTRUCTOR = 1469292078;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateTermsOfService extends Update {
        public String termsOfServiceId;
        public TermsOfService termsOfService;

        public UpdateTermsOfService() {
        }

        public UpdateTermsOfService(String termsOfServiceId, TermsOfService termsOfService) {
            this.termsOfServiceId = termsOfServiceId;
            this.termsOfService = termsOfService;
        }

        public static final int CONSTRUCTOR = -1304640162;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateNewInlineQuery extends Update {
        public long id;
        public int senderUserId;
        public Location userLocation;
        public String query;
        public String offset;

        public UpdateNewInlineQuery() {
        }

        public UpdateNewInlineQuery(long id, int senderUserId, Location userLocation, String query, String offset) {
            this.id = id;
            this.senderUserId = senderUserId;
            this.userLocation = userLocation;
            this.query = query;
            this.offset = offset;
        }

        public static final int CONSTRUCTOR = 2064730634;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateNewChosenInlineResult extends Update {
        public int senderUserId;
        public Location userLocation;
        public String query;
        public String resultId;
        public String inlineMessageId;

        public UpdateNewChosenInlineResult() {
        }

        public UpdateNewChosenInlineResult(int senderUserId, Location userLocation, String query, String resultId, String inlineMessageId) {
            this.senderUserId = senderUserId;
            this.userLocation = userLocation;
            this.query = query;
            this.resultId = resultId;
            this.inlineMessageId = inlineMessageId;
        }

        public static final int CONSTRUCTOR = 527526965;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateNewCallbackQuery extends Update {
        public long id;
        public int senderUserId;
        public long chatId;
        public long messageId;
        public long chatInstance;
        public CallbackQueryPayload payload;

        public UpdateNewCallbackQuery() {
        }

        public UpdateNewCallbackQuery(long id, int senderUserId, long chatId, long messageId, long chatInstance, CallbackQueryPayload payload) {
            this.id = id;
            this.senderUserId = senderUserId;
            this.chatId = chatId;
            this.messageId = messageId;
            this.chatInstance = chatInstance;
            this.payload = payload;
        }

        public static final int CONSTRUCTOR = -2044226370;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateNewInlineCallbackQuery extends Update {
        public long id;
        public int senderUserId;
        public String inlineMessageId;
        public long chatInstance;
        public CallbackQueryPayload payload;

        public UpdateNewInlineCallbackQuery() {
        }

        public UpdateNewInlineCallbackQuery(long id, int senderUserId, String inlineMessageId, long chatInstance, CallbackQueryPayload payload) {
            this.id = id;
            this.senderUserId = senderUserId;
            this.inlineMessageId = inlineMessageId;
            this.chatInstance = chatInstance;
            this.payload = payload;
        }

        public static final int CONSTRUCTOR = -1879154829;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateNewShippingQuery extends Update {
        public long id;
        public int senderUserId;
        public String invoicePayload;
        public Address shippingAddress;

        public UpdateNewShippingQuery() {
        }

        public UpdateNewShippingQuery(long id, int senderUserId, String invoicePayload, Address shippingAddress) {
            this.id = id;
            this.senderUserId = senderUserId;
            this.invoicePayload = invoicePayload;
            this.shippingAddress = shippingAddress;
        }

        public static final int CONSTRUCTOR = -817474682;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateNewPreCheckoutQuery extends Update {
        public long id;
        public int senderUserId;
        public String currency;
        public long totalAmount;
        public byte[] invoicePayload;
        public String shippingOptionId;
        public OrderInfo orderInfo;

        public UpdateNewPreCheckoutQuery() {
        }

        public UpdateNewPreCheckoutQuery(long id, int senderUserId, String currency, long totalAmount, byte[] invoicePayload, String shippingOptionId, OrderInfo orderInfo) {
            this.id = id;
            this.senderUserId = senderUserId;
            this.currency = currency;
            this.totalAmount = totalAmount;
            this.invoicePayload = invoicePayload;
            this.shippingOptionId = shippingOptionId;
            this.orderInfo = orderInfo;
        }

        public static final int CONSTRUCTOR = 87964006;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateNewCustomEvent extends Update {
        public String event;

        public UpdateNewCustomEvent() {
        }

        public UpdateNewCustomEvent(String event) {
            this.event = event;
        }

        public static final int CONSTRUCTOR = 1994222092;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpdateNewCustomQuery extends Update {
        public long id;
        public String data;
        public int timeout;

        public UpdateNewCustomQuery() {
        }

        public UpdateNewCustomQuery(long id, String data, int timeout) {
            this.id = id;
            this.data = data;
            this.timeout = timeout;
        }

        public static final int CONSTRUCTOR = -687670874;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class User extends Object {
        public int id;
        public String firstName;
        public String lastName;
        public String username;
        public String phoneNumber;
        public UserStatus status;
        public ProfilePhoto profilePhoto;
        public LinkState outgoingLink;
        public LinkState incomingLink;
        public boolean isVerified;
        public String restrictionReason;
        public boolean haveAccess;
        public UserType type;
        public String languageCode;

        public User() {
        }

        public User(int id, String firstName, String lastName, String username, String phoneNumber, UserStatus status, ProfilePhoto profilePhoto, LinkState outgoingLink, LinkState incomingLink, boolean isVerified, String restrictionReason, boolean haveAccess, UserType type, String languageCode) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.username = username;
            this.phoneNumber = phoneNumber;
            this.status = status;
            this.profilePhoto = profilePhoto;
            this.outgoingLink = outgoingLink;
            this.incomingLink = incomingLink;
            this.isVerified = isVerified;
            this.restrictionReason = restrictionReason;
            this.haveAccess = haveAccess;
            this.type = type;
            this.languageCode = languageCode;
        }

        public static final int CONSTRUCTOR = -732086407;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UserFullInfo extends Object {
        public boolean isBlocked;
        public boolean canBeCalled;
        public boolean hasPrivateCalls;
        public String bio;
        public String shareText;
        public int groupInCommonCount;
        public BotInfo botInfo;

        public UserFullInfo() {
        }

        public UserFullInfo(boolean isBlocked, boolean canBeCalled, boolean hasPrivateCalls, String bio, String shareText, int groupInCommonCount, BotInfo botInfo) {
            this.isBlocked = isBlocked;
            this.canBeCalled = canBeCalled;
            this.hasPrivateCalls = hasPrivateCalls;
            this.bio = bio;
            this.shareText = shareText;
            this.groupInCommonCount = groupInCommonCount;
            this.botInfo = botInfo;
        }

        public static final int CONSTRUCTOR = 1076948004;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class UserPrivacySetting extends Object {
    }

    public static class UserPrivacySettingShowStatus extends UserPrivacySetting {

        public UserPrivacySettingShowStatus() {
        }

        public static final int CONSTRUCTOR = 1862829310;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UserPrivacySettingAllowChatInvites extends UserPrivacySetting {

        public UserPrivacySettingAllowChatInvites() {
        }

        public static final int CONSTRUCTOR = 1271668007;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UserPrivacySettingAllowCalls extends UserPrivacySetting {

        public UserPrivacySettingAllowCalls() {
        }

        public static final int CONSTRUCTOR = -906967291;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class UserPrivacySettingRule extends Object {
    }

    public static class UserPrivacySettingRuleAllowAll extends UserPrivacySettingRule {

        public UserPrivacySettingRuleAllowAll() {
        }

        public static final int CONSTRUCTOR = -1967186881;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UserPrivacySettingRuleAllowContacts extends UserPrivacySettingRule {

        public UserPrivacySettingRuleAllowContacts() {
        }

        public static final int CONSTRUCTOR = -1892733680;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UserPrivacySettingRuleAllowUsers extends UserPrivacySettingRule {
        public int[] userIds;

        public UserPrivacySettingRuleAllowUsers() {
        }

        public UserPrivacySettingRuleAllowUsers(int[] userIds) {
            this.userIds = userIds;
        }

        public static final int CONSTRUCTOR = 427601278;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UserPrivacySettingRuleRestrictAll extends UserPrivacySettingRule {

        public UserPrivacySettingRuleRestrictAll() {
        }

        public static final int CONSTRUCTOR = -1406495408;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UserPrivacySettingRuleRestrictContacts extends UserPrivacySettingRule {

        public UserPrivacySettingRuleRestrictContacts() {
        }

        public static final int CONSTRUCTOR = 1008389378;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UserPrivacySettingRuleRestrictUsers extends UserPrivacySettingRule {
        public int[] userIds;

        public UserPrivacySettingRuleRestrictUsers() {
        }

        public UserPrivacySettingRuleRestrictUsers(int[] userIds) {
            this.userIds = userIds;
        }

        public static final int CONSTRUCTOR = 2119951802;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UserPrivacySettingRules extends Object {
        public UserPrivacySettingRule[] rules;

        public UserPrivacySettingRules() {
        }

        public UserPrivacySettingRules(UserPrivacySettingRule[] rules) {
            this.rules = rules;
        }

        public static final int CONSTRUCTOR = 322477541;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UserProfilePhotos extends Object {
        public int totalCount;
        public Photo[] photos;

        public UserProfilePhotos() {
        }

        public UserProfilePhotos(int totalCount, Photo[] photos) {
            this.totalCount = totalCount;
            this.photos = photos;
        }

        public static final int CONSTRUCTOR = 1388892074;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class UserStatus extends Object {
    }

    public static class UserStatusEmpty extends UserStatus {

        public UserStatusEmpty() {
        }

        public static final int CONSTRUCTOR = 164646985;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UserStatusOnline extends UserStatus {
        public int expires;

        public UserStatusOnline() {
        }

        public UserStatusOnline(int expires) {
            this.expires = expires;
        }

        public static final int CONSTRUCTOR = -1529460876;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UserStatusOffline extends UserStatus {
        public int wasOnline;

        public UserStatusOffline() {
        }

        public UserStatusOffline(int wasOnline) {
            this.wasOnline = wasOnline;
        }

        public static final int CONSTRUCTOR = -759984891;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UserStatusRecently extends UserStatus {

        public UserStatusRecently() {
        }

        public static final int CONSTRUCTOR = -496024847;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UserStatusLastWeek extends UserStatus {

        public UserStatusLastWeek() {
        }

        public static final int CONSTRUCTOR = 129960444;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UserStatusLastMonth extends UserStatus {

        public UserStatusLastMonth() {
        }

        public static final int CONSTRUCTOR = 2011940674;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public abstract static class UserType extends Object {
    }

    public static class UserTypeRegular extends UserType {

        public UserTypeRegular() {
        }

        public static final int CONSTRUCTOR = -598644325;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UserTypeDeleted extends UserType {

        public UserTypeDeleted() {
        }

        public static final int CONSTRUCTOR = -1807729372;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UserTypeBot extends UserType {
        public boolean canJoinGroups;
        public boolean canReadAllGroupMessages;
        public boolean isInline;
        public String inlineQueryPlaceholder;
        public boolean needLocation;

        public UserTypeBot() {
        }

        public UserTypeBot(boolean canJoinGroups, boolean canReadAllGroupMessages, boolean isInline, String inlineQueryPlaceholder, boolean needLocation) {
            this.canJoinGroups = canJoinGroups;
            this.canReadAllGroupMessages = canReadAllGroupMessages;
            this.isInline = isInline;
            this.inlineQueryPlaceholder = inlineQueryPlaceholder;
            this.needLocation = needLocation;
        }

        public static final int CONSTRUCTOR = 1262387765;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UserTypeUnknown extends UserType {

        public UserTypeUnknown() {
        }

        public static final int CONSTRUCTOR = -724541123;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Users extends Object {
        public int totalCount;
        public int[] userIds;

        public Users() {
        }

        public Users(int totalCount, int[] userIds) {
            this.totalCount = totalCount;
            this.userIds = userIds;
        }

        public static final int CONSTRUCTOR = 273760088;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ValidatedOrderInfo extends Object {
        public String orderInfoId;
        public ShippingOption[] shippingOptions;

        public ValidatedOrderInfo() {
        }

        public ValidatedOrderInfo(String orderInfoId, ShippingOption[] shippingOptions) {
            this.orderInfoId = orderInfoId;
            this.shippingOptions = shippingOptions;
        }

        public static final int CONSTRUCTOR = 1511451484;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Venue extends Object {
        public Location location;
        public String title;
        public String address;
        public String provider;
        public String id;
        public String type;

        public Venue() {
        }

        public Venue(Location location, String title, String address, String provider, String id, String type) {
            this.location = location;
            this.title = title;
            this.address = address;
            this.provider = provider;
            this.id = id;
            this.type = type;
        }

        public static final int CONSTRUCTOR = 1070406393;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Video extends Object {
        public int duration;
        public int width;
        public int height;
        public String fileName;
        public String mimeType;
        public boolean hasStickers;
        public boolean supportsStreaming;
        public PhotoSize thumbnail;
        public File video;

        public Video() {
        }

        public Video(int duration, int width, int height, String fileName, String mimeType, boolean hasStickers, boolean supportsStreaming, PhotoSize thumbnail, File video) {
            this.duration = duration;
            this.width = width;
            this.height = height;
            this.fileName = fileName;
            this.mimeType = mimeType;
            this.hasStickers = hasStickers;
            this.supportsStreaming = supportsStreaming;
            this.thumbnail = thumbnail;
            this.video = video;
        }

        public static final int CONSTRUCTOR = -437410347;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class VideoNote extends Object {
        public int duration;
        public int length;
        public PhotoSize thumbnail;
        public File video;

        public VideoNote() {
        }

        public VideoNote(int duration, int length, PhotoSize thumbnail, File video) {
            this.duration = duration;
            this.length = length;
            this.thumbnail = thumbnail;
            this.video = video;
        }

        public static final int CONSTRUCTOR = -1177396120;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class VoiceNote extends Object {
        public int duration;
        public byte[] waveform;
        public String mimeType;
        public File voice;

        public VoiceNote() {
        }

        public VoiceNote(int duration, byte[] waveform, String mimeType, File voice) {
            this.duration = duration;
            this.waveform = waveform;
            this.mimeType = mimeType;
            this.voice = voice;
        }

        public static final int CONSTRUCTOR = -2066012058;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Wallpaper extends Object {
        public int id;
        public PhotoSize[] sizes;
        public int color;

        public Wallpaper() {
        }

        public Wallpaper(int id, PhotoSize[] sizes, int color) {
            this.id = id;
            this.sizes = sizes;
            this.color = color;
        }

        public static final int CONSTRUCTOR = 282771691;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Wallpapers extends Object {
        public Wallpaper[] wallpapers;

        public Wallpapers() {
        }

        public Wallpapers(Wallpaper[] wallpapers) {
            this.wallpapers = wallpapers;
        }

        public static final int CONSTRUCTOR = 877926640;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class WebPage extends Object {
        public String url;
        public String displayUrl;
        public String type;
        public String siteName;
        public String title;
        public String description;
        public Photo photo;
        public String embedUrl;
        public String embedType;
        public int embedWidth;
        public int embedHeight;
        public int duration;
        public String author;
        public Animation animation;
        public Audio audio;
        public Document document;
        public Sticker sticker;
        public Video video;
        public VideoNote videoNote;
        public VoiceNote voiceNote;
        public boolean hasInstantView;

        public WebPage() {
        }

        public WebPage(String url, String displayUrl, String type, String siteName, String title, String description, Photo photo, String embedUrl, String embedType, int embedWidth, int embedHeight, int duration, String author, Animation animation, Audio audio, Document document, Sticker sticker, Video video, VideoNote videoNote, VoiceNote voiceNote, boolean hasInstantView) {
            this.url = url;
            this.displayUrl = displayUrl;
            this.type = type;
            this.siteName = siteName;
            this.title = title;
            this.description = description;
            this.photo = photo;
            this.embedUrl = embedUrl;
            this.embedType = embedType;
            this.embedWidth = embedWidth;
            this.embedHeight = embedHeight;
            this.duration = duration;
            this.author = author;
            this.animation = animation;
            this.audio = audio;
            this.document = document;
            this.sticker = sticker;
            this.video = video;
            this.videoNote = videoNote;
            this.voiceNote = voiceNote;
            this.hasInstantView = hasInstantView;
        }

        public static final int CONSTRUCTOR = 1465949075;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class WebPageInstantView extends Object {
        public PageBlock[] pageBlocks;
        public boolean isFull;

        public WebPageInstantView() {
        }

        public WebPageInstantView(PageBlock[] pageBlocks, boolean isFull) {
            this.pageBlocks = pageBlocks;
            this.isFull = isFull;
        }

        public static final int CONSTRUCTOR = 1804324850;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AcceptCall extends Function {
        public int callId;
        public CallProtocol protocol;

        public AcceptCall() {
        }

        public AcceptCall(int callId, CallProtocol protocol) {
            this.callId = callId;
            this.protocol = protocol;
        }

        public static final int CONSTRUCTOR = -646618416;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AcceptTermsOfService extends Function {
        public String termsOfServiceId;

        public AcceptTermsOfService() {
        }

        public AcceptTermsOfService(String termsOfServiceId) {
            this.termsOfServiceId = termsOfServiceId;
        }

        public static final int CONSTRUCTOR = 2130576356;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AddChatMember extends Function {
        public long chatId;
        public int userId;
        public int forwardLimit;

        public AddChatMember() {
        }

        public AddChatMember(long chatId, int userId, int forwardLimit) {
            this.chatId = chatId;
            this.userId = userId;
            this.forwardLimit = forwardLimit;
        }

        public static final int CONSTRUCTOR = 1182817962;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AddChatMembers extends Function {
        public long chatId;
        public int[] userIds;

        public AddChatMembers() {
        }

        public AddChatMembers(long chatId, int[] userIds) {
            this.chatId = chatId;
            this.userIds = userIds;
        }

        public static final int CONSTRUCTOR = 1234094617;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AddFavoriteSticker extends Function {
        public InputFile sticker;

        public AddFavoriteSticker() {
        }

        public AddFavoriteSticker(InputFile sticker) {
            this.sticker = sticker;
        }

        public static final int CONSTRUCTOR = 324504799;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AddLocalMessage extends Function {
        public long chatId;
        public int senderUserId;
        public long replyToMessageId;
        public boolean disableNotification;
        public InputMessageContent inputMessageContent;

        public AddLocalMessage() {
        }

        public AddLocalMessage(long chatId, int senderUserId, long replyToMessageId, boolean disableNotification, InputMessageContent inputMessageContent) {
            this.chatId = chatId;
            this.senderUserId = senderUserId;
            this.replyToMessageId = replyToMessageId;
            this.disableNotification = disableNotification;
            this.inputMessageContent = inputMessageContent;
        }

        public static final int CONSTRUCTOR = -348943149;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AddNetworkStatistics extends Function {
        public NetworkStatisticsEntry entry;

        public AddNetworkStatistics() {
        }

        public AddNetworkStatistics(NetworkStatisticsEntry entry) {
            this.entry = entry;
        }

        public static final int CONSTRUCTOR = 1264825305;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AddProxy extends Function {
        public String server;
        public int port;
        public boolean enable;
        public ProxyType type;

        public AddProxy() {
        }

        public AddProxy(String server, int port, boolean enable, ProxyType type) {
            this.server = server;
            this.port = port;
            this.enable = enable;
            this.type = type;
        }

        public static final int CONSTRUCTOR = 331529432;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AddRecentSticker extends Function {
        public boolean isAttached;
        public InputFile sticker;

        public AddRecentSticker() {
        }

        public AddRecentSticker(boolean isAttached, InputFile sticker) {
            this.isAttached = isAttached;
            this.sticker = sticker;
        }

        public static final int CONSTRUCTOR = -1478109026;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AddRecentlyFoundChat extends Function {
        public long chatId;

        public AddRecentlyFoundChat() {
        }

        public AddRecentlyFoundChat(long chatId) {
            this.chatId = chatId;
        }

        public static final int CONSTRUCTOR = -1746396787;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AddSavedAnimation extends Function {
        public InputFile animation;

        public AddSavedAnimation() {
        }

        public AddSavedAnimation(InputFile animation) {
            this.animation = animation;
        }

        public static final int CONSTRUCTOR = -1538525088;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AddStickerToSet extends Function {
        public int userId;
        public String name;
        public InputSticker sticker;

        public AddStickerToSet() {
        }

        public AddStickerToSet(int userId, String name, InputSticker sticker) {
            this.userId = userId;
            this.name = name;
            this.sticker = sticker;
        }

        public static final int CONSTRUCTOR = 1422402800;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AnswerCallbackQuery extends Function {
        public long callbackQueryId;
        public String text;
        public boolean showAlert;
        public String url;
        public int cacheTime;

        public AnswerCallbackQuery() {
        }

        public AnswerCallbackQuery(long callbackQueryId, String text, boolean showAlert, String url, int cacheTime) {
            this.callbackQueryId = callbackQueryId;
            this.text = text;
            this.showAlert = showAlert;
            this.url = url;
            this.cacheTime = cacheTime;
        }

        public static final int CONSTRUCTOR = -1153028490;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AnswerCustomQuery extends Function {
        public long customQueryId;
        public String data;

        public AnswerCustomQuery() {
        }

        public AnswerCustomQuery(long customQueryId, String data) {
            this.customQueryId = customQueryId;
            this.data = data;
        }

        public static final int CONSTRUCTOR = -1293603521;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AnswerInlineQuery extends Function {
        public long inlineQueryId;
        public boolean isPersonal;
        public InputInlineQueryResult[] results;
        public int cacheTime;
        public String nextOffset;
        public String switchPmText;
        public String switchPmParameter;

        public AnswerInlineQuery() {
        }

        public AnswerInlineQuery(long inlineQueryId, boolean isPersonal, InputInlineQueryResult[] results, int cacheTime, String nextOffset, String switchPmText, String switchPmParameter) {
            this.inlineQueryId = inlineQueryId;
            this.isPersonal = isPersonal;
            this.results = results;
            this.cacheTime = cacheTime;
            this.nextOffset = nextOffset;
            this.switchPmText = switchPmText;
            this.switchPmParameter = switchPmParameter;
        }

        public static final int CONSTRUCTOR = 485879477;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AnswerPreCheckoutQuery extends Function {
        public long preCheckoutQueryId;
        public String errorMessage;

        public AnswerPreCheckoutQuery() {
        }

        public AnswerPreCheckoutQuery(long preCheckoutQueryId, String errorMessage) {
            this.preCheckoutQueryId = preCheckoutQueryId;
            this.errorMessage = errorMessage;
        }

        public static final int CONSTRUCTOR = -1486789653;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class AnswerShippingQuery extends Function {
        public long shippingQueryId;
        public ShippingOption[] shippingOptions;
        public String errorMessage;

        public AnswerShippingQuery() {
        }

        public AnswerShippingQuery(long shippingQueryId, ShippingOption[] shippingOptions, String errorMessage) {
            this.shippingQueryId = shippingQueryId;
            this.shippingOptions = shippingOptions;
            this.errorMessage = errorMessage;
        }

        public static final int CONSTRUCTOR = -434601324;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class BlockUser extends Function {
        public int userId;

        public BlockUser() {
        }

        public BlockUser(int userId) {
            this.userId = userId;
        }

        public static final int CONSTRUCTOR = -1239315139;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CancelDownloadFile extends Function {
        public int fileId;
        public boolean onlyIfPending;

        public CancelDownloadFile() {
        }

        public CancelDownloadFile(int fileId, boolean onlyIfPending) {
            this.fileId = fileId;
            this.onlyIfPending = onlyIfPending;
        }

        public static final int CONSTRUCTOR = -1954524450;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CancelUploadFile extends Function {
        public int fileId;

        public CancelUploadFile() {
        }

        public CancelUploadFile(int fileId) {
            this.fileId = fileId;
        }

        public static final int CONSTRUCTOR = 1623539600;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChangeChatReportSpamState extends Function {
        public long chatId;
        public boolean isSpamChat;

        public ChangeChatReportSpamState() {
        }

        public ChangeChatReportSpamState(long chatId, boolean isSpamChat) {
            this.chatId = chatId;
            this.isSpamChat = isSpamChat;
        }

        public static final int CONSTRUCTOR = 1768597097;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChangeImportedContacts extends Function {
        public Contact[] contacts;

        public ChangeImportedContacts() {
        }

        public ChangeImportedContacts(Contact[] contacts) {
            this.contacts = contacts;
        }

        public static final int CONSTRUCTOR = 1968207955;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChangePhoneNumber extends Function {
        public String phoneNumber;
        public boolean allowFlashCall;
        public boolean isCurrentPhoneNumber;

        public ChangePhoneNumber() {
        }

        public ChangePhoneNumber(String phoneNumber, boolean allowFlashCall, boolean isCurrentPhoneNumber) {
            this.phoneNumber = phoneNumber;
            this.allowFlashCall = allowFlashCall;
            this.isCurrentPhoneNumber = isCurrentPhoneNumber;
        }

        public static final int CONSTRUCTOR = -1510625218;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ChangeStickerSet extends Function {
        public long setId;
        public boolean isInstalled;
        public boolean isArchived;

        public ChangeStickerSet() {
        }

        public ChangeStickerSet(long setId, boolean isInstalled, boolean isArchived) {
            this.setId = setId;
            this.isInstalled = isInstalled;
            this.isArchived = isArchived;
        }

        public static final int CONSTRUCTOR = 449357293;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CheckAuthenticationBotToken extends Function {
        public String token;

        public CheckAuthenticationBotToken() {
        }

        public CheckAuthenticationBotToken(String token) {
            this.token = token;
        }

        public static final int CONSTRUCTOR = 639321206;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CheckAuthenticationCode extends Function {
        public String code;
        public String firstName;
        public String lastName;

        public CheckAuthenticationCode() {
        }

        public CheckAuthenticationCode(String code, String firstName, String lastName) {
            this.code = code;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public static final int CONSTRUCTOR = -707293555;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CheckAuthenticationPassword extends Function {
        public String password;

        public CheckAuthenticationPassword() {
        }

        public CheckAuthenticationPassword(String password) {
            this.password = password;
        }

        public static final int CONSTRUCTOR = -2025698400;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CheckChangePhoneNumberCode extends Function {
        public String code;

        public CheckChangePhoneNumberCode() {
        }

        public CheckChangePhoneNumberCode(String code) {
            this.code = code;
        }

        public static final int CONSTRUCTOR = -1720278429;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CheckChatInviteLink extends Function {
        public String inviteLink;

        public CheckChatInviteLink() {
        }

        public CheckChatInviteLink(String inviteLink) {
            this.inviteLink = inviteLink;
        }

        public static final int CONSTRUCTOR = -496940997;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CheckChatUsername extends Function {
        public long chatId;
        public String username;

        public CheckChatUsername() {
        }

        public CheckChatUsername(long chatId, String username) {
            this.chatId = chatId;
            this.username = username;
        }

        public static final int CONSTRUCTOR = -2003506154;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CheckDatabaseEncryptionKey extends Function {
        public byte[] encryptionKey;

        public CheckDatabaseEncryptionKey() {
        }

        public CheckDatabaseEncryptionKey(byte[] encryptionKey) {
            this.encryptionKey = encryptionKey;
        }

        public static final int CONSTRUCTOR = 1018769307;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CheckEmailAddressVerificationCode extends Function {
        public String code;

        public CheckEmailAddressVerificationCode() {
        }

        public CheckEmailAddressVerificationCode(String code) {
            this.code = code;
        }

        public static final int CONSTRUCTOR = -426386685;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CheckPhoneNumberConfirmationCode extends Function {
        public String code;

        public CheckPhoneNumberConfirmationCode() {
        }

        public CheckPhoneNumberConfirmationCode(String code) {
            this.code = code;
        }

        public static final int CONSTRUCTOR = -1348060966;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CheckPhoneNumberVerificationCode extends Function {
        public String code;

        public CheckPhoneNumberVerificationCode() {
        }

        public CheckPhoneNumberVerificationCode(String code) {
            this.code = code;
        }

        public static final int CONSTRUCTOR = 1497462718;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CleanFileName extends Function {
        public String fileName;

        public CleanFileName() {
        }

        public CleanFileName(String fileName) {
            this.fileName = fileName;
        }

        public static final int CONSTRUCTOR = 967964667;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ClearAllDraftMessages extends Function {
        public boolean excludeSecretChats;

        public ClearAllDraftMessages() {
        }

        public ClearAllDraftMessages(boolean excludeSecretChats) {
            this.excludeSecretChats = excludeSecretChats;
        }

        public static final int CONSTRUCTOR = -46369573;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ClearImportedContacts extends Function {

        public ClearImportedContacts() {
        }

        public static final int CONSTRUCTOR = 869503298;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ClearRecentStickers extends Function {
        public boolean isAttached;

        public ClearRecentStickers() {
        }

        public ClearRecentStickers(boolean isAttached) {
            this.isAttached = isAttached;
        }

        public static final int CONSTRUCTOR = -321242684;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ClearRecentlyFoundChats extends Function {

        public ClearRecentlyFoundChats() {
        }

        public static final int CONSTRUCTOR = -285582542;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Close extends Function {

        public Close() {
        }

        public static final int CONSTRUCTOR = -1187782273;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CloseChat extends Function {
        public long chatId;

        public CloseChat() {
        }

        public CloseChat(long chatId) {
            this.chatId = chatId;
        }

        public static final int CONSTRUCTOR = 39749353;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CloseSecretChat extends Function {
        public int secretChatId;

        public CloseSecretChat() {
        }

        public CloseSecretChat(int secretChatId) {
            this.secretChatId = secretChatId;
        }

        public static final int CONSTRUCTOR = -471006133;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CreateBasicGroupChat extends Function {
        public int basicGroupId;
        public boolean force;

        public CreateBasicGroupChat() {
        }

        public CreateBasicGroupChat(int basicGroupId, boolean force) {
            this.basicGroupId = basicGroupId;
            this.force = force;
        }

        public static final int CONSTRUCTOR = 642492777;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CreateCall extends Function {
        public int userId;
        public CallProtocol protocol;

        public CreateCall() {
        }

        public CreateCall(int userId, CallProtocol protocol) {
            this.userId = userId;
            this.protocol = protocol;
        }

        public static final int CONSTRUCTOR = -1742408159;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CreateNewBasicGroupChat extends Function {
        public int[] userIds;
        public String title;

        public CreateNewBasicGroupChat() {
        }

        public CreateNewBasicGroupChat(int[] userIds, String title) {
            this.userIds = userIds;
            this.title = title;
        }

        public static final int CONSTRUCTOR = 1874532069;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CreateNewSecretChat extends Function {
        public int userId;

        public CreateNewSecretChat() {
        }

        public CreateNewSecretChat(int userId) {
            this.userId = userId;
        }

        public static final int CONSTRUCTOR = 1689344881;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CreateNewStickerSet extends Function {
        public int userId;
        public String title;
        public String name;
        public boolean isMasks;
        public InputSticker[] stickers;

        public CreateNewStickerSet() {
        }

        public CreateNewStickerSet(int userId, String title, String name, boolean isMasks, InputSticker[] stickers) {
            this.userId = userId;
            this.title = title;
            this.name = name;
            this.isMasks = isMasks;
            this.stickers = stickers;
        }

        public static final int CONSTRUCTOR = 205093058;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CreateNewSupergroupChat extends Function {
        public String title;
        public boolean isChannel;
        public String description;

        public CreateNewSupergroupChat() {
        }

        public CreateNewSupergroupChat(String title, boolean isChannel, String description) {
            this.title = title;
            this.isChannel = isChannel;
            this.description = description;
        }

        public static final int CONSTRUCTOR = 1284982268;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CreatePrivateChat extends Function {
        public int userId;
        public boolean force;

        public CreatePrivateChat() {
        }

        public CreatePrivateChat(int userId, boolean force) {
            this.userId = userId;
            this.force = force;
        }

        public static final int CONSTRUCTOR = -1807530364;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CreateSecretChat extends Function {
        public int secretChatId;

        public CreateSecretChat() {
        }

        public CreateSecretChat(int secretChatId) {
            this.secretChatId = secretChatId;
        }

        public static final int CONSTRUCTOR = 1930285615;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CreateSupergroupChat extends Function {
        public int supergroupId;
        public boolean force;

        public CreateSupergroupChat() {
        }

        public CreateSupergroupChat(int supergroupId, boolean force) {
            this.supergroupId = supergroupId;
            this.force = force;
        }

        public static final int CONSTRUCTOR = 352742758;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class CreateTemporaryPassword extends Function {
        public String password;
        public int validFor;

        public CreateTemporaryPassword() {
        }

        public CreateTemporaryPassword(String password, int validFor) {
            this.password = password;
            this.validFor = validFor;
        }

        public static final int CONSTRUCTOR = -1626509434;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DeleteAccount extends Function {
        public String reason;

        public DeleteAccount() {
        }

        public DeleteAccount(String reason) {
            this.reason = reason;
        }

        public static final int CONSTRUCTOR = -1203056508;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DeleteChatHistory extends Function {
        public long chatId;
        public boolean removeFromChatList;

        public DeleteChatHistory() {
        }

        public DeleteChatHistory(long chatId, boolean removeFromChatList) {
            this.chatId = chatId;
            this.removeFromChatList = removeFromChatList;
        }

        public static final int CONSTRUCTOR = -1384632722;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DeleteChatMessagesFromUser extends Function {
        public long chatId;
        public int userId;

        public DeleteChatMessagesFromUser() {
        }

        public DeleteChatMessagesFromUser(long chatId, int userId) {
            this.chatId = chatId;
            this.userId = userId;
        }

        public static final int CONSTRUCTOR = -1599689199;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DeleteChatReplyMarkup extends Function {
        public long chatId;
        public long messageId;

        public DeleteChatReplyMarkup() {
        }

        public DeleteChatReplyMarkup(long chatId, long messageId) {
            this.chatId = chatId;
            this.messageId = messageId;
        }

        public static final int CONSTRUCTOR = 100637531;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DeleteFile extends Function {
        public int fileId;

        public DeleteFile() {
        }

        public DeleteFile(int fileId) {
            this.fileId = fileId;
        }

        public static final int CONSTRUCTOR = 1807653676;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DeleteLanguagePack extends Function {
        public String languagePackId;

        public DeleteLanguagePack() {
        }

        public DeleteLanguagePack(String languagePackId) {
            this.languagePackId = languagePackId;
        }

        public static final int CONSTRUCTOR = -2108761026;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DeleteMessages extends Function {
        public long chatId;
        public long[] messageIds;
        public boolean revoke;

        public DeleteMessages() {
        }

        public DeleteMessages(long chatId, long[] messageIds, boolean revoke) {
            this.chatId = chatId;
            this.messageIds = messageIds;
            this.revoke = revoke;
        }

        public static final int CONSTRUCTOR = 1130090173;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DeletePassportElement extends Function {
        public PassportElementType type;

        public DeletePassportElement() {
        }

        public DeletePassportElement(PassportElementType type) {
            this.type = type;
        }

        public static final int CONSTRUCTOR = -1719555468;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DeleteProfilePhoto extends Function {
        public long profilePhotoId;

        public DeleteProfilePhoto() {
        }

        public DeleteProfilePhoto(long profilePhotoId) {
            this.profilePhotoId = profilePhotoId;
        }

        public static final int CONSTRUCTOR = 1319794625;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DeleteSavedCredentials extends Function {

        public DeleteSavedCredentials() {
        }

        public static final int CONSTRUCTOR = 826300114;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DeleteSavedOrderInfo extends Function {

        public DeleteSavedOrderInfo() {
        }

        public static final int CONSTRUCTOR = 1629058164;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DeleteSupergroup extends Function {
        public int supergroupId;

        public DeleteSupergroup() {
        }

        public DeleteSupergroup(int supergroupId) {
            this.supergroupId = supergroupId;
        }

        public static final int CONSTRUCTOR = -1999855965;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class Destroy extends Function {

        public Destroy() {
        }

        public static final int CONSTRUCTOR = 685331274;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DisableProxy extends Function {

        public DisableProxy() {
        }

        public static final int CONSTRUCTOR = -2100095102;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DiscardCall extends Function {
        public int callId;
        public boolean isDisconnected;
        public int duration;
        public long connectionId;

        public DiscardCall() {
        }

        public DiscardCall(int callId, boolean isDisconnected, int duration, long connectionId) {
            this.callId = callId;
            this.isDisconnected = isDisconnected;
            this.duration = duration;
            this.connectionId = connectionId;
        }

        public static final int CONSTRUCTOR = -923187372;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DisconnectAllWebsites extends Function {

        public DisconnectAllWebsites() {
        }

        public static final int CONSTRUCTOR = -1082985981;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DisconnectWebsite extends Function {
        public long websiteId;

        public DisconnectWebsite() {
        }

        public DisconnectWebsite(long websiteId) {
            this.websiteId = websiteId;
        }

        public static final int CONSTRUCTOR = -778767395;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class DownloadFile extends Function {
        public int fileId;
        public int priority;

        public DownloadFile() {
        }

        public DownloadFile(int fileId, int priority) {
            this.fileId = fileId;
            this.priority = priority;
        }

        public static final int CONSTRUCTOR = 1531851978;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class EditCustomLanguagePackInfo extends Function {
        public LanguagePackInfo info;

        public EditCustomLanguagePackInfo() {
        }

        public EditCustomLanguagePackInfo(LanguagePackInfo info) {
            this.info = info;
        }

        public static final int CONSTRUCTOR = 1320751257;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class EditInlineMessageCaption extends Function {
        public String inlineMessageId;
        public ReplyMarkup replyMarkup;
        public FormattedText caption;

        public EditInlineMessageCaption() {
        }

        public EditInlineMessageCaption(String inlineMessageId, ReplyMarkup replyMarkup, FormattedText caption) {
            this.inlineMessageId = inlineMessageId;
            this.replyMarkup = replyMarkup;
            this.caption = caption;
        }

        public static final int CONSTRUCTOR = -760985929;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class EditInlineMessageLiveLocation extends Function {
        public String inlineMessageId;
        public ReplyMarkup replyMarkup;
        public Location location;

        public EditInlineMessageLiveLocation() {
        }

        public EditInlineMessageLiveLocation(String inlineMessageId, ReplyMarkup replyMarkup, Location location) {
            this.inlineMessageId = inlineMessageId;
            this.replyMarkup = replyMarkup;
            this.location = location;
        }

        public static final int CONSTRUCTOR = 655046316;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class EditInlineMessageMedia extends Function {
        public String inlineMessageId;
        public ReplyMarkup replyMarkup;
        public InputMessageContent inputMessageContent;

        public EditInlineMessageMedia() {
        }

        public EditInlineMessageMedia(String inlineMessageId, ReplyMarkup replyMarkup, InputMessageContent inputMessageContent) {
            this.inlineMessageId = inlineMessageId;
            this.replyMarkup = replyMarkup;
            this.inputMessageContent = inputMessageContent;
        }

        public static final int CONSTRUCTOR = 23553921;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class EditInlineMessageReplyMarkup extends Function {
        public String inlineMessageId;
        public ReplyMarkup replyMarkup;

        public EditInlineMessageReplyMarkup() {
        }

        public EditInlineMessageReplyMarkup(String inlineMessageId, ReplyMarkup replyMarkup) {
            this.inlineMessageId = inlineMessageId;
            this.replyMarkup = replyMarkup;
        }

        public static final int CONSTRUCTOR = -67565858;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class EditInlineMessageText extends Function {
        public String inlineMessageId;
        public ReplyMarkup replyMarkup;
        public InputMessageContent inputMessageContent;

        public EditInlineMessageText() {
        }

        public EditInlineMessageText(String inlineMessageId, ReplyMarkup replyMarkup, InputMessageContent inputMessageContent) {
            this.inlineMessageId = inlineMessageId;
            this.replyMarkup = replyMarkup;
            this.inputMessageContent = inputMessageContent;
        }

        public static final int CONSTRUCTOR = -855457307;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class EditMessageCaption extends Function {
        public long chatId;
        public long messageId;
        public ReplyMarkup replyMarkup;
        public FormattedText caption;

        public EditMessageCaption() {
        }

        public EditMessageCaption(long chatId, long messageId, ReplyMarkup replyMarkup, FormattedText caption) {
            this.chatId = chatId;
            this.messageId = messageId;
            this.replyMarkup = replyMarkup;
            this.caption = caption;
        }

        public static final int CONSTRUCTOR = 1154677038;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class EditMessageLiveLocation extends Function {
        public long chatId;
        public long messageId;
        public ReplyMarkup replyMarkup;
        public Location location;

        public EditMessageLiveLocation() {
        }

        public EditMessageLiveLocation(long chatId, long messageId, ReplyMarkup replyMarkup, Location location) {
            this.chatId = chatId;
            this.messageId = messageId;
            this.replyMarkup = replyMarkup;
            this.location = location;
        }

        public static final int CONSTRUCTOR = -1146772745;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class EditMessageMedia extends Function {
        public long chatId;
        public long messageId;
        public ReplyMarkup replyMarkup;
        public InputMessageContent inputMessageContent;

        public EditMessageMedia() {
        }

        public EditMessageMedia(long chatId, long messageId, ReplyMarkup replyMarkup, InputMessageContent inputMessageContent) {
            this.chatId = chatId;
            this.messageId = messageId;
            this.replyMarkup = replyMarkup;
            this.inputMessageContent = inputMessageContent;
        }

        public static final int CONSTRUCTOR = -1152678125;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class EditMessageReplyMarkup extends Function {
        public long chatId;
        public long messageId;
        public ReplyMarkup replyMarkup;

        public EditMessageReplyMarkup() {
        }

        public EditMessageReplyMarkup(long chatId, long messageId, ReplyMarkup replyMarkup) {
            this.chatId = chatId;
            this.messageId = messageId;
            this.replyMarkup = replyMarkup;
        }

        public static final int CONSTRUCTOR = 332127881;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class EditMessageText extends Function {
        public long chatId;
        public long messageId;
        public ReplyMarkup replyMarkup;
        public InputMessageContent inputMessageContent;

        public EditMessageText() {
        }

        public EditMessageText(long chatId, long messageId, ReplyMarkup replyMarkup, InputMessageContent inputMessageContent) {
            this.chatId = chatId;
            this.messageId = messageId;
            this.replyMarkup = replyMarkup;
            this.inputMessageContent = inputMessageContent;
        }

        public static final int CONSTRUCTOR = 196272567;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class EditProxy extends Function {
        public int proxyId;
        public String server;
        public int port;
        public boolean enable;
        public ProxyType type;

        public EditProxy() {
        }

        public EditProxy(int proxyId, String server, int port, boolean enable, ProxyType type) {
            this.proxyId = proxyId;
            this.server = server;
            this.port = port;
            this.enable = enable;
            this.type = type;
        }

        public static final int CONSTRUCTOR = -1605883821;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class EnableProxy extends Function {
        public int proxyId;

        public EnableProxy() {
        }

        public EnableProxy(int proxyId) {
            this.proxyId = proxyId;
        }

        public static final int CONSTRUCTOR = 1494450838;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class FinishFileGeneration extends Function {
        public long generationId;
        public Error error;

        public FinishFileGeneration() {
        }

        public FinishFileGeneration(long generationId, Error error) {
            this.generationId = generationId;
            this.error = error;
        }

        public static final int CONSTRUCTOR = -1055060835;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ForwardMessages extends Function {
        public long chatId;
        public long fromChatId;
        public long[] messageIds;
        public boolean disableNotification;
        public boolean fromBackground;
        public boolean asAlbum;

        public ForwardMessages() {
        }

        public ForwardMessages(long chatId, long fromChatId, long[] messageIds, boolean disableNotification, boolean fromBackground, boolean asAlbum) {
            this.chatId = chatId;
            this.fromChatId = fromChatId;
            this.messageIds = messageIds;
            this.disableNotification = disableNotification;
            this.fromBackground = fromBackground;
            this.asAlbum = asAlbum;
        }

        public static final int CONSTRUCTOR = -537573308;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GenerateChatInviteLink extends Function {
        public long chatId;

        public GenerateChatInviteLink() {
        }

        public GenerateChatInviteLink(long chatId) {
            this.chatId = chatId;
        }

        public static final int CONSTRUCTOR = 1945532500;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetAccountTtl extends Function {

        public GetAccountTtl() {
        }

        public static final int CONSTRUCTOR = -443905161;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetActiveLiveLocationMessages extends Function {

        public GetActiveLiveLocationMessages() {
        }

        public static final int CONSTRUCTOR = -1425459567;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetActiveSessions extends Function {

        public GetActiveSessions() {
        }

        public static final int CONSTRUCTOR = 1119710526;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetAllPassportElements extends Function {
        public String password;

        public GetAllPassportElements() {
        }

        public GetAllPassportElements(String password) {
            this.password = password;
        }

        public static final int CONSTRUCTOR = -2038945045;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetArchivedStickerSets extends Function {
        public boolean isMasks;
        public long offsetStickerSetId;
        public int limit;

        public GetArchivedStickerSets() {
        }

        public GetArchivedStickerSets(boolean isMasks, long offsetStickerSetId, int limit) {
            this.isMasks = isMasks;
            this.offsetStickerSetId = offsetStickerSetId;
            this.limit = limit;
        }

        public static final int CONSTRUCTOR = 1996943238;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetAttachedStickerSets extends Function {
        public int fileId;

        public GetAttachedStickerSets() {
        }

        public GetAttachedStickerSets(int fileId) {
            this.fileId = fileId;
        }

        public static final int CONSTRUCTOR = 1302172429;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetAuthorizationState extends Function {

        public GetAuthorizationState() {
        }

        public static final int CONSTRUCTOR = 1949154877;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetBasicGroup extends Function {
        public int basicGroupId;

        public GetBasicGroup() {
        }

        public GetBasicGroup(int basicGroupId) {
            this.basicGroupId = basicGroupId;
        }

        public static final int CONSTRUCTOR = 561775568;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetBasicGroupFullInfo extends Function {
        public int basicGroupId;

        public GetBasicGroupFullInfo() {
        }

        public GetBasicGroupFullInfo(int basicGroupId) {
            this.basicGroupId = basicGroupId;
        }

        public static final int CONSTRUCTOR = 1770517905;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetBlockedUsers extends Function {
        public int offset;
        public int limit;

        public GetBlockedUsers() {
        }

        public GetBlockedUsers(int offset, int limit) {
            this.offset = offset;
            this.limit = limit;
        }

        public static final int CONSTRUCTOR = -742912777;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetCallbackQueryAnswer extends Function {
        public long chatId;
        public long messageId;
        public CallbackQueryPayload payload;

        public GetCallbackQueryAnswer() {
        }

        public GetCallbackQueryAnswer(long chatId, long messageId, CallbackQueryPayload payload) {
            this.chatId = chatId;
            this.messageId = messageId;
            this.payload = payload;
        }

        public static final int CONSTRUCTOR = 116357727;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetChat extends Function {
        public long chatId;

        public GetChat() {
        }

        public GetChat(long chatId) {
            this.chatId = chatId;
        }

        public static final int CONSTRUCTOR = 1866601536;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetChatAdministrators extends Function {
        public long chatId;

        public GetChatAdministrators() {
        }

        public GetChatAdministrators(long chatId) {
            this.chatId = chatId;
        }

        public static final int CONSTRUCTOR = 508231041;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetChatEventLog extends Function {
        public long chatId;
        public String query;
        public long fromEventId;
        public int limit;
        public ChatEventLogFilters filters;
        public int[] userIds;

        public GetChatEventLog() {
        }

        public GetChatEventLog(long chatId, String query, long fromEventId, int limit, ChatEventLogFilters filters, int[] userIds) {
            this.chatId = chatId;
            this.query = query;
            this.fromEventId = fromEventId;
            this.limit = limit;
            this.filters = filters;
            this.userIds = userIds;
        }

        public static final int CONSTRUCTOR = 206900967;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetChatHistory extends Function {
        public long chatId;
        public long fromMessageId;
        public int offset;
        public int limit;
        public boolean onlyLocal;

        public GetChatHistory() {
        }

        public GetChatHistory(long chatId, long fromMessageId, int offset, int limit, boolean onlyLocal) {
            this.chatId = chatId;
            this.fromMessageId = fromMessageId;
            this.offset = offset;
            this.limit = limit;
            this.onlyLocal = onlyLocal;
        }

        public static final int CONSTRUCTOR = -799960451;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetChatMember extends Function {
        public long chatId;
        public int userId;

        public GetChatMember() {
        }

        public GetChatMember(long chatId, int userId) {
            this.chatId = chatId;
            this.userId = userId;
        }

        public static final int CONSTRUCTOR = 677085892;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetChatMessageByDate extends Function {
        public long chatId;
        public int date;

        public GetChatMessageByDate() {
        }

        public GetChatMessageByDate(long chatId, int date) {
            this.chatId = chatId;
            this.date = date;
        }

        public static final int CONSTRUCTOR = 1062564150;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetChatMessageCount extends Function {
        public long chatId;
        public SearchMessagesFilter filter;
        public boolean returnLocal;

        public GetChatMessageCount() {
        }

        public GetChatMessageCount(long chatId, SearchMessagesFilter filter, boolean returnLocal) {
            this.chatId = chatId;
            this.filter = filter;
            this.returnLocal = returnLocal;
        }

        public static final int CONSTRUCTOR = 205435308;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetChatPinnedMessage extends Function {
        public long chatId;

        public GetChatPinnedMessage() {
        }

        public GetChatPinnedMessage(long chatId) {
            this.chatId = chatId;
        }

        public static final int CONSTRUCTOR = 359865008;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetChatReportSpamState extends Function {
        public long chatId;

        public GetChatReportSpamState() {
        }

        public GetChatReportSpamState(long chatId) {
            this.chatId = chatId;
        }

        public static final int CONSTRUCTOR = -748866856;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetChats extends Function {
        public long offsetOrder;
        public long offsetChatId;
        public int limit;

        public GetChats() {
        }

        public GetChats(long offsetOrder, long offsetChatId, int limit) {
            this.offsetOrder = offsetOrder;
            this.offsetChatId = offsetChatId;
            this.limit = limit;
        }

        public static final int CONSTRUCTOR = -2121381601;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetConnectedWebsites extends Function {

        public GetConnectedWebsites() {
        }

        public static final int CONSTRUCTOR = -170536110;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetContacts extends Function {

        public GetContacts() {
        }

        public static final int CONSTRUCTOR = -1417722768;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetCountryCode extends Function {

        public GetCountryCode() {
        }

        public static final int CONSTRUCTOR = 1540593906;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetCreatedPublicChats extends Function {

        public GetCreatedPublicChats() {
        }

        public static final int CONSTRUCTOR = 1609082914;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetDeepLinkInfo extends Function {
        public String link;

        public GetDeepLinkInfo() {
        }

        public GetDeepLinkInfo(String link) {
            this.link = link;
        }

        public static final int CONSTRUCTOR = 680673150;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetFavoriteStickers extends Function {

        public GetFavoriteStickers() {
        }

        public static final int CONSTRUCTOR = -338964672;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetFile extends Function {
        public int fileId;

        public GetFile() {
        }

        public GetFile(int fileId) {
            this.fileId = fileId;
        }

        public static final int CONSTRUCTOR = 1553923406;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetFileExtension extends Function {
        public String mimeType;

        public GetFileExtension() {
        }

        public GetFileExtension(String mimeType) {
            this.mimeType = mimeType;
        }

        public static final int CONSTRUCTOR = -106055372;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetFileMimeType extends Function {
        public String fileName;

        public GetFileMimeType() {
        }

        public GetFileMimeType(String fileName) {
            this.fileName = fileName;
        }

        public static final int CONSTRUCTOR = -2073879671;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetGameHighScores extends Function {
        public long chatId;
        public long messageId;
        public int userId;

        public GetGameHighScores() {
        }

        public GetGameHighScores(long chatId, long messageId, int userId) {
            this.chatId = chatId;
            this.messageId = messageId;
            this.userId = userId;
        }

        public static final int CONSTRUCTOR = 1920923753;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetGroupsInCommon extends Function {
        public int userId;
        public long offsetChatId;
        public int limit;

        public GetGroupsInCommon() {
        }

        public GetGroupsInCommon(int userId, long offsetChatId, int limit) {
            this.userId = userId;
            this.offsetChatId = offsetChatId;
            this.limit = limit;
        }

        public static final int CONSTRUCTOR = -23238689;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetImportedContactCount extends Function {

        public GetImportedContactCount() {
        }

        public static final int CONSTRUCTOR = -656336346;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetInlineGameHighScores extends Function {
        public String inlineMessageId;
        public int userId;

        public GetInlineGameHighScores() {
        }

        public GetInlineGameHighScores(String inlineMessageId, int userId) {
            this.inlineMessageId = inlineMessageId;
            this.userId = userId;
        }

        public static final int CONSTRUCTOR = -1833445800;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetInlineQueryResults extends Function {
        public int botUserId;
        public long chatId;
        public Location userLocation;
        public String query;
        public String offset;

        public GetInlineQueryResults() {
        }

        public GetInlineQueryResults(int botUserId, long chatId, Location userLocation, String query, String offset) {
            this.botUserId = botUserId;
            this.chatId = chatId;
            this.userLocation = userLocation;
            this.query = query;
            this.offset = offset;
        }

        public static final int CONSTRUCTOR = -1182511172;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetInstalledStickerSets extends Function {
        public boolean isMasks;

        public GetInstalledStickerSets() {
        }

        public GetInstalledStickerSets(boolean isMasks) {
            this.isMasks = isMasks;
        }

        public static final int CONSTRUCTOR = 1214523749;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetInviteText extends Function {

        public GetInviteText() {
        }

        public static final int CONSTRUCTOR = 794573512;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetLanguagePackString extends Function {
        public String languagePackDatabasePath;
        public String localizationTarget;
        public String languagePackId;
        public String key;

        public GetLanguagePackString() {
        }

        public GetLanguagePackString(String languagePackDatabasePath, String localizationTarget, String languagePackId, String key) {
            this.languagePackDatabasePath = languagePackDatabasePath;
            this.localizationTarget = localizationTarget;
            this.languagePackId = languagePackId;
            this.key = key;
        }

        public static final int CONSTRUCTOR = 150789747;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetLanguagePackStrings extends Function {
        public String languagePackId;
        public String[] keys;

        public GetLanguagePackStrings() {
        }

        public GetLanguagePackStrings(String languagePackId, String[] keys) {
            this.languagePackId = languagePackId;
            this.keys = keys;
        }

        public static final int CONSTRUCTOR = 1246259088;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetLocalizationTargetInfo extends Function {
        public boolean onlyLocal;

        public GetLocalizationTargetInfo() {
        }

        public GetLocalizationTargetInfo(boolean onlyLocal) {
            this.onlyLocal = onlyLocal;
        }

        public static final int CONSTRUCTOR = 1849499526;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetMapThumbnailFile extends Function {
        public Location location;
        public int zoom;
        public int width;
        public int height;
        public int scale;
        public long chatId;

        public GetMapThumbnailFile() {
        }

        public GetMapThumbnailFile(Location location, int zoom, int width, int height, int scale, long chatId) {
            this.location = location;
            this.zoom = zoom;
            this.width = width;
            this.height = height;
            this.scale = scale;
            this.chatId = chatId;
        }

        public static final int CONSTRUCTOR = -152660070;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetMe extends Function {

        public GetMe() {
        }

        public static final int CONSTRUCTOR = -191516033;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetMessage extends Function {
        public long chatId;
        public long messageId;

        public GetMessage() {
        }

        public GetMessage(long chatId, long messageId) {
            this.chatId = chatId;
            this.messageId = messageId;
        }

        public static final int CONSTRUCTOR = -1821196160;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetMessages extends Function {
        public long chatId;
        public long[] messageIds;

        public GetMessages() {
        }

        public GetMessages(long chatId, long[] messageIds) {
            this.chatId = chatId;
            this.messageIds = messageIds;
        }

        public static final int CONSTRUCTOR = 425299338;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetNetworkStatistics extends Function {
        public boolean onlyCurrent;

        public GetNetworkStatistics() {
        }

        public GetNetworkStatistics(boolean onlyCurrent) {
            this.onlyCurrent = onlyCurrent;
        }

        public static final int CONSTRUCTOR = -986228706;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetOption extends Function {
        public String name;

        public GetOption() {
        }

        public GetOption(String name) {
            this.name = name;
        }

        public static final int CONSTRUCTOR = -1572495746;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetPassportAuthorizationForm extends Function {
        public int botUserId;
        public String scope;
        public String publicKey;
        public String nonce;
        public String password;

        public GetPassportAuthorizationForm() {
        }

        public GetPassportAuthorizationForm(int botUserId, String scope, String publicKey, String nonce, String password) {
            this.botUserId = botUserId;
            this.scope = scope;
            this.publicKey = publicKey;
            this.nonce = nonce;
            this.password = password;
        }

        public static final int CONSTRUCTOR = 1998294216;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetPassportElement extends Function {
        public PassportElementType type;
        public String password;

        public GetPassportElement() {
        }

        public GetPassportElement(PassportElementType type, String password) {
            this.type = type;
            this.password = password;
        }

        public static final int CONSTRUCTOR = -1882398342;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetPasswordState extends Function {

        public GetPasswordState() {
        }

        public static final int CONSTRUCTOR = -174752904;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetPaymentForm extends Function {
        public long chatId;
        public long messageId;

        public GetPaymentForm() {
        }

        public GetPaymentForm(long chatId, long messageId) {
            this.chatId = chatId;
            this.messageId = messageId;
        }

        public static final int CONSTRUCTOR = -2146950882;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetPaymentReceipt extends Function {
        public long chatId;
        public long messageId;

        public GetPaymentReceipt() {
        }

        public GetPaymentReceipt(long chatId, long messageId) {
            this.chatId = chatId;
            this.messageId = messageId;
        }

        public static final int CONSTRUCTOR = 1013758294;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetPreferredCountryLanguage extends Function {
        public String countryCode;

        public GetPreferredCountryLanguage() {
        }

        public GetPreferredCountryLanguage(String countryCode) {
            this.countryCode = countryCode;
        }

        public static final int CONSTRUCTOR = -933049386;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetProxies extends Function {

        public GetProxies() {
        }

        public static final int CONSTRUCTOR = -95026381;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetProxyLink extends Function {
        public int proxyId;

        public GetProxyLink() {
        }

        public GetProxyLink(int proxyId) {
            this.proxyId = proxyId;
        }

        public static final int CONSTRUCTOR = -1285597664;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetPublicMessageLink extends Function {
        public long chatId;
        public long messageId;
        public boolean forAlbum;

        public GetPublicMessageLink() {
        }

        public GetPublicMessageLink(long chatId, long messageId, boolean forAlbum) {
            this.chatId = chatId;
            this.messageId = messageId;
            this.forAlbum = forAlbum;
        }

        public static final int CONSTRUCTOR = -374642839;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetRecentInlineBots extends Function {

        public GetRecentInlineBots() {
        }

        public static final int CONSTRUCTOR = 1437823548;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetRecentStickers extends Function {
        public boolean isAttached;

        public GetRecentStickers() {
        }

        public GetRecentStickers(boolean isAttached) {
            this.isAttached = isAttached;
        }

        public static final int CONSTRUCTOR = -579622241;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetRecentlyVisitedTMeUrls extends Function {
        public String referrer;

        public GetRecentlyVisitedTMeUrls() {
        }

        public GetRecentlyVisitedTMeUrls(String referrer) {
            this.referrer = referrer;
        }

        public static final int CONSTRUCTOR = 806754961;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetRecoveryEmailAddress extends Function {
        public String password;

        public GetRecoveryEmailAddress() {
        }

        public GetRecoveryEmailAddress(String password) {
            this.password = password;
        }

        public static final int CONSTRUCTOR = -1594770947;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetRemoteFile extends Function {
        public String remoteFileId;
        public FileType fileType;

        public GetRemoteFile() {
        }

        public GetRemoteFile(String remoteFileId, FileType fileType) {
            this.remoteFileId = remoteFileId;
            this.fileType = fileType;
        }

        public static final int CONSTRUCTOR = 2137204530;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetRepliedMessage extends Function {
        public long chatId;
        public long messageId;

        public GetRepliedMessage() {
        }

        public GetRepliedMessage(long chatId, long messageId) {
            this.chatId = chatId;
            this.messageId = messageId;
        }

        public static final int CONSTRUCTOR = -641918531;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetSavedAnimations extends Function {

        public GetSavedAnimations() {
        }

        public static final int CONSTRUCTOR = 7051032;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetSavedOrderInfo extends Function {

        public GetSavedOrderInfo() {
        }

        public static final int CONSTRUCTOR = -1152016675;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetScopeNotificationSettings extends Function {
        public NotificationSettingsScope scope;

        public GetScopeNotificationSettings() {
        }

        public GetScopeNotificationSettings(NotificationSettingsScope scope) {
            this.scope = scope;
        }

        public static final int CONSTRUCTOR = -995613361;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetSecretChat extends Function {
        public int secretChatId;

        public GetSecretChat() {
        }

        public GetSecretChat(int secretChatId) {
            this.secretChatId = secretChatId;
        }

        public static final int CONSTRUCTOR = 40599169;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetStickerEmojis extends Function {
        public InputFile sticker;

        public GetStickerEmojis() {
        }

        public GetStickerEmojis(InputFile sticker) {
            this.sticker = sticker;
        }

        public static final int CONSTRUCTOR = 95352475;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetStickerSet extends Function {
        public long setId;

        public GetStickerSet() {
        }

        public GetStickerSet(long setId) {
            this.setId = setId;
        }

        public static final int CONSTRUCTOR = 1052318659;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetStickers extends Function {
        public String emoji;
        public int limit;

        public GetStickers() {
        }

        public GetStickers(String emoji, int limit) {
            this.emoji = emoji;
            this.limit = limit;
        }

        public static final int CONSTRUCTOR = -1594919556;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetStorageStatistics extends Function {
        public int chatLimit;

        public GetStorageStatistics() {
        }

        public GetStorageStatistics(int chatLimit) {
            this.chatLimit = chatLimit;
        }

        public static final int CONSTRUCTOR = -853193929;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetStorageStatisticsFast extends Function {

        public GetStorageStatisticsFast() {
        }

        public static final int CONSTRUCTOR = 61368066;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetSupergroup extends Function {
        public int supergroupId;

        public GetSupergroup() {
        }

        public GetSupergroup(int supergroupId) {
            this.supergroupId = supergroupId;
        }

        public static final int CONSTRUCTOR = -2063063706;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetSupergroupFullInfo extends Function {
        public int supergroupId;

        public GetSupergroupFullInfo() {
        }

        public GetSupergroupFullInfo(int supergroupId) {
            this.supergroupId = supergroupId;
        }

        public static final int CONSTRUCTOR = -1150331262;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetSupergroupMembers extends Function {
        public int supergroupId;
        public SupergroupMembersFilter filter;
        public int offset;
        public int limit;

        public GetSupergroupMembers() {
        }

        public GetSupergroupMembers(int supergroupId, SupergroupMembersFilter filter, int offset, int limit) {
            this.supergroupId = supergroupId;
            this.filter = filter;
            this.offset = offset;
            this.limit = limit;
        }

        public static final int CONSTRUCTOR = 1427643098;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetSupportUser extends Function {

        public GetSupportUser() {
        }

        public static final int CONSTRUCTOR = -1733497700;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetTemporaryPasswordState extends Function {

        public GetTemporaryPasswordState() {
        }

        public static final int CONSTRUCTOR = -12670830;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetTextEntities extends Function {
        public String text;

        public GetTextEntities() {
        }

        public GetTextEntities(String text) {
            this.text = text;
        }

        public static final int CONSTRUCTOR = -341490693;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetTopChats extends Function {
        public TopChatCategory category;
        public int limit;

        public GetTopChats() {
        }

        public GetTopChats(TopChatCategory category, int limit) {
            this.category = category;
            this.limit = limit;
        }

        public static final int CONSTRUCTOR = -388410847;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetTrendingStickerSets extends Function {

        public GetTrendingStickerSets() {
        }

        public static final int CONSTRUCTOR = -1729129957;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetUser extends Function {
        public int userId;

        public GetUser() {
        }

        public GetUser(int userId) {
            this.userId = userId;
        }

        public static final int CONSTRUCTOR = -47586017;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetUserFullInfo extends Function {
        public int userId;

        public GetUserFullInfo() {
        }

        public GetUserFullInfo(int userId) {
            this.userId = userId;
        }

        public static final int CONSTRUCTOR = -655443263;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetUserPrivacySettingRules extends Function {
        public UserPrivacySetting setting;

        public GetUserPrivacySettingRules() {
        }

        public GetUserPrivacySettingRules(UserPrivacySetting setting) {
            this.setting = setting;
        }

        public static final int CONSTRUCTOR = -2077223311;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetUserProfilePhotos extends Function {
        public int userId;
        public int offset;
        public int limit;

        public GetUserProfilePhotos() {
        }

        public GetUserProfilePhotos(int userId, int offset, int limit) {
            this.userId = userId;
            this.offset = offset;
            this.limit = limit;
        }

        public static final int CONSTRUCTOR = -2062927433;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetWallpapers extends Function {

        public GetWallpapers() {
        }

        public static final int CONSTRUCTOR = 2097518555;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetWebPageInstantView extends Function {
        public String url;
        public boolean forceFull;

        public GetWebPageInstantView() {
        }

        public GetWebPageInstantView(String url, boolean forceFull) {
            this.url = url;
            this.forceFull = forceFull;
        }

        public static final int CONSTRUCTOR = -1962649975;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class GetWebPagePreview extends Function {
        public FormattedText text;

        public GetWebPagePreview() {
        }

        public GetWebPagePreview(FormattedText text) {
            this.text = text;
        }

        public static final int CONSTRUCTOR = 573441580;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ImportContacts extends Function {
        public Contact[] contacts;

        public ImportContacts() {
        }

        public ImportContacts(Contact[] contacts) {
            this.contacts = contacts;
        }

        public static final int CONSTRUCTOR = -215132767;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class JoinChat extends Function {
        public long chatId;

        public JoinChat() {
        }

        public JoinChat(long chatId) {
            this.chatId = chatId;
        }

        public static final int CONSTRUCTOR = 326769313;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class JoinChatByInviteLink extends Function {
        public String inviteLink;

        public JoinChatByInviteLink() {
        }

        public JoinChatByInviteLink(String inviteLink) {
            this.inviteLink = inviteLink;
        }

        public static final int CONSTRUCTOR = -1049973882;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class LeaveChat extends Function {
        public long chatId;

        public LeaveChat() {
        }

        public LeaveChat(long chatId) {
            this.chatId = chatId;
        }

        public static final int CONSTRUCTOR = -1825080735;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class LogOut extends Function {

        public LogOut() {
        }

        public static final int CONSTRUCTOR = -1581923301;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class OpenChat extends Function {
        public long chatId;

        public OpenChat() {
        }

        public OpenChat(long chatId) {
            this.chatId = chatId;
        }

        public static final int CONSTRUCTOR = -323371509;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class OpenMessageContent extends Function {
        public long chatId;
        public long messageId;

        public OpenMessageContent() {
        }

        public OpenMessageContent(long chatId, long messageId) {
            this.chatId = chatId;
            this.messageId = messageId;
        }

        public static final int CONSTRUCTOR = -739088005;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class OptimizeStorage extends Function {
        public long size;
        public int ttl;
        public int count;
        public int immunityDelay;
        public FileType[] fileTypes;
        public long[] chatIds;
        public long[] excludeChatIds;
        public int chatLimit;

        public OptimizeStorage() {
        }

        public OptimizeStorage(long size, int ttl, int count, int immunityDelay, FileType[] fileTypes, long[] chatIds, long[] excludeChatIds, int chatLimit) {
            this.size = size;
            this.ttl = ttl;
            this.count = count;
            this.immunityDelay = immunityDelay;
            this.fileTypes = fileTypes;
            this.chatIds = chatIds;
            this.excludeChatIds = excludeChatIds;
            this.chatLimit = chatLimit;
        }

        public static final int CONSTRUCTOR = 980397489;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ParseTextEntities extends Function {
        public String text;
        public TextParseMode parseMode;

        public ParseTextEntities() {
        }

        public ParseTextEntities(String text, TextParseMode parseMode) {
            this.text = text;
            this.parseMode = parseMode;
        }

        public static final int CONSTRUCTOR = -1709194593;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PinSupergroupMessage extends Function {
        public int supergroupId;
        public long messageId;
        public boolean disableNotification;

        public PinSupergroupMessage() {
        }

        public PinSupergroupMessage(int supergroupId, long messageId, boolean disableNotification) {
            this.supergroupId = supergroupId;
            this.messageId = messageId;
            this.disableNotification = disableNotification;
        }

        public static final int CONSTRUCTOR = 1141187557;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class PingProxy extends Function {
        public int proxyId;

        public PingProxy() {
        }

        public PingProxy(int proxyId) {
            this.proxyId = proxyId;
        }

        public static final int CONSTRUCTOR = -979681103;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ProcessDcUpdate extends Function {
        public String dc;
        public String addr;

        public ProcessDcUpdate() {
        }

        public ProcessDcUpdate(String dc, String addr) {
            this.dc = dc;
            this.addr = addr;
        }

        public static final int CONSTRUCTOR = -1806562997;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ReadAllChatMentions extends Function {
        public long chatId;

        public ReadAllChatMentions() {
        }

        public ReadAllChatMentions(long chatId) {
            this.chatId = chatId;
        }

        public static final int CONSTRUCTOR = 1357558453;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class RecoverAuthenticationPassword extends Function {
        public String recoveryCode;

        public RecoverAuthenticationPassword() {
        }

        public RecoverAuthenticationPassword(String recoveryCode) {
            this.recoveryCode = recoveryCode;
        }

        public static final int CONSTRUCTOR = 787436412;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class RecoverPassword extends Function {
        public String recoveryCode;

        public RecoverPassword() {
        }

        public RecoverPassword(String recoveryCode) {
            this.recoveryCode = recoveryCode;
        }

        public static final int CONSTRUCTOR = 1660185903;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class RegisterDevice extends Function {
        public DeviceToken deviceToken;
        public int[] otherUserIds;

        public RegisterDevice() {
        }

        public RegisterDevice(DeviceToken deviceToken, int[] otherUserIds) {
            this.deviceToken = deviceToken;
            this.otherUserIds = otherUserIds;
        }

        public static final int CONSTRUCTOR = -413637293;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class RemoveContacts extends Function {
        public int[] userIds;

        public RemoveContacts() {
        }

        public RemoveContacts(int[] userIds) {
            this.userIds = userIds;
        }

        public static final int CONSTRUCTOR = -615510759;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class RemoveFavoriteSticker extends Function {
        public InputFile sticker;

        public RemoveFavoriteSticker() {
        }

        public RemoveFavoriteSticker(InputFile sticker) {
            this.sticker = sticker;
        }

        public static final int CONSTRUCTOR = 1152945264;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class RemoveProxy extends Function {
        public int proxyId;

        public RemoveProxy() {
        }

        public RemoveProxy(int proxyId) {
            this.proxyId = proxyId;
        }

        public static final int CONSTRUCTOR = 1369219847;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class RemoveRecentHashtag extends Function {
        public String hashtag;

        public RemoveRecentHashtag() {
        }

        public RemoveRecentHashtag(String hashtag) {
            this.hashtag = hashtag;
        }

        public static final int CONSTRUCTOR = -1013735260;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class RemoveRecentSticker extends Function {
        public boolean isAttached;
        public InputFile sticker;

        public RemoveRecentSticker() {
        }

        public RemoveRecentSticker(boolean isAttached, InputFile sticker) {
            this.isAttached = isAttached;
            this.sticker = sticker;
        }

        public static final int CONSTRUCTOR = 1246577677;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class RemoveRecentlyFoundChat extends Function {
        public long chatId;

        public RemoveRecentlyFoundChat() {
        }

        public RemoveRecentlyFoundChat(long chatId) {
            this.chatId = chatId;
        }

        public static final int CONSTRUCTOR = 717340444;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class RemoveSavedAnimation extends Function {
        public InputFile animation;

        public RemoveSavedAnimation() {
        }

        public RemoveSavedAnimation(InputFile animation) {
            this.animation = animation;
        }

        public static final int CONSTRUCTOR = -495605479;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class RemoveStickerFromSet extends Function {
        public InputFile sticker;

        public RemoveStickerFromSet() {
        }

        public RemoveStickerFromSet(InputFile sticker) {
            this.sticker = sticker;
        }

        public static final int CONSTRUCTOR = 1642196644;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class RemoveTopChat extends Function {
        public TopChatCategory category;
        public long chatId;

        public RemoveTopChat() {
        }

        public RemoveTopChat(TopChatCategory category, long chatId) {
            this.category = category;
            this.chatId = chatId;
        }

        public static final int CONSTRUCTOR = -1907876267;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ReorderInstalledStickerSets extends Function {
        public boolean isMasks;
        public long[] stickerSetIds;

        public ReorderInstalledStickerSets() {
        }

        public ReorderInstalledStickerSets(boolean isMasks, long[] stickerSetIds) {
            this.isMasks = isMasks;
            this.stickerSetIds = stickerSetIds;
        }

        public static final int CONSTRUCTOR = 1114537563;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ReportChat extends Function {
        public long chatId;
        public ChatReportReason reason;
        public long[] messageIds;

        public ReportChat() {
        }

        public ReportChat(long chatId, ChatReportReason reason, long[] messageIds) {
            this.chatId = chatId;
            this.reason = reason;
            this.messageIds = messageIds;
        }

        public static final int CONSTRUCTOR = -312579772;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ReportSupergroupSpam extends Function {
        public int supergroupId;
        public int userId;
        public long[] messageIds;

        public ReportSupergroupSpam() {
        }

        public ReportSupergroupSpam(int supergroupId, int userId, long[] messageIds) {
            this.supergroupId = supergroupId;
            this.userId = userId;
            this.messageIds = messageIds;
        }

        public static final int CONSTRUCTOR = -2125451498;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class RequestAuthenticationPasswordRecovery extends Function {

        public RequestAuthenticationPasswordRecovery() {
        }

        public static final int CONSTRUCTOR = 1393896118;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class RequestPasswordRecovery extends Function {

        public RequestPasswordRecovery() {
        }

        public static final int CONSTRUCTOR = -13777582;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ResendAuthenticationCode extends Function {

        public ResendAuthenticationCode() {
        }

        public static final int CONSTRUCTOR = -814377191;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ResendChangePhoneNumberCode extends Function {

        public ResendChangePhoneNumberCode() {
        }

        public static final int CONSTRUCTOR = -786772060;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ResendEmailAddressVerificationCode extends Function {

        public ResendEmailAddressVerificationCode() {
        }

        public static final int CONSTRUCTOR = -1872416732;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ResendPhoneNumberConfirmationCode extends Function {

        public ResendPhoneNumberConfirmationCode() {
        }

        public static final int CONSTRUCTOR = 2069068522;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ResendPhoneNumberVerificationCode extends Function {

        public ResendPhoneNumberVerificationCode() {
        }

        public static final int CONSTRUCTOR = 1367629820;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ResetAllNotificationSettings extends Function {

        public ResetAllNotificationSettings() {
        }

        public static final int CONSTRUCTOR = -174020359;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ResetNetworkStatistics extends Function {

        public ResetNetworkStatistics() {
        }

        public static final int CONSTRUCTOR = 1646452102;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchCallMessages extends Function {
        public long fromMessageId;
        public int limit;
        public boolean onlyMissed;

        public SearchCallMessages() {
        }

        public SearchCallMessages(long fromMessageId, int limit, boolean onlyMissed) {
            this.fromMessageId = fromMessageId;
            this.limit = limit;
            this.onlyMissed = onlyMissed;
        }

        public static final int CONSTRUCTOR = -1077230820;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchChatMembers extends Function {
        public long chatId;
        public String query;
        public int limit;
        public ChatMembersFilter filter;

        public SearchChatMembers() {
        }

        public SearchChatMembers(long chatId, String query, int limit, ChatMembersFilter filter) {
            this.chatId = chatId;
            this.query = query;
            this.limit = limit;
            this.filter = filter;
        }

        public static final int CONSTRUCTOR = -445823291;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchChatMessages extends Function {
        public long chatId;
        public String query;
        public int senderUserId;
        public long fromMessageId;
        public int offset;
        public int limit;
        public SearchMessagesFilter filter;

        public SearchChatMessages() {
        }

        public SearchChatMessages(long chatId, String query, int senderUserId, long fromMessageId, int offset, int limit, SearchMessagesFilter filter) {
            this.chatId = chatId;
            this.query = query;
            this.senderUserId = senderUserId;
            this.fromMessageId = fromMessageId;
            this.offset = offset;
            this.limit = limit;
            this.filter = filter;
        }

        public static final int CONSTRUCTOR = -1528846671;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchChatRecentLocationMessages extends Function {
        public long chatId;
        public int limit;

        public SearchChatRecentLocationMessages() {
        }

        public SearchChatRecentLocationMessages(long chatId, int limit) {
            this.chatId = chatId;
            this.limit = limit;
        }

        public static final int CONSTRUCTOR = 950238950;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchChats extends Function {
        public String query;
        public int limit;

        public SearchChats() {
        }

        public SearchChats(String query, int limit) {
            this.query = query;
            this.limit = limit;
        }

        public static final int CONSTRUCTOR = -1879787060;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchChatsOnServer extends Function {
        public String query;
        public int limit;

        public SearchChatsOnServer() {
        }

        public SearchChatsOnServer(String query, int limit) {
            this.query = query;
            this.limit = limit;
        }

        public static final int CONSTRUCTOR = -1158402188;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchContacts extends Function {
        public String query;
        public int limit;

        public SearchContacts() {
        }

        public SearchContacts(String query, int limit) {
            this.query = query;
            this.limit = limit;
        }

        public static final int CONSTRUCTOR = -1794690715;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchHashtags extends Function {
        public String prefix;
        public int limit;

        public SearchHashtags() {
        }

        public SearchHashtags(String prefix, int limit) {
            this.prefix = prefix;
            this.limit = limit;
        }

        public static final int CONSTRUCTOR = 1043637617;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchInstalledStickerSets extends Function {
        public boolean isMasks;
        public String query;
        public int limit;

        public SearchInstalledStickerSets() {
        }

        public SearchInstalledStickerSets(boolean isMasks, String query, int limit) {
            this.isMasks = isMasks;
            this.query = query;
            this.limit = limit;
        }

        public static final int CONSTRUCTOR = 681171344;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchMessages extends Function {
        public String query;
        public int offsetDate;
        public long offsetChatId;
        public long offsetMessageId;
        public int limit;

        public SearchMessages() {
        }

        public SearchMessages(String query, int offsetDate, long offsetChatId, long offsetMessageId, int limit) {
            this.query = query;
            this.offsetDate = offsetDate;
            this.offsetChatId = offsetChatId;
            this.offsetMessageId = offsetMessageId;
            this.limit = limit;
        }

        public static final int CONSTRUCTOR = 1579305146;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchPublicChat extends Function {
        public String username;

        public SearchPublicChat() {
        }

        public SearchPublicChat(String username) {
            this.username = username;
        }

        public static final int CONSTRUCTOR = 857135533;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchPublicChats extends Function {
        public String query;

        public SearchPublicChats() {
        }

        public SearchPublicChats(String query) {
            this.query = query;
        }

        public static final int CONSTRUCTOR = 970385337;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchSecretMessages extends Function {
        public long chatId;
        public String query;
        public long fromSearchId;
        public int limit;
        public SearchMessagesFilter filter;

        public SearchSecretMessages() {
        }

        public SearchSecretMessages(long chatId, String query, long fromSearchId, int limit, SearchMessagesFilter filter) {
            this.chatId = chatId;
            this.query = query;
            this.fromSearchId = fromSearchId;
            this.limit = limit;
            this.filter = filter;
        }

        public static final int CONSTRUCTOR = -1670627915;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchStickerSet extends Function {
        public String name;

        public SearchStickerSet() {
        }

        public SearchStickerSet(String name) {
            this.name = name;
        }

        public static final int CONSTRUCTOR = 1157930222;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchStickerSets extends Function {
        public String query;

        public SearchStickerSets() {
        }

        public SearchStickerSets(String query) {
            this.query = query;
        }

        public static final int CONSTRUCTOR = -1082314629;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SearchStickers extends Function {
        public String emoji;
        public int limit;

        public SearchStickers() {
        }

        public SearchStickers(String emoji, int limit) {
            this.emoji = emoji;
            this.limit = limit;
        }

        public static final int CONSTRUCTOR = 1555771203;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SendBotStartMessage extends Function {
        public int botUserId;
        public long chatId;
        public String parameter;

        public SendBotStartMessage() {
        }

        public SendBotStartMessage(int botUserId, long chatId, String parameter) {
            this.botUserId = botUserId;
            this.chatId = chatId;
            this.parameter = parameter;
        }

        public static final int CONSTRUCTOR = 1112181339;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SendCallDebugInformation extends Function {
        public int callId;
        public String debugInformation;

        public SendCallDebugInformation() {
        }

        public SendCallDebugInformation(int callId, String debugInformation) {
            this.callId = callId;
            this.debugInformation = debugInformation;
        }

        public static final int CONSTRUCTOR = 2019243839;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SendCallRating extends Function {
        public int callId;
        public int rating;
        public String comment;

        public SendCallRating() {
        }

        public SendCallRating(int callId, int rating, String comment) {
            this.callId = callId;
            this.rating = rating;
            this.comment = comment;
        }

        public static final int CONSTRUCTOR = 243075146;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SendChatAction extends Function {
        public long chatId;
        public ChatAction action;

        public SendChatAction() {
        }

        public SendChatAction(long chatId, ChatAction action) {
            this.chatId = chatId;
            this.action = action;
        }

        public static final int CONSTRUCTOR = -841357536;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SendChatScreenshotTakenNotification extends Function {
        public long chatId;

        public SendChatScreenshotTakenNotification() {
        }

        public SendChatScreenshotTakenNotification(long chatId) {
            this.chatId = chatId;
        }

        public static final int CONSTRUCTOR = 448399457;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SendChatSetTtlMessage extends Function {
        public long chatId;
        public int ttl;

        public SendChatSetTtlMessage() {
        }

        public SendChatSetTtlMessage(long chatId, int ttl) {
            this.chatId = chatId;
            this.ttl = ttl;
        }

        public static final int CONSTRUCTOR = 1432535564;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SendCustomRequest extends Function {
        public String method;
        public String parameters;

        public SendCustomRequest() {
        }

        public SendCustomRequest(String method, String parameters) {
            this.method = method;
            this.parameters = parameters;
        }

        public static final int CONSTRUCTOR = 285045153;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SendEmailAddressVerificationCode extends Function {
        public String emailAddress;

        public SendEmailAddressVerificationCode() {
        }

        public SendEmailAddressVerificationCode(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public static final int CONSTRUCTOR = -221621379;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SendInlineQueryResultMessage extends Function {
        public long chatId;
        public long replyToMessageId;
        public boolean disableNotification;
        public boolean fromBackground;
        public long queryId;
        public String resultId;

        public SendInlineQueryResultMessage() {
        }

        public SendInlineQueryResultMessage(long chatId, long replyToMessageId, boolean disableNotification, boolean fromBackground, long queryId, String resultId) {
            this.chatId = chatId;
            this.replyToMessageId = replyToMessageId;
            this.disableNotification = disableNotification;
            this.fromBackground = fromBackground;
            this.queryId = queryId;
            this.resultId = resultId;
        }

        public static final int CONSTRUCTOR = -643910868;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SendMessage extends Function {
        public long chatId;
        public long replyToMessageId;
        public boolean disableNotification;
        public boolean fromBackground;
        public ReplyMarkup replyMarkup;
        public InputMessageContent inputMessageContent;

        public SendMessage() {
        }

        public SendMessage(long chatId, long replyToMessageId, boolean disableNotification, boolean fromBackground, ReplyMarkup replyMarkup, InputMessageContent inputMessageContent) {
            this.chatId = chatId;
            this.replyToMessageId = replyToMessageId;
            this.disableNotification = disableNotification;
            this.fromBackground = fromBackground;
            this.replyMarkup = replyMarkup;
            this.inputMessageContent = inputMessageContent;
        }

        public static final int CONSTRUCTOR = 1694632114;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SendMessageAlbum extends Function {
        public long chatId;
        public long replyToMessageId;
        public boolean disableNotification;
        public boolean fromBackground;
        public InputMessageContent[] inputMessageContents;

        public SendMessageAlbum() {
        }

        public SendMessageAlbum(long chatId, long replyToMessageId, boolean disableNotification, boolean fromBackground, InputMessageContent[] inputMessageContents) {
            this.chatId = chatId;
            this.replyToMessageId = replyToMessageId;
            this.disableNotification = disableNotification;
            this.fromBackground = fromBackground;
            this.inputMessageContents = inputMessageContents;
        }

        public static final int CONSTRUCTOR = -291823014;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SendPassportAuthorizationForm extends Function {
        public int autorizationFormId;
        public PassportElementType[] types;

        public SendPassportAuthorizationForm() {
        }

        public SendPassportAuthorizationForm(int autorizationFormId, PassportElementType[] types) {
            this.autorizationFormId = autorizationFormId;
            this.types = types;
        }

        public static final int CONSTRUCTOR = -602402218;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SendPaymentForm extends Function {
        public long chatId;
        public long messageId;
        public String orderInfoId;
        public String shippingOptionId;
        public InputCredentials credentials;

        public SendPaymentForm() {
        }

        public SendPaymentForm(long chatId, long messageId, String orderInfoId, String shippingOptionId, InputCredentials credentials) {
            this.chatId = chatId;
            this.messageId = messageId;
            this.orderInfoId = orderInfoId;
            this.shippingOptionId = shippingOptionId;
            this.credentials = credentials;
        }

        public static final int CONSTRUCTOR = 591581572;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SendPhoneNumberConfirmationCode extends Function {
        public String hash;
        public String phoneNumber;
        public boolean allowFlashCall;
        public boolean isCurrentPhoneNumber;

        public SendPhoneNumberConfirmationCode() {
        }

        public SendPhoneNumberConfirmationCode(String hash, String phoneNumber, boolean allowFlashCall, boolean isCurrentPhoneNumber) {
            this.hash = hash;
            this.phoneNumber = phoneNumber;
            this.allowFlashCall = allowFlashCall;
            this.isCurrentPhoneNumber = isCurrentPhoneNumber;
        }

        public static final int CONSTRUCTOR = 1975492794;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SendPhoneNumberVerificationCode extends Function {
        public String phoneNumber;
        public boolean allowFlashCall;
        public boolean isCurrentPhoneNumber;

        public SendPhoneNumberVerificationCode() {
        }

        public SendPhoneNumberVerificationCode(String phoneNumber, boolean allowFlashCall, boolean isCurrentPhoneNumber) {
            this.phoneNumber = phoneNumber;
            this.allowFlashCall = allowFlashCall;
            this.isCurrentPhoneNumber = isCurrentPhoneNumber;
        }

        public static final int CONSTRUCTOR = -280632685;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetAccountTtl extends Function {
        public AccountTtl ttl;

        public SetAccountTtl() {
        }

        public SetAccountTtl(AccountTtl ttl) {
            this.ttl = ttl;
        }

        public static final int CONSTRUCTOR = 701389032;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetAlarm extends Function {
        public double seconds;

        public SetAlarm() {
        }

        public SetAlarm(double seconds) {
            this.seconds = seconds;
        }

        public static final int CONSTRUCTOR = -873497067;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetAuthenticationPhoneNumber extends Function {
        public String phoneNumber;
        public boolean allowFlashCall;
        public boolean isCurrentPhoneNumber;

        public SetAuthenticationPhoneNumber() {
        }

        public SetAuthenticationPhoneNumber(String phoneNumber, boolean allowFlashCall, boolean isCurrentPhoneNumber) {
            this.phoneNumber = phoneNumber;
            this.allowFlashCall = allowFlashCall;
            this.isCurrentPhoneNumber = isCurrentPhoneNumber;
        }

        public static final int CONSTRUCTOR = -856055465;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetBio extends Function {
        public String bio;

        public SetBio() {
        }

        public SetBio(String bio) {
            this.bio = bio;
        }

        public static final int CONSTRUCTOR = -1619582124;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetBotUpdatesStatus extends Function {
        public int pendingUpdateCount;
        public String errorMessage;

        public SetBotUpdatesStatus() {
        }

        public SetBotUpdatesStatus(int pendingUpdateCount, String errorMessage) {
            this.pendingUpdateCount = pendingUpdateCount;
            this.errorMessage = errorMessage;
        }

        public static final int CONSTRUCTOR = -1154926191;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetChatClientData extends Function {
        public long chatId;
        public String clientData;

        public SetChatClientData() {
        }

        public SetChatClientData(long chatId, String clientData) {
            this.chatId = chatId;
            this.clientData = clientData;
        }

        public static final int CONSTRUCTOR = -827119811;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetChatDraftMessage extends Function {
        public long chatId;
        public DraftMessage draftMessage;

        public SetChatDraftMessage() {
        }

        public SetChatDraftMessage(long chatId, DraftMessage draftMessage) {
            this.chatId = chatId;
            this.draftMessage = draftMessage;
        }

        public static final int CONSTRUCTOR = -588175579;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetChatMemberStatus extends Function {
        public long chatId;
        public int userId;
        public ChatMemberStatus status;

        public SetChatMemberStatus() {
        }

        public SetChatMemberStatus(long chatId, int userId, ChatMemberStatus status) {
            this.chatId = chatId;
            this.userId = userId;
            this.status = status;
        }

        public static final int CONSTRUCTOR = -1754439241;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetChatNotificationSettings extends Function {
        public long chatId;
        public ChatNotificationSettings notificationSettings;

        public SetChatNotificationSettings() {
        }

        public SetChatNotificationSettings(long chatId, ChatNotificationSettings notificationSettings) {
            this.chatId = chatId;
            this.notificationSettings = notificationSettings;
        }

        public static final int CONSTRUCTOR = 777199614;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetChatPhoto extends Function {
        public long chatId;
        public InputFile photo;

        public SetChatPhoto() {
        }

        public SetChatPhoto(long chatId, InputFile photo) {
            this.chatId = chatId;
            this.photo = photo;
        }

        public static final int CONSTRUCTOR = 132244217;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetChatTitle extends Function {
        public long chatId;
        public String title;

        public SetChatTitle() {
        }

        public SetChatTitle(long chatId, String title) {
            this.chatId = chatId;
            this.title = title;
        }

        public static final int CONSTRUCTOR = 164282047;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetCustomLanguagePack extends Function {
        public LanguagePackInfo info;
        public LanguagePackString[] strings;

        public SetCustomLanguagePack() {
        }

        public SetCustomLanguagePack(LanguagePackInfo info, LanguagePackString[] strings) {
            this.info = info;
            this.strings = strings;
        }

        public static final int CONSTRUCTOR = -296742819;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetCustomLanguagePackString extends Function {
        public String languagePackId;
        public LanguagePackString newString;

        public SetCustomLanguagePackString() {
        }

        public SetCustomLanguagePackString(String languagePackId, LanguagePackString newString) {
            this.languagePackId = languagePackId;
            this.newString = newString;
        }

        public static final int CONSTRUCTOR = 1316365592;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetDatabaseEncryptionKey extends Function {
        public byte[] newEncryptionKey;

        public SetDatabaseEncryptionKey() {
        }

        public SetDatabaseEncryptionKey(byte[] newEncryptionKey) {
            this.newEncryptionKey = newEncryptionKey;
        }

        public static final int CONSTRUCTOR = -1204599371;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetFileGenerationProgress extends Function {
        public long generationId;
        public int expectedSize;
        public int localPrefixSize;

        public SetFileGenerationProgress() {
        }

        public SetFileGenerationProgress(long generationId, int expectedSize, int localPrefixSize) {
            this.generationId = generationId;
            this.expectedSize = expectedSize;
            this.localPrefixSize = localPrefixSize;
        }

        public static final int CONSTRUCTOR = -540459953;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetGameScore extends Function {
        public long chatId;
        public long messageId;
        public boolean editMessage;
        public int userId;
        public int score;
        public boolean force;

        public SetGameScore() {
        }

        public SetGameScore(long chatId, long messageId, boolean editMessage, int userId, int score, boolean force) {
            this.chatId = chatId;
            this.messageId = messageId;
            this.editMessage = editMessage;
            this.userId = userId;
            this.score = score;
            this.force = force;
        }

        public static final int CONSTRUCTOR = -1768307069;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetInlineGameScore extends Function {
        public String inlineMessageId;
        public boolean editMessage;
        public int userId;
        public int score;
        public boolean force;

        public SetInlineGameScore() {
        }

        public SetInlineGameScore(String inlineMessageId, boolean editMessage, int userId, int score, boolean force) {
            this.inlineMessageId = inlineMessageId;
            this.editMessage = editMessage;
            this.userId = userId;
            this.score = score;
            this.force = force;
        }

        public static final int CONSTRUCTOR = 758435487;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetName extends Function {
        public String firstName;
        public String lastName;

        public SetName() {
        }

        public SetName(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public static final int CONSTRUCTOR = 1711693584;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetNetworkType extends Function {
        public NetworkType type;

        public SetNetworkType() {
        }

        public SetNetworkType(NetworkType type) {
            this.type = type;
        }

        public static final int CONSTRUCTOR = -701635234;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetOption extends Function {
        public String name;
        public OptionValue value;

        public SetOption() {
        }

        public SetOption(String name, OptionValue value) {
            this.name = name;
            this.value = value;
        }

        public static final int CONSTRUCTOR = 2114670322;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetPassportElement extends Function {
        public InputPassportElement element;
        public String password;

        public SetPassportElement() {
        }

        public SetPassportElement(InputPassportElement element, String password) {
            this.element = element;
            this.password = password;
        }

        public static final int CONSTRUCTOR = 2068173212;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetPassportElementErrors extends Function {
        public int userId;
        public InputPassportElementError[] errors;

        public SetPassportElementErrors() {
        }

        public SetPassportElementErrors(int userId, InputPassportElementError[] errors) {
            this.userId = userId;
            this.errors = errors;
        }

        public static final int CONSTRUCTOR = 1455869875;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetPassword extends Function {
        public String oldPassword;
        public String newPassword;
        public String newHint;
        public boolean setRecoveryEmailAddress;
        public String newRecoveryEmailAddress;

        public SetPassword() {
        }

        public SetPassword(String oldPassword, String newPassword, String newHint, boolean setRecoveryEmailAddress, String newRecoveryEmailAddress) {
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
            this.newHint = newHint;
            this.setRecoveryEmailAddress = setRecoveryEmailAddress;
            this.newRecoveryEmailAddress = newRecoveryEmailAddress;
        }

        public static final int CONSTRUCTOR = -1193589027;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetPinnedChats extends Function {
        public long[] chatIds;

        public SetPinnedChats() {
        }

        public SetPinnedChats(long[] chatIds) {
            this.chatIds = chatIds;
        }

        public static final int CONSTRUCTOR = -1369665719;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetProfilePhoto extends Function {
        public InputFile photo;

        public SetProfilePhoto() {
        }

        public SetProfilePhoto(InputFile photo) {
            this.photo = photo;
        }

        public static final int CONSTRUCTOR = 1594734550;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetRecoveryEmailAddress extends Function {
        public String password;
        public String newRecoveryEmailAddress;

        public SetRecoveryEmailAddress() {
        }

        public SetRecoveryEmailAddress(String password, String newRecoveryEmailAddress) {
            this.password = password;
            this.newRecoveryEmailAddress = newRecoveryEmailAddress;
        }

        public static final int CONSTRUCTOR = -1981836385;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetScopeNotificationSettings extends Function {
        public NotificationSettingsScope scope;
        public ScopeNotificationSettings notificationSettings;

        public SetScopeNotificationSettings() {
        }

        public SetScopeNotificationSettings(NotificationSettingsScope scope, ScopeNotificationSettings notificationSettings) {
            this.scope = scope;
            this.notificationSettings = notificationSettings;
        }

        public static final int CONSTRUCTOR = -2049984966;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetStickerPositionInSet extends Function {
        public InputFile sticker;
        public int position;

        public SetStickerPositionInSet() {
        }

        public SetStickerPositionInSet(InputFile sticker, int position) {
            this.sticker = sticker;
            this.position = position;
        }

        public static final int CONSTRUCTOR = 2075281185;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetSupergroupDescription extends Function {
        public int supergroupId;
        public String description;

        public SetSupergroupDescription() {
        }

        public SetSupergroupDescription(int supergroupId, String description) {
            this.supergroupId = supergroupId;
            this.description = description;
        }

        public static final int CONSTRUCTOR = 227623488;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetSupergroupStickerSet extends Function {
        public int supergroupId;
        public long stickerSetId;

        public SetSupergroupStickerSet() {
        }

        public SetSupergroupStickerSet(int supergroupId, long stickerSetId) {
            this.supergroupId = supergroupId;
            this.stickerSetId = stickerSetId;
        }

        public static final int CONSTRUCTOR = -295782298;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetSupergroupUsername extends Function {
        public int supergroupId;
        public String username;

        public SetSupergroupUsername() {
        }

        public SetSupergroupUsername(int supergroupId, String username) {
            this.supergroupId = supergroupId;
            this.username = username;
        }

        public static final int CONSTRUCTOR = -1428333122;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetTdlibParameters extends Function {
        public TdlibParameters parameters;

        public SetTdlibParameters() {
        }

        public SetTdlibParameters(TdlibParameters parameters) {
            this.parameters = parameters;
        }

        public static final int CONSTRUCTOR = -1912557997;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetUserPrivacySettingRules extends Function {
        public UserPrivacySetting setting;
        public UserPrivacySettingRules rules;

        public SetUserPrivacySettingRules() {
        }

        public SetUserPrivacySettingRules(UserPrivacySetting setting, UserPrivacySettingRules rules) {
            this.setting = setting;
            this.rules = rules;
        }

        public static final int CONSTRUCTOR = -473812741;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class SetUsername extends Function {
        public String username;

        public SetUsername() {
        }

        public SetUsername(String username) {
            this.username = username;
        }

        public static final int CONSTRUCTOR = 439901214;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TerminateAllOtherSessions extends Function {

        public TerminateAllOtherSessions() {
        }

        public static final int CONSTRUCTOR = 1874485523;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TerminateSession extends Function {
        public long sessionId;

        public TerminateSession() {
        }

        public TerminateSession(long sessionId) {
            this.sessionId = sessionId;
        }

        public static final int CONSTRUCTOR = -407385812;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TestCallBytes extends Function {
        public byte[] x;

        public TestCallBytes() {
        }

        public TestCallBytes(byte[] x) {
            this.x = x;
        }

        public static final int CONSTRUCTOR = -736011607;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TestCallEmpty extends Function {

        public TestCallEmpty() {
        }

        public static final int CONSTRUCTOR = -627291626;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TestCallString extends Function {
        public String x;

        public TestCallString() {
        }

        public TestCallString(String x) {
            this.x = x;
        }

        public static final int CONSTRUCTOR = -1732818385;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TestCallVectorInt extends Function {
        public int[] x;

        public TestCallVectorInt() {
        }

        public TestCallVectorInt(int[] x) {
            this.x = x;
        }

        public static final int CONSTRUCTOR = -2137277793;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TestCallVectorIntObject extends Function {
        public TestInt[] x;

        public TestCallVectorIntObject() {
        }

        public TestCallVectorIntObject(TestInt[] x) {
            this.x = x;
        }

        public static final int CONSTRUCTOR = 1825428218;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TestCallVectorString extends Function {
        public String[] x;

        public TestCallVectorString() {
        }

        public TestCallVectorString(String[] x) {
            this.x = x;
        }

        public static final int CONSTRUCTOR = -408600900;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TestCallVectorStringObject extends Function {
        public TestString[] x;

        public TestCallVectorStringObject() {
        }

        public TestCallVectorStringObject(TestString[] x) {
            this.x = x;
        }

        public static final int CONSTRUCTOR = 1527666429;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TestGetDifference extends Function {

        public TestGetDifference() {
        }

        public static final int CONSTRUCTOR = 1747084069;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TestNetwork extends Function {

        public TestNetwork() {
        }

        public static final int CONSTRUCTOR = -1343998901;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TestSquareInt extends Function {
        public int x;

        public TestSquareInt() {
        }

        public TestSquareInt(int x) {
            this.x = x;
        }

        public static final int CONSTRUCTOR = -60135024;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TestUseError extends Function {

        public TestUseError() {
        }

        public static final int CONSTRUCTOR = 528842186;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class TestUseUpdate extends Function {

        public TestUseUpdate() {
        }

        public static final int CONSTRUCTOR = 717094686;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ToggleBasicGroupAdministrators extends Function {
        public int basicGroupId;
        public boolean everyoneIsAdministrator;

        public ToggleBasicGroupAdministrators() {
        }

        public ToggleBasicGroupAdministrators(int basicGroupId, boolean everyoneIsAdministrator) {
            this.basicGroupId = basicGroupId;
            this.everyoneIsAdministrator = everyoneIsAdministrator;
        }

        public static final int CONSTRUCTOR = -591395611;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ToggleChatDefaultDisableNotification extends Function {
        public long chatId;
        public boolean defaultDisableNotification;

        public ToggleChatDefaultDisableNotification() {
        }

        public ToggleChatDefaultDisableNotification(long chatId, boolean defaultDisableNotification) {
            this.chatId = chatId;
            this.defaultDisableNotification = defaultDisableNotification;
        }

        public static final int CONSTRUCTOR = 314794002;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ToggleChatIsMarkedAsUnread extends Function {
        public long chatId;
        public boolean isMarkedAsUnread;

        public ToggleChatIsMarkedAsUnread() {
        }

        public ToggleChatIsMarkedAsUnread(long chatId, boolean isMarkedAsUnread) {
            this.chatId = chatId;
            this.isMarkedAsUnread = isMarkedAsUnread;
        }

        public static final int CONSTRUCTOR = -986129697;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ToggleChatIsPinned extends Function {
        public long chatId;
        public boolean isPinned;

        public ToggleChatIsPinned() {
        }

        public ToggleChatIsPinned(long chatId, boolean isPinned) {
            this.chatId = chatId;
            this.isPinned = isPinned;
        }

        public static final int CONSTRUCTOR = -1166802621;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ToggleSupergroupInvites extends Function {
        public int supergroupId;
        public boolean anyoneCanInvite;

        public ToggleSupergroupInvites() {
        }

        public ToggleSupergroupInvites(int supergroupId, boolean anyoneCanInvite) {
            this.supergroupId = supergroupId;
            this.anyoneCanInvite = anyoneCanInvite;
        }

        public static final int CONSTRUCTOR = -797384141;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ToggleSupergroupIsAllHistoryAvailable extends Function {
        public int supergroupId;
        public boolean isAllHistoryAvailable;

        public ToggleSupergroupIsAllHistoryAvailable() {
        }

        public ToggleSupergroupIsAllHistoryAvailable(int supergroupId, boolean isAllHistoryAvailable) {
            this.supergroupId = supergroupId;
            this.isAllHistoryAvailable = isAllHistoryAvailable;
        }

        public static final int CONSTRUCTOR = 1701526555;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ToggleSupergroupSignMessages extends Function {
        public int supergroupId;
        public boolean signMessages;

        public ToggleSupergroupSignMessages() {
        }

        public ToggleSupergroupSignMessages(int supergroupId, boolean signMessages) {
            this.supergroupId = supergroupId;
            this.signMessages = signMessages;
        }

        public static final int CONSTRUCTOR = -558196581;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UnblockUser extends Function {
        public int userId;

        public UnblockUser() {
        }

        public UnblockUser(int userId) {
            this.userId = userId;
        }

        public static final int CONSTRUCTOR = -307286367;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UnpinSupergroupMessage extends Function {
        public int supergroupId;

        public UnpinSupergroupMessage() {
        }

        public UnpinSupergroupMessage(int supergroupId) {
            this.supergroupId = supergroupId;
        }

        public static final int CONSTRUCTOR = -1987029530;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UpgradeBasicGroupChatToSupergroupChat extends Function {
        public long chatId;

        public UpgradeBasicGroupChatToSupergroupChat() {
        }

        public UpgradeBasicGroupChatToSupergroupChat(long chatId) {
            this.chatId = chatId;
        }

        public static final int CONSTRUCTOR = 300488122;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UploadFile extends Function {
        public InputFile file;
        public FileType fileType;
        public int priority;

        public UploadFile() {
        }

        public UploadFile(InputFile file, FileType fileType, int priority) {
            this.file = file;
            this.fileType = fileType;
            this.priority = priority;
        }

        public static final int CONSTRUCTOR = -745597786;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class UploadStickerFile extends Function {
        public int userId;
        public InputFile pngSticker;

        public UploadStickerFile() {
        }

        public UploadStickerFile(int userId, InputFile pngSticker) {
            this.userId = userId;
            this.pngSticker = pngSticker;
        }

        public static final int CONSTRUCTOR = 1134087551;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ValidateOrderInfo extends Function {
        public long chatId;
        public long messageId;
        public OrderInfo orderInfo;
        public boolean allowSave;

        public ValidateOrderInfo() {
        }

        public ValidateOrderInfo(long chatId, long messageId, OrderInfo orderInfo, boolean allowSave) {
            this.chatId = chatId;
            this.messageId = messageId;
            this.orderInfo = orderInfo;
            this.allowSave = allowSave;
        }

        public static final int CONSTRUCTOR = 9480644;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ViewMessages extends Function {
        public long chatId;
        public long[] messageIds;
        public boolean forceRead;

        public ViewMessages() {
        }

        public ViewMessages(long chatId, long[] messageIds, boolean forceRead) {
            this.chatId = chatId;
            this.messageIds = messageIds;
            this.forceRead = forceRead;
        }

        public static final int CONSTRUCTOR = -1925784915;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

    public static class ViewTrendingStickerSets extends Function {
        public long[] stickerSetIds;

        public ViewTrendingStickerSets() {
        }

        public ViewTrendingStickerSets(long[] stickerSetIds) {
            this.stickerSetIds = stickerSetIds;
        }

        public static final int CONSTRUCTOR = -952416520;

        @Override
        public int getConstructor() {
            return CONSTRUCTOR;
        }
    }

}
