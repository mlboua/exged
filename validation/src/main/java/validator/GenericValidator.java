package validator;

import config.json.mapping.headers.MappingHeaders;
import data.Fold;
import org.reflections.Reflections;
import validator.complex.ComplexValidationCondition;
import validator.complex.ComplexValidator;
import validator.simple.SimpleValidationCondition;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class GenericValidator implements Validator {

    private static final Map<String, ComplexValidationCondition> complexConditions;
    private static final Map<String, SimpleValidationCondition> simpleConditions;

    static {
        Reflections ref = new Reflections("validator");
        final Map<String, List<Class<?>>> validatorsMap = ref.getTypesAnnotatedWith(ValidatorAnnotation.class)
                .stream()
                .collect(Collectors.groupingBy(validatorClass -> validatorClass.getAnnotation(ValidatorAnnotation.class).type()));

        simpleConditions = validatorsMap.get("simple").stream()
                .collect(Collectors.toMap(GenericValidator::getValidatorName, GenericValidator::createSimpleValidatorInstance));
        complexConditions = validatorsMap.get("complex").stream()
                .collect(Collectors.toMap(GenericValidator::getValidatorName, GenericValidator::createComplexValidatorInstance));
    }

    public GenericValidator(MappingHeaders mappingHeaders) {
        final Map<String, List<String>> simpleValidatorsToHeadersMap = simpleConditions.keySet().stream().collect(Collectors.toMap(validatorName -> validatorName, validatorName -> new ArrayList<>()));
        final Map<String, List<ComplexValidator>> complexValidatorsToHeadersMap = complexConditions.keySet().stream().collect(Collectors.toMap(validatorName -> validatorName, validatorName -> new ArrayList<>()));
        mappingHeaders.getUniqueHeader().forEach(uniqueHeader -> {
            if (uniqueHeader.getValidators().isPresent() && uniqueHeader.getValidators().get().getSimple().isPresent()) {
                uniqueHeader.getValidators().get().getSimple().get().forEach(validator -> {
                    if (simpleValidatorsToHeadersMap.containsKey(validator)) {
                        simpleValidatorsToHeadersMap.get(validator).add(uniqueHeader.getName());
                    }
                });
            }
            if (uniqueHeader.getValidators().isPresent() && uniqueHeader.getValidators().get().getComplex().isPresent()) {
                uniqueHeader.getValidators().get().getComplex().get().forEach(validator -> {
                    if (validator.getArguments().isPresent() && complexValidatorsToHeadersMap.containsKey(validator.getName())) {
                        complexValidatorsToHeadersMap.get(validator.getName()).add(new ComplexValidator(uniqueHeader.getName(), validator.getArguments().get()));
                    }
                });
            }
        });

        System.out.println(complexValidatorsToHeadersMap);

    }

    private static String getValidatorName(Class<?> simpleValidator) {
        return simpleValidator.getAnnotation(ValidatorAnnotation.class).name();
    }

    private static SimpleValidationCondition createSimpleValidatorInstance(Class<?> simpleValidator) {
        try {
            return (SimpleValidationCondition) simpleValidator.getConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ComplexValidationCondition createComplexValidatorInstance(Class<?> complexValidator) {
        try {
            return (ComplexValidationCondition) complexValidator.getConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<Reject> validateFold(Fold fold) {
        return null;
    }
}
