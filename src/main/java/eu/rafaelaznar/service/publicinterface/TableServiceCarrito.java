package eu.rafaelaznar.service.publicinterface;

import eu.rafaelaznar.bean.ReplyBean;

public interface TableServiceCarrito {

    public ReplyBean add() throws Exception;

    public ReplyBean remove() throws Exception;
}
