package my.vaadin.app;


import com.vaadin.ui.*;


/**
 * Демонстрационое окно логина.
 * Не должно пускать пользователя в систему, пока тот не введёт валидный login/пароль
 */
//FIXME: так делать нельзя, это не безопасно, посольку может быть сломано средствами отладки клиентского браузера
public class LoginDailog extends Window {

    private UserRole role;

    public LoginDailog(String caption, Component content) {
        this(caption);
    }

    public LoginDailog() {
        this("Login");
    }

    public LoginDailog(String caption) {
        super(caption);

        setModal( true);
        setClosable(false);
        setResizable(false);
        center();

        VerticalLayout layout = new VerticalLayout();

        FormLayout loginControls = new FormLayout();
        HorizontalLayout buttons = new HorizontalLayout();
        layout.addComponents(loginControls, buttons);
        buttons.setWidth(100,Unit.PERCENTAGE);
        buttons.setSpacing( true);

        TextField loginFld = new TextField("Login");
        PasswordField passwordFld = new PasswordField("Pasword");
        loginControls.addComponents(loginFld, passwordFld);

        Button loginButton = new Button("Login!");
        Button cancelButton = new Button("Cancel");
        buttons.addComponents( loginButton, cancelButton);

        loginButton.setStyleName("v-button-primary");
        loginButton.addClickListener(e -> {
            final UserRole foundRole = UserService.getRoleFor(loginFld.getValue(), passwordFld.getValue());
            if (foundRole != null) {
                role = foundRole;
                close();
            } else {
                Notification.show("Auth error", Notification.Type.ERROR_MESSAGE);
            }
        });
        cancelButton.addClickListener(e -> Notification.show("Welcome later..."));

        Panel content = new Panel(layout);
        content.setSizeUndefined();
        setContent(content);
    }

    public UserRole getRole() {
        return role;
    }

}
