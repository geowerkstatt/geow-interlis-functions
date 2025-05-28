package ch.geowerkstatt.ilivalidator.extensions.functions;

import ch.interlis.ili2c.metamodel.PathEl;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.metamodel.Type;
import ch.interlis.ili2c.metamodel.Viewable;
import ch.interlis.iom.IomObject;
import ch.interlis.iox_j.validator.Validator;
import ch.interlis.iox_j.validator.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class ExtractAttributeIoxPlugin extends BaseInterlisFunction {
    @Override
    public String getQualifiedIliName() {
        return "GeoW_FunctionsExt.ExtractAttribute";
    }

    protected Value evaluateInternal(String validationKind, String usageScope, IomObject contextObject, Value[] actualArguments) {
        Value argObjects = actualArguments[0];
        Value argPath = actualArguments[1];

        if (argObjects.isUndefined()) {
            return Value.createSkipEvaluation();
        }

        Value attributes = evaluateObjectPathValues(td, validator, argObjects, argPath, usageScope);
        return attributes;
    }

    /**
     * Same as {@link EvaluationHelper#evaluateObjectPath(TransferDescription, Validator, Value, Value, IomObject, String)} but returns
     * a {@link Value} instead of a collection of {@link IomObject}.
     */
    public static Value evaluateObjectPathValues(TransferDescription td, Validator validator, Value object, Value path, String usageScope) {
        if (path.isUndefined() || object.isUndefined()) {
            return object;
        } else if (object.getComplexObjects().isEmpty()) {
            return Value.createUndefined();
        } else {
            Viewable contextClass = EvaluationHelper.getContextClass(td, null, object);
            if (contextClass == null) {
                throw new IllegalStateException("unknown class in " + usageScope);
            }

            PathEl[] attributePath = EvaluationHelper.getAttributePathEl(validator, contextClass, path);
            return evaluateSimpleAttributes(validator, object, attributePath);
        }
    }

    /**
     * Similar to {@link EvaluationHelper#evaluateAttributes(Validator, Value, PathEl[])} but returns a {@link Value} instead of a collection of {@link IomObject}.
     */
    public static Value evaluateSimpleAttributes(Validator validator, Value argObjects, PathEl[] attributePath) {
        Collection<Value> attributes = new ArrayList<>();
        Type valueType = null;

        for (IomObject rootObject : argObjects.getComplexObjects()) {
            Value attribute = validator.getValueFromObjectPath(null, rootObject, attributePath, null);

            if (!(attribute.isUndefined() || attribute.skipEvaluation())) {
                if (valueType != null && attribute.getType() != valueType) {
                    throw new IllegalStateException("Not all attributes on the path have the same type");
                } else if (valueType == null) {
                    valueType = attribute.getType();
                }

                attributes.add(attribute);
            }
        }

        if (attributes.isEmpty()) {
            return Value.createUndefined();
        } else if (attributes.size() == 1) {
            return attributes.iterator().next();
        } else {
            // Combine resulting values into a single Value
            List<String> values = new ArrayList<>();
            List<IomObject> complexObjects = new ArrayList<>();
            for (Value attr : attributes) {
                if (attr.getValue() != null) {
                    values.add(attr.getValue());
                } else if (attr.getValues() != null) {
                    values.addAll(Arrays.asList(attr.getValues()));
                } else if (attr.getComplexObjects() != null) {
                    complexObjects.addAll(attr.getComplexObjects());
                }
            }

            if (!values.isEmpty()) {
                return new Value(valueType, values.toArray(new String[0]));
            } else if (!complexObjects.isEmpty()) {
                return new Value(complexObjects);
            } else {
                return Value.createUndefined();
            }
        }
    }
}
