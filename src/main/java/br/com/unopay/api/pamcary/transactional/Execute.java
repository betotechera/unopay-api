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
@XmlType(name = "execute", propOrder = {
    "arg0"
}, namespace = "execute")

public class Execute implements Equals2, HashCode2
{

    protected RequestTO arg0;

    public RequestTO getArg0() {
        return arg0;
    }

    public void setArg0(RequestTO value) {
        this.arg0 = value;
    }

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator,
                          Object object, EqualsStrategy2 strategy) {
        if ((object == null)||(this.getClass()!= object.getClass())) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final Execute that = ((Execute) object);
        RequestTO lhsArg0;
        lhsArg0 = this.getArg0();
        RequestTO rhsArg0;
        rhsArg0 = that.getArg0();
        if (!strategy.equals(LocatorUtils.property(thisLocator, "arg0", lhsArg0),
                LocatorUtils.property(thatLocator, "arg0", rhsArg0), lhsArg0, rhsArg0,
                this.arg0 != null, that.arg0 != null)) {
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
            RequestTO theArg0;
            theArg0 = this.getArg0();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "arg0", theArg0),
                    currentHashCode, theArg0, (this.arg0 != null));
        }
        return currentHashCode;
    }

    public int hashCode() {
        final HashCodeStrategy2 strategy = JAXBHashCodeStrategy.INSTANCE;
        return this.hashCode(null, strategy);
    }

}
