/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.rafaelaznar.service.specificimplementation;

import eu.rafaelaznar.service.genericimplementation.GenericTableService;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author tolosap
 */
public class PedidoSpecificServiceImplementation extends GenericTableService {
    
    public PedidoSpecificServiceImplementation(HttpServletRequest request) {
        super(request);
    }
    
}
