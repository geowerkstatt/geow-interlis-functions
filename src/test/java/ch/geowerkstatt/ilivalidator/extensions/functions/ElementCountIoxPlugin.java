package ch.geowerkstatt.ilivalidator.extensions.functions;

import ch.interlis.iom.IomObject;
import ch.interlis.iox_j.validator.Value;

/**
 * Counts the number of elements.
 * This function solely exists because the internal implementation does not work on bags of primitive types.
 * @see <a href="https://github.com/claeis/ilivalidator/issues/445">ElementCount does not work on Bag/List of Primitive type</a>
 */
public final class ElementCountIoxPlugin extends BaseInterlisFunction {
    @Override
    public String getQualifiedIliName() {
        return "TestSuite.elementCount";
    }

    protected Value evaluateInternal(String validationKind, String usageScope, IomObject contextObject, Value[] actualArguments) {
        Value argObjects = actualArguments[0];

        if (argObjects.isUndefined()) {
            return new Value(0);
        } else if (argObjects.getComplexObjects() != null) {
            return new Value(argObjects.getComplexObjects().size());
        } else if (argObjects.getValues() != null) {
            return new Value(argObjects.getValues().length);
        } else if (argObjects.getValue() != null) {
            return new Value(1);
        } else {
            return Value.createUndefined();
        }
    }
}
