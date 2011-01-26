package no.ums.pas.beans;

/**
 *
 * @author Ståle Undheim <su@ums.no>
 */
public interface BindingProperty<SS, SV> {

    BindingFactory<SS, SV> autobind(SS src);
}
