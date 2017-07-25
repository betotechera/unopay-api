package br.com.unopay.api.pamcary.transactional;

import java.util.List;
import lombok.Value;
import org.jvnet.jaxb2_commons.lang.EqualsStrategy2;
import org.jvnet.jaxb2_commons.lang.HashCodeStrategy2;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;
import org.jvnet.jaxb2_commons.locator.util.LocatorUtils;

@Value
public class TransacionalUtil {

    private String context;
    private List<FieldTO> fields;

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object,
                          EqualsStrategy2 strategy) {
        if (object == null||this.getClass()!= object.getClass()) {
            return false;
        }
        if (this.equals(object)) {
            return true;
        }
        final RequestTO that = ((RequestTO) object);
        String lhsContext;
        lhsContext = this.getContext();
        String rhsContext;
        rhsContext = that.getContext();
        if (!strategy.equals(LocatorUtils.property(thisLocator, "context", lhsContext),
                LocatorUtils.property(thatLocator, "context", rhsContext), lhsContext, rhsContext,
                this.context!= null, that.context!= null)) {
            return false;
        }
        List<FieldTO> lhsFields;
        lhsFields = this.fields!= null&&!this.fields.isEmpty()?this.getFields():null;
        List<FieldTO> rhsFields;
        rhsFields = that.fields!= null&&!that.fields.isEmpty()?that.getFields():null;
        if(!strategy.equals(LocatorUtils.property(thisLocator, "fields", lhsFields),
                LocatorUtils.property(thatLocator, "fields", rhsFields), lhsFields, rhsFields,
                this.fields!= null&&!this.fields.isEmpty(), that.fields!= null&&
                        !that.fields.isEmpty())) {
            return false;
        }
        return true;
    }

    public int hashCode(ObjectLocator locator, HashCodeStrategy2 strategy) {
        int currentHashCode = 1;
        String theContext;
        theContext = this.getContext();
        currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "context", theContext),
                currentHashCode, theContext, this.context!= null);
        List<FieldTO> theFields;
        theFields = this.fields!= null&&!this.fields.isEmpty()?this.getFields():null;
        currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "fields", theFields),
                currentHashCode, theFields, this.fields!= null&&!this.fields.isEmpty());
        return currentHashCode;
    }
}
