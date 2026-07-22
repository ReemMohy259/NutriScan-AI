<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=true; section>
    <#if section = "form">
        <h2 class="ns-form-title">Reset Password</h2>
        <p class="ns-form-subtitle">Enter your email to receive a reset link</p>

        <form id="kc-reset-password-form" class="ns-form" action="${url.loginAction}" method="post">
            <div class="ns-field">
                <label class="ns-label" for="username"><#if !realm.loginWithEmailAllowed>${msg("username")}<#elseif !realm.registrationEmailAsUsername>${msg("email")}<#else>${msg("email")}</#if></label>
                <input class="ns-input" type="text" id="username" name="username"
                       autofocus autocomplete="username"
                       value="${(auth?.attemptedUsername)!}"
                       aria-invalid="<#if messagesPerField.existsError('username')>true</#if>">
                <#if messagesPerField.existsError('username')>
                    <span class="ns-field-error" aria-live="polite">${kcSanitize(messagesPerField.get('username'))?no_esc}</span>
                </#if>
            </div>

            <button class="ns-submit" type="submit">${msg("doSubmit")}</button>
        </form>

        <p class="ns-form-footer">
            <a class="ns-link" href="${url.loginUrl}">${msg("backToLogin")}</a>
        </p>
    </#if>
</@layout.registrationLayout>
