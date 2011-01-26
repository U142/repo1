package no.ums.pas.lang;

import org.hamcrest.CoreMatchers;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.junit.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class MessageFactoryTest {

    interface Test1 extends  Messages {

        @Default("Test")
        public String getTest();
    }

    @Test
    public void testSimpleConstruction() {
        final Test1 messages = MessageFactory.getMessages(Test1.class);
        assertThat(messages.getTest(), equalTo("Test"));
    }

    @Test
    public void testWithProperties() {
        final MessageTest test = MessageFactory.getMessages(MessageTest.class);
        assertThat(test.getMessageOne(), equalTo("Message One"));
        assertThat(test.getFormattedMessage(5), equalTo("Formatted message [5]"));
    }

    @Test
    public void testWithPropertiesNorwegian() {
        final MessageTest test = MessageFactory.getMessages(MessageTest.class, new Locale("no", "NO"));
        assertThat(test.getMessageOne(), equalTo("Melding en"));
        assertThat(test.getFormattedMessage(5), equalTo("Formattert melding [5]"));
    }

    @Test
    public void testChangeLocale() {
        final MessageTest test = MessageFactory.getMessages(MessageTest.class);
        assertThat(test.getMessageOne(), equalTo("Message One"));
        assertThat(test.getFormattedMessage(5), equalTo("Formatted message [5]"));
        test.setLocale(new Locale("no", "NO"));
        assertThat(test.getMessageOne(), equalTo("Melding en"));
        assertThat(test.getFormattedMessage(5), equalTo("Formattert melding [5]"));
    }

    @Test
    public void testEventsFired() {
        final MessageTest test = MessageFactory.getMessages(MessageTest.class, Locale.ENGLISH);
        final AtomicInteger changeCount = new AtomicInteger(0);
        test.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                changeCount.incrementAndGet();
                assertThat(evt.getSource(), CoreMatchers.<Object>equalTo(test));
            }
        });
        test.setLocale(new Locale("no", "NO"));
        assertThat(changeCount.get(), equalTo(2));
    }

    public static class Bean {
        private String messageOne;

        public String getMessageOne() {
            return messageOne;
        }

        public void setMessageOne(String messageOne) {
            this.messageOne = messageOne;
        }
    }

    @Test
    public void testBinding() {
        final MessageTest test = MessageFactory.getMessages(MessageTest.class, Locale.ENGLISH);
        final Bean bean = new Bean();
        Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, test, BeanProperty.create("messageOne"), bean, BeanProperty.create("messageOne")).bind();

        String old = bean.getMessageOne();
        test.setLocale(new Locale("no", "NO"));
        String updated = bean.getMessageOne();
        assertThat(old, not(equalTo(updated)));
    }
}
