package validator;

public @interface ValidatorAnnotation {
    String name() default "unknownValidator";
}
