package ec.facturacion.electronica.controllers;

import ec.facturacion.electronica.entities.Iva;
import ec.facturacion.electronica.controllers.util.JsfUtil;
import ec.facturacion.electronica.controllers.util.JsfUtil.PersistAction;
import ec.facturacion.electronica.services.IvaFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@ManagedBean(name = "ivaController")
@SessionScoped
public class IvaController implements Serializable {

    @EJB
    private ec.facturacion.electronica.services.IvaFacade ejbFacade;
    private List<Iva> items = null;
    private Iva selected;

    public IvaController() {
    }

    public Iva getSelected() {
        return selected;
    }

    public void setSelected(Iva selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private IvaFacade getFacade() {
        return ejbFacade;
    }

    public Iva prepareCreate() {
        selected = new Iva();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("ec.facturacion.electronica.util.Bundle").getString("IvaCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("ec.facturacion.electronica.util.Bundle").getString("IvaUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("ec.facturacion.electronica.util.Bundle").getString("IvaDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Iva> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("ec.facturacion.electronica.util.Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("ec.facturacion.electronica.util.Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public List<Iva> getItemsAvailableSelectMany() {
        return getFacade().findByEnabled(new Boolean(true));
    }

    public List<Iva> getItemsAvailableSelectOne() {
        return getFacade().findByEnabled(new Boolean(true));
    }

    @FacesConverter(forClass = Iva.class)
    public static class IvaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            IvaController controller = (IvaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "ivaController");
            return controller.getFacade().find(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Iva) {
                Iva o = (Iva) object;
                return getStringKey(o.getIvaCode());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Iva.class.getName()});
                return null;
            }
        }

    }

}
