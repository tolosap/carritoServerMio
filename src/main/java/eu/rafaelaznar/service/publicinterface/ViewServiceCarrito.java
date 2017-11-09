package eu.rafaelaznar.service.publicinterface;

import eu.rafaelaznar.bean.ReplyBean;

public interface ViewServiceCarrito {

    public ReplyBean list() throws Exception;

    public ReplyBean buy() throws Exception;

    public ReplyBean empty() throws Exception;
}
