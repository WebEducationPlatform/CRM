package com.ewp.crm.utils.tdlib;

import org.telegram.api.TLConfig;
import org.telegram.api.auth.TLAuthorization;
import org.telegram.api.engine.storage.AbsApiState;
import org.telegram.mtproto.state.AbsMTProtoState;
import org.telegram.mtproto.state.ConnectionInfo;

public class CRMAbsApiState implements AbsApiState {

    private int primaryDc;
    private boolean authenticated;

    @Override
    public int getPrimaryDc() {
        return this.primaryDc;
    }

    @Override
    public void setPrimaryDc(int i) {
        this.primaryDc = i;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public boolean isAuthenticated(int i) {
        return primaryDc == i && authenticated;
    }

    @Override
    public void setAuthenticated(int i, boolean b) {
        this.primaryDc = i;
        this.authenticated = b;
    }

    @Override
    public void updateSettings(TLConfig tlConfig) {

    }

    @Override
    public byte[] getAuthKey(int i) {
        return new byte[0];
    }

    @Override
    public void putAuthKey(int i, byte[] bytes) {

    }

    @Override
    public ConnectionInfo[] getAvailableConnections(int i) {
        return new ConnectionInfo[0];
    }

    @Override
    public AbsMTProtoState getMtProtoState(int i) {
        return null;
    }

    @Override
    public void doAuth(TLAuthorization tlAuthorization) {

    }

    @Override
    public void resetAuth() {

    }

    @Override
    public void reset() {

    }

    @Override
    public int getUserId() {
        return 0;
    }
}
