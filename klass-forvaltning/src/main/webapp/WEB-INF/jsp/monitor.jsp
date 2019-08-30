<?xml version="1.0" encoding="utf-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="no.ssb.klass.forvaltning.controllers.monitor.MonitorStatus" %>
<%@page import="java.net.HttpURLConnection" %>
<%@page import="java.net.InetAddress" %>
<%@page import="java.net.URL" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%!


    String makeStatusLine(boolean success, String statusName, String optionalDescription) {
        String klasse = success ? "applicationOK" : "applicationERROR";
        String html = "<div class=\"" + klasse + "\">"
                + "<strong>" + statusName + ": </strong>"
                + optionalDescription + "</div>";
        return html;
    }

    String testHttpConnectionTil(String beskrivelse, String inputUrl) {
        String html = "";
        try {
            URL url = new URL(inputUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                html = "<div class=\"applicationOK\">"
                        + "<strong>" + beskrivelse + ":</strong>"
                        + " Applikasjonen kan nå " + beskrivelse + " (" + inputUrl + " )</div>";
            } else {
                html = "<div class=\"applicationERROR\">"
                        + "<strong>" + beskrivelse + ":</strong>"
                        + " Feil i kommunikasjon med " + beskrivelse + ". URL: " + inputUrl + " med HTTP response code: " + responseCode + "</div>";
            }

            return html;
        } catch (Exception ex) {
            html = "<div class=\"applicationERROR\">"
                    + "<strong>" + beskrivelse + ":</strong>"
                    + " Feil i kommunikasjon med " + beskrivelse + ". URL: " + inputUrl + ". Feil: " + ex + "</div>";
            return html;
        }

    }

    String generateStatusLines(List<MonitorStatus> statusList) {
        StringBuilder builder = new StringBuilder();
        for (MonitorStatus status : statusList) {
            builder.append(makeStatusLine(status.isSuccessful(), status.getName(), status.getDescription()));
        }
        return builder.toString();
    }


%>

<%

    InetAddress ia = InetAddress.getLocalHost();
    String hostName = ia.getHostName();
    String version = (String) request.getAttribute("version");
    String timestamp = new Date().toString();
    List<MonitorStatus> statusList = (List<MonitorStatus>) request.getAttribute("statusList");


%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <title>Klass monitor</title>
    <style type="text/css">
        .applicationOK {
            background: #EEEEEE url(images/accept.png) no-repeat scroll 2px 5px;
            padding-left: 20px;
            border: solid 1px;
            margin-bottom: 5px;
            padding-top: 5px;
            padding-right: 5px;
        }

        .applicationERROR {
            background: #EEEEEE url(images/exclamation.png) no-repeat scroll 2px 5px;;
            padding-left: 20px;
            border: solid 1px;
            margin-bottom: 5px;
            padding-top: 5px;
        }

        .melding {
            padding: 5px;
            margin-left: -20px;
            background: #F7F7F7;
        }

        .seksjon {
            border-bottom: 2px groove #EEEEEE;
            padding-bottom: 5px;
            margin-left: 20px;
            margin-right: 20px;
        }

        h1 {
            margin-left: 20px;
        }

        h2 {
            margin: 5px 0;
        }

        ul {
            margin-top: 0;
            margin-bottom: 0;
        }

        .info {
            background: #EFEFEF;
            color: #555555;
            font-size: 0.8em;
            text-align: right;
            margin-left: 20px;
            margin-right: 20px;
            margin-top: 5px;
        }

        .info p {
            padding: 0 10px;
            margin: 0;
            display: inline;
        }

        button {
            width: 140px;
        }

        table {
            border-spacing: 0px;
            margin-top: 25px;
        }

        table td {
            border-bottom: 1px solid grey;
        }

        table th {
            padding: 0 40px 12px 0px;
            border-bottom: 1px solid grey;
        }

    </style>
</head>
<body>
<h1>Klass monitor</h1>
<div class="seksjon">
    <h2>Tester Integrasjonspunkter</h2>
    <%= generateStatusLines(statusList) %>
</div>


<div class="info">
    <p>Server: <strong><%=hostName%>
    </strong></p>
    <p>Timestamp: <strong><%=timestamp%>
    </strong></p>
</div>

<div class="seksjon">
    <h2>System</h2>
    <ul>
        <li>App versjon: <%= version %>
        </li>
    </ul>
</div>

</body>
</html>
