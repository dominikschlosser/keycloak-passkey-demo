package com.github.dominikschlosser.keycloak.passkeys;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.credential.WebAuthnCredentialModel;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

@AutoService(AuthenticatorFactory.class)
public class PasskeysUsernamePasswordFormFactory implements AuthenticatorFactory {
    public static final String PROVIDER_ID = "auth-passkeys-username-password-form";
    public static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = new AuthenticationExecutionModel.Requirement[]{
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.ALTERNATIVE
    };

    public PasskeysUsernamePasswordFormFactory() {
    }

    public Authenticator create(KeycloakSession session) {
        return new PasskeysUsernamePasswordForm(session);
    }

    public void init(Config.Scope config) {
    }

    public void postInit(KeycloakSessionFactory factory) {
    }

    public void close() {
    }

    public String getId() {
        return PROVIDER_ID;
    }

    public String getReferenceCategory() {
        return WebAuthnCredentialModel.TYPE_PASSWORDLESS;
    }

    public boolean isConfigurable() {
        return false;
    }

    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    public String getDisplayType() {
        return "Passkeys + Username Password Form";
    }

    public String getHelpText() {
        return "Validates a username and password from login form or by passkey (Conditional UI).";
    }

    public List<ProviderConfigProperty> getConfigProperties() {
        return null;
    }

    public boolean isUserSetupAllowed() {
        return true;
    }

}
