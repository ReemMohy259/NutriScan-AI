<#import "template.ftl" as layout>
<@layout.emailLayout>
    <h2 style="color:#0f4c4c; margin-top:0;">Verify your email address</h2>
    <p>Welcome to NutriScan! Please confirm this is your email address by clicking the button below.</p>
    <p style="text-align:center; margin:28px 0;">
        <a href="${link}" style="background:#0f4c4c; color:#ffffff; padding:12px 24px; border-radius:8px; text-decoration:none; font-weight:600; display:inline-block;">
            Verify Email
        </a>
    </p>
    <p style="color:#667;">This link expires in ${linkExpirationFormatter(linkExpiration)}.</p>
    <p style="color:#667;">If you didn't create an account with NutriScan, you can safely ignore this email.</p>
</@layout.emailLayout>