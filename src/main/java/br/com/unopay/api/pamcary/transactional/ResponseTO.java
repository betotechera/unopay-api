
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
@XmlType(name = "responseTO", propOrder = {
    "fields"
}, namespace = "responseTO")
public class ResponseTO implements Equals2, HashCode2
{

    @XmlElement(nillable = true)
    protected List<FieldTO> fields;

    public List<FieldTO> getFields() {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        return this.fields;
    }

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object,
                          EqualsStrategy2 strategy) {
        if (object == null||this.getClass()!= object.getClass()) {
            return false;
        }
        if (this.equals(object)) {
            return true;
        }
        final ResponseTO that = ((ResponseTO) object);
        List<FieldTO> lhsFields;
        lhsFields = this.fields!= null&&!this.fields.isEmpty()?this.getFields():null;
        List<FieldTO> rhsFields;
        rhsFields = that.fields!= null&&!that.fields.isEmpty()?that.getFields():null;
        if (!strategy.equals(LocatorUtils.property(thisLocator, "fields", lhsFields),
                LocatorUtils.property(thatLocator, "fields", rhsFields), lhsFields, rhsFields,
                this.fields!= null&&!this.fields.isEmpty(),
                that.fields!= null&&!that.fields.isEmpty())) {
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
        List<FieldTO> theFields;
        theFields = this.fields!= null&&!this.fields.isEmpty()?this.getFields():null;
        currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "fields", theFields),
                currentHashCode, theFields, this.fields!= null&&!this.fields.isEmpty());
        return currentHashCode;
    }

    public int hashCode() {
        final HashCodeStrategy2 strategy = JAXBHashCodeStrategy.INSTANCE;
        return this.hashCode(null, strategy);
    }

}
