package no.ums.pas.plugins.centric;

public class CentricVariables {
	private static CentricSendOptionToolbar centric_send = null;

    public static CentricSendOptionToolbar getCentric_send() {
        return centric_send;
    }

    public static void setCentric_send(CentricSendOptionToolbar centric_send) {
        CentricVariables.centric_send = centric_send;
    }
}
