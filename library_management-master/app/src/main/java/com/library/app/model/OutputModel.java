package com.library.app.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OutputModel {
    private Object object;
    public Object getObject(String defaultMessage){
        if(object==null){
            object = defaultMessage;
            return object;
        }
        return object;
    }

}
