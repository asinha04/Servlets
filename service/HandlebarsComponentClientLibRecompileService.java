package org.kp.foundation.core.service;

import org.kp.foundation.core.exception.DynamicClientLibException;

import javax.jcr.Session;
import java.util.ArrayList;

public interface HandlebarsComponentClientLibRecompileService {

    /**
     * Given a contentPath and a Handlebar component path {/apps}, find all instances and recompile handlebar
     * Optional: Replicate content to publisher instances
     * @param componentPath
     * @param contentPath
     * @param isReplicate
     * @throws DynamicClientLibException
     */
    ArrayList<String> recompileHandlebarComponents(String componentPath, String contentPath, boolean isReplicate, Session currentUserSession) throws DynamicClientLibException;
}
