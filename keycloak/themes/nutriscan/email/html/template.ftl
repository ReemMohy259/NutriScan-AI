<#macro emailLayout>
    <html>
    <body style="margin:0; padding:0; background-color:#0d5c5c; font-family: -apple-system, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;">
    <table width="100%" cellpadding="0" cellspacing="0" style="background-color:#0d5c5c; padding:40px 0;">
        <tr>
            <td align="center">
                <table width="480" cellpadding="0" cellspacing="0"
                       style="background-color:#ffffff; border-radius:16px; overflow:hidden;">
                    <tr>
                        <td style="background:linear-gradient(180deg,#0d5c5c,#0a4a4a); padding:32px; text-align:center;">
                            <div style="color:#ffffff; font-size:20px; font-weight:700;">NutriScan AI</div>
                            <div style="color:#9fd6d6; font-size:13px; margin-top:4px;">Intelligence for Nutrition</div>
                        </td>
                    </tr>
                    <tr>
                        <td style="padding:32px; color:#1f2d2d; font-size:14px; line-height:1.6;">
                            <#nested>
                        </td>
                    </tr>
                    <tr>
                        <td style="padding:20px 32px; border-top:1px solid #eee; text-align:center; color:#9aa5a5; font-size:11px;">
                            🛡️ Secure Health Protocol · © NutriScan
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
    </body>
    </html>
</#macro>