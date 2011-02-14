package no.ums.pas.parm2;

import com.google.common.primitives.Longs;
import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.ws.parm.*;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.xml.ws.AsyncHandler;
import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class ParmUiServiceSoapImpl implements ParmUiService {

    private static final Log log = UmsLog.getLogger(ParmUiServiceSoapImpl.class);

    private final ParmSoap parmSoap;
    private final ListModel rootFolders;

    public ParmUiServiceSoapImpl(final String sessionId, final ParmSoap parmSoap, final ScheduledExecutorService executorService) {
        this.parmSoap = parmSoap;
        rootFolders = new AsyncSoapListModel<GetPaRootObjectsResponse, PaObject>(executorService) {
            @Override
            protected Collection<PaObject> unpack(final GetPaRootObjectsResponse response) {
                DataResultOfListOfPaObject result = response.getGetPaRootObjectsResult();
                return (result.isValid()) ? result.getValue().getPaObject() : null;
            }

            @Override
            protected void invoke(final AsyncHandler<GetPaRootObjectsResponse> handler) {
                parmSoap.getPaRootObjectsAsync(sessionId, handler);
            }

            @Override
            protected boolean isEqual(@Nonnull final PaObject a, @Nonnull final PaObject b) {
                return a.getObjectpk() == b.getObjectpk();
            }
        };
    }

    @Override
    public void deletePaObject(final PaObject object) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ListModel getRootFolders() {
        return rootFolders;
    }
}
