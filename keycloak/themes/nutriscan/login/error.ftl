<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false; section>
    <#if section = "form">
        <div class="ns-message-block">
            <div class="ns-error-icon">&#33;</div>
            <h2 class="ns-message-header">Verification link issue</h2>
            <#if message?has_content>
                <p class="ns-message-body">${kcSanitize(message.summary)?no_esc}</p>
            </#if>
            <#if client?? && client.baseUrl?has_content>
                <a class="ns-btn" href="${client.baseUrl}">Back to NutriScan AI</a>
            </#if>
        </div>
    </#if>
</@layout.registrationLayout>
