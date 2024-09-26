package com.github.dominikschlosser.keycloak.passkeys;

import com.webauthn4j.data.AuthenticationRequest;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.util.exception.WebAuthnException;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.WebAuthnConstants;
import org.keycloak.authentication.*;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordForm;
import org.keycloak.authentication.authenticators.browser.WebAuthnPasswordlessAuthenticator;
import org.keycloak.authentication.requiredactions.WebAuthnPasswordlessRegisterFactory;
import org.keycloak.common.util.Base64Url;
import org.keycloak.common.util.UriUtils;
import org.keycloak.credential.*;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.forms.login.freemarker.model.LoginBean;
import org.keycloak.forms.login.freemarker.model.WebAuthnAuthenticatorsBean;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.WebAuthnPolicy;
import org.keycloak.models.credential.WebAuthnCredentialModel;
import org.keycloak.sessions.AuthenticationSessionModel;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.keycloak.WebAuthnConstants.AUTH_ERR_DETAIL_LABEL;
import static org.keycloak.WebAuthnConstants.AUTH_ERR_LABEL;
import static org.keycloak.services.messages.Messages.*;

@JBossLog
public class PasskeysUsernamePasswordForm extends WebAuthnPasswordlessAuthenticator {
    public PasskeysUsernamePasswordForm(KeycloakSession session) {
        super(session);
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        UsernamePasswordForm usernamePasswordForm = new UsernamePasswordForm();
        usernamePasswordForm.authenticate(context);

        super.authenticate(context);

        LoginFormsProvider form = context.form();
        form.setAttribute("login", new LoginBean(null));
        context.challenge(form.createForm("login-username-password-passkeys.ftl"));
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();

        if (formData.containsKey("password")) {
            UsernamePasswordForm usernamePasswordForm = new UsernamePasswordForm() {

                @Override
                protected Response challenge(AuthenticationFlowContext context, String error, String field) {
                    PasskeysUsernamePasswordForm.this.authenticate(context); // add webauthn attributes
                    return super.challenge(context, error, field);
                }

                @Override
                protected Response createLoginForm(LoginFormsProvider form) {
                    form.setAttribute("login", new LoginBean(formData));
                    return form.createForm("login-username-password-passkeys.ftl");
                }
            };

            usernamePasswordForm.action(context);
        } else {
            super.action(context);
        }

    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }
}
