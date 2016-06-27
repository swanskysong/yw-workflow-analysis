package org.yesworkflow.validate;

import org.yesworkflow.annotations.Annotation;

import java.util.List;

/**
 * Created by thsong on 5/21/16.
 */
public class DefaultValidator {
    List<Annotation> annotations;

    public DefaultValidator annotation(List<Annotation> annotations){
        if(annotations == null) throw new IllegalArgumentException("validator: Null annoataions passed to DefaultValidator.");
        this.annotations = annotations;
        return this;
    }

    public void validate() {

    }
}
