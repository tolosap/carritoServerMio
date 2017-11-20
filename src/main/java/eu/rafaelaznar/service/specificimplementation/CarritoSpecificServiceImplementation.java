/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.rafaelaznar.service.specificimplementation;

import com.google.gson.Gson;
import eu.rafaelaznar.bean.specificimplementation.CarritoSpecificBeanImplementation;
import eu.rafaelaznar.bean.ReplyBean;
import eu.rafaelaznar.bean.specificimplementation.LineapedidoSpecificBeanImplementation;
import eu.rafaelaznar.bean.specificimplementation.PedidoSpecificBeanImplementation;
import eu.rafaelaznar.bean.specificimplementation.ProductoSpecificBeanImplementation;
import eu.rafaelaznar.bean.specificimplementation.UsuarioSpecificBeanImplementation;
import eu.rafaelaznar.connection.ConnectionInterface;
import eu.rafaelaznar.dao.specificimplementation.LineapedidoSpecificDaoImplementation;
import eu.rafaelaznar.dao.specificimplementation.PedidoSpecificDaoImplementation;
import eu.rafaelaznar.dao.specificimplementation.ProductoSpecificDaoImplementation;
import eu.rafaelaznar.helper.AppConfigurationHelper;
import eu.rafaelaznar.helper.Log4jConfigurationHelper;
import eu.rafaelaznar.service.publicinterface.TableServiceCarrito;
import eu.rafaelaznar.service.publicinterface.ViewServiceCarrito;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author tolosap
 */
public class CarritoSpecificServiceImplementation implements TableServiceCarrito, ViewServiceCarrito {

    HttpServletRequest oRequest = null;

    public CarritoSpecificServiceImplementation(HttpServletRequest request) {
        oRequest = request;
    }

    private Boolean checkPermission(String strMethodName) throws Exception {
        UsuarioSpecificBeanImplementation oUsuarioBean = (UsuarioSpecificBeanImplementation) oRequest.getSession().getAttribute("user");
        if (oUsuarioBean != null) {
            return true;
        } else {
            return false;
        }
    }

    private CarritoSpecificBeanImplementation find(ArrayList<CarritoSpecificBeanImplementation> alCarrito, int id) {
        Iterator<CarritoSpecificBeanImplementation> iterator = alCarrito.iterator();
        while (iterator.hasNext()) {
            CarritoSpecificBeanImplementation oCarrito = iterator.next();
            if (id == (oCarrito.getProducto().getId())) {
                return oCarrito;
            }
        }
        return null;
    }

    @Override
    public ReplyBean add() throws Exception {
        if (this.checkPermission("add")) {
            ArrayList<CarritoSpecificBeanImplementation> alCarrito = (ArrayList) oRequest.getSession().getAttribute("carrito");
            ReplyBean oReplyBean = null;
            CarritoSpecificBeanImplementation oCarritoBean = null;
            int id = Integer.parseInt(oRequest.getParameter("id"));
            int cantidad = Integer.parseInt(oRequest.getParameter("cantidad"));
            Connection oConnection = null;
            ConnectionInterface oPooledConnection = null;
            try {
                oPooledConnection = AppConfigurationHelper.getSourceConnection();
                oConnection = oPooledConnection.newConnection();
                
                
                ProductoSpecificBeanImplementation oBean = new ProductoSpecificBeanImplementation(id);
                ProductoSpecificDaoImplementation oDao = new ProductoSpecificDaoImplementation(oConnection, (UsuarioSpecificBeanImplementation) oRequest.getSession().getAttribute("user"), null);
                oBean = (ProductoSpecificBeanImplementation) oDao.get(id, AppConfigurationHelper.getJsonMsgDepth());
                oCarritoBean = new CarritoSpecificBeanImplementation(cantidad, oBean);
                
                CarritoSpecificBeanImplementation oCarrito = find(alCarrito, oCarritoBean.getProducto().getId());
                
                if (oCarrito == null) {
                    CarritoSpecificBeanImplementation oCarroBean = new CarritoSpecificBeanImplementation(cantidad, oBean);
                    alCarrito.add(oCarroBean);
                } else {
                    Integer oldCantidad = oCarrito.getCantidad();
                    oCarrito.setCantidad(oldCantidad + cantidad);
                }
                
                
                Gson oGson = AppConfigurationHelper.getGson();
                String strJson = oGson.toJson(alCarrito);
                oReplyBean = new ReplyBean(200, strJson);
            } catch (Exception ex) {
                String msg = this.getClass().getName() + ":" + (ex.getStackTrace()[0]).getMethodName();
                Log4jConfigurationHelper.errorLog(msg, ex);
                throw new Exception(msg, ex);
            } finally {
                if (oConnection != null) {
                    oConnection.close();
                }
                if (AppConfigurationHelper.getSourceConnection() != null) {
                    AppConfigurationHelper.getSourceConnection().disposeConnection();
                }
            }
            return oReplyBean;
        } else {
            return new ReplyBean(401, "Unauthorized operation");
        }
    }

    @Override
    public ReplyBean remove() throws Exception {
        if (this.checkPermission("remove")) {
            ArrayList<CarritoSpecificBeanImplementation> alCarrito = (ArrayList) oRequest.getSession().getAttribute("carrito");
            int id = Integer.parseInt(oRequest.getParameter("id"));
            ReplyBean oReplyBean = null;
            try {
                CarritoSpecificBeanImplementation oCarrito = find(alCarrito, id);
                alCarrito.remove(oCarrito);
                
                Gson oGson = AppConfigurationHelper.getGson();
                String strJson = oGson.toJson(alCarrito);
                oReplyBean = new ReplyBean(200, strJson);
            } catch (Exception ex) {
                String msg = this.getClass().getName() + ":" + (ex.getStackTrace()[0]).getMethodName();
                Log4jConfigurationHelper.errorLog(msg, ex);
                throw new Exception(msg, ex);
            }
            return oReplyBean;
        } else {
            return new ReplyBean(401, "Unauthorized operation");
        }
    }

    @Override
    public ReplyBean list() throws Exception {
        if (this.checkPermission("list")) {
            ArrayList<CarritoSpecificBeanImplementation> alCarrito = (ArrayList) oRequest.getSession().getAttribute("carrito");
            ReplyBean oReplyBean = null;
            try {
                Gson oGson = AppConfigurationHelper.getGson();
                String strJson = oGson.toJson(alCarrito);
                oReplyBean = new ReplyBean(200, strJson);
            } catch (Exception ex) {
                String msg = this.getClass().getName() + ":" + (ex.getStackTrace()[0]).getMethodName();
                Log4jConfigurationHelper.errorLog(msg, ex);
                throw new Exception(msg, ex);
            }
            return oReplyBean;
        } else {
            return new ReplyBean(401, "Unauthorized operation");
        }
    }

    @Override
    public ReplyBean buy() throws Exception {
        if (this.checkPermission("buy")) {
            ArrayList<CarritoSpecificBeanImplementation> alCarrito = (ArrayList) oRequest.getSession().getAttribute("carrito");
            Connection oConnection = null;
            ReplyBean oReplyBean = null;
            ConnectionInterface oPooledConnection = null;
            Date fecha = new Date(456789123);
            Integer numped;
            String strJson;
            try {
                oPooledConnection = AppConfigurationHelper.getSourceConnection();
                oConnection = oPooledConnection.newConnection();
                oConnection.setAutoCommit(false);
                UsuarioSpecificBeanImplementation oUsuarioBean = (UsuarioSpecificBeanImplementation) oRequest.getSession().getAttribute("user");                
                
                PedidoSpecificBeanImplementation oPedidoBean = new PedidoSpecificBeanImplementation(oUsuarioBean.getId(), fecha, true);
                PedidoSpecificDaoImplementation oPedidoDao = new PedidoSpecificDaoImplementation(oConnection, (UsuarioSpecificBeanImplementation) oRequest.getSession().getAttribute("user"), null);
                oPedidoBean.setId(oPedidoDao.set(oPedidoBean));
                
                numped = oPedidoDao.set(oPedidoBean);
                
                ProductoSpecificBeanImplementation oProductoBean = null;
                ProductoSpecificDaoImplementation oProductoDao = new ProductoSpecificDaoImplementation(oConnection, (UsuarioSpecificBeanImplementation) oRequest.getSession().getAttribute("user"), null);
                LineapedidoSpecificDaoImplementation oLineadepedidoDao = new LineapedidoSpecificDaoImplementation(oConnection, (UsuarioSpecificBeanImplementation) oRequest.getSession().getAttribute("user"), null);
                
                Integer alCarritoSize = alCarrito.size();
                for (int i = 0; i < alCarritoSize; i++) {
                    oProductoBean = alCarrito.get(i).getProducto();
                    Integer newCantidad = alCarrito.get(i).getCantidad();
                    LineapedidoSpecificBeanImplementation oLineadepedidoBean = new LineapedidoSpecificBeanImplementation();
                    oLineadepedidoBean.setCantidad(newCantidad);
                    oLineadepedidoBean.setId_pedido(oPedidoBean.getId());
                    oLineadepedidoBean.setId_producto(oProductoBean.getId());
                    oLineadepedidoBean.setId(oLineadepedidoDao.set(oLineadepedidoBean));
                    oProductoBean.setExistencias(oProductoBean.getExistencias() - newCantidad);
                    oProductoDao.set(oProductoBean);
                }
                Gson oGson = AppConfigurationHelper.getGson();
                strJson = oGson.toJson(numped);
                alCarrito.clear();
                oConnection.commit();
            } catch (Exception ex) {
                oConnection.rollback();
                String msg = this.getClass().getName() + ":" + (ex.getStackTrace()[0]).getMethodName();
                Log4jConfigurationHelper.errorLog(msg, ex);
                throw new Exception(msg, ex);
            } finally {
                if (oConnection != null) {
                    oConnection.close();
                }
                if (AppConfigurationHelper.getSourceConnection() != null) {
                    AppConfigurationHelper.getSourceConnection().disposeConnection();
                }
            }
            return oReplyBean = new ReplyBean(200, strJson);
        } else {
            return new ReplyBean(401, "Unauthorized operation");
        }
    }

    @Override
    public ReplyBean empty() throws Exception {
        if (this.checkPermission("empty")) {
            ArrayList<CarritoSpecificBeanImplementation> alCarrito = (ArrayList) oRequest.getSession().getAttribute("carrito");
            ReplyBean oReplyBean = null;
            try {
                alCarrito.clear();

                Gson oGson = AppConfigurationHelper.getGson();
                String strJson = oGson.toJson(alCarrito);
                oReplyBean = new ReplyBean(200, strJson);

            } catch (Exception ex) {
                String msg = this.getClass().getName() + ":" + (ex.getStackTrace()[0]).getMethodName();
                Log4jConfigurationHelper.errorLog(msg, ex);
                throw new Exception(msg, ex);
            }
            return oReplyBean;
        } else {
            return new ReplyBean(401, "Unauthorized operation");
        }
    }
    
    public ReplyBean count() throws Exception {
        if (this.checkPermission("count")) {
            ArrayList<CarritoSpecificBeanImplementation> alCarrito = (ArrayList) oRequest.getSession().getAttribute("carrito");
            ReplyBean oReplyBean = null;            
            Gson oGson = AppConfigurationHelper.getGson();           
            String strJson = oGson.toJson(alCarrito.size());
            oReplyBean = new ReplyBean(200, strJson);
            return oReplyBean;
        } else {
            return new ReplyBean(401, "Unauthorized operation");
        }
    }
}
