<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=true; section>
    <#if section = "form">
        <h2 class="ns-form-title">Update Password</h2>
        <p class="ns-form-subtitle">Create a new password for your account</p>

        <form id="kc-update-password-form" class="ns-form" action="${url.loginAction}" method="post">
            <input type="text" id="username" name="username" value="${(account.username)!}" autocomplete="username"
                   aria-hidden="true" class="ns-hidden-input">

            <#if password??>
                <div class="ns-field">
                    <label class="ns-label" for="password">${msg("password")}</label>
                    <input class="ns-input" type="password" id="password" name="password" autofocus
                           autocomplete="current-password"
                           aria-invalid="<#if messagesPerField.existsError('password')>true</#if>">
                    <#if messagesPerField.existsError('password')>
                        <span class="ns-field-error" aria-live="polite">${kcSanitize(messagesPerField.get('password'))?no_esc}</span>
                    </#if>
                </div>
            </#if>

            <div class="ns-field">
                <label class="ns-label" for="password-new">${msg("passwordNew")}</label>
                <input class="ns-input" type="password" id="password-new" name="password-new"
                       autocomplete="new-password"
                       aria-invalid="<#if messagesPerField.existsError('password-new')>true</#if>">
                <#if messagesPerField.existsError('password-new')>
                    <span class="ns-field-error" aria-live="polite">${kcSanitize(messagesPerField.get('password-new'))?no_esc}</span>
                </#if>
            </div>

            <div class="ns-field">
                <label class="ns-label" for="password-confirm">${msg("passwordConfirm")}</label>
                <input class="ns-input" type="password" id="password-confirm" name="password-confirm"
                       autocomplete="new-password"
                       aria-invalid="<#if messagesPerField.existsError('password-confirm')>true</#if>">
                <#if messagesPerField.existsError('password-confirm')>
                    <span class="ns-field-error" aria-live="polite">${kcSanitize(messagesPerField.get('password-confirm'))?no_esc}</span>
                </#if>
            </div>

            <button class="ns-submit" type="submit">${msg("doSubmit")}</button>
        </form>
    </#if>
</@layout.registrationLayout>
