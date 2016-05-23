package za.co.entelect.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;
import org.meanbean.lang.Factory;
import org.meanbean.test.BeanTester;
import org.meanbean.test.ConfigurationBuilder;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

/**
 * Blatantly stolen from this nice article: http://www.jdev.it/tips-unit-testing-javabeans/
 */
public abstract class AbstractEntityTest<T> {

    protected abstract T getBeanInstance();

    protected abstract Object[] getPrefabInstances();

    protected abstract String[] getIgnoredProperties();

    @Test
    public void beanIsSerializable() throws Exception {
        final T myBean = getBeanInstance();
        final byte[] serializedMyBean = SerializationUtils.serialize((Serializable) myBean);
        @SuppressWarnings("unchecked")
        final T deserializedMyBean = (T) SerializationUtils.deserialize(serializedMyBean);
        assertEquals(myBean, deserializedMyBean);
    }

    @Test
    public void equalsAndHashCodeContract() {
        EqualsVerifier verifier = EqualsVerifier.forClass(getBeanInstance().getClass())
            .withRedefinedSuperclass()
            .suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS);

        Object[] prefabs = getPrefabInstances();
        for (int i = 0; i < prefabs.length; i += 2) {
            verifier = verifier.withPrefabValues(prefabs[i].getClass(), prefabs[i], prefabs[i + 1]);
        }

        verifier.verify();
    }

    @Test
    public void getterAndSetterCorrectness() throws Exception {
        final BeanTester beanTester = new BeanTester();
        beanTester.getFactoryCollection().addFactory(LocalDateTime.class, new LocalDateTimeFactory());
        beanTester.getFactoryCollection().addFactory(LocalDate.class, new LocalDateFactory());

        ConfigurationBuilder configBuilder = new ConfigurationBuilder();
        for (String ignoredProperty : getIgnoredProperties()) {
            configBuilder.ignoreProperty(ignoredProperty);
        }

        beanTester.testBean(getBeanInstance().getClass(), configBuilder.build());
    }

    /**
     * Concrete Factory that creates a LocalDateTime.
     */
    class LocalDateTimeFactory implements Factory<LocalDateTime> {

        @Override
        public LocalDateTime create() {
            return LocalDateTime.now();
        }
    }

    /**
     * Concrete Factory that creates a LocalDate.
     */
    class LocalDateFactory implements Factory<LocalDate> {

        @Override
        public LocalDate create() {
            return LocalDate.now();
        }
    }
}
