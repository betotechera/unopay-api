
package br.com.unopay.api.pamcary.transactional;

import groovy.transform.EqualsAndHashCode;
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
import org.jvnet.jaxb2_commons.locator.ObjectLocator;

@EqualsAndHashCode
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
        return new TransacionalUtil(null, fields).equals(thisLocator, thatLocator, object, strategy);
    }

    public int hashCode(ObjectLocator locator, HashCodeStrategy2 strategy) {
        return new TransacionalUtil(null, fields).hashCode(locator, strategy);
    }

}
