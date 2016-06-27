<%@ page info="com.mooreb.config help page" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
 
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
  <head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <title>com.mooreb.config Service</title>
    <link rel="stylesheet" href="main.css">
  </head>
  <body>
<h2>Help</h2>
<h4>Context</h4>
<p>Defining a property with a context:</p>
<ul>
<li>Allows the user of the config service to submit identically named properties</li>
<li>Allows the config client to select the most appropriate property</li> 
</ul>
<p>Each config client has a context which contains:</p>
<ul>
<li>Its environment</li>
<li>Its hostname</li>
<li>Its application name</li>
</ul>
<p>Given a property name, the config client will select the most appropriate property from the properties provided by the server. This selection will occur based on the most closely-matching context.</p>
<p>The context field can contain a pipe-separated key-value string including optional definitions for host, env, app. E.g. host=lvdma1006|env=FastFeedback|app=PWS</p>
<ul>
<li>env: The environment level of the client. Defined as a system property on the client (com.mooreb.config.descriptiveEnv)</li>
<li>host: The hostname of the machine executing the request</li>
<li>app: The application making the request. Defined as a system property on the client (com.mooreb.config.appName)</li>
</ul>
<p>Matching preference order:</p>
<ul>
<li>Match on env, app, host</li>
<li>Match on host</li>
<li>Match on env, app</li>
<li>Match on app</li>
<li>Match on env</li>
<li>Empty context on property definition</li>
</ul>
<p>Any mismatch will result in the property being ignored, e.g. a property defined with env=Dev will not be returned where the client submits env=FastFeedback. Note that matches are also case-sensitive.</p>
</body>
</html>