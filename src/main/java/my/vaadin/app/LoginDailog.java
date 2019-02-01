package my.vaadin.app;


import com.vaadin.ui.*;

import java.util.function.Function;


/**
 * Демонстрационое окно логина.
 * Не должно пускать пользователя в систему, пока тот не введёт валидный login/пароль
 */
//FIXME: так делать нельзя, это не безопасно, посольку может быть сломано средствами отладки клиентского браузера
public class LoginDailog extends Window {

    private UserRole role;

    private LoginDailog(String caption, Component content) {
        super(caption, content);
    }

    private LoginDailog() {
        super();
    }

    public LoginDailog( IApply<UserRole> onSuccess) {
        super("Login");

        setModal(true);
        setClosable(false);
        setResizable(false);
        center();

        VerticalLayout layout = new VerticalLayout();

        FormLayout loginControls = new FormLayout();
        HorizontalLayout buttons = new HorizontalLayout();
        layout.addComponents(loginControls, buttons);
        buttons.setWidth(100, Unit.PERCENTAGE);
        buttons.setSpacing(true);

        TextField loginFld = new TextField("Login");
        PasswordField passwordFld = new PasswordField("Pasword");
        loginControls.addComponents(loginFld, passwordFld);

        Button loginButton = new Button("Login!");
        Button cancelButton = new Button("Cancel");
        buttons.addComponents(loginButton, cancelButton);

        loginButton.setStyleName("v-button-primary");
        loginButton.addClickListener(e -> {
            final UserRole foundRole = UserService.getRoleFor(loginFld.getValue(), passwordFld.getValue());
            if (foundRole != null) {
                onSuccess.apply(foundRole) ;
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
