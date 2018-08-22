package de.intension.lizzy.converter.gherkin;

import static de.intension.lizzy.converter.CaseFormat.SNAKE_CASE;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import gherkin.ast.Feature;

@RunWith(PowerMockRunner.class)
public class GherkinConverterTest
{

    private static final String TEST_CLASS_PATH = "src/test/java/de/intension/lizzy/converter/gherkin/TestClassNameTest.java";

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
     * Given Converter configured to generate a file at {@link #TEST_CLASS_PATH}
     * And a test file with a test method at said location
     * When Converter is executed with {@link GherkinParserTest#TEST_FEATURE}
     * And the feature generates the same name as the existing test file
     * Then Test methods are appended to the existing file.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void should_append_methods_to_existing_file()
        throws Exception
    {
        // given
        GherkinConverter converter = new GherkinConverter().setPackageName("de.intension.lizzy.converter.gherkin");
        aTestFile();
        // when
        converter.convert(aGherkin());
        // then
        File file = new File(TEST_CLASS_PATH);
        List<String> content = Files.readAllLines(file.toPath());
        assertThat(content, hasItems(containsString("should_still_be_there"),
                                     containsString("scenario_1")));
        assertThat(content, hasItems(containsString("Test javadoc"),
                                     containsString("some precondition")));
        assertThat(content, not(hasItem(containsString("@Test()"))));
    }

    /**
     * Given Converter configured to generate a file at {@link #TEST_CLASS_PATH}
     * And a test file with a test method at said location
     * When Converter is executed with {@link GherkinParserTest#TEST_FEATURE}
     * And the feature generates the same name as the existing test file
     * And a scenario generates the same name as the exisiting test method
     * Then Test method is not changed in the existing file.
     */
    @Test
    public void should_not_append_method_if_already_existing()
        throws Exception
    {
        // given
        GherkinConverter converter = new GherkinConverter().setPackageName("de.intension.lizzy.converter.gherkin");
        aTestFile();
        // when
        converter.convert(aGherkin().replace("Scenario 1", "should_still_be_there"));
        // then
        File file = new File(TEST_CLASS_PATH);
        CompilationUnit unit = JavaParser.parse(file);
        assertThat(unit.getClassByName("TestClassNameTest").get().getMethods(), hasSize(1));
        List<String> content = Files.readAllLines(file.toPath());
        assertThat(content, hasItem(containsString("Test javadoc")));
    }

    /**
     * @return Gherkin to generate a test class called 'TestClassNameTest'.
     */
    private String aGherkin()
    {
        return GherkinParserTest.TEST_FEATURE.replace("The name of the feature here", "TestClassName");
    }

    /**
     * Stores a test class with a method at {@link #TEST_CLASS_PATH}.
     */
    private void aTestFile()
        throws IOException
    {
        File testFile = new File(TEST_CLASS_PATH);
        testFile.createNewFile();
        FileWriter writer = new FileWriter(testFile);
        writer.write("package de.intension.lizzy.converter.gherkin;\n" +
                "\n" +
                "import org.junit.Test;\n" +
                "\n" +
                "class TestClassNameTest\n" +
                "{\n" +
                "\n" +
                "/**Test javadoc*/\n" +
                "    @Test\n" +
                "    public void should_still_be_there()\n" +
                "    {\n" +
                "\n" +
                "    }\n" +
                "}");
        writer.close();
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
        JavaFile javaFile = JavaFile.builder(this.getClass().getPackage().getName(), TypeSpec.classBuilder("TestClassNameTest").build()).build();
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
