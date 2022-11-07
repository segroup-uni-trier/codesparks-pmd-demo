/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package de.unitrier.codesparks.demo;

import de.unitrier.st.codesparks.java.JavaClassArtifact;

@JavaClassArtifact
public class PMDClassArtifact extends PMDArtifact
{
    public PMDClassArtifact(final String identifier, final String name)
    {
        super(identifier, name);
        isClass = true;
    }
}
