<#macro registrationLayout bodyClass="" displayInfo=false displayMessage=true displayRequiredFields=false>
<!DOCTYPE html>
<html class="${properties.kcHtmlClass!}"<#if realm.internationalizationEnabled> lang="${locale.currentLanguageTag}"</#if>>

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <meta name="robots" content="noindex, nofollow">
    <title>NutriScan AI</title>
    <link rel="icon" href="${url.resourcesPath}/img/favicon.svg">
    <#if properties.styles?has_content>
        <#list properties.styles?split(' ') as style>
            <link href="${url.resourcesPath}/${style}" rel="stylesheet">
        </#list>
    </#if>
</head>

<body class="ns-body">
    <div class="ns-page">
        <div class="ns-card">

            <div class="ns-logo-badge">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M5 3h9l5 5v13a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1z" fill="#0d5c5c"/>
                    <path d="M14 3v5h5" fill="#0a4a4a"/>
                    <circle cx="15.5" cy="14.5" r="3.2" fill="#ffffff"/>
                    <circle cx="15.5" cy="13.2" r="1.1" fill="#0d5c5c"/>
                    <path d="M13 17.3c0-1.4 1.1-2.3 2.5-2.3s2.5.9 2.5 2.3" stroke="#0d5c5c" stroke-width="0.9" fill="none" stroke-linecap="round"/>
                    <rect x="7" y="8" width="6" height="1.3" rx="0.6" fill="#ffffff"/>
                    <rect x="7" y="11" width="5" height="1.3" rx="0.6" fill="#ffffff"/>
                    <rect x="7" y="14" width="4" height="1.3" rx="0.6" fill="#ffffff"/>
                </svg>
            </div>

            <h1 class="ns-title">NutriScan AI</h1>
            <p class="ns-tagline">  Intelligence for Nutrition</p>

            <div class="ns-divider"></div>

            <#-- Page-specific header, e.g. "Email verified" -->
            <#if displayMessage && message?has_content>
                <div class="alert alert-${message.type}">
                    <#if message.type = 'success'><span class="ns-alert-icon">&#10003;</span></#if>
                    <#if message.type = 'error'><span class="ns-alert-icon">&#33;</span></#if>
                    <span class="ns-alert-text">${kcSanitize(message.summary)?no_esc}</span>
                </div>
            </#if>

            <div class="ns-content">
                <#nested "form">
            </div>

            <#if displayInfo>
                <div class="ns-info">
                    <#nested "info">
                </div>
            </#if>

            <div class="ns-footer">
                <span class="ns-shield">&#128274;</span>
                <span>SECURE HEALTH PROTOCOL</span>
            </div>

        </div>
    </div>
</body>
</html>
</#macro>
