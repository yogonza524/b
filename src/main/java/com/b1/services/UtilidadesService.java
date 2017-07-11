/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.b1.services;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gonzalo
 */
public class UtilidadesService {
    
    public List<String> getExcepciones(Throwable throwable) {
        List<String> result = new ArrayList<>();
        while (throwable != null) {
            result.add(throwable.getMessage());
            throwable = throwable.getCause();
        }
        return result; //["THIRD EXCEPTION", "SECOND EXCEPTION", "FIRST EXCEPTION"]
    }
    
    public String stack(Throwable throwable){
        return String.join(", ", this.getExcepciones(throwable));
    }
}
