<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false; section>
    <#if section = "form">
        <div class="ns-message-block">
            <#if messageHeader??>
                <h2 class="ns-message-header">${kcSanitize(msg("${messageHeader}"))?no_esc}</h2>
            <#else>
                <h2 class="ns-message-header">${message.summary}</h2>
            </#if>

            <#if requiredActions??>
                <p class="ns-message-body">
                    <#list requiredActions>
                        <#items as reqActionItem>
                            ${msg("requiredAction.${reqActionItem}")}<#sep>, </#sep>
                        </#items>
                    </#list>
                </p>
            </#if>

            <#if skipLink??>
            <#else>
                <#if pageRedirectUri??>
                    <a class="ns-btn" href="${pageRedirectUri}">${kcSanitize(msg("backToApplication"))?no_esc}</a>
                <#elseif actionUri??>
                    <a class="ns-btn" href="${actionUri}">${kcSanitize(msg("proceedWithAction"))?no_esc}</a>
                <#elseif (client.baseUrl)??>
                    <a class="ns-btn" href="${client.baseUrl}">${kcSanitize(msg("backToApplication"))?no_esc}</a>
                </#if>
            </#if>
        </div>
    </#if>
</@layout.registrationLayout>
