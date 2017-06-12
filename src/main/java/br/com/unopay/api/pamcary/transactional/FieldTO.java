package br.com.unopay.api.pamcary.transactional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.jvnet.jaxb2_commons.lang.Equals2;
import org.jvnet.jaxb2_commons.lang.EqualsStrategy2;
import org.jvnet.jaxb2_commons.lang.HashCode2;
import org.jvnet.jaxb2_commons.lang.HashCodeStrategy2;
import org.jvnet.jaxb2_commons.lang.JAXBEqualsStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBHashCodeStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;
import org.jvnet.jaxb2_commons.locator.util.LocatorUtils;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fieldTO", propOrder = {
    "key",
    "value"
}, namespace = "fieldTO")
public class FieldTO implements Equals2, HashCode2
{

    protected String key;
    protected String value;

    public String getKey() {
        return key;
    }

    public void setKey(String value) {
        this.key = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object,
                          EqualsStrategy2 strategy) {
        if ((object == null)||(this.getClass()!= object.getClass())) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final FieldTO that = ((FieldTO) object);
        String lhsKey;
        lhsKey = this.getKey();
        String rhsKey;
        rhsKey = that.getKey();
        if (!strategy.equals(LocatorUtils.property(thisLocator, "key", lhsKey),
                LocatorUtils.property(thatLocator, "key", rhsKey), lhsKey, rhsKey,
                this.key!= null, that.key!= null)) {
            return false;
        }
        String lhsValue;
        lhsValue = this.getValue();
        String rhsValue;
        rhsValue = that.getValue();
        if (!strategy.equals(LocatorUtils.property(thisLocator, "value", lhsValue),
                LocatorUtils.property(thatLocator, "value", rhsValue), lhsValue, rhsValue,
                this.value!= null, that.value!= null)) {
            return false;
        }
        return true;
    }

    public boolean equals(Object object) {
        final EqualsStrategy2 strategy = JAXBEqualsStrategy.INSTANCE;
        return equals(null, null, object, strategy);
    }

    public int hashCode(ObjectLocator locator, HashCodeStrategy2 strategy) {
        int currentHashCode = 1;
        {
            String theKey;
            theKey = this.getKey();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "key", theKey),
                    currentHashCode, theKey, (this.key!= null));
        }
        {
            String theValue;
            theValue = this.getValue();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "value", theValue),
                    currentHashCode, theValue, (this.value!= null));
        }
        return currentHashCode;
    }

    public int hashCode() {
        final HashCodeStrategy2 strategy = JAXBHashCodeStrategy.INSTANCE;
        return this.hashCode(null, strategy);
    }

}
