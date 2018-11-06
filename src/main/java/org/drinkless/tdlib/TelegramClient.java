package org.drinkless.tdlib;

import org.drinkless.tdlib.TdApi;

public final class TelegramClient implements Runnable {

    private final long nativeClientId;
    private static final int MAX_EVENTS = 1000;
    private final long[] eventIds = new long[MAX_EVENTS];
    private final TdApi.Object[] events = new TdApi.Object[MAX_EVENTS];

    static {
        System.loadLibrary("tdjni");
    }

    public TelegramClient() {
        this.nativeClientId = createNativeClient();
    }

    public long[] getEventIds() {
        return eventIds;
    }

    public TdApi.Object[] getEvents() {
        return events;
    }

    public void receiveQueries(double timeout) {
        int resultN = nativeClientReceive(nativeClientId, eventIds, events, timeout);
        for (int i = 0; i < resultN; i++) {
//            processResult(eventIds[i], events[i]);
            events[i] = null;
        }
    }


    private static native long createNativeClient();

    private static native void nativeClientSend(long nativeClientId, long eventId, TdApi.Function function);

    private static native int nativeClientReceive(long nativeClientId, long[] eventIds, TdApi.Object[] events, double timeout);

    private static native TdApi.Object nativeClientExecute(TdApi.Function function);

    private static native void destroyNativeClient(long nativeClientId);

    @Override
    public void run() {
        System.out.println("Run!");
    }
}
