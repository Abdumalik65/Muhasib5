package sample.Tools;

import sample.Data.Aloqa;

public class ConnectionType {
    static Boolean isRemoteConnection;
    static Aloqa aloqa;

    public static Boolean getIsRemoteConnection() {
        return isRemoteConnection;
    }

    public static void setIsRemoteConnection(Boolean isRemoteConnection) {
        ConnectionType.isRemoteConnection = isRemoteConnection;
    }

    public static Aloqa getAloqa() {
        return aloqa;
    }

    public static void setAloqa(Aloqa aloqa) {
        ConnectionType.aloqa = aloqa;
    }
}
