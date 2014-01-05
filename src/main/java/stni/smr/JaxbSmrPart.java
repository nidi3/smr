package stni.smr;

import stni.smr.model.ObjectFactory;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import java.io.File;
import java.lang.reflect.ParameterizedType;

/**
 *
 */
abstract class JaxbSmrPart<T> extends AbstractSmrPart {
    protected final T data;

    public JaxbSmrPart(Smr smr, File file) {
        super(smr, file);
        data = JAXB.unmarshal(file, findActualTypeParameter());
    }

    private Class<T> findActualTypeParameter() {
        Class<?> last = getClass();
        while (last.getSuperclass() != JaxbSmrPart.class) {
            last = last.getSuperclass();
        }
        return (Class<T>) ((ParameterizedType) last.getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public void save() {
        JAXB.marshal(data, file);
    }
}
