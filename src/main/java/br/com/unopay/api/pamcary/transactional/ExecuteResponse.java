package br.com.unopay.api.pamcary.transactional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
@XmlType(name = "executeResponse", propOrder = {
    "_return"
},namespace = "executeResponse")
public class ExecuteResponse implements Equals2, HashCode2
{

    @XmlElement(name = "return")
    protected ResponseTO _return;

    public ResponseTO getReturn() {
        return _return;
    }

    public void setReturn(ResponseTO value) {
        this._return = value;
    }

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object,
                          EqualsStrategy2 strategy) {
        if (object == null||this.getClass()!= object.getClass()) {
            return false;
        }
        if (this.equals(object)) {
            return true;
        }
        final ExecuteResponse that = ((ExecuteResponse) object);
        ResponseTO lhsReturn;
        lhsReturn = this.getReturn();
        ResponseTO rhsReturn;
        rhsReturn = that.getReturn();
        return !strategy.equals(LocatorUtils.property(thisLocator, "_return", lhsReturn),
                LocatorUtils.property(thatLocator, "_return", rhsReturn), lhsReturn,
                rhsReturn, this._return!= null, that._return!= null);
    }

    public boolean equals(Object object) {
        final EqualsStrategy2 strategy = JAXBEqualsStrategy.INSTANCE;
        return equals(null, null, object, strategy);
    }

    public int hashCode(ObjectLocator locator, HashCodeStrategy2 strategy) {
        int currentHashCode = 1;
        ResponseTO theReturn;
        theReturn = this.getReturn();
        currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "_return", theReturn),
                currentHashCode, theReturn, this._return!= null);
        return currentHashCode;
    }

    public int hashCode() {
        final HashCodeStrategy2 strategy = JAXBHashCodeStrategy.INSTANCE;
        return this.hashCode(null, strategy);
    }

}
