package de.intension.lizzy.converter.gherkin;

import static de.intension.lizzy.converter.CaseFormat.SNAKE_CASE;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import gherkin.ast.Feature;

@RunWith(PowerMockRunner.class)
public class GherkinConverterTest
{

    private static final String TEST_CLASS_PATH = "src/test/java/de/intension/lizzy/converter/gherkin/TestClassName.java";

    /**
     * Given A valid input string for the parser
     * When Converter is executed
     * Then Parser is called with the same string argument
     */
    @Test
    @PrepareForTest({GherkinParser.class})
    public void should_use_parser()
        throws Exception
    {
        // given
        String input = "test success";
        aParser(input);
        try {
            // when
            new GherkinConverter().convert(input);
        } catch (RuntimeException ex) {
            // then
            assertThat(ex.getMessage(), equalTo(input));
        }
    }

    /**
     * Given A parser returning valid output
     * When Converter is executed
     * Then Generator is called with the output from the parser
     */
    @Test
    @PrepareForTest({GherkinParser.class, GherkinGenerator.class})
    public void should_use_generator()
        throws Exception
    {
        // given
        String input = "test success";
        aGenerator(input);
        try {
            // when
            new GherkinConverter().convert(input);
        } catch (RuntimeException ex) {
            // then
            assertThat(ex.getMessage(), equalTo(input));
        }
    }

    /**
     * Given Generator returning valid java file object
     * When Converter is executed
     * Then Java file is created
     */
    @Test
    @PrepareForTest({GherkinParser.class, GherkinGenerator.class})
    public void should_create_java_file_from_generator_output()
        throws Exception
    {
        // given
        aGenerator();
        // when
        new GherkinConverter().convert(null);
        // then
        File file = new File(TEST_CLASS_PATH);
        assertThat(file.exists() && !file.isDirectory(), equalTo(true));
    }

    /**
     * Mocks a {@link GherkinGenerator} to return a createable java file object.
     */
    private void aGenerator()
    {
        Feature feature = Mockito.mock(Feature.class);
        PowerMockito.mockStatic(GherkinParser.class);
        PowerMockito.when(GherkinParser.parseFeature(null)).thenReturn(feature);
        PowerMockito.mockStatic(GherkinGenerator.class);
        JavaFile javaFile = JavaFile.builder(this.getClass().getPackage().getName(), TypeSpec.classBuilder("TestClassName").build()).build();
        PowerMockito.when(GherkinGenerator.generate(feature, null, SNAKE_CASE)).thenReturn(javaFile);
    }

    /**
     * Mocks a {@link GherkinGenerator} to throw a {@link RuntimeException} with a test message.
     */
    private void aGenerator(String string)
    {
        Feature feature = Mockito.mock(Feature.class);
        PowerMockito.mockStatic(GherkinParser.class);
        PowerMockito.when(GherkinParser.parseFeature(string)).thenReturn(feature);
        PowerMockito.mockStatic(GherkinGenerator.class);
        PowerMockito.when(GherkinGenerator.generate(feature, null, SNAKE_CASE)).thenThrow(new RuntimeException(string));
    }

    /**
     * Mocks a {@link GherkinParser} to throw a {@link RuntimeException} with a test message.
     */
    private void aParser(String string)
    {
        PowerMockito.mockStatic(GherkinParser.class);
        PowerMockito.when(GherkinParser.parseFeature(string)).thenThrow(new RuntimeException(string));
    }

    @After
    public void cleanup()
    {
        File file = new File(TEST_CLASS_PATH);
        if (file.exists()) {
            file.delete();
        }
    }
}
