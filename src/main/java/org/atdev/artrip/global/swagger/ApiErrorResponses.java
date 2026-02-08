package org.atdev.artrip.global.swagger;


import org.atdev.artrip.global.apipayload.code.status.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorResponses {

    CommonErrorCode[] common() default {};

    ExhibitErrorCode[] exhibit() default {};

    FavoriteErrorCode[] favorite() default {};

    UserErrorCode[] user() default {};

    ReviewErrorCode[] review() default {};

    S3ErrorCode[] s3() default {};

    SearchErrorCode[] search() default {};

    KeywordErrorCode[] keyword() default {};

    HomeErrorCode[] home() default {};
}
