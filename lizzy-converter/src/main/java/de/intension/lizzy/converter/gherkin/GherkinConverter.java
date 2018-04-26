package de.intension.lizzy.converter.gherkin;

import java.io.File;
import java.io.IOException;

import com.squareup.javapoet.JavaFile;

import de.intension.lizzy.converter.CaseFormat;
import gherkin.ast.Feature;

/**
 * Converts a gherkin feature string to a java class file.
 * See more about the file generation at: {@link GherkinGenerator}.
 */
public class GherkinConverter
{

    private String     location     = "src/test/java";
    private String     packageName;
    private CaseFormat methodFormat = CaseFormat.SNAKE_CASE;

    /**
     * Converts a gherkin string to a java test class.
     * Uses {@link GherkinParser#parseFeature(String)}
     * And {@link GherkinGenerator#generate(Feature, String, CaseFormat)}
     * to generate the file content.
     *
     * @param string Gherkin feature string to convert.
     * @throws IOException When the file could not be written to.
     */
    public void convert(String string)
        throws IOException
    {
        Feature feature = GherkinParser.parseFeature(string);
        JavaFile javaFile = GherkinGenerator.generate(feature, packageName, methodFormat);
        File file = new File(location);
        javaFile.writeTo(file);
    }

    /**
     * Determines where the java file is located.
     * Default is src/test/java/&#60;packageName>
     *
     * @see #setPackageName(String)
     */
    public GherkinConverter setLocation(String location)
    {
        this.location = location;
        return this;
    }

    /**
     * Determines the package name of the class to generate and where the java file is located.
     *
     * @see #setLocation(String)
     */
    public GherkinConverter setPackageName(String packageName)
    {
        this.packageName = packageName;
        return this;
    }

    /**
     * @see #setMethodFormat(CaseFormat)
     */
    public GherkinConverter setMethodFormat(String methodFormat)
    {
        this.methodFormat = CaseFormat.valueOf(methodFormat);
        return this;
    }

    /**
     * Use this format to determine whether the method names
     * should be generated in snake or camel case.
     *
     * @see CaseFormat
     */
    public GherkinConverter setMethodFormat(CaseFormat methodFormat)
    {
        this.methodFormat = methodFormat;
        return this;
    }
}
