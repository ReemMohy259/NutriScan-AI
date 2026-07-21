<#import "template.ftl" as layout>
<@layout.emailLayout>
    <h2 style="color:#0f4c4c; margin-top:0;">Reset your password</h2>
    <p>A password reset has been requested for your NutriScan account. If this was you, click below:</p>
    <p style="text-align:center; margin:28px 0;">
        <a href="${link}" style="background:#0f4c4c; color:#ffffff; padding:12px 24px; border-radius:8px; text-decoration:none; font-weight:600;">
            Reset Password
        </a>
    </p>
    <p style="color:#667;">This link expires in ${linkExpirationFormatter(linkExpiration)}. If you didn't request this, you can ignore this email.</p>
</@layout.emailLayout>