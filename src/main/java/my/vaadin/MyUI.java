package my.vaadin;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import my.vaadin.app.Customer;
import my.vaadin.app.CustomerService;

import javax.servlet.annotation.WebServlet;
import java.util.List;

/**
 * This UI is the application entry point. A UI may either represent a browser window
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {


    private CustomerService service = CustomerService.getInstance();
    private Grid<Customer> grid = new Grid<>(Customer.class);
    private TextField filterText = new TextField();
    private Button clearFilterTextBtn = new Button(FontAwesome.TIMES);

    private CustomerForm form = new CustomerForm( this);

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();

        grid.setColumns("firstName", "lastName", "email");

        filterText.setPlaceholder("filter by name");
        filterText.addValueChangeListener(e -> updateList());
        filterText.setValueChangeMode(ValueChangeMode.LAZY);


        CssLayout filtering = new CssLayout();
        filtering.addComponents( filterText, clearFilterTextBtn);
        filtering.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);


        HorizontalLayout main = new HorizontalLayout( grid, form);
        main.setSizeFull();
        grid.setSizeFull();
        main.setExpandRatio(grid,1);

        layout.addComponents(filtering, main);


        updateList();

        form.setVisible(false);


        grid.asSingleSelect().addValueChangeListener( e->{
            if (e.getValue() == null) {

                form.setVisible( false);
            }
            else {
                form.setCustomer( e.getValue());
            }
        });

        setContent(layout);
    }

    void updateList() {
        // fetch list of Customers from service and assign it to Grid
        List<Customer> customers = service.findAll( filterText.getValue());
        grid.setItems(customers);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
