package no.ums.pas.cellbroadcast;

public class Area {

    private String m_id;
    private String m_name;

    public Area(String id, String name) {
        m_id = id;
        m_name = name;
    }

    public String get_id() {
        return m_id;
    }

    public void set_id(String m_id) {
        this.m_id = m_id;
    }

    public String get_name() {
        return m_name;
    }

    public void set_name(String m_name) {
        this.m_name = m_name;
    }

    public String toString() {
        return m_name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Area area = (Area) o;

        return !(m_id != null ? !m_id.equals(area.m_id) : area.m_id != null);
    }

    @Override
    public int hashCode() {
        return m_id != null ? m_id.hashCode() : 0;
    }
}
