package no.ums.pas.parm2;

import no.ums.ws.parm.PaObject;

import javax.swing.*;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public interface ParmUiService {

    void deletePaObject(PaObject object);

    ListModel getRootFolders();
}
