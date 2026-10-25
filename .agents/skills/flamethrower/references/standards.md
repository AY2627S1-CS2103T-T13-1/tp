# Standards

These cached summaries support offline and quick review. When online, consult the linked section for disputed or unlisted rules; offline, identify such rules as unverified rather than blocking findings supported here. Apply only basic and intermediate Java rules; do not import advanced rules as requirements.

## Java

[SE-Education Java standard: basic and intermediate](https://se-education.org/guides/conventions/java/intermediate.html)

* Names: lowercase packages; PascalCase class/enum nouns; camelCase variables and method verbs; uppercase underscore-separated constants. Boolean names should read as predicates, collection names as plurals, and embedded acronyms as ordinary words. Test names may use `feature_scenario_expected` with optional suffixes.
* Layout: four spaces, no tabs; 110-character soft limit, 120 hard; continuations eight spaces beyond the parent. Use K&R braces, including single-statement branches/loops. Break after commas and before operators; separate logical blocks with blank lines.
* Statements: package declarations, explicit imports in consistent order, `[]` on the type, initialized variables in minimal scope. Avoid public fields except constants and behaviorless data classes. Mark intentional switch fallthrough with `// Fallthrough`.
* Comments: American English; descriptive Javadoc for classes and public methods, with accessor, inherited-contract, and test exceptions. Method summaries start with third-person verbs. Keep the opening delimiter separate, stars aligned, a blank line before tags, and no gap before the declaration. Parameter tags are all-or-none when explanatory; return tags may be redundant.

The linked standard delegates uncovered topics to Google Java Style; do not substitute Google's defaults for explicit course rules.

## Git

[SE-Education Git conventions](https://se-education.org/guides/conventions/git.html)

Commit subjects use a capitalized imperative, no final period, about 50 characters and at most 72; an applicable scope/category prefix is allowed. Nontrivial commits need a body separated by a blank line, wrapped at 72 characters, explaining the situation, motivation, change, and rationale rather than narrating implementation. Branch names use meaningful kebab-case; issue branches use `issueNumber-keywords`. Respect any required branch namespace. Review history only when relevant to the requested PR/process scope.

## GitHub Flavored Markdown

[SE-Education Markdown standard](https://se-education.org/guides/conventions/markdown.html)

Use strict GFM syntax, natural prose lines without fixed-width wrapping, blank lines before lists/code blocks and around headings, and a space after heading markers. Prefix every blockquote line with `>`. Use `1.` for every ordered-list item, `*` for bullets, and `_` for italics. Check the rendered output and links, including the project's actual documentation renderer.

## Documentation prose

[Google documentation style guide](https://developers.google.com/style) supplies editorial guidance after project-specific style; its recommendations allow justified, consistent departures.

[Highlights](https://developers.google.com/style/highlights): address the reader as “you,” use active voice and American spelling, put conditions before instructions, and use sentence-case headings, descriptive links, unambiguous dates, and useful image alt text.

[Text formatting](https://developers.google.com/style/text-formatting): use code formatting for identifiers, filenames, and commands; bold for UI labels; italics sparingly. Judge UG clarity for the intended user and DG clarity for an incoming developer. A preference needs a reader-facing consequence before becoming a PE documentation bug.
