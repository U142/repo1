package no.ums.pas.ui;

public class PropertyTest {

    public static void main(String[] args) {
        PropertyInterface pi = PropertyFactory.get(PropertyInterface.class);
        pi.setUsername("Test");
        System.out.println(pi.getUsername());
        // Result: Test
    }
}
