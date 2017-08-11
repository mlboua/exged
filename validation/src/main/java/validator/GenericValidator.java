package validator;

import config.mapping.reject.RejectConfig;
import config.mapping.validations.ValidatorsConfig;
import data.Fold;
import exception.ExgedValidatorException;
import org.reflections.Reflections;
import validator.complex.ComplexValidationCondition;
import validator.complex.ComplexValidator;
import validator.simple.SimpleValidationCondition;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
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

    private final Map<String, List<String>> simpleValidatorsToHeadersMap;
    private final Map<String, List<ComplexValidator>> complexValidatorsToHeadersMap;
    Map<String, RejectConfig> validatorToReject;
    Map<String, String> detailRejectMap;

    public GenericValidator(ValidatorsConfig validatorsConfig, List<RejectConfig> rejectConfig) {
        simpleValidatorsToHeadersMap = simpleConditions.keySet().stream().collect(Collectors.toMap(validatorName -> validatorName, validatorName -> new ArrayList<>()));
        complexValidatorsToHeadersMap = complexConditions.keySet().stream().collect(Collectors.toMap(validatorName -> validatorName, validatorName -> new ArrayList<>()));
        validatorsConfig.getUnique().forEach(uniqueHeader -> {
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
        simpleValidatorsToHeadersMap.values().removeIf(List::isEmpty);
        complexValidatorsToHeadersMap.values().removeIf(List::isEmpty);
        validatorToReject = new HashMap<>();
        simpleValidatorsToHeadersMap.forEach((name, validator) -> rejectConfig.forEach(mapReject -> {
            if (mapReject.getValidators().getSimple().isPresent()) {
                mapReject.getValidators().getSimple().get().stream()
                        .filter(validatorName -> validatorName.equals(name))
                        .findFirst()
                        .ifPresent(validatorFind -> validatorToReject.put(validatorFind, mapReject));
            }
        }));
        complexValidatorsToHeadersMap.forEach((name, validator) -> rejectConfig.forEach(mapReject -> {
            if (mapReject.getValidators().getComplex().isPresent()) {
                mapReject.getValidators().getComplex().get().stream()
                        .filter(validatorName -> validatorName.getName().equals(name))
                        .findFirst()
                        .ifPresent(validatorFind -> {
                            validatorToReject.put(validatorFind.getName(), mapReject);
                        });
            }
        }));
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
    public Optional<List<DetailReject>> validateFold(Fold fold) {
        final List<DetailReject> simpleReject = simpleValidatorsToHeadersMap.keySet().stream().map(key -> {
            try {
                final Optional<Reject> reject = simpleConditions.get(key).validate(validatorToReject.get(key).getCode(), fold, simpleValidatorsToHeadersMap.get(key), fold.getHeader());
                if (reject.isPresent()) {
                    return Optional.of(new DetailReject(reject.get().getCode(), reject.get().getValues().get(), validatorToReject.get(key).getDetail(), fold));
                }
            } catch (ExgedValidatorException e) {
                e.printStackTrace();
            }
            return Optional.<Reject>empty();
        })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(reject -> {
                    final Optional<RejectConfig> firstReject = validatorToReject.values().stream().filter(rejectMap -> rejectMap.getCode().equals(reject.getCode())).findFirst();
                    return firstReject.map(rejectConfig -> new DetailReject(reject.getCode(), reject.getValues().get(), rejectConfig.getDetail(), fold)).orElse(null);
                })
                .collect(Collectors.toList());
        if (simpleReject.isEmpty()) {
            final List<DetailReject> complexReject = complexValidatorsToHeadersMap.keySet().stream().map(key -> {
                try {
                    final Optional<Reject> reject = complexConditions.get(key).validate(validatorToReject.get(key).getCode(), fold, complexValidatorsToHeadersMap.get(key), fold.getHeader());
                    if (reject.isPresent()) {
                        return Optional.of(new DetailReject(reject.get().getCode(), reject.get().getValues().get(), validatorToReject.get(key).getDetail(), fold));
                    }
                } catch (ExgedValidatorException e) {
                    e.printStackTrace();
                }
                return Optional.<DetailReject>empty();
            })
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            if (complexReject.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(complexReject);
            }
        } else {
            return Optional.of(simpleReject);
        }
    }
}
