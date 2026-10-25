# PE and grading

## Reporting rules

[Current PE instructions](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-pe.html#tp-practical-exam-pe):

* Scope: released JAR behavior and rendered UG/DG pages. Code quality and unrelated website/source Markdown are excluded; code inspection can guide testing.
* Reports must result from your own testing. Tools, including AI, are allowed. Product testing precedes documentation testing.
* One issue per independent bug; put the complete report in its body, with exactly one `type.*` and one `severity.*`. Avoid inter-issue references. Select up to six reports.

| Type | Basis |
| --- | --- |
| `type.FunctionalityBug` | Behavior contradicts specified or reasonable expectations. |
| `type.FeatureFlaw` | Design or missing functionality impairs normal target-user work. |
| `type.DocumentationBug` | Documentation is incorrect, incomplete, or hinders readers. |

| Severity | Impact |
| --- | --- |
| `severity.VeryLow` | Purely cosmetic; usage unaffected. |
| `severity.Low` | Rare, minor inconvenience. |
| `severity.Medium` | Occasional inconvenience; usage remains possible. |
| `severity.High` | Almost unusable for most users. |

Use the lower severity when evenly balanced. Known issues and inherited AB3 defects are not automatically exempt. Distinguish plausible user mistakes from deliberate sabotage. Recheck the live triaging rules for duplicates, data-file edits, and `NotInScope`; recheck phase-specific instructions before drafting developer/tester responses.

## Grading lenses

[tP grading](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-grading.html): review product fit/cohesion, implementation quality, QA, documentation, and project management when within scope. Check meaningful logging, exceptions, assertions, defensive coding, SLAP, duplication, and standards beyond Checkstyle. Assess UG/DG explanations, navigation, examples, visuals, links, UML, requirements, and glossary against their audience. Tie each concern to a concrete obstacle; avoid demanding more content merely for volume. Accuracy of classifications and responses matters; do not inflate severity or invent exact penalties. Use the live rubric for marks and administrative details.
