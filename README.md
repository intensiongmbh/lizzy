<img src="src/site/resources/images/Lizzy_logo.png" alt="Lizzy">

[![CircleCI](https://circleci.com/gh/intensiongmbh/lizzy.svg?style=svg)](https://circleci.com/gh/intensiongmbh/lizzy) [![Codecov](https://codecov.io/gh/intensiongmbh/lizzy/branch/master/graph/badge.svg)](https://codecov.io/gh/intensiongmbh/lizzy) [![Vulnerability](https://copilot.blackducksoftware.com/github/repos/intensiongmbh/lizzy/branches/master/badge-risk.svg)](https://copilot.blackducksoftware.com/github/repos/intensiongmbh/lizzy/results)

# Eclipse plugin for unit test generation for connection to ticket systems.

Lizzy is a free, open-source Eclipse plugin to generate test code from BusinessReadableDSL taken from ticket systems.

## Project Layout

##### <a href="lizzy-adapter/">lizzy-adapter</a> - Interfaces between ticket systems and the plugin.
Supported ticket providers:
<ul>
<li><a href="https://www.atlassian.com/software/jira">Jira</a></li>
</ul>

##### <a href="lizzy-converter/">lizzy-converter</a> - Parser to turn BusinessReadableDSL into Java test classes.<br/>
Supported dialects:
<ul>
<li><a href="https://docs.cucumber.io/gherkin/">Gherkin</a></li>
</ul>

##### <a href="lizzy-eclipse/">lizzy-eclipse</a> - Eclipse plugin project; built with <a href="https://www.eclipse.org/tycho/">Tycho</a>
Submodules:
<ul>
<li><a href="lizzy-eclipse/lizzy-eclipse-plugin">lizzy-eclipse-plugin</a> - Contains UI elements and plugin specific content.</li>
<li><a href="lizzy-eclipse/lizzy-eclipse-feature">lizzy-eclipse-feature</a> - Feature project to wrap the plugin.</li>
<li><a href="lizzy-eclipse/lizzy-eclipse-updatesite">lizzy-eclipse-updatesite</a> - Update site/repository to publish the feature.</li>
<li><a href="lizzy-eclipse/lizzy-eclipse-license">lizzy-eclipse-license</a> - Contains the license to use in the feature.</li>
</ul>

## License

Lizzy is licensed under the <a href="http://www.eclipse.org/legal/epl-2.0/">Eclipse Public License 2.0</a>. See the license header in the respective file to be sure.