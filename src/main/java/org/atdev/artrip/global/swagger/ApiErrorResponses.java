package org.atdev.artrip.global.swagger;


import org.atdev.artrip.global.apipayload.code.status.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorResponses {

    CommonError[] common() default {};
    ExhibitError[] exhibit() default {};
    FavoriteError[] favorite() default {};
    UserError[] user() default {};
    ReviewError[] review() default {};
    S3Error[] s3() default {};

}
