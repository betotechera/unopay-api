
package br.com.unopay.api.pamcary.transactional;

import java.util.ArrayList;
import java.util.List;
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
@XmlType(name = "requestTO", propOrder = {
    "context",
    "fields"
}, namespace = "requestTO")
public class RequestTO implements Equals2, HashCode2
{

    protected String context;
    @XmlElement(nillable = true)
    protected List<FieldTO> fields;

    public String getContext() {
        return context;
    }

    public void setContext(String value) {
        this.context = value;
    }

    public List<FieldTO> getFields() {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        return this.fields;
    }

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object,
                          EqualsStrategy2 strategy) {
        if ((object == null)||(this.getClass()!= object.getClass())) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final RequestTO that = ((RequestTO) object);
        {
            String lhsContext;
            lhsContext = this.getContext();
            String rhsContext;
            rhsContext = that.getContext();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "context", lhsContext),
                    LocatorUtils.property(thatLocator, "context", rhsContext), lhsContext, rhsContext,
                    (this.context!= null), (that.context!= null))) {
                return false;
            }
        }
        {
            List<FieldTO> lhsFields;
            lhsFields = (((this.fields!= null)&&(!this.fields.isEmpty()))?this.getFields():null);
            List<FieldTO> rhsFields;
            rhsFields = (((that.fields!= null)&&(!that.fields.isEmpty()))?that.getFields():null);
            if (!strategy.equals(LocatorUtils.property(thisLocator, "fields", lhsFields),
                    LocatorUtils.property(thatLocator, "fields", rhsFields), lhsFields, rhsFields,
                    ((this.fields!= null)&&(!this.fields.isEmpty())), ((that.fields!= null)&&
                            (!that.fields.isEmpty())))) {
                return false;
            }
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
            String theContext;
            theContext = this.getContext();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "context", theContext),
                    currentHashCode, theContext, (this.context!= null));
        }
        {
            List<FieldTO> theFields;
            theFields = (((this.fields!= null)&&(!this.fields.isEmpty()))?this.getFields():null);
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "fields", theFields),
                    currentHashCode, theFields, ((this.fields!= null)&&(!this.fields.isEmpty())));
        }
        return currentHashCode;
    }

    public int hashCode() {
        final HashCodeStrategy2 strategy = JAXBHashCodeStrategy.INSTANCE;
        return this.hashCode(null, strategy);
    }

}