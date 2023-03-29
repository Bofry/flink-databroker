package com.bofry.databroker.core.component;

import org.apache.http.HttpStatus;

public class HttpStatusExtension implements HttpStatus {

    // --- 1xx Informational ---
    public static final int SC_CHECKPOINT = 103;

    // --- 2xx Success ---
    public static final int SC_IM_USED = 208;

    // --- 3xx Redirection ---
    public static final int SC_PERMANENT_REDIRECT = 308;

    // --- 4xx Client Error ---
    public static final int SC_I_AM_A_TEAPOT = 418;
    /**
     * @deprecated
     * See <a href="https://tools.ietf.org/rfcdiff?difftype=--hwdiff&amp;url2=draft-ietf-webdav-protocol-06.txt">
     *     WebDAV Draft Changes</a>
     */
    @Deprecated
    public static final int SC_DESTINATION_LOCKED = 421;
    public static final int SC_TOO_EARLY = 425;
    public static final int SC_UPGRADE_REQUIRED = 426;
    public static final int SC_PRECONDITION_REQUIRED = 428;
    public static final int SC_TOO_MANY_REQUESTS = 429;
    public static final int SC_REQUEST_HEADER_FIELDS_TOO_LARGE = 431;
    public static final int SC_UNAVAILABLE_FOR_LEGAL_REASONS = 451;

    // --- 5xx Server Error ---
    public static final int SC_VARIANT_ALSO_NEGOTIATES = 506;
    public static final int SC_LOOP_DETECTED = 508;
    public static final int SC_BANDWIDTH_LIMIT_EXCEEDED = 509;
    public static final int SC_NOT_EXTENDED = 510;
    public static final int SC_NETWORK_AUTHENTICATION_REQUIRED = 511;

}
